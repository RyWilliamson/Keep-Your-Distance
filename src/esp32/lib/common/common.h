#include <U8x8lib.h>
#include <BLEUtils.h>

#ifndef MYCOMMONLIB
#define MYCOMMONLIB

void clearLine(U8X8_SSD1306_128X64_NONAME_SW_I2C *screen, int line);
void clear2x2Line(U8X8_SSD1306_128X64_NONAME_SW_I2C *screen, int line);
void notification(U8X8_SSD1306_128X64_NONAME_SW_I2C *screen, bool state);
void notification(int pinNo, bool state);
void setupTile();

// Callbacks - defined in main
extern BLEServerCallbacks* servercb;
extern BLECharacteristicCallbacks* bulkackcb;
extern BLECharacteristicCallbacks* configcb;

#endif