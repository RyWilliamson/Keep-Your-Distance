#include <Arduino.h>
#include <BLEUtils.h>

void constructBLEServer(String name, BLEServerCallbacks* servercb, BLECharacteristicCallbacks* callbacks, 
    BLECharacteristicCallbacks* rssicb, BLECharacteristicCallbacks* configcb);
BLECharacteristic * getRSSICharacteristic();
BLECharacteristic * getConfigACKCharacteristic();
BLEAdvertising * startBLEAdvertising();
BLEAdvertising * startJustESPAdvertising();
BLEScan * startBLEScanning(BLEAdvertisedDeviceCallbacks* callbacks);