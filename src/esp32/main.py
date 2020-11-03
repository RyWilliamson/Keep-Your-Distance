import ubluetooth

bt = ubluetooth.BLE()
bt.active(True)
print(bt.config('mac'))
print(bt.config('addr_mode'))
#bt.gap_advertise(10000, adv_data='payload')
