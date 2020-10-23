from time import sleep
from machine import Pin

led = Pin(34, Pin.OUT)  # 25 for onboard LED

while True:
    led.value(not led.value())
    sleep(0.5)
