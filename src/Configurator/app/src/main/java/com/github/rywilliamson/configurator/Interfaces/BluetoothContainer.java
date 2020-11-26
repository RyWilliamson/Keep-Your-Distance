package com.github.rywilliamson.configurator.Interfaces;

import com.welie.blessed.BluetoothCentral;

import java.io.Serializable;

public interface BluetoothContainer extends Serializable {
    BluetoothCentral getCentral();
}
