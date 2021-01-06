#include <Arduino.h>
#include <U8x8lib.h>
#include <BLEAdvertisedDevice.h>
#include <BLE2902.h>

#include "bluetoothlib.h"
#include "common.h"

U8X8_SSD1306_128X64_NONAME_SW_I2C screen(/* clock=*/ 15, /* data=*/ 4, /* reset=*/ 16);

int scanTime = 1;
int measured_power = -81;
int environment = 3;
float distance = 1.5;
BLEScan *pBLEScanner;
BLEAdvertising *pBLEAdvertiser;
bool foundESP = false;
uint8_t data = 5;
int rssi = 0;
String mac;
int nullValue = 99;

BLECharacteristic *rssiCharacteristic;

float calculateDistance(int rssi, float measuredPower, float environment) {
    return pow(10, (measuredPower - rssi) / (10 * environment));
}

String combineMacRSSI(int rssi, String mac) {
    mac.replace(":", "");
    return mac + String(",") + String(rssi);
}

class AdvertisedDeviceCallbacks: public BLEAdvertisedDeviceCallbacks {
    void onResult(BLEAdvertisedDevice advertisedDevice) {
        // Only an inital check - bad for security long-term
        if (advertisedDevice.getName() == "ESP32") {
            rssi = advertisedDevice.getRSSI();
            mac = advertisedDevice.getAddress().toString().c_str();
            rssiCharacteristic->setValue(combineMacRSSI(rssi, mac).c_str());
            rssiCharacteristic->notify();
            String lineData = "ESP: " + String(rssi);
            //screen.drawString(0, 2, lineData.c_str());
            screen.draw2x2String(0, 4, lineData.c_str());

            lineData = "D: " + String(calculateDistance(rssi, measured_power, environment), 3);
            screen.draw2x2String(0, 6, lineData.c_str());
            foundESP = true;
        }
        //Serial.printf("Advertised Device: %s RSSI: %d \n", advertisedDevice.toString().c_str(), advertisedDevice.getRSSI());
    }
};

class ServerCallbacks: public BLEServerCallbacks {
    void onConnect(BLEServer* pServer) {
        pServer->getAdvertising()->stop();
        pBLEAdvertiser = startJustESPAdvertising();
    }

    void onDisconnect(BLEServer* pServer) {
        pServer->getAdvertising()->stop();
        pBLEAdvertiser = startBLEAdvertising();
    }
};

class RSSICallbacks: public BLECharacteristicCallbacks {
    void onRead(BLECharacteristic* characteristic) {
        Serial.println("Has been read from " + String(*characteristic->getData()));
    }
};

class CharacteristicCallbacks: public BLECharacteristicCallbacks {
    void onWrite(BLECharacteristic* characteristic) {
        uint8_t* val = characteristic->getData();
        Serial.println("2From Device value is: " + String(*val));
        //characteristic->setValue(&data, 1);
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
    }
};

void setup() {
    Serial.begin(115200);
    screen.begin();
    screen.setFont(u8x8_font_chroma48medium8_r);

    rssiCharacteristic = constructBLEServer("ESP32", new ServerCallbacks, new BLE2902(), new CharacteristicCallbacks, new RSSICallbacks, new ConfigCallbacks);
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
        // rssiCharacteristic->setValue(nullValue);
        // rssiCharacteristic->notify();
    }
    foundESP = false;
    //delay(200);
}