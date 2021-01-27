#ifndef RSSIHANDLER
#define RSSIHANDLER

#include <Arduino.h>
#include <BLEAdvertisedDevice.h>

#include "circularqueue.h"
#include "rbtree.h"

class RSSIHandler {
private:
    bool testing;
    float weight;

    CircularQueueLog *RSSIlog;
public:
    RSSIHandler(CircularQueueLog *log, bool testing) {
        this->RSSIlog = log;
        this->testing = testing;
        weight = 2.0 / (5 + 1.0); // Decaying average over last 5 elements
    }

    bool checkInteraction(float rssi, float target, String mac, unsigned long time, bool connected, BLECharacteristic *rssiCharacteristic) {
        bool interaction = false;
        if (rssi >= target) {
            interaction = true;
            if (connected) {
                uint8_t packet[13] = {};
                setupPacket(packet, (int16_t) rssi, mac);
                rssiCharacteristic->setValue(packet, 13);
                if (!testing) { rssiCharacteristic->notify(); }
            } else {
                setupBulkPacket((int16_t) rssi, mac, time);
            }
        }
        return interaction;
    }

    pNode handleAverage(AverageRBTree *tree, float rssi, uint8_t *address, unsigned long time) {
        uint64_t mac64 = macToInt64(address);

        pNode node = tree->search(mac64);
        if (node == tree->getLeaf()) {
            node = tree->insertNode(mac64, rssi, time);
            if (!testing) { Serial.println("insert"); }
        } else {
            if (time - node->timestamp > 5000) {
                node->rssi = rssi;
                if (!testing) { Serial.printf("update new: %f - %f\n", rssi, node->rssi); }
            } else {
                node->rssi = exponentialWeightedAverage(node->rssi, rssi);
                if (!testing) { Serial.printf("update average: %f - %f\n", rssi, node->rssi); }
            }

            node->timestamp = time;
        }
        
        return node;
    }

    uint64_t macToInt64(uint8_t *mac) {
        uint64_t output = 0;
        // memcpy(&output, mac.getNative(), 6);
        memcpy(&output, mac, 6);
        return output;
    }

    float exponentialWeightedAverage(float oldRSSI, float newRSSI) {
        return (newRSSI * weight) + (oldRSSI * (1 - weight));
    }

    void setupPacket(uint8_t* packet, int16_t rssi, String mac) {
        // Address (12 bytes), rssi (1 byte) - 13 bytes total
        uint8_t data_arr[13] = {};
        mac.replace(":", "");
        uint8_t rssi_byte = (uint8_t) rssi;
        memcpy(&data_arr, mac.c_str(), 12);
        data_arr[12] = rssi_byte;
        memcpy(packet, &data_arr, 13);
    }

    void setupBulkPacket(int16_t rssi, String mac, unsigned long timestamp) {
        // Address (12 bytes), rssi (1 byte), timestamp (4 bytes) - 17 bytes total
        uint8_t data_arr[17] = {};
        mac.replace(":", "");
        uint8_t rssi_byte = (uint8_t) rssi;
        memcpy(&data_arr, mac.c_str(), 12);
        data_arr[12] = rssi_byte;
        memcpy(&data_arr[13], &timestamp, 4);
        RSSIlog->addToLog(data_arr);
    }

    void sendBulkPacket(BLECharacteristic *bulkCharacteristic) {
        // Step 1 - if log was not empty grab the value from circular queue.
        // Step 2 - Update time offset so the app knows when the interaction occurred.
        // Step 3 - send packet over bulkRSSICharacteristic from rssiLog at position logIndex
        // Step 4 - clear memory at that position in the rssiLog
        // Step 5 - decrement logIndex
        uint8_t packet[17] = {};
        RSSIlog->popFromLog(packet);
        if (!RSSIlog->wasLogEmpty()) {
            unsigned long newTime;
            memcpy(&newTime, &packet[13], 4);
            newTime = millis() - newTime;
            memcpy(&packet[13], &newTime, 4);
            bulkCharacteristic->setValue(packet, 17);
            if (!testing) { bulkCharacteristic->notify(); }
        }
    }

    void setWeight(float weight) {
        this->weight = weight;
    }

    /* For testing only */
    CircularQueueLog * getLog() {
        return this->RSSIlog;
    }
};

#endif