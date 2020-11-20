#include <Arduino.h>
#include <BLEUtils.h>

BLECharacteristic * constructBLEServer(String name, BLEDescriptor* descriptor, 
    BLECharacteristicCallbacks* callbacks, BLECharacteristicCallbacks* rssicb);
BLEAdvertising * startBLEAdvertising();
BLEScan * startBLEScanning(BLEAdvertisedDeviceCallbacks* callbacks);