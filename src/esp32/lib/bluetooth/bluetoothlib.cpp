#include <Arduino.h>
#include <BLEDevice.h>
#include <BLEServer.h>
#include <BLEUtils.h>
#include <BLEScan.h>

#define SERVICE_UUID        "4fafc201-1fb5-459e-8fcc-c5c9c331914b"
#define CHARACTERISTIC_UUID "beb5483e-36e1-4688-b7f5-ea07361b26a8"
#define CHARA_RSSI_UUID     "3f237eb3-99b4-4bbd-9475-f2e7b39ac899"

void constructBLEServer(String name, BLEDescriptor* descriptor, BLECharacteristicCallbacks* callbacks) {
    BLEDevice::init(name.c_str());

    BLEServer *pServer = BLEDevice::createServer();

    BLEService *pService = pServer->createService(SERVICE_UUID);

    BLECharacteristic *pCharacteristic = pService->createCharacteristic(
        CHARACTERISTIC_UUID,
        BLECharacteristic::PROPERTY_READ |
        BLECharacteristic::PROPERTY_WRITE |
        BLECharacteristic::PROPERTY_NOTIFY |
        BLECharacteristic::PROPERTY_INDICATE
    );

    // BLECharacteristic *pRSSICharacteristic = pService->createCharacteristic(
    //     CHARA_RSSI_UUID,
    //     BLECharacteristic::PROPERTY_READ |
    //     BLECharacteristic::PROPERTY_WRITE |
    //     BLECharacteristic::PROPERTY_NOTIFY |
    //     BLECharacteristic::PROPERTY_INDICATE
    // );
    pCharacteristic->addDescriptor(descriptor);
    pCharacteristic->setCallbacks(callbacks);

    // pRSSICharacteristic->addDescriptor(descriptor);
    // pRSSICharacteristic->setCallbacks(callbacks);

    pService->start();
}

BLEAdvertising * startBLEAdvertising() {
    BLEAdvertising *pAdvertising = BLEDevice::getAdvertising();
    pAdvertising->addServiceUUID(SERVICE_UUID);
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