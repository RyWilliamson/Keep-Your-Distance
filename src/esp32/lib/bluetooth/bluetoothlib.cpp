#include <Arduino.h>
#include <BLEDevice.h>
#include <BLEServer.h>
#include <BLEUtils.h>
#include <BLEScan.h>
#include <BLE2902.h>

#define SERVICE_RSSI_UUID       "f23ab3c8-506a-41ec-90e0-e6cbb6304e03"
#define SERVICE_HEARTBEAT_UUID  "4fafc201-1fb5-459e-8fcc-c5c9c331914b"
#define SERVICE_ESP_UUID        "c84ee767-a061-46d5-9072-2109eaaa2673"
#define SERVICE_CONFIG_UUID     "24c2285b-4f54-4af7-a021-b86ea4374345"

#define CHARA_CONNECT_UUID      "beb5483e-36e1-4688-b7f5-ea07361b26a8"

#define CHARA_RSSI_UUID         "3f237eb3-99b4-4bbd-9475-f2e7b39ac899"

#define CHARA_MAC_UUID          "80ba50ee-e010-4d75-b568-99de8adb10e4"

#define CHARA_ADV_UUID          "c74f40df-d464-4dea-818c-13e7914f332a"

#define CHARA_CONFIG_UUID       "757affde-78ab-49d6-84a5-16193ad80b13"
#define CHARA_CONFIG_ACK_UUID   "41a8c415-7ad6-4efd-8638-9d5d504039ce"

BLECharacteristic * createCharacteristic(const char* id) {
    BLECharacteristic *pCharacteristic = new BLECharacteristic(
        id,
        BLECharacteristic::PROPERTY_READ |
        BLECharacteristic::PROPERTY_WRITE |
        BLECharacteristic::PROPERTY_NOTIFY |
        BLECharacteristic::PROPERTY_INDICATE
    );
    return pCharacteristic;
}

BLECharacteristic *pConnectCharacteristic = createCharacteristic(CHARA_CONNECT_UUID);

BLECharacteristic *pRSSICharacteristic = createCharacteristic(CHARA_RSSI_UUID);

BLECharacteristic *pAdvCharacteristic = createCharacteristic(CHARA_ADV_UUID);

BLECharacteristic *pConfigCharacteristic = createCharacteristic(CHARA_CONFIG_UUID);

BLECharacteristic *pConfigACKCharacteristic = createCharacteristic(CHARA_CONFIG_ACK_UUID);



void constructBLEServer(String name, BLEServerCallbacks* servercb, BLECharacteristicCallbacks* normalcb,
        BLECharacteristicCallbacks* rssicb, BLECharacteristicCallbacks* configcb) {
    BLEDevice::init(name.c_str());

    BLEServer *pServer = BLEDevice::createServer();
    pServer->setCallbacks(servercb);

    BLEService *pRSSIService = pServer->createService(SERVICE_RSSI_UUID);
    BLEService *pHeartbeatService = pServer->createService(SERVICE_HEARTBEAT_UUID);
    BLEService *pESPService = pServer->createService(SERVICE_ESP_UUID);
    BLEService *pConfigService = pServer->createService(SERVICE_CONFIG_UUID);

    pConnectCharacteristic->setCallbacks(normalcb);

    pRSSICharacteristic->addDescriptor(new BLE2902());
    pRSSICharacteristic->setCallbacks(rssicb);

    pConfigCharacteristic->setCallbacks(configcb);

    pConfigACKCharacteristic->addDescriptor(new BLE2902());

    pESPService->addCharacteristic(pAdvCharacteristic);
    pRSSIService->addCharacteristic(pRSSICharacteristic);
    pConfigService->addCharacteristic(pConfigCharacteristic);
    pConfigService->addCharacteristic(pConfigACKCharacteristic);

    pESPService->start();
    pRSSIService->start();
    pHeartbeatService->start();
    pConfigService->start();
}

BLECharacteristic * getRSSICharacteristic() {
    return pRSSICharacteristic;
}

BLECharacteristic * getConfigACKCharacteristic() {
    return pConfigACKCharacteristic;
}

BLEAdvertising * startBLEAdvertising() {
    BLEAdvertising *pAdvertising = BLEDevice::getAdvertising();
    pAdvertising->addServiceUUID(SERVICE_HEARTBEAT_UUID);
    pAdvertising->addServiceUUID(SERVICE_RSSI_UUID);
    pAdvertising->addServiceUUID(SERVICE_ESP_UUID);
    pAdvertising->addServiceUUID(SERVICE_CONFIG_UUID);
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