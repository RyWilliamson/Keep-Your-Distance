import ssd1306
from time import sleep
from machine import Pin, I2C


def initialise_OLED(pin_no=16):
    ic2_reset = Pin(16, Pin.OUT)
    ic2_reset.value(0)
    sleep(0.010)
    ic2_reset.value(1)


def create_oled(width=128, height=64, scl=15, sda=4):
    i2c_scl = Pin(15, Pin.OUT, Pin.PULL_UP)
    i2c_sda = Pin(4, Pin.OUT, Pin.PULL_UP)
    i2c = I2C(scl=i2c_scl, sda=i2c_sda)
    return ssd1306.SSD1306_I2C(width, height, i2c)


oled = create_oled()
oled.fill(0)
oled.text('Hello, World 1!', 1, 0)
oled.text('Hello, World 2!', 1, 10)
oled.text('Hello, World 3!', 1, 20)
oled.text('Hello, World 4!', 1, 30)
oled.show()
sleep(10)
print("test")
