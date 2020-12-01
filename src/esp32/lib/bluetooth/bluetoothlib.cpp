#include <Arduino.h>
#include <BLEDevice.h>
#include <BLEServer.h>
#include <BLEUtils.h>
#include <BLEScan.h>

#define SERVICE_RSSI_UUID   "4fafc201-1fb5-459e-8fcc-c5c9c331914b"
#define SERVICE_ESP_UUID    "681d037e-9b10-43ca-b213-9c71f84ce458"
#define CHARA_ESP_UUID      "beb5483e-36e1-4688-b7f5-ea07361b26a8"
#define CHARA_RSSI_UUID     "3f237eb3-99b4-4bbd-9475-f2e7b39ac899"

BLECharacteristic * constructBLEServer(String name, BLEServerCallbacks* servercb,BLEDescriptor* descriptor, 
    BLECharacteristicCallbacks* normalcb, BLECharacteristicCallbacks* rssicb) {
    BLEDevice::init(name.c_str());

    BLEServer *pServer = BLEDevice::createServer();
    pServer->setCallbacks(servercb);

    BLEService *pRSSIService = pServer->createService(SERVICE_RSSI_UUID);
    BLEService *pESPService = pServer->createService(SERVICE_ESP_UUID);

    BLECharacteristic *pESPCharacteristic = new BLECharacteristic(
        CHARA_ESP_UUID,
        BLECharacteristic::PROPERTY_READ |
        BLECharacteristic::PROPERTY_WRITE |
        BLECharacteristic::PROPERTY_NOTIFY |
        BLECharacteristic::PROPERTY_INDICATE
    );

    BLECharacteristic *pRSSICharacteristic = new BLECharacteristic(
        CHARA_RSSI_UUID,
        BLECharacteristic::PROPERTY_READ |
        BLECharacteristic::PROPERTY_WRITE |
        BLECharacteristic::PROPERTY_NOTIFY |
        BLECharacteristic::PROPERTY_INDICATE
    );
    // pCharacteristic->addDescriptor(descriptor);
    pESPCharacteristic->setCallbacks(normalcb);

    pRSSICharacteristic->addDescriptor(descriptor);
    pRSSICharacteristic->setCallbacks(rssicb);

    pESPService->addCharacteristic(pESPCharacteristic);
    pRSSIService->addCharacteristic(pRSSICharacteristic);

    pESPService->start();
    pRSSIService->start();
    return pRSSICharacteristic;
}

BLEAdvertising * startBLEAdvertising() {
    BLEAdvertising *pAdvertising = BLEDevice::getAdvertising();
    pAdvertising->addServiceUUID(SERVICE_ESP_UUID);
    pAdvertising->addServiceUUID(SERVICE_RSSI_UUID);
    pAdvertising->setScanResponse(false);
    pAdvertising->setMinPreferred(0x0);  // set value to 0x00 to not advertise this parameter
    BLEDevice::startAdvertising();
    Serial.println("Waiting a client connection to notify...");
    return pAdvertising;
}

BLEAdvertising * startJustESPAdvertising() {
    BLEAdvertising *pAdvertising = BLEDevice::getAdvertising();
    pAdvertising->addServiceUUID(SERVICE_ESP_UUID);
    pAdvertising->setScanResponse(false);
    pAdvertising->setMinPreferred(0x0);  // set value to 0x00 to not advertise this parameter
    BLEDevice::startAdvertising();
    Serial.println("Waiting a client connection to notify...");
    return pAdvertising;
}

BLEScan * startBLEScanning(BLEAdvertisedDeviceCallbacks* callbacks) {
    BLEScan *pBLEScan = BLEDevice::getScan();
    pBLEScan->setAdvertisedDeviceCallbacks(callbacks);
    pBLEScan->setActiveScan(true);
    pBLEScan->setInterval(100);
    pBLEScan->setWindow(99);
    return pBLEScan;
}