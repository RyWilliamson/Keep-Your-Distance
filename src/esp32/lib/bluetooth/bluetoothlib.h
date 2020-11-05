#include <Arduino.h>
#include <BLEUtils.h>

BLEServer * constructBLEServer(String name, BLEDescriptor* descriptor);
BLEAdvertising * startBLEAdvertising();
BLEScan * startBLEScanning(BLEAdvertisedDeviceCallbacks* callbacks);