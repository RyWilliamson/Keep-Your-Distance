package com.example.configurator;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.configurator.BluetoothHighLevel.BLEFunctions;
import com.example.configurator.BluetoothHighLevel.BluetoothHighLevel;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private BluetoothHighLevel bluetooth;

    private TextView deviceNameLabel;
    private TextView deviceMacLabel;
    protected TextView deviceCharacteristicLabel;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        deviceNameLabel = findViewById( R.id.foundInfo );
        deviceMacLabel = findViewById( R.id.macInfo );
        deviceCharacteristicLabel = findViewById( R.id.serviceInfo );

        this.bluetooth = new BluetoothHighLevel( this, BLEScanCallback,
                new MainBLEFunctions() );
    }

    public void deviceScan( View view ) {
        if ( bluetooth.checkEnabled() ) {
            bluetooth.startBLEScan();
        }
    }

    public void connectToDevice( View view ) {
        bluetooth.connect();
    }

    public void disconnectFromDevice( View view ) {
        bluetooth.disconnect();
    }

    public void onRead( View view ) {
        for ( BluetoothGattService service : bluetooth.getBLEGatt().getServices() ) {
            for ( BluetoothGattCharacteristic characteristic : service.getCharacteristics() ) {
                bluetooth.readCharacteristic( characteristic );
            }
        }
    }

    private final ScanCallback BLEScanCallback = new ScanCallback() {
        @Override
        public void onScanResult( int callbackType, ScanResult result ) {
            super.onScanResult( callbackType, result );
            Log.d( "Scan Result", "Found: " + result.getDevice().getName() );
            String deviceName = result.getDevice().getName();
            if ( deviceName != null && deviceName.equals( "ESP32" ) ) {
                bluetooth.setBLEDevice(result.getDevice());
                deviceNameLabel.setText( "ESP32" );
                deviceMacLabel.setText( bluetooth.getBLEDevice().getAddress() );
            }
        }
    };

    class MainBLEFunctions implements BLEFunctions {

        @Override
        public void characteristicReadFunction( BluetoothGattCharacteristic characteristic ) {
            if ( characteristic.getUuid().toString().equals(
                    "beb5483e-36e1-4688-b7f5-ea07361b26a8" ) ) {
                BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                        UUID.fromString( "00002902-0000-1000-8000-00805f9b34fb" ) );
                if ( descriptor != null ) {
                    Log.d( "Character Read Callback",
                            String.format( "Success - found relevant characteristic: %s",
                                    descriptor.getUuid().toString() ) );
                    updateTextViewFromRunnable( deviceCharacteristicLabel,
                            descriptor.getUuid().toString() );
                }
            }
        }

        private void updateTextViewFromRunnable( TextView tv, String data ) {
            MainActivity.this.runOnUiThread( () -> tv.setText( data ) );
        }
    }
}