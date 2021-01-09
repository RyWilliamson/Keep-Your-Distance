#include <U8x8lib.h>

void clearLine(U8X8_SSD1306_128X64_NONAME_SW_I2C *screen, int line) {
    screen->drawString(0, line, "                ");
}

void clear2x2Line(U8X8_SSD1306_128X64_NONAME_SW_I2C *screen, int line) {
    screen->draw2x2String(0, line, "                ");
}

void printByteArrayAsHex(uint8_t* arr, int length) {
    for (int i = 0; i < length; i++) Serial.print(arr[i], HEX);
    Serial.println();
}