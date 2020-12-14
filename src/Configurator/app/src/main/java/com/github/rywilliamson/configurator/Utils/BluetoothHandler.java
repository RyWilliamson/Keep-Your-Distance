package com.github.rywilliamson.configurator.Utils;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.welie.blessed.BluetoothCentral;
import com.welie.blessed.BluetoothCentralCallback;
import com.welie.blessed.BluetoothPeripheral;

import static com.github.rywilliamson.configurator.Utils.CustomCharacteristics.CONN_CHARACTERISTIC_ID;
import static com.github.rywilliamson.configurator.Utils.CustomCharacteristics.HEARTBEAT_SERVICE_ID;
import static com.github.rywilliamson.configurator.Utils.CustomCharacteristics.RSSI_CHARACTERISTIC_ID;
import static com.github.rywilliamson.configurator.Utils.CustomCharacteristics.RSSI_SERVICE_ID;

public class BluetoothHandler {

    private final BluetoothCentral central;
    private BluetoothPeripheral BLEPeripheral;
    private BluetoothGattCharacteristic rssiCharacteristic;
    private BluetoothGattCharacteristic connectionCharacteristic;
    private boolean connected;

    public BluetoothHandler( Activity activity, BluetoothCentralCallback callback ) {
        central = new BluetoothCentral( activity, callback, new Handler(
                Looper.getMainLooper() ) );
    }

    public void checkBLEPermissions( Activity activity ) {
        if ( ContextCompat.checkSelfPermission( activity,
                Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( activity,
                    new String[]{ Manifest.permission.ACCESS_FINE_LOCATION },
                    Keys.REQUEST_FINE_LOCATION );
        }
    }


    public BluetoothCentral getCentral() {
        return central;
    }

    public boolean isConnected() {
        return connected;
    }

    public BluetoothPeripheral getBLEPeripheral() {
        return BLEPeripheral;
    }

    public BluetoothGattCharacteristic getRssiCharacteristic() {
        return rssiCharacteristic;
    }

    public BluetoothGattCharacteristic getConnectionCharacteristic() {
        return connectionCharacteristic;
    }

    public void scan(Activity activity) {
        checkBLEPermissions(activity);
        central.scanForPeripheralsWithNames( new String[]{ "ESP32" } );
    }

    public void onConnect( BluetoothPeripheral peripheral ) {
        this.BLEPeripheral = peripheral;
        connected = true;
    }

    public void disconnect() {
        this.BLEPeripheral = null;
        this.rssiCharacteristic = null;
        this.connectionCharacteristic = null;
        this.connected = false;
    }

    public void setupServices( BluetoothPeripheral peripheral ) {
        this.rssiCharacteristic = peripheral.getCharacteristic( RSSI_SERVICE_ID, RSSI_CHARACTERISTIC_ID );
        this.connectionCharacteristic = peripheral.getCharacteristic( HEARTBEAT_SERVICE_ID, CONN_CHARACTERISTIC_ID );
    }
}
