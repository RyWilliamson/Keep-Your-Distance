package com.example.configurator;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.welie.blessed.BluetoothCentral;
import com.welie.blessed.BluetoothCentralCallback;
import com.welie.blessed.BluetoothPeripheral;
import com.welie.blessed.BluetoothPeripheralCallback;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

import static android.bluetooth.BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT;

public class MainActivity extends AppCompatActivity {

    private TextView deviceNameLabel;
    private TextView deviceMacLabel;
    private TextView deviceCharacteristicLabel;
    private byte vals = 0;
    private boolean notify = false;

    private BluetoothCentral central;
    private BluetoothPeripheral BLEPeripheral;
    private BluetoothGattCharacteristic rssiCharcteristic;
    private BluetoothGattCharacteristic normalCharacteristic;
    private final UUID ESP_SERVICE_ID = UUID.fromString( "4fafc201-1fb5-459e-8fcc-c5c9c331914b" );
    private final UUID ESP_CHARACTERISTIC_ID = UUID.fromString(
            "beb5483e-36e1-4688-b7f5-ea07361b26a8" );
    private final UUID RSSI_CHARACTERISTIC_ID = UUID.fromString(
            "3f237eb3-99b4-4bbd-9475-f2e7b39ac899" );

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        deviceNameLabel = findViewById( R.id.foundInfo );
        deviceMacLabel = findViewById( R.id.macInfo );
        deviceCharacteristicLabel = findViewById( R.id.serviceInfo );

        central = new BluetoothCentral( this, bluetoothCentralCallback, new Handler(
                Looper.getMainLooper() ) );
    }

    public void deviceScan( View view ) {
        // Start Scan
        central.scanForPeripheralsWithNames( new String[]{ "ESP32" } );
    }

    public void disconnectFromDevice( View view ) {
        central.cancelConnection( BLEPeripheral );
    }

    public void onRead( View view ) {
//        Log.d("read", String.valueOf(BLEPeripheral.readCharacteristic( rssiCharcteristic )));
        notify = !notify;
        Log.d("read", String.valueOf(notify));
        Log.d("read", String.valueOf(BLEPeripheral.setNotify( rssiCharcteristic, notify )));
    }

    public void onWrite( View view ) {
        boolean written = BLEPeripheral.writeCharacteristic( normalCharacteristic, new byte[]{ vals++ },
                WRITE_TYPE_DEFAULT );
        Log.d("write", String.valueOf(written));
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
            deviceNameLabel.setText( peripheral.getName() );
            deviceMacLabel.setText( peripheral.getAddress() );
            Log.d( "Central Callback", "Connected" );
        }

        @Override
        public void onConnectionFailed( BluetoothPeripheral peripheral, int status ) {
            super.onConnectionFailed( peripheral, status );
            BLEPeripheral = null;
            normalCharacteristic = null;
            rssiCharcteristic = null;
            deviceNameLabel.setText( "" );
            deviceMacLabel.setText( "" );
            deviceCharacteristicLabel.setText( "" );
            Log.d( "Central Callback", "Connection Failed" );
        }

        @Override
        public void onDisconnectedPeripheral( BluetoothPeripheral peripheral, int status ) {
            super.onDisconnectedPeripheral( peripheral, status );
            BLEPeripheral = null;
            normalCharacteristic = null;
            rssiCharcteristic = null;
            deviceNameLabel.setText( "" );
            deviceMacLabel.setText( "" );
            deviceCharacteristicLabel.setText( "" );
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

            Log.d("test", String.valueOf( value.length ));
            int val = ByteBuffer.wrap(value).order(ByteOrder.LITTLE_ENDIAN).getInt();
            deviceCharacteristicLabel.setText( String.valueOf( val ) );
        }
    };

}