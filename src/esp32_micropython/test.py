import ssd1306
from time import sleep
from machine import Pin, I2C, TouchPad
from common import *

initialise_OLED()
oled = create_oled()
oled.fill(0)
touchpad = TouchPad(Pin(13))

while True:
    #oled.text(str(touchpad.read()), 1, 0)
    if touchpad.read() < 500:
        oled.fill(1)
    else:
        oled.fill(0)
    oled.show()
