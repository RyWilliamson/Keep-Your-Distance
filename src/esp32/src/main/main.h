#ifndef CUSTOM_MAIN
#define CUSTOM_MAIN

#include <Arduino.h>
#include <BLEAdvertisedDevice.h>

float calculateDistance(int16_t rssi, float measuredPower, float environment);

int16_t calculateTargetRSSI(float distance, float measuredPower, int environment);

void notify(bool value);

uint64_t macToInt64(BLEAddress mac);

float exponentialWeightedAverage(float oldRSSI, float newRSSI);

int logLength();

void addToLog(uint8_t* packet);

void popFromLog(uint8_t* destination);

void setupPacket(uint8_t* packet, int16_t rssi, String mac);

void setupBulkPacket(int16_t rssi, String mac, unsigned long timestamp);

void sendBulkPacket();

#endif