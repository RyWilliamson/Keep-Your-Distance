import ssd1306
import ubluetooth
from time import sleep
from machine import Pin, I2C, TouchPad
from micropython import const

_IRQ_SCAN_RESULT = const(5)


def initialise_OLED(reset_pin=16):
    ic2_reset = Pin(reset_pin, Pin.OUT)
    ic2_reset.value(0)
    sleep(0.010)
    ic2_reset.value(1)


def create_oled(width=128, height=64, scl=15, sda=4):
    i2c_scl = Pin(scl, Pin.OUT, Pin.PULL_UP)
    i2c_sda = Pin(sda, Pin.OUT, Pin.PULL_UP)
    i2c = I2C(scl=i2c_scl, sda=i2c_sda)
    return ssd1306.SSD1306_I2C(width, height, i2c)


def output_touch_values(oled, touch_pin):
    touchpad = TouchPad(Pin(touch_pin))
    oled.text(str(touchpad.read()), 1, 0)


def init_bluetooth():
    bluetooth = ubluetooth.BLE
    bluetooth.active()
