package com.github.rywilliamson.configurator.Utils;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.welie.blessed.BluetoothCentral;
import com.welie.blessed.BluetoothCentralCallback;
import com.welie.blessed.BluetoothPeripheral;
import com.welie.blessed.BluetoothPeripheralCallback;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static android.bluetooth.BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT;
import static com.github.rywilliamson.configurator.Utils.CustomCharacteristics.CONFIG_CHARACTERISTIC_ID;
import static com.github.rywilliamson.configurator.Utils.CustomCharacteristics.CONFIG_SERVICE_ID;
import static com.github.rywilliamson.configurator.Utils.CustomCharacteristics.CONN_CHARACTERISTIC_ID;
import static com.github.rywilliamson.configurator.Utils.CustomCharacteristics.HEARTBEAT_SERVICE_ID;
import static com.github.rywilliamson.configurator.Utils.CustomCharacteristics.RSSI_CHARACTERISTIC_ID;
import static com.github.rywilliamson.configurator.Utils.CustomCharacteristics.RSSI_SERVICE_ID;

public class BluetoothHandler {

    private final BluetoothCentral central;
    private BluetoothPeripheral BLEPeripheral;
    private BluetoothGattCharacteristic rssiCharacteristic;
    private BluetoothGattCharacteristic connectionCharacteristic;
    private BluetoothGattCharacteristic configCharacteristic;
    private final List<BluetoothPeripheral> scannedPeripherals;
    private final SharedPreferences prefs;
    private Profile.ProfileEnum profile;
    private final HashMap<String, InteractionTimeout> interactionTimeMap;
    private boolean connected;
    private final int SCAN_TIMEOUT = 3000; // Milliseconds
    private final int INTERACTION_TIMEOUT = 3000; // Milliseconds

    public BluetoothHandler( Activity activity, BluetoothCentralCallback callback ) {
        central = new BluetoothCentral( activity, callback, new Handler(
                Looper.getMainLooper() ) );
        scannedPeripherals = new ArrayList<>();
        prefs = activity.getSharedPreferences( Keys.PREFS, Context.MODE_PRIVATE );
        interactionTimeMap = new HashMap<>();
        this.profile = Profile.getFromPreferences( activity );
    }

    public void checkBLEPermissions( Activity activity ) {
        if ( ContextCompat.checkSelfPermission( activity,
                Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( activity,
                    new String[]{ Manifest.permission.ACCESS_FINE_LOCATION },
                    Keys.REQUEST_FINE_LOCATION );
        }
    }

    public String getPrevMac() {
        return prefs.getString( Keys.PREV_MAC, "" );
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

    public BluetoothGattCharacteristic getConfigCharacteristic() {
        return configCharacteristic;
    }

    public void scan( Activity activity ) {
        checkBLEPermissions( activity );
        scannedPeripherals.clear();
        central.scanForPeripheralsWithNames( new String[]{ "ESP32" } );
        new Handler( Looper.getMainLooper() ).postDelayed( central::stopScan, SCAN_TIMEOUT );
    }

    public boolean addPeripheral( BluetoothPeripheral peripheral ) {
        if ( scannedPeripherals.contains( peripheral ) ) {
            return false;
        }
        scannedPeripherals.add( peripheral );
        return true;
    }

    public void onConnect( BluetoothPeripheral peripheral ) {
        this.prefs.edit().putString( Keys.PREV_MAC, peripheral.getAddress() ).apply();
        this.BLEPeripheral = peripheral;
        connected = true;
    }

    public void directConnect( String UUID, BluetoothPeripheralCallback callback ) {
        central.connectPeripheral( central.getPeripheral( UUID ), callback );
    }

    public void disconnect() {
        this.BLEPeripheral.setNotify( rssiCharacteristic, false );
        central.cancelConnection( BLEPeripheral );
    }

    public void clearForDisconnect() {
        this.BLEPeripheral = null;
        this.rssiCharacteristic = null;
        this.connectionCharacteristic = null;
        this.connected = false;
        clearMap();
    }

    private void clearMap() {
        interactionTimeMap.clear();
    }

    public void sendConfig() {
        ByteBuffer packetBuffer = ByteBuffer.allocate( 12 );
        packetBuffer.putFloat( getDistance() );
        packetBuffer.putInt( getMeasuredPower() );
        packetBuffer.putInt( getEnvironmentVar() );
        boolean written = BLEPeripheral.writeCharacteristic( configCharacteristic, packetBuffer.array(),
                WRITE_TYPE_DEFAULT );
    }

    public void setupServices( BluetoothPeripheral peripheral ) {
        if ( verifyServices( peripheral ) ) {
            this.rssiCharacteristic = peripheral.getCharacteristic( RSSI_SERVICE_ID, RSSI_CHARACTERISTIC_ID );
            this.connectionCharacteristic = peripheral.getCharacteristic( HEARTBEAT_SERVICE_ID,
                    CONN_CHARACTERISTIC_ID );
            this.configCharacteristic = peripheral.getCharacteristic( CONFIG_SERVICE_ID, CONFIG_CHARACTERISTIC_ID );

            this.BLEPeripheral.setNotify( rssiCharacteristic, true );
        }
    }

    private boolean verifyServices( BluetoothPeripheral peripheral ) {
        // Can only implement this once device is working with ALL services
        return true;
    }

    public Date insertStartTimeAndGet( String sender, String receiver, Date startTime ) {
        String key = sender + receiver;
        if ( !interactionTimeMap.containsKey( key ) ) {
            interactionTimeMap.put( key, new InteractionTimeout( this, key, startTime, INTERACTION_TIMEOUT ) );
        } else {
            interactionTimeMap.get( key ).setLapsed( false );
        }
        return interactionTimeMap.get( key ).getStartTime();
    }

    public void removeStartTime( String key ) {
        interactionTimeMap.remove( key );
    }

    public void setProfile( Activity activity, Profile.ProfileEnum prof ) {
        profile = prof;
        Profile.setProfilePreference( activity, profile );
    }

    public void setDistance( float distance ) {
        prefs.edit().putFloat( Keys.DISTANCE, distance ).apply();
    }

    // Distance in metres
    public float calculateDistance( int rssi ) {
        //return pow(10, (measuredPower - rssi) / (10 * environment));
        int mp = getMeasuredPower();
        int ev = getEnvironmentVar();
        return (float) Math.pow( 10.0, ( mp - rssi ) / ( 10.0 * ev ) );
    }

    public int getMeasuredPower() {
        return profile.getMeasuredPower();
    }

    public int getEnvironmentVar() {
        return profile.getEnvironmentVar();
    }

    public float getDistance() {
        return prefs.getFloat( Keys.DISTANCE, (float) 1.0 );
    }

    public Profile.ProfileEnum getProfile() {
        return profile;
    }
}
