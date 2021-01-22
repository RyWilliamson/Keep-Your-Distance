#ifndef RSSILOG
#define RSSILOG

#include <Arduino.h>
#include <U8x8lib.h>

#define MAXLOG 3000 // 10 minutes worth at 1 interaction per second.

class CircularQueueLog {
private:
    uint8_t rssiLog[MAXLOG][17] = {};
    int frontLogIndex = -1; // Removes from here
    int rearLogIndex = -1; // Adds to here
    U8X8_SSD1306_128X64_NONAME_SW_I2C *screen;
    bool logWasEmpty;

public:
    CircularQueueLog(U8X8_SSD1306_128X64_NONAME_SW_I2C *screen) {
        this->screen = screen;
    }

    int logLength() {
        return abs(frontLogIndex - rearLogIndex);
    }

    bool wasLogEmpty() {
        return logWasEmpty;
    }

    void addToLog(uint8_t* packet) {
        if ((frontLogIndex == 0 && rearLogIndex == MAXLOG - 1) ||
            (rearLogIndex == (frontLogIndex - 1) % MAXLOG - 1)) {
            Serial.println("Log Full");
            return;
        } else if (frontLogIndex == -1) {
            frontLogIndex = 0;
            rearLogIndex = 0;
        } else if (rearLogIndex == MAXLOG - 1 && frontLogIndex != 0) {
            rearLogIndex = 0;
        } else {
            rearLogIndex++;
        }
        memcpy(rssiLog[rearLogIndex], packet, 17);
        Serial.println("Rear Log Index is at " + String(rearLogIndex));
        screen->draw2x2String(0, 0, String(logLength()).c_str());
    }

    void popFromLog(uint8_t* destination) {
        if (frontLogIndex == -1) {
            Serial.println("Log Empty");
            logWasEmpty = true;
            return;
        }
        logWasEmpty = false;

        memcpy(destination, rssiLog[frontLogIndex], 17);
        memset(rssiLog[frontLogIndex], 0, 17);

        if (frontLogIndex == rearLogIndex) {
            frontLogIndex = -1;
            rearLogIndex = -1;
        } else if (frontLogIndex == MAXLOG - 1) {
            frontLogIndex = 0;
        } else {
            frontLogIndex++;
        }
        Serial.println("Front Log Index is at " + String(frontLogIndex));
        String logLenStr = String(logLength());
        char buf[9] = {};
        sprintf(buf, "%s%.*s", logLenStr.c_str(), 8 - logLenStr.length(), "        ");
        screen->draw2x2String(0, 0, buf);
    }
};

#endif