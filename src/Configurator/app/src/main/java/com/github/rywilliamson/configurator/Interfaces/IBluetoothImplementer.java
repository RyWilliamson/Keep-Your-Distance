package com.github.rywilliamson.configurator.Interfaces;

import com.welie.blessed.BluetoothCentralCallback;
import com.welie.blessed.BluetoothPeripheralCallback;

public interface IBluetoothImplementer {
    BluetoothCentralCallback getCentralCallback();

    BluetoothPeripheralCallback getPeripheralCallback();

}
