#include <Arduino.h>
#include <BLEUtils.h>

BLEServer * constructBLEServer(String name, BLEDescriptor* descriptor, BLECharacteristicCallbacks* callbacks);
BLEAdvertising * startBLEAdvertising();
BLEScan * startBLEScanning(BLEAdvertisedDeviceCallbacks* callbacks);