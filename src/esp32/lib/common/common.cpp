#include <U8x8lib.h>

uint8_t tiles[128];

void clearLine(U8X8_SSD1306_128X64_NONAME_SW_I2C *screen, int line) {
    screen->drawString(0, line, "                ");
}

void clear2x2Line(U8X8_SSD1306_128X64_NONAME_SW_I2C *screen, int line) {
    screen->draw2x2String(0, line, "                ");
}

void notification(U8X8_SSD1306_128X64_NONAME_SW_I2C *screen, bool state) {
    if (state) {
        screen->drawTile(0, 4, 16, tiles);
        screen->drawTile(0, 5, 16, tiles);
        screen->drawTile(0, 6, 16, tiles);
        screen->drawTile(0, 7, 16, tiles);
    } else {
        screen->draw2x2String(0, 4, "                ");
        screen->draw2x2String(0, 6, "                ");
    }
}

void notification(int pinNo, bool state) {
    if (state) {
        digitalWrite(pinNo, HIGH);
    } else {
        digitalWrite(pinNo, LOW);
    }
}

void setupTile() {
    memset(tiles, 255, 128);
}