package com.example.configurator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter BLEAdapter;
    private BluetoothLeScanner BLEScanner;
    private BluetoothGatt BLEGatt;
    private BluetoothDevice BLEDevice;
    private List<ScanFilter> filterList;
    private ScanSettings settings;
    private boolean mScanning;
    private Handler handler = new Handler();

    private Queue<Runnable> commandQueue;
    private boolean commandBusy;

    private TextView deviceNameLabel;
    private TextView deviceMacLabel;
    private TextView deviceCharacteristicLabel;

    private static final long SCAN_PERIOD = 10000;
    private static final int GATT_INTERNAL_ERROR = 129;

    @Override

    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        final BluetoothManager btManager = (BluetoothManager) getSystemService(
                Context.BLUETOOTH_SERVICE );
        BLEAdapter = btManager.getAdapter();
        BLEScanner = BLEAdapter.getBluetoothLeScanner();

        deviceNameLabel = (TextView) findViewById( R.id.foundInfo );
        deviceMacLabel = (TextView) findViewById( R.id.macInfo );
        deviceCharacteristicLabel = (TextView) findViewById( R.id.serviceLabel );
        setupFilter();
        setupSettings();
        checkBLEPermissions();
    }

    private void setupFilter() {
        // TODO: Filter on more than just name characteristic
        filterList = new ArrayList<>();
        filterList.add( new ScanFilter.Builder().setDeviceName( "ESP32" ).build() );
    }

    private void setupSettings() {
        settings = new ScanSettings.Builder()
                .setScanMode( ScanSettings.SCAN_MODE_LOW_LATENCY )
                .setCallbackType( ScanSettings.CALLBACK_TYPE_FIRST_MATCH )
                .setMatchMode( ScanSettings.MATCH_MODE_AGGRESSIVE )
                .setNumOfMatches( ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT )
                .setReportDelay( 0L )
                .build();

    }

    public void deviceScan( View view ) {
        if ( BLEAdapter == null || !BLEAdapter.isEnabled() ) {
            Intent enableBtIntent = new Intent( BluetoothAdapter.ACTION_REQUEST_ENABLE );
            startActivityForResult( enableBtIntent, Keys.REQUEST_ENABLE_BT );
            return;
        }
        checkBLEPermissions();
        startBLEScan();
    }

    public void connectToDevice( View view ) {
        BLEGatt = BLEDevice.connectGatt( MainActivity.this,
                false, BLEGattCallback, BluetoothDevice.TRANSPORT_LE );
    }

    public void disconnectFromDevice( View view ) {
        BLEGatt.disconnect();
    }

    private void checkBLEPermissions() {
        if ( ContextCompat.checkSelfPermission( this,
                Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( this,
                    new String[]{ Manifest.permission.ACCESS_FINE_LOCATION },
                    Keys.REQUEST_FINE_LOCATION );
        }
    }

    private void startBLEScan() {
        if ( !mScanning ) {
            handler.postDelayed( new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    BLEScanner.stopScan( BLEScanCallback );
                }
            }, SCAN_PERIOD );

            mScanning = true;
            BLEScanner.startScan( filterList, settings, BLEScanCallback );
        } else {
            mScanning = false;
            BLEScanner.stopScan( BLEScanCallback );
        }
    }

    private ScanCallback BLEScanCallback = new ScanCallback() {
        @Override
        public void onScanResult( int callbackType, ScanResult result ) {
            super.onScanResult( callbackType, result );
            Log.d( "Scan Result", "Found: " + result.getDevice().getName() );
            String deviceName = result.getDevice().getName();
            if ( deviceName != null && deviceName.equals( "ESP32" ) ) {
                deviceNameLabel.setText( "ESP32" );
                deviceMacLabel.setText( result.getDevice().getAddress() );
                BLEDevice = result.getDevice();
            }
        }
    };

    private BluetoothGattCallback BLEGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange( BluetoothGatt gatt, int status, int newState ) {
            super.onConnectionStateChange( gatt, status, newState );
            if ( status == BluetoothGatt.GATT_SUCCESS ) {
                if ( newState == BluetoothProfile.STATE_CONNECTED ) {
                    int bondstate = BLEDevice.getBondState();
                    if ( bondstate == BluetoothDevice.BOND_NONE ||
                            bondstate == BluetoothDevice.BOND_BONDED ) {
                        int delay;
                        if ( Build.VERSION.SDK_INT <= Build.VERSION_CODES.N ) {
                            delay = bondstate == BluetoothDevice.BOND_BONDED ? 1000 : 0;
                        } else {
                            delay = 0;
                        }

                        Runnable discoverServicesRunnable = new Runnable() {
                            @Override
                            public void run() {
                                Log.d( "Connection State Change", "Discovering Services" );
                                boolean result = gatt.discoverServices();
                                if ( !result ) {
                                    Log.e( "Connection State Change", "discoverServices Failed" );
                                }
                            }
                        };
                        handler.postDelayed( discoverServicesRunnable, delay );
                    } else if ( bondstate == BluetoothDevice.BOND_BONDING ) {
                        Log.d( "Connection State Change", "Waiting for bonding" );
                    }
                } else if ( newState == BluetoothProfile.STATE_DISCONNECTED ) {
                    gatt.close();
                } else {
                    Log.d( "Connection State Change", "Weird Behaviour - Handle Later" );
                }
            } else {
                gatt.close();
            }
        }

        @Override
        public void onServicesDiscovered( BluetoothGatt gatt, int status ) {
            super.onServicesDiscovered( gatt, status );
            if ( status == GATT_INTERNAL_ERROR ) {
                gatt.disconnect();
                return;
            }
            Log.d( "Services Discovered", gatt.getServices().toString() );
        }
    };
}