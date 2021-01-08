#include <Arduino.h>
#include <BLEUtils.h>

void constructBLEServer(String name);

BLECharacteristic * getRSSICharacteristic();
BLECharacteristic * getBulkCharacteristic();
BLECharacteristic * getConfigACKCharacteristic();

BLEAdvertising * startBLEAdvertising();
BLEAdvertising * startJustESPAdvertising();
BLEScan * startBLEScanning(BLEAdvertisedDeviceCallbacks* callbacks);