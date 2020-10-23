import ssd1306
import ubluetooth
from time import sleep
from machine import Pin, I2C, TouchPad
from common import *
from micropython import const

_IRQ_SCAN_RESULT = const(5)

initialise_OLED()
oled = create_oled()
oled.fill(0)
touchpad = TouchPad(Pin(13))
i = 0

while i < 100:
    oled.fill(0)
    oled.text(str(touchpad.read()), 1, 0)
    oled.show()
    sleep(1)
    i += 1
