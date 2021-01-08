#include <Arduino.h>
#include <U8x8lib.h>
#include <BLEAdvertisedDevice.h>

#include "bluetoothlib.h"
#include "common.h"

// Screen
U8X8_SSD1306_128X64_NONAME_SW_I2C screen(/* clock=*/ 15, /* data=*/ 4, /* reset=*/ 16);

// Config Data
int measured_power = -81;
int environment = 3;
float distance = 1.5;

// BLE Data
int scanTime = 1;
bool connected = false;
BLEScan *pBLEScanner;
BLEAdvertising *pBLEAdvertiser;
bool foundESP = false;

// RSSI Data
#define MAXLOG 100
uint8_t rssiLog[MAXLOG][17] = {};
//int logIndex = -1;
int frontLogIndex = -1; // Removes from here
int rearLogIndex = -1; // Adds to here
int rssi = 0;
String mac;
int nullValue = 99; // This was for testing purposes - can probably be removed.



BLECharacteristic *rssiCharacteristic;
BLECharacteristic *bulkCharacteristic;
BLECharacteristic *configACKCharacteristic;

float calculateDistance(int rssi, float measuredPower, float environment) {
    return pow(10, (measuredPower - rssi) / (10 * environment));
}

// String combineMacRSSI(int rssi, String mac) {
//     mac.replace(":", "");
//     return mac + String(",") + String(rssi);
// }

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
}

void popFromLog(uint8_t* destination) {

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
    //memcpy(packet, &data_arr, 17);
    addToLog(data_arr);
}

void sendBulkPacket() {
    // Step 1 - if logIndex is 0 return
    // Step 2 - send packet over bulkRSSICharacteristic from rssiLog at position logIndex
    // Step 3 - clear memory at that position in the rssiLog
    // Step 4 - decrement logIndex
    if (logIndex >= 0) {
        Serial.println("Transfer index " + String(logIndex));
        unsigned long newTime;
        memcpy(&newTime, &rssiLog[logIndex][13], 4);
        newTime = millis() - newTime;
        memcpy(&rssiLog[logIndex][13], &newTime, 4);
        bulkCharacteristic->setValue(rssiLog[logIndex], 17);
        bulkCharacteristic->notify();
        memset(rssiLog[logIndex], 0, 17);
        logIndex--;
    } else {
        Serial.println("Log Empty");
    }
}

class AdvertisedDeviceCallbacks: public BLEAdvertisedDeviceCallbacks {
    void onResult(BLEAdvertisedDevice advertisedDevice) {
        // Only an inital check - bad for security long-term
        if (advertisedDevice.getName() == "ESP32") {
            rssi = advertisedDevice.getRSSI();
            mac = advertisedDevice.getAddress().toString().c_str();
            if (connected) {
                uint8_t packet[13] = {};
                setupPacket(packet, rssi, mac);
                rssiCharacteristic->setValue(packet, 13);
                rssiCharacteristic->notify();
            } else {
                if (logIndex+1 == MAXLOG) {
                    
                } else {
                    logIndex++;
                    setupBulkPacket(rssi, mac, millis());
                    Serial.println("Log Index is at " + String(logIndex));
                }
            }
            String lineData = "ESP: " + String(rssi);
            //screen.drawString(0, 2, lineData.c_str());
            screen.draw2x2String(0, 4, lineData.c_str());

            lineData = "D: " + String(calculateDistance(rssi, measured_power, environment), 3);
            screen.draw2x2String(0, 6, lineData.c_str());
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
        int distance_bytes = (*(vals + 0) << 24) | (*(vals+1) << 16) | (*(vals+2) << 8) | *(vals + 3);
        memcpy(&distance, &distance_bytes, 4);
        measured_power = (*(vals+4) << 24) | (*(vals+5) << 16) | (*(vals+6) << 8) | *(vals+7);
        environment = (*(vals+8) << 24) | (*(vals+9) << 16) | (*(vals+10) << 8) | *(vals+11);
        Serial.println("Config Data is: " + String(distance));
        Serial.println("Config Data is: " + String(measured_power));
        Serial.println("Config Data is: " + String(environment));

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

    constructBLEServer("ESP32");
    rssiCharacteristic = getRSSICharacteristic();
    configACKCharacteristic = getConfigACKCharacteristic();
    bulkCharacteristic = getBulkCharacteristic();
    pBLEAdvertiser = startBLEAdvertising();
    pBLEScanner = startBLEScanning(new AdvertisedDeviceCallbacks);
}

void loop() {
    //screen.drawString(0, 0, "Advertising...");
    //screen.drawString(0, 1, "Scanning...");
    screen.draw2x2String(0, 0, "Advert..");
    screen.draw2x2String(0, 2, "Scan..");

    BLEScanResults foundDevices = pBLEScanner->start(scanTime, false); // Blocks until done
    //Serial.print("Devices found: ");
    //Serial.println(foundDevices.getCount());
    //Serial.println("Scan done!");
    pBLEScanner->clearResults();   // delete results fromBLEScan buffer to release memory

    clear2x2Line(&screen, 2);

    if (!foundESP) {
        clear2x2Line(&screen, 4);
        clear2x2Line(&screen, 6);
    }
    foundESP = false;
    //delay(200);
}