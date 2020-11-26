package com.github.rywilliamson.configurator.Activities;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.github.rywilliamson.configurator.Interfaces.BluetoothContainer;
import com.github.rywilliamson.configurator.R;
import com.welie.blessed.BluetoothCentral;
import com.welie.blessed.BluetoothCentralCallback;
import com.welie.blessed.BluetoothPeripheral;
import com.welie.blessed.BluetoothPeripheralCallback;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static com.github.rywilliamson.configurator.Utils.CustomCharacteristics.ESP_CHARACTERISTIC_ID;
import static com.github.rywilliamson.configurator.Utils.CustomCharacteristics.ESP_SERVICE_ID;
import static com.github.rywilliamson.configurator.Utils.CustomCharacteristics.RSSI_CHARACTERISTIC_ID;

public class MainActivity extends AppCompatActivity implements BluetoothContainer {

    private BluetoothCentral central;
    private BluetoothPeripheral BLEPeripheral;
    private BluetoothGattCharacteristic rssiCharcteristic;
    private BluetoothGattCharacteristic normalCharacteristic;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        central = new BluetoothCentral( this, bluetoothCentralCallback, new Handler(
                Looper.getMainLooper() ) );
    }

    public BluetoothCentral getCentral() {
        return this.central;
    }

    public void swapToDebug( View view ) {
        startActivity(new Intent(this, DevActivity.class));
    }

    private final BluetoothCentralCallback bluetoothCentralCallback = new BluetoothCentralCallback() {
        @Override
        public void onDiscoveredPeripheral( BluetoothPeripheral peripheral,
                ScanResult scanResult ) {
            central.stopScan();
            central.connectPeripheral( peripheral, peripheralCallback );
        }

        @Override
        public void onConnectedPeripheral( BluetoothPeripheral peripheral ) {
            super.onConnectedPeripheral( peripheral );
            BLEPeripheral = peripheral;
            Log.d( "Central Callback", "Connection Completed" );
        }

        @Override
        public void onConnectionFailed( BluetoothPeripheral peripheral, int status ) {
            super.onConnectionFailed( peripheral, status );
            BLEPeripheral = null;
            normalCharacteristic = null;
            rssiCharcteristic = null;
            Log.d( "Central Callback", "Connection Failed" );
        }

        @Override
        public void onDisconnectedPeripheral( BluetoothPeripheral peripheral, int status ) {
            super.onDisconnectedPeripheral( peripheral, status );
            BLEPeripheral = null;
            normalCharacteristic = null;
            rssiCharcteristic = null;
            Log.d( "Central Callback", "Disconnected" );
        }
    };

    private final BluetoothPeripheralCallback peripheralCallback = new BluetoothPeripheralCallback() {
        @Override
        public void onServicesDiscovered( BluetoothPeripheral peripheral ) {
            super.onServicesDiscovered( peripheral );
            for (BluetoothGattCharacteristic characteristic : peripheral.getService( ESP_SERVICE_ID ).getCharacteristics()) {
                Log.d("test", String.valueOf( characteristic.getUuid()));
            }
            rssiCharcteristic = peripheral.getCharacteristic( ESP_SERVICE_ID, RSSI_CHARACTERISTIC_ID );
            normalCharacteristic = peripheral.getCharacteristic( ESP_SERVICE_ID, ESP_CHARACTERISTIC_ID );
        }

        @Override
        public void onCharacteristicUpdate( BluetoothPeripheral peripheral, byte[] value,
                BluetoothGattCharacteristic characteristic, int status ) {
            super.onCharacteristicUpdate( peripheral, value, characteristic, status );

            int val = ByteBuffer.wrap(value).order( ByteOrder.LITTLE_ENDIAN).getInt();
            Log.d("Default update", String.valueOf( val ));
        }
    };
}