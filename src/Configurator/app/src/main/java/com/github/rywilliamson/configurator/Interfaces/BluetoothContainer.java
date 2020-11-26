package com.github.rywilliamson.configurator.Interfaces;

import com.welie.blessed.BluetoothCentral;
import com.welie.blessed.BluetoothCentralCallback;
import com.welie.blessed.BluetoothPeripheralCallback;

import java.io.Serializable;

public interface BluetoothContainer extends Serializable {
    BluetoothCentral getCentral();
    void checkBLEPermissions();
}
