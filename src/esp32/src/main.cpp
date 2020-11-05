#include <Arduino.h>
#include <U8x8lib.h>
#include <BLEAdvertisedDevice.h>
#include <BLE2902.h>
#include "bluetoothlib.h"

U8X8_SSD1306_128X64_NONAME_SW_I2C screen(/* clock=*/ 15, /* data=*/ 4, /* reset=*/ 16);

int scanTime = 5;
BLEScan *pBLEScanner;

class AdvertisedDeviceCallbacks: public BLEAdvertisedDeviceCallbacks {
    void onResult(BLEAdvertisedDevice advertisedDevice) {
        // Only an inital check - bad for security long-term
        if (advertisedDevice.getName() == "ESP32") {
            String lineData = "ESP32: " + String(advertisedDevice.getRSSI());
            screen.drawString(0, 2, lineData.c_str());
        }
        Serial.printf("Advertised Device: %s RSSI: %d \n", advertisedDevice.toString().c_str(), advertisedDevice.getRSSI());
    }
};

void setup() {
    Serial.begin(115200);
    screen.begin();
    screen.setFont(u8x8_font_chroma48medium8_r);

    constructBLEServer("ESP32", new BLE2902());
    startBLEAdvertising();
    pBLEScanner = startBLEScanning(new AdvertisedDeviceCallbacks);
}

void loop() {
    BLEScanResults foundDevices = pBLEScanner->start(scanTime, false);
    Serial.print("Devices found: ");
    Serial.println(foundDevices.getCount());
    Serial.println("Scan done!");
    pBLEScanner->clearResults();   // delete results fromBLEScan buffer to release memory
    delay(200);
}