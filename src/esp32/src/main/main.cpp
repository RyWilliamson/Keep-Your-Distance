#include <Arduino.h>
#include <U8x8lib.h>
#include <BLEAdvertisedDevice.h>

#include "bluetoothlib.h"
#include "common.h"
#include "rbtree.h"
#include "circularqueue.h"
#include "configdata.h"
#include "rssihandler.h"

// Screen
U8X8_SSD1306_128X64_NONAME_SW_I2C screen(/* clock=*/ 15, /* data=*/ 4, /* reset=*/ 16);

//Config
ConfigData *configData;

// BLE Data
int scanTime = 1;
bool connected = false;
BLEScan *pBLEScanner;
BLEAdvertising *pBLEAdvertiser;

// RSSI Data
CircularQueueLog *RSSIlog;
float rssi = 0;
String mac;

// Averaging
AverageRBTree *tree;
RSSIHandler *rssiHandler;
bool interaction;
#define CLEARTIME 600000
unsigned long clearTrackTimer = 0;

BLECharacteristic *rssiCharacteristic;
BLECharacteristic *bulkCharacteristic;
BLECharacteristic *configACKCharacteristic;

void notify(bool value) {
    notification(&screen, value);
}

class AdvertisedDeviceCallbacks: public BLEAdvertisedDeviceCallbacks {
    void onResult(BLEAdvertisedDevice advertisedDevice) {
        // Only an inital check - bad for security long-term
        if (advertisedDevice.getName() == "ESP32") {
            unsigned long time = millis();
            rssi = (float) advertisedDevice.getRSSI();
            mac = advertisedDevice.getAddress().toString().c_str();

            pNode node = rssiHandler->handleAverage(tree, rssi, (uint8_t *) advertisedDevice.getAddress().getNative(), time);
            
            interaction = rssiHandler->checkInteraction(node->rssi, configData->getTargetRSSI(), mac, time, connected, rssiCharacteristic);
        }
    }
};

class ServerCallbacks: public BLEServerCallbacks {
    void onConnect(BLEServer* pServer) {
        connected = true;
        pServer->getAdvertising()->stop();
        pBLEAdvertiser = startJustESPAdvertising();
        Serial.println("Connected");
    }

    void onDisconnect(BLEServer* pServer) {
        connected = false;
        pServer->getAdvertising()->stop();
        pBLEAdvertiser = startBLEAdvertising();
        Serial.println("Disconnected");
    }
};

class ConfigCallbacks: public BLECharacteristicCallbacks {
    void onWrite(BLECharacteristic* characteristic) {
        // Should be a 12 byte array, first 4 bytes are distance float, next 4 is measured power, final 4 is environment variable
        uint8_t* vals = characteristic->getData();

        configData->updateData(vals);

        configACKCharacteristic->setValue("ACK");
        configACKCharacteristic->notify();
    }
};

class BulkACKCallbacks: public BLECharacteristicCallbacks {
    void onWrite(BLECharacteristic* characteristic) {
        Serial.println("Bulk ACK");
        rssiHandler->sendBulkPacket(bulkCharacteristic);
    }
};

BLEServerCallbacks* servercb = new ServerCallbacks;
BLECharacteristicCallbacks* bulkackcb = new BulkACKCallbacks;
BLECharacteristicCallbacks* configcb = new ConfigCallbacks;

void setup() {
    Serial.begin(115200);
    screen.begin();
    screen.setFont(u8x8_font_chroma48medium8_r);
    screen.draw2x2String(0, 0, "0");
    setupTile();

    RSSIlog = new CircularQueueLog(&screen);
    tree = new AverageRBTree(false);
    configData = new ConfigData(false);
    rssiHandler = new RSSIHandler(RSSIlog, false);

    constructBLEServer("ESP32");
    rssiCharacteristic = getRSSICharacteristic();
    configACKCharacteristic = getConfigACKCharacteristic();
    bulkCharacteristic = getBulkCharacteristic();
    pBLEAdvertiser = startBLEAdvertising();
    pBLEScanner = startBLEScanning(new AdvertisedDeviceCallbacks);
}

void loop() {

    BLEScanResults foundDevices = pBLEScanner->start(scanTime, false); // Blocks until done
    pBLEScanner->clearResults();   // delete results fromBLEScan buffer to release memory

    uint32_t time = millis();
    if (time - clearTrackTimer > CLEARTIME) {
        tree->cleanTree(time);
        clearTrackTimer = millis();
    }
    notify(interaction);
    interaction = false;
    //delay(200);
}