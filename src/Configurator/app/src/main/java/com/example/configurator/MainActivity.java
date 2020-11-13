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
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
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
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter BLEAdapter;
    private BluetoothLeScanner BLEScanner;
    private BluetoothGatt BLEGatt;
    private BluetoothDevice BLEDevice;
    private List<ScanFilter> filterList;
    private ScanSettings settings;
    private boolean mScanning;
    private Handler handler = new Handler();

    private Queue<Runnable> commandQueue = new ConcurrentLinkedQueue<>();
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

        deviceNameLabel = findViewById( R.id.foundInfo );
        deviceMacLabel = findViewById( R.id.macInfo );
        deviceCharacteristicLabel = findViewById( R.id.serviceInfo );
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

    public void onRead( View view ) {
        for ( BluetoothGattService service : BLEGatt.getServices() ) {
            for ( BluetoothGattCharacteristic characteristic : service.getCharacteristics() ) {
                readCharacteristic( characteristic );
            }
        }
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
                BLEDevice = result.getDevice();
                deviceNameLabel.setText( "ESP32" );
                deviceMacLabel.setText( BLEDevice.getAddress() );
            }
        }
    };

    private boolean readCharacteristic( final BluetoothGattCharacteristic characteristic ) {
        if ( BLEGatt == null ) {
            Log.e( "Read Characteristic", "gatt is null" );
            return false;
        }

        if ( characteristic == null ) {
            Log.e( "Read Characteristic", "characteristic is null" );
            return false;
        }

        if ( ( characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_READ ) == 0 ) {
            Log.e( "Read Characteristic",
                    String.format( "Characteristic %s doesn't have read property",
                            characteristic.getUuid() ) );
            return false;
        }

        boolean result = commandQueue.add( new Runnable() {
            @Override
            public void run() {
                if ( !BLEGatt.readCharacteristic( characteristic ) ) {
                    Log.e( "Read Characteristic", String.format(
                            "ERROR: readCharacteristic failed for characteristic: %s",
                            characteristic.getUuid() ) );
                    completedCommand();
                } else {
                    Log.d( "Read Characteristic", String.format( "reading characteristic <%s>",
                            characteristic.getUuid() ) );
                }
            }
        } );

        if ( result ) {
            nextCommand();
        } else {
            Log.e( "Read Characteristic", "ERROR: Could not enqueue read characteristic command" );
        }
        return result;
    }

    private void completedCommand() {
        commandBusy = false;
        commandQueue.poll();
        nextCommand();
    }

    private void nextCommand() {
        if ( commandBusy ) {
            return;
        }

        if ( BLEGatt == null ) {
            Log.e( "Next Command", "Gatt is null" );
            commandQueue.clear();
            commandBusy = false;
            return;
        }

        if ( commandQueue.size() > 0 ) {
            final Runnable command = commandQueue.peek();
            commandBusy = true;

            handler.post( new Runnable() {
                @Override
                public void run() {
                    try {
                        command.run();
                    } catch ( Exception ex ) {
                        Log.e( "Next Command", "Error in command" );
                    }
                }
            } );
        }
    }

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

        @Override
        public void onCharacteristicRead( BluetoothGatt gatt,
                BluetoothGattCharacteristic characteristic, int status ) {
            super.onCharacteristicRead( gatt, characteristic, status );
            if ( status != BluetoothGatt.GATT_SUCCESS ) {
                Log.e( "Character Read Callback", "Read Failed" );
                completedCommand();
                return;
            }
            if ( characteristic.getUuid().toString().equals(
                    "beb5483e-36e1-4688-b7f5-ea07361b26a8" ) ) {
                BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                        UUID.fromString( "00002902-0000-1000-8000-00805f9b34fb" ));
                if ( descriptor != null ){
                    Log.d( "Character Read Callback", String.format("Success - found relevant characteristic: %s" , descriptor.getUuid().toString()));
                    updateTextViewFromRunnable( deviceCharacteristicLabel, descriptor.getUuid().toString() );
                }
            }
            completedCommand();
        }
    };

    private void updateTextViewFromRunnable(TextView tv, String data) {
        MainActivity.this.runOnUiThread( new Runnable() {
            @Override
            public void run() {
                tv.setText( data );
            }
        } );
    }
}