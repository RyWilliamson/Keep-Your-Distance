package com.example.configurator.BluetoothHighLevel;

import android.Manifest;
import android.app.Activity;
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
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.configurator.Keys;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BluetoothHighLevel {

    private final Activity activity;
    private final ScanCallback BLEScanCallback;
    private final BLEFunctions bleFunctions;

    private final BluetoothAdapter BLEAdapter;
    private final BluetoothLeScanner BLEScanner;
    private BluetoothDevice BLEDevice;
    private BluetoothGatt BLEGatt;
    private boolean mScanning;
    private final Handler handler = new Handler();

    private final Queue<Runnable> commandQueue = new ConcurrentLinkedQueue<>();
    private boolean commandBusy;

    private List<ScanFilter> filterList;
    private ScanSettings settings;

    private static final long SCAN_PERIOD = 10000;
    private static final int GATT_INTERNAL_ERROR = 129;

    public BluetoothHighLevel( Activity activity, ScanCallback scanCallback,
            BLEFunctions bleFunctions ) {
        this.activity = activity;
        this.BLEScanCallback = scanCallback;
        this.bleFunctions = bleFunctions;

        final BluetoothManager btManager = (BluetoothManager) activity.getSystemService(
                Context.BLUETOOTH_SERVICE );

        BLEAdapter = btManager.getAdapter();
        BLEScanner = BLEAdapter.getBluetoothLeScanner();

        setupFilter();
        setupSettings();
        checkBLEPermissions();
    }

    public boolean checkEnabled() {
        if ( BLEAdapter == null || !BLEAdapter.isEnabled() ) {
            Intent enableBtIntent = new Intent( BluetoothAdapter.ACTION_REQUEST_ENABLE );
            activity.startActivityForResult( enableBtIntent, Keys.REQUEST_ENABLE_BT );
            return false;
        }
        return true;
    }

    public void startBLEScan() {
        checkBLEPermissions();
        if ( !mScanning ) {
            handler.postDelayed( () -> {
                mScanning = false;
                BLEScanner.stopScan( BLEScanCallback );
            }, SCAN_PERIOD );

            mScanning = true;
            BLEScanner.startScan( filterList, settings, BLEScanCallback );
        } else {
            mScanning = false;
            BLEScanner.stopScan( BLEScanCallback );
        }
    }

    public void disconnect() {
        BLEGatt.disconnect();
    }

    public void connect() {
        BLEGatt = BLEDevice.connectGatt( activity,
                false, BLEGattCallback, BluetoothDevice.TRANSPORT_LE );
    }

    public BluetoothGatt getBLEGatt() {
        return BLEGatt;
    }

    public BluetoothDevice getBLEDevice() {
        return BLEDevice;
    }

    public void setBLEDevice( BluetoothDevice device ) {
        this.BLEDevice = device;
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

    private void checkBLEPermissions() {
        if ( ContextCompat.checkSelfPermission( activity,
                Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( activity,
                    new String[]{ Manifest.permission.ACCESS_FINE_LOCATION },
                    Keys.REQUEST_FINE_LOCATION );
        }
    }

    public boolean readCharacteristic( final BluetoothGattCharacteristic characteristic ) {
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

        boolean result = commandQueue.add( () -> {
            if ( !BLEGatt.readCharacteristic( characteristic ) ) {
                Log.e( "Read Characteristic", String.format(
                        "ERROR: readCharacteristic failed for characteristic: %s",
                        characteristic.getUuid() ) );
                completedCommand();
            } else {
                Log.d( "Read Characteristic", String.format( "reading characteristic <%s>",
                        characteristic.getUuid() ) );
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

            handler.post( () -> {
                try {
                    command.run();
                } catch ( Exception ex ) {
                    Log.e( "Next Command", "Error in command" );
                }
            } );
        }
    }

    private final BluetoothGattCallback BLEGattCallback = new BluetoothGattCallback() {
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

                        Runnable discoverServicesRunnable = () -> {
                            Log.d( "Connection State Change", "Discovering Services" );
                            boolean result = gatt.discoverServices();
                            if ( !result ) {
                                Log.e( "Connection State Change", "discoverServices Failed" );
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
            bleFunctions.characteristicReadFunction( characteristic );
            completedCommand();
        }
    };

}
