#include <Arduino.h>
#include <U8x8lib.h>
#include <BLEAdvertisedDevice.h>
#include <BLE2902.h>

#include "bluetoothlib.h"
#include "common.h"

U8X8_SSD1306_128X64_NONAME_SW_I2C screen(/* clock=*/ 15, /* data=*/ 4, /* reset=*/ 16);

int scanTime = 1;
float measuredPower = -80;
BLEScan *pBLEScanner;
BLEAdvertising *pBLEAdvertiser;
bool foundESP = false;

class AdvertisedDeviceCallbacks: public BLEAdvertisedDeviceCallbacks {
    void onResult(BLEAdvertisedDevice advertisedDevice) {
        // Only an inital check - bad for security long-term
        if (advertisedDevice.getName() == "ESP32") {
            int rssi = advertisedDevice.getRSSI();
            String lineData = "ESP: " + String(rssi);
            //screen.drawString(0, 2, lineData.c_str());
            screen.draw2x2String(0, 4, lineData.c_str());

            lineData = "D: " + String(rssi / measuredPower);
            screen.draw2x2String(0, 6, lineData.c_str());
            foundESP = true;
        }
        Serial.printf("Advertised Device: %s RSSI: %d \n", advertisedDevice.toString().c_str(), advertisedDevice.getRSSI());
    }
};

void setup() {
    Serial.begin(115200);
    screen.begin();
    screen.setFont(u8x8_font_chroma48medium8_r);

    constructBLEServer("ESP32", new BLE2902());
    pBLEAdvertiser = startBLEAdvertising();
    pBLEScanner = startBLEScanning(new AdvertisedDeviceCallbacks);
}

void loop() {
    //screen.drawString(0, 0, "Advertising...");
    //screen.drawString(0, 1, "Scanning...");
    screen.draw2x2String(0, 0, "Advert..");
    screen.draw2x2String(0, 2, "Scan..");

    BLEScanResults foundDevices = pBLEScanner->start(scanTime, false); // Blocks until done
    Serial.print("Devices found: ");
    Serial.println(foundDevices.getCount());
    Serial.println("Scan done!");
    pBLEScanner->clearResults();   // delete results fromBLEScan buffer to release memory

    clear2x2Line(&screen, 2);

    if (!foundESP) {
        clear2x2Line(&screen, 4);
        clear2x2Line(&screen, 6);
    }
    foundESP = false;
    delay(200);
}