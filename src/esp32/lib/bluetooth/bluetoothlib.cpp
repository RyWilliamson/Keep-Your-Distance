#include <Arduino.h>
#include <BLEDevice.h>
#include <BLEServer.h>
#include <BLEUtils.h>
#include <BLEScan.h>

#define SERVICE_RSSI_UUID       "f23ab3c8-506a-41ec-90e0-e6cbb6304e03"
#define SERVICE_HEARTBEAT_UUID  "4fafc201-1fb5-459e-8fcc-c5c9c331914b"
#define SERVICE_ESP_UUID        "c84ee767-a061-46d5-9072-2109eaaa2673"

#define CHARA_CONNECT_UUID      "beb5483e-36e1-4688-b7f5-ea07361b26a8"
#define CHARA_RSSI_UUID         "3f237eb3-99b4-4bbd-9475-f2e7b39ac899"
#define CHARA_ADV_UUID          "c74f40df-d464-4dea-818c-13e7914f332a"

BLECharacteristic * constructBLEServer(String name, BLEServerCallbacks* servercb,BLEDescriptor* descriptor, 
    BLECharacteristicCallbacks* normalcb, BLECharacteristicCallbacks* rssicb) {
    BLEDevice::init(name.c_str());

    BLEServer *pServer = BLEDevice::createServer();
    pServer->setCallbacks(servercb);

    BLEService *pRSSIService = pServer->createService(SERVICE_RSSI_UUID);
    BLEService *pHeartbeatService = pServer->createService(SERVICE_HEARTBEAT_UUID);
    BLEService *pESPService = pServer->createService(SERVICE_ESP_UUID);

    BLECharacteristic *pConnectCharacteristic = new BLECharacteristic(
        CHARA_CONNECT_UUID,
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

    BLECharacteristic *pAdvCharacteristic = new BLECharacteristic(
        CHARA_ADV_UUID,
        BLECharacteristic::PROPERTY_READ |
        BLECharacteristic::PROPERTY_WRITE |
        BLECharacteristic::PROPERTY_NOTIFY |
        BLECharacteristic::PROPERTY_INDICATE
    );
    pConnectCharacteristic->setCallbacks(normalcb);

    pRSSICharacteristic->addDescriptor(descriptor);
    pRSSICharacteristic->setCallbacks(rssicb);

    pESPService->addCharacteristic(pAdvCharacteristic);
    pRSSIService->addCharacteristic(pRSSICharacteristic);

    pESPService->start();
    pRSSIService->start();
    pHeartbeatService->start();
    return pRSSICharacteristic;
}

BLEAdvertising * startBLEAdvertising() {
    BLEAdvertising *pAdvertising = BLEDevice::getAdvertising();
    pAdvertising->addServiceUUID(SERVICE_HEARTBEAT_UUID);
    pAdvertising->addServiceUUID(SERVICE_RSSI_UUID);
    pAdvertising->addServiceUUID(SERVICE_ESP_UUID);
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