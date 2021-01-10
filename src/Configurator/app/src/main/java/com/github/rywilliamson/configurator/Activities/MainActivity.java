package com.github.rywilliamson.configurator.Activities;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.github.rywilliamson.configurator.Database.DatabaseViewModel;
import com.github.rywilliamson.configurator.Database.Entity.Device;
import com.github.rywilliamson.configurator.Database.Entity.RSSI;
import com.github.rywilliamson.configurator.Interfaces.IBackendContainer;
import com.github.rywilliamson.configurator.Interfaces.IBluetoothImplementer;
import com.github.rywilliamson.configurator.NavGraphDirections;
import com.github.rywilliamson.configurator.R;
import com.github.rywilliamson.configurator.Utils.BluetoothHandler;
import com.github.rywilliamson.configurator.Utils.Keys;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.welie.blessed.BluetoothCentralCallback;
import com.welie.blessed.BluetoothPeripheral;
import com.welie.blessed.BluetoothPeripheralCallback;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements IBackendContainer {

    private BottomNavigationView bottomNavigation;
    private BluetoothHandler bt;
    private DatabaseViewModel dbViewModel;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        dbViewModel = new ViewModelProvider( this ).get( DatabaseViewModel.class );
        bt = new BluetoothHandler( this, bluetoothCentralCallback );
        bottomNavigation = findViewById( R.id.bottom_navigation );
        bottomNavigation.setOnNavigationItemSelectedListener( navigationItemSelectedListener );

        dbViewModel.getAllDevices().observe( this, devices -> {
            Log.d( Keys.DB_DEVICE_ALL, String.valueOf( devices ) );
        } );
    }

    public DatabaseViewModel getDatabaseViewModel() {
        return dbViewModel;
    }

    public BluetoothHandler getBluetoothHandler() {
        return bt;
    }

    public void scan() {
        bt.scan( this );
    }

    public void directConnect( String UUID ) {
        bt.directConnect( UUID, peripheralCallback );
    }

    public void swapToDebug( View view ) {
        startActivity( new Intent( this, DevActivity.class ) );
    }

    private final BluetoothCentralCallback bluetoothCentralCallback = new BluetoothCentralCallback() {
        @Override
        public void onDiscoveredPeripheral( @NonNull BluetoothPeripheral peripheral,
                @NonNull ScanResult scanResult ) {
            if ( bt.addPeripheral( peripheral ) ) {
                Log.d( Keys.GLOBAL_CENTRAL, "Discovered Peripheral: " + peripheral.getAddress() );
                getCurrentImplementer().getCentralCallback().onDiscoveredPeripheral( peripheral, scanResult );
            }
        }

        @Override
        public void onConnectedPeripheral( @NonNull BluetoothPeripheral peripheral ) {
            super.onConnectedPeripheral( peripheral );
            Log.d( Keys.GLOBAL_CENTRAL, "Connection Completed to: " + peripheral.getAddress() );
            bt.onConnect( peripheral );
            String mac = peripheral.getAddress();
            dbViewModel.insert( new Device( mac, mac, 1 ) );
            dbViewModel.setInteractionCountReceiver( peripheral.getAddress() );
            getCurrentImplementer().getCentralCallback().onConnectedPeripheral( peripheral );
        }

        @Override
        public void onConnectionFailed( @NonNull BluetoothPeripheral peripheral, int status ) {
            super.onConnectionFailed( peripheral, status );
            Log.d( Keys.GLOBAL_CENTRAL, "Connection Failed to " + peripheral.getAddress() );
            bt.clearForDisconnect();
            getCurrentImplementer().getCentralCallback().onConnectionFailed( peripheral, status );
        }

        @Override
        public void onDisconnectedPeripheral( @NonNull BluetoothPeripheral peripheral, int status ) {
            super.onDisconnectedPeripheral( peripheral, status );
            Log.d( Keys.GLOBAL_CENTRAL, "Disconnected from: " + peripheral.getAddress() );
            bt.clearForDisconnect();
            getCurrentImplementer().getCentralCallback().onDisconnectedPeripheral( peripheral, status );
        }

        @Override
        public void onScanFailed( int errorCode ) {
            super.onScanFailed( errorCode );
            Log.d( Keys.GLOBAL_CENTRAL, "Scan failed with error code: " + errorCode );
            getCurrentImplementer().getCentralCallback().onScanFailed( errorCode );
        }
    };

    private final BluetoothPeripheralCallback peripheralCallback = new BluetoothPeripheralCallback() {
        @Override
        public void onServicesDiscovered( @NonNull BluetoothPeripheral peripheral ) {
            super.onServicesDiscovered( peripheral );
            Log.d( Keys.GLOBAL_PERIPHERAL, "Discovered " + peripheral.getServices() );
            bt.setupServices( peripheral );
            bt.sendConfig();
            bt.sendBulkACK( "Init" );
            getCurrentImplementer().getPeripheralCallback().onServicesDiscovered( peripheral );
        }

        @Override
        public void onCharacteristicUpdate( @NonNull BluetoothPeripheral peripheral, @NonNull byte[] value,
                @NonNull BluetoothGattCharacteristic characteristic, int status ) {
            super.onCharacteristicUpdate( peripheral, value, characteristic, status );
            if ( characteristic == bt.getRssiCharacteristic() ) {
                handleIncomingRSSI( value, peripheral );
            } else if ( characteristic == bt.getBulkCharacteristic() ) {
                handleBulkRSSI( value, peripheral );
            }

            getCurrentImplementer().getPeripheralCallback().onCharacteristicUpdate( peripheral, value, characteristic,
                    status );
        }
    };

    private void handleIncomingRSSI( @NonNull byte[] value, @NonNull BluetoothPeripheral peripheral ) {
        if ( value.length != 13 ) {
            Log.d( Keys.GLOBAL_PERIPHERAL, "Incorrect packet length " + value.length );
            return;
        }
        insertRSSI( value, peripheral, false );

    }

    private void handleBulkRSSI( @NonNull byte[] value, @NonNull BluetoothPeripheral peripheral ) {
        if ( value.length != 17 ) {
            Log.d( Keys.GLOBAL_PERIPHERAL, "Incorrect bulk packet length " + value.length );
            return;
        }
        insertRSSI( value, peripheral, true );
        bt.sendBulkACK( "ACK" );
    }

    private void insertRSSI( byte[] value, BluetoothPeripheral peripheral, boolean isBulk ) {
        String mac = new String( Arrays.copyOfRange( value, 0, 12 ), StandardCharsets.UTF_8 );
        int rssi = value[12];
        Date endTime = Calendar.getInstance().getTime();
        if ( isBulk ) {
            ByteBuffer buffer = ByteBuffer.allocate( 4 ).order( ByteOrder.LITTLE_ENDIAN ).put( value, 13, 4 );
            buffer.rewind();
            long offset = buffer.getInt();
            endTime = new Date( endTime.getTime() - offset );
        }

        Log.d( Keys.GLOBAL_PERIPHERAL, "Received value: " + mac + " " + rssi + " " + endTime );

        mac = mac.replaceAll( "(.{2})", "$1:" );
        mac = mac.substring( 0, mac.length() - 1 );
        mac = mac.toUpperCase();

        // Database Calls
        dbViewModel.insertScanned( new Device( mac, mac, 0 ) );
        Date startTime;
        if (isBulk) {
            startTime = bt.insertBulkStartTimeAndGet( mac, peripheral.getAddress(), endTime );
        } else {
            startTime = bt.insertStartTimeAndGet( mac, peripheral.getAddress(), endTime );
        }

        dbViewModel.insert(
                new RSSI( mac, peripheral.getAddress(), startTime, endTime, rssi,
                        bt.calculateDistance( rssi ),
                        bt.getMeasuredPower(), bt.getEnvironmentVar() ), true );

    }

    private IBluetoothImplementer getCurrentImplementer() {
        return (IBluetoothImplementer) getSupportFragmentManager().findFragmentById(
                R.id.nav_host_fragment ).getChildFragmentManager().getFragments().get( 0 );
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = item -> {
        NavDirections action = null;
        if ( item.getItemId() == R.id.device ) {
            // Move to device
            action = bt.isConnected() ?
                    NavGraphDirections.actionGlobalDeviceInfoFragment() :
                    NavGraphDirections.actionGlobalDeviceConnectFragment();
        } else if ( item.getItemId() == R.id.graphs ) {
            //Move to graphs
            action = NavGraphDirections.actionGlobalGraphFragment();
        } else if ( item.getItemId() == R.id.settings ) {
            // Move to settings
            action = NavGraphDirections.actionGlobalSettingsFragment();
        }
        if ( action != null ) {
            Navigation.findNavController( this, R.id.nav_host_fragment ).navigate( action );
            return true;
        }
        return false;
    };
}