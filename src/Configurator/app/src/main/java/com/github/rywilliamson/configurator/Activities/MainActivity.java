package com.github.rywilliamson.configurator.Activities;

import android.Manifest;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.github.rywilliamson.configurator.Interfaces.BluetoothContainer;
import com.github.rywilliamson.configurator.Interfaces.BluetoothImplementer;
import com.github.rywilliamson.configurator.NavGraphDirections;
import com.github.rywilliamson.configurator.R;
import com.github.rywilliamson.configurator.Utils.Keys;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.welie.blessed.BluetoothCentral;
import com.welie.blessed.BluetoothCentralCallback;
import com.welie.blessed.BluetoothPeripheral;
import com.welie.blessed.BluetoothPeripheralCallback;

import static com.github.rywilliamson.configurator.Utils.CustomCharacteristics.ESP_CHARACTERISTIC_ID;
import static com.github.rywilliamson.configurator.Utils.CustomCharacteristics.ESP_SERVICE_ID;
import static com.github.rywilliamson.configurator.Utils.CustomCharacteristics.RSSI_CHARACTERISTIC_ID;

public class MainActivity extends AppCompatActivity implements BluetoothContainer {

    private BluetoothCentral central;
    private BluetoothPeripheral BLEPeripheral;
    private BluetoothGattCharacteristic rssiCharacteristic;
    private BluetoothGattCharacteristic normalCharacteristic;

    private BottomNavigationView bottomNavigation;
    private boolean connected;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        central = new BluetoothCentral( this, bluetoothCentralCallback, new Handler(
                Looper.getMainLooper() ) );
        connected = false;
        bottomNavigation = findViewById( R.id.bottom_navigation );
        bottomNavigation.setOnNavigationItemSelectedListener( navigationItemSelectedListener );
    }

    public BluetoothCentral getCentral() {
        return this.central;
    }

    public BluetoothPeripheral getPeripheral() {
        return this.BLEPeripheral;
    }

    public BluetoothGattCharacteristic getRssiCharacteristic() {
        return this.rssiCharacteristic;
    }

    public BluetoothGattCharacteristic getNormalCharacteristic() {
        return this.normalCharacteristic;
    }

    public boolean getConnected() {
        return connected;
    }

    public void setConnected(boolean bool) {
        connected = bool;
    }

    public void checkBLEPermissions() {
        if ( ContextCompat.checkSelfPermission( this,
                Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( this,
                    new String[]{ Manifest.permission.ACCESS_FINE_LOCATION },
                    Keys.REQUEST_FINE_LOCATION );
        }
    }

    public void swapToDebug( View view ) {
        startActivity( new Intent( this, DevActivity.class ) );
    }

    private final BluetoothCentralCallback bluetoothCentralCallback = new BluetoothCentralCallback() {
        @Override
        public void onDiscoveredPeripheral( BluetoothPeripheral peripheral,
                ScanResult scanResult ) {
            central.stopScan();
            central.connectPeripheral( peripheral, peripheralCallback );
            getCurrentImplementer().getCentralCallback().onDiscoveredPeripheral( peripheral,
                    scanResult );
        }

        @Override
        public void onConnectedPeripheral( BluetoothPeripheral peripheral ) {
            super.onConnectedPeripheral( peripheral );
            BLEPeripheral = peripheral;
            Log.d( "Central Callback", "Connection Completed" );
            getCurrentImplementer().getCentralCallback().onConnectedPeripheral( peripheral );
        }

        @Override
        public void onConnectionFailed( BluetoothPeripheral peripheral, int status ) {
            super.onConnectionFailed( peripheral, status );
            BLEPeripheral = null;
            normalCharacteristic = null;
            rssiCharacteristic = null;
            Log.d( "Central Callback", "Connection Failed" );
            getCurrentImplementer().getCentralCallback().onConnectionFailed( peripheral,
                    status );
        }

        @Override
        public void onDisconnectedPeripheral( BluetoothPeripheral peripheral, int status ) {
            super.onDisconnectedPeripheral( peripheral, status );
            BLEPeripheral = null;
            normalCharacteristic = null;
            rssiCharacteristic = null;
            Log.d( "Central Callback", "Disconnected" );
            getCurrentImplementer().getCentralCallback().onDisconnectedPeripheral( peripheral,
                    status );
        }
    };

    private final BluetoothPeripheralCallback peripheralCallback = new BluetoothPeripheralCallback() {
        @Override
        public void onServicesDiscovered( BluetoothPeripheral peripheral ) {
            super.onServicesDiscovered( peripheral );
            for ( BluetoothGattCharacteristic characteristic : peripheral.getService(
                    ESP_SERVICE_ID ).getCharacteristics() ) {
                Log.d( "test", String.valueOf( characteristic.getUuid() ) );
            }
            rssiCharacteristic = peripheral.getCharacteristic( ESP_SERVICE_ID,
                    RSSI_CHARACTERISTIC_ID );
            normalCharacteristic = peripheral.getCharacteristic( ESP_SERVICE_ID,
                    ESP_CHARACTERISTIC_ID );

            getCurrentImplementer().getPeripheralCallback().onServicesDiscovered( peripheral );
        }

        @Override
        public void onCharacteristicUpdate( BluetoothPeripheral peripheral, byte[] value,
                BluetoothGattCharacteristic characteristic, int status ) {
            super.onCharacteristicUpdate( peripheral, value, characteristic, status );
            getCurrentImplementer().getPeripheralCallback().onCharacteristicUpdate( peripheral,
                    value, characteristic, status );
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
            action = connected ?
                    NavGraphDirections.actionGlobalDeviceInfoFragment() :
                    NavGraphDirections.actionGlobalDeviceConnectFragment();
        } else if ( item.getItemId() == R.id.graphs ) {
            //Move to graphs
            action = NavGraphDirections.actionGlobalGraphFragment();
        } else if ( item.getItemId() == R.id.settings ) {
            // Move to settings
            action = NavGraphDirections.actionGlobalSettingsFragment();
        }
        if (action != null) {
            Navigation.findNavController( this, R.id.nav_host_fragment ).navigate( action );
            return true;
        }
        return false;
    };
}