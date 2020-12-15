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
import com.welie.blessed.BluetoothPeripheralCallback;

import java.util.ArrayList;
import java.util.List;

import static com.github.rywilliamson.configurator.Utils.CustomCharacteristics.CONN_CHARACTERISTIC_ID;
import static com.github.rywilliamson.configurator.Utils.CustomCharacteristics.HEARTBEAT_SERVICE_ID;
import static com.github.rywilliamson.configurator.Utils.CustomCharacteristics.RSSI_CHARACTERISTIC_ID;
import static com.github.rywilliamson.configurator.Utils.CustomCharacteristics.RSSI_SERVICE_ID;

public class BluetoothHandler {

    private final BluetoothCentral central;
    private BluetoothPeripheral BLEPeripheral;
    private BluetoothGattCharacteristic rssiCharacteristic;
    private BluetoothGattCharacteristic connectionCharacteristic;
    private final List<BluetoothPeripheral> scannedPeripherals;
    private boolean connected;
    private final int scanTimeout = 3000; // Milliseconds

    public BluetoothHandler( Activity activity, BluetoothCentralCallback callback ) {
        central = new BluetoothCentral( activity, callback, new Handler(
                Looper.getMainLooper() ) );
        scannedPeripherals = new ArrayList<>();
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

    public void scan( Activity activity ) {
        checkBLEPermissions( activity );
        scannedPeripherals.clear();
        central.scanForPeripheralsWithNames( new String[]{ "ESP32" } );
        new Handler( Looper.getMainLooper() ).postDelayed( central::stopScan, scanTimeout );
    }

    public boolean addPeripheral( BluetoothPeripheral peripheral ) {
        if ( scannedPeripherals.contains( peripheral ) ) {
            return false;
        }
        scannedPeripherals.add( peripheral );
        return true;
    }

    public void onConnect( BluetoothPeripheral peripheral ) {
        this.BLEPeripheral = peripheral;
        connected = true;
    }

    public void directConnect( String UUID, BluetoothPeripheralCallback callback ) {
        central.connectPeripheral( central.getPeripheral( UUID ), callback );
    }

    public void disconnect() {
        this.BLEPeripheral = null;
        this.rssiCharacteristic = null;
        this.connectionCharacteristic = null;
        this.connected = false;
    }

    public void setupServices( BluetoothPeripheral peripheral ) {
        if ( verifyServices( peripheral ) ) {
            this.rssiCharacteristic = peripheral.getCharacteristic( RSSI_SERVICE_ID, RSSI_CHARACTERISTIC_ID );
            this.connectionCharacteristic = peripheral.getCharacteristic( HEARTBEAT_SERVICE_ID,
                    CONN_CHARACTERISTIC_ID );
        }
    }

    private boolean verifyServices( BluetoothPeripheral peripheral ) {
        // Can only implement this once device is working with ALL services
        return true;
    }
}