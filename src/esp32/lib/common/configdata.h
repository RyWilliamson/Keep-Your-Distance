#ifndef CONFIGDATA
#define CONFIGDATA

#include <Arduino.h>

class ConfigData {
private:
    int measured_power;
    int environment;
    float distance;
    int16_t target_rssi;
    bool testing;

    void printByteArrayAsHex(uint8_t* arr, int length) {
        for (int i = 0; i < length; i++) Serial.print(arr[i], HEX);
        Serial.println();
    }

    // Formula in survey of COVID-19 contact tracing apps - d0 is 1m so just 1
    int16_t calculateTargetRSSI(float distance, float measuredPower, int environment) {
        return measuredPower - 10 * environment * log10f(distance);
    }

    // Path loss model of free space propogation.
    /* Deprecated - kept for potential future use */
    float calculateDistance(int16_t rssi, float measuredPower, float environment) {
        return pow(10, (measuredPower - rssi) / (10 * environment));
    }

public:
    ConfigData(bool testing) {
        this->measured_power = -78;
        this->environment = 2;
        this->distance = 1.5;
        this->target_rssi = -81;
        // this->target_rssi = -115;
        this->testing = testing;
    }

    void updateData(uint8_t *bytes) {
        if (!testing) { printByteArrayAsHex(bytes, 12); }

        int distance_bytes = (*(bytes + 0) << 24) | (*(bytes+1) << 16) | (*(bytes+2) << 8) | *(bytes + 3);
        memcpy(&distance, &distance_bytes, 4);
        measured_power = (*(bytes+4) << 24) | (*(bytes+5) << 16) | (*(bytes+6) << 8) | *(bytes+7);
        environment = (*(bytes+8) << 24) | (*(bytes+9) << 16) | (*(bytes+10) << 8) | *(bytes+11);
        target_rssi = calculateTargetRSSI(distance, measured_power, environment);

        if (!testing) { Serial.println("Config Data is: " + String(distance) + " " + String(measured_power) + " " + String(environment) + " " + String(target_rssi)); }
    }

    int16_t getTargetRSSI() {
        return this->target_rssi;
    }

    /* For Testing Only */
    float getDistance() {
        return this->distance;
    }

    /* For Testing Only */
    int getEnvironment() {
        return this->environment;
    }

    /* For Testing Only */
    int getMeasuredPower() {
        return this->measured_power;
    }
};

#endif