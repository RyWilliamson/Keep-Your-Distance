#include <U8x8lib.h>
#include <BLEUtils.h>

void clearLine(U8X8_SSD1306_128X64_NONAME_SW_I2C *screen, int line);
void clear2x2Line(U8X8_SSD1306_128X64_NONAME_SW_I2C *screen, int line);
void notification(U8X8_SSD1306_128X64_NONAME_SW_I2C *screen, bool state);
void setupTile();

void printByteArrayAsHex(uint8_t* arr, int length);

// Callbacks - defined in main
extern BLEServerCallbacks* servercb;
extern BLECharacteristicCallbacks* rssicb;
extern BLECharacteristicCallbacks* bulkackcb;
extern BLECharacteristicCallbacks* configcb;