package com.github.rywilliamson.configurator.Interfaces;

import com.welie.blessed.BluetoothCentralCallback;
import com.welie.blessed.BluetoothPeripheralCallback;

public interface BluetoothImplementer {
    BluetoothCentralCallback getCentralCallback();
    BluetoothPeripheralCallback getPeripheralCallback();

}
