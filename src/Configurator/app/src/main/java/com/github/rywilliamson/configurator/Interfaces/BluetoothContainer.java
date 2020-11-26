package com.github.rywilliamson.configurator.Interfaces;

import android.bluetooth.BluetoothGattCharacteristic;

import com.welie.blessed.BluetoothCentral;
import com.welie.blessed.BluetoothPeripheral;

import java.io.Serializable;

public interface BluetoothContainer extends Serializable {
    BluetoothCentral getCentral();
    BluetoothPeripheral getPeripheral();
    BluetoothGattCharacteristic getRssiCharacteristic();
    BluetoothGattCharacteristic getNormalCharacteristic();
    void checkBLEPermissions();
}
