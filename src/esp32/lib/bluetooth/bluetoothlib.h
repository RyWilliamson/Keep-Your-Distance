#include <Arduino.h>
#include <BLEUtils.h>

BLECharacteristic * constructBLEServer(String name, BLEServerCallbacks* servercb, BLEDescriptor* descriptor, 
    BLECharacteristicCallbacks* callbacks, BLECharacteristicCallbacks* rssicb);
BLEAdvertising * startBLEAdvertising();
BLEAdvertising * startJustESPAdvertising();
BLEScan * startBLEScanning(BLEAdvertisedDeviceCallbacks* callbacks);