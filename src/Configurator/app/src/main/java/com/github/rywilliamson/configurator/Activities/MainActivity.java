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
import com.github.rywilliamson.configurator.Interfaces.BluetoothContainer;
import com.github.rywilliamson.configurator.Interfaces.BluetoothImplementer;
import com.github.rywilliamson.configurator.Interfaces.DatabaseContainer;
import com.github.rywilliamson.configurator.NavGraphDirections;
import com.github.rywilliamson.configurator.R;
import com.github.rywilliamson.configurator.Utils.BluetoothHandler;
import com.github.rywilliamson.configurator.Utils.Keys;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.welie.blessed.BluetoothCentral;
import com.welie.blessed.BluetoothCentralCallback;
import com.welie.blessed.BluetoothPeripheral;
import com.welie.blessed.BluetoothPeripheralCallback;

public class MainActivity extends AppCompatActivity implements BluetoothContainer, DatabaseContainer {

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

    public String getPrevMac() {
        return bt.getPrevMac();
    }

    public BluetoothCentral getCentral() {
        return bt.getCentral();
    }

    public BluetoothPeripheral getPeripheral() {
        return bt.getBLEPeripheral();
    }

    public BluetoothGattCharacteristic getRssiCharacteristic() {
        return bt.getRssiCharacteristic();
    }

    public BluetoothGattCharacteristic getConnectionCharacteristic() {
        return bt.getConnectionCharacteristic();
    }

    public boolean getConnected() {
        return bt.isConnected();
    }

    public void checkBLEPermissions() {
        bt.checkBLEPermissions( this );
    }

    public void scan() {
        bt.scan( this );
    }

    public void directConnect( String UUID ) {
        bt.directConnect( UUID, peripheralCallback );
    }

    public void disconnect() {
        bt.getCentral().cancelConnection( bt.getBLEPeripheral() );
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
            getCurrentImplementer().getPeripheralCallback().onServicesDiscovered( peripheral );
        }

        @Override
        public void onCharacteristicUpdate( @NonNull BluetoothPeripheral peripheral, @NonNull byte[] value,
                @NonNull BluetoothGattCharacteristic characteristic, int status ) {
            super.onCharacteristicUpdate( peripheral, value, characteristic, status );
            Log.d( Keys.GLOBAL_PERIPHERAL, "Updating " + characteristic.getUuid() );
            getCurrentImplementer().getPeripheralCallback().onCharacteristicUpdate( peripheral, value, characteristic,
                    status );
        }
    };

    private BluetoothImplementer getCurrentImplementer() {
        return (BluetoothImplementer) getSupportFragmentManager().findFragmentById(
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