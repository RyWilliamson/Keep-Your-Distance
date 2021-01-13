#include <Arduino.h>
#include <U8x8lib.h>
#include <BLEAdvertisedDevice.h>

#include "bluetoothlib.h"
#include "common.h"
#include "rbtree.h"

// Screen
U8X8_SSD1306_128X64_NONAME_SW_I2C screen(/* clock=*/ 15, /* data=*/ 4, /* reset=*/ 16);

// Config Data Defaults
int measured_power = -81;
int environment = 3;
float distance = 1.5;
int target_rssi = -86;

// BLE Data
int scanTime = 1;
bool connected = false;
BLEScan *pBLEScanner;
BLEAdvertising *pBLEAdvertiser;
bool foundESP = false;

// RSSI Data
#define MAXLOG 3000 // 10 minutes worth at 1 interaction per second.
uint8_t rssiLog[MAXLOG][17] = {};
int frontLogIndex = -1; // Removes from here
int rearLogIndex = -1; // Adds to here
bool logWasEmpty;
int rssi = 0;
String mac;
AverageRBTree *tree;

BLECharacteristic *rssiCharacteristic;
BLECharacteristic *bulkCharacteristic;
BLECharacteristic *configACKCharacteristic;

// Path loss model of free space propogation.
float calculateDistance(int rssi, float measuredPower, float environment) {
    return pow(10, (measuredPower - rssi) / (10 * environment));
}

int calculateTargetRSSI(float distance, float measuredPower, int environment) {
    return measuredPower - 10 * environment * log10f(distance);
}

void notify(bool value) {
    notification(&screen, value);
}

int logLength() {
    return abs(frontLogIndex - rearLogIndex);
}

void addToLog(uint8_t* packet) {
    if ((frontLogIndex == 0 && rearLogIndex == MAXLOG - 1) ||
        (rearLogIndex == (frontLogIndex - 1) % MAXLOG - 1)) {
        Serial.println("Log Full");
        return;
    } else if (frontLogIndex == -1) {
        frontLogIndex = 0;
        rearLogIndex = 0;
    } else if (rearLogIndex == MAXLOG - 1 && frontLogIndex != 0) {
        rearLogIndex = 0;
    } else {
        rearLogIndex++;
    }
    memcpy(rssiLog[rearLogIndex], packet, 17);
    Serial.println("Rear Log Index is at " + String(rearLogIndex));
    screen.draw2x2String(0, 0, String(logLength()).c_str());
}

void popFromLog(uint8_t* destination) {
    if (frontLogIndex == -1) {
        Serial.println("Log Empty");
        logWasEmpty = true;
        return;
    }
    logWasEmpty = false;

    memcpy(destination, rssiLog[frontLogIndex], 17);
    memset(rssiLog[frontLogIndex], 0, 17);

    if (frontLogIndex == rearLogIndex) {
        frontLogIndex = -1;
        rearLogIndex = -1;
    } else if (frontLogIndex == MAXLOG - 1) {
        frontLogIndex = 0;
    } else {
        frontLogIndex++;
    }
    Serial.println("Front Log Index is at " + String(frontLogIndex));
    screen.draw2x2String(0, 0, String(logLength()).c_str());
}

void setupPacket(uint8_t* packet, int rssi, String mac) {
    // Address (12 bytes), rssi (1 byte) - 13 bytes total
    uint8_t data_arr[13] = {};
    mac.replace(":", "");
    uint8_t rssi_byte = (uint8_t) rssi;
    memcpy(&data_arr, mac.c_str(), 12);
    data_arr[12] = rssi_byte;
    memcpy(packet, &data_arr, 13);
}

void setupBulkPacket(int rssi, String mac, unsigned long timestamp) {
    // Address (12 bytes), rssi (1 byte), timestamp (4 bytes) - 17 bytes total
    uint8_t data_arr[17] = {};
    mac.replace(":", "");
    uint8_t rssi_byte = (uint8_t) rssi;
    memcpy(&data_arr, mac.c_str(), 12);
    data_arr[12] = rssi_byte;
    memcpy(&data_arr[13], &timestamp, 4);
    addToLog(data_arr);
}

void sendBulkPacket() {
    // Step 1 - if log was not empty grab the value from circular queue.
    // Step 2 - Update time offset so the app knows when the interaction occurred.
    // Step 3 - send packet over bulkRSSICharacteristic from rssiLog at position logIndex
    // Step 4 - clear memory at that position in the rssiLog
    // Step 5 - decrement logIndex
    uint8_t packet[17] = {};
    popFromLog(packet);
    if (!logWasEmpty) {
        unsigned long newTime;
        memcpy(&newTime, &packet[13], 4);
        newTime = millis() - newTime;
        memcpy(&packet[13], &newTime, 4);
        bulkCharacteristic->setValue(packet, 17);
        bulkCharacteristic->notify();
    }
}

class AdvertisedDeviceCallbacks: public BLEAdvertisedDeviceCallbacks {
    void onResult(BLEAdvertisedDevice advertisedDevice) {
        // Only an inital check - bad for security long-term
        if (advertisedDevice.getName() == "ESP32") {
            rssi = advertisedDevice.getRSSI();
            mac = advertisedDevice.getAddress().toString().c_str();
            
            if (rssi >= target_rssi) {
                notify(true);
                if (connected) {
                    uint8_t packet[13] = {};
                    setupPacket(packet, rssi, mac);
                    rssiCharacteristic->setValue(packet, 13);
                    rssiCharacteristic->notify();
                } else {
                    setupBulkPacket(rssi, mac, millis());
                }
            } else {
                notify(false);
            }

            foundESP = true;
        }
    }
};

class ServerCallbacks: public BLEServerCallbacks {
    void onConnect(BLEServer* pServer) {
        connected = true;
        pServer->getAdvertising()->stop();
        pBLEAdvertiser = startJustESPAdvertising();
    }

    void onDisconnect(BLEServer* pServer) {
        connected = false;
        pServer->getAdvertising()->stop();
        pBLEAdvertiser = startBLEAdvertising();
    }
};

class RSSICallbacks: public BLECharacteristicCallbacks {
    void onRead(BLECharacteristic* characteristic) {
        Serial.println("Has been read from " + String(*characteristic->getData()));
    }
};

class ConfigCallbacks: public BLECharacteristicCallbacks {
    void onWrite(BLECharacteristic* characteristic) {
        // Should be a 12 byte array, first 4 bytes are distance float, next 4 is measured power, final 4 is environment variable
        uint8_t* vals = characteristic->getData();
        printByteArrayAsHex(vals, 12);
        int distance_bytes = (*(vals + 0) << 24) | (*(vals+1) << 16) | (*(vals+2) << 8) | *(vals + 3);
        memcpy(&distance, &distance_bytes, 4);
        measured_power = (*(vals+4) << 24) | (*(vals+5) << 16) | (*(vals+6) << 8) | *(vals+7);
        environment = (*(vals+8) << 24) | (*(vals+9) << 16) | (*(vals+10) << 8) | *(vals+11);
        target_rssi = calculateTargetRSSI(distance, measured_power, environment);
        Serial.println("Config Data is: " + String(distance) + " " + String(measured_power) + " " + String(environment) + " " + String(target_rssi));

        configACKCharacteristic->setValue("ACK");
        configACKCharacteristic->notify();
    }
};

class BulkACKCallbacks: public BLECharacteristicCallbacks {
    void onWrite(BLECharacteristic* characteristic) {
        Serial.println("Bulk ACK");
        sendBulkPacket();
    }
};

BLEServerCallbacks* servercb = new ServerCallbacks;
BLECharacteristicCallbacks* rssicb = new RSSICallbacks;
BLECharacteristicCallbacks* bulkackcb = new BulkACKCallbacks;
BLECharacteristicCallbacks* configcb = new ConfigCallbacks;

void setup() {
    Serial.begin(115200);
    screen.begin();
    screen.setFont(u8x8_font_chroma48medium8_r);
    screen.draw2x2String(0, 0, "0");
    setupTile();

    tree = new AverageRBTree();

    constructBLEServer("ESP32");
    rssiCharacteristic = getRSSICharacteristic();
    configACKCharacteristic = getConfigACKCharacteristic();
    bulkCharacteristic = getBulkCharacteristic();
    pBLEAdvertiser = startBLEAdvertising();
    pBLEScanner = startBLEScanning(new AdvertisedDeviceCallbacks);
    Serial.println(ESP.getHeapSize());
    Serial.println(ESP.getFreeHeap());
}

void loop() {
    //screen.drawString(0, 0, "Advertising...");
    //screen.drawString(0, 1, "Scanning...");
    // screen.draw2x2String(0, 0, "Advert..");
    // screen.draw2x2String(0, 2, "Scan..");

    BLEScanResults foundDevices = pBLEScanner->start(scanTime, false); // Blocks until done
    //Serial.print("Devices found: ");
    //Serial.println(foundDevices.getCount());
    //Serial.println("Scan done!");
    pBLEScanner->clearResults();   // delete results fromBLEScan buffer to release memory

    if (!foundESP) {
        // Serial.println(ESP.getFreeHeap());
        clear2x2Line(&screen, 4);
        clear2x2Line(&screen, 6);
    }
    foundESP = false;
    //delay(200);
}