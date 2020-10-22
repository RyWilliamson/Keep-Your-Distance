import ssd1306
from time import sleep
from machine import Pin, I2C, TouchPad


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


def clear_screen(oled, colour):
    oled.fill(colour)
    oled.show()


initialise_OLED()
oled = create_oled()

clear_screen(oled, 0)
oled.text("This is a test!", 1, 20)
oled.show()
