package com.github.rywilliamson.configurator.Utils;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;

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
import static com.github.rywilliamson.configurator.Utils.CustomCharacteristics.BULK_ACK_CHARACTERISTIC_ID;
import static com.github.rywilliamson.configurator.Utils.CustomCharacteristics.BULK_CHARACTERISTIC_ID;
import static com.github.rywilliamson.configurator.Utils.CustomCharacteristics.CONFIG_ACK_CHARACTERISTIC_ID;
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
    private BluetoothGattCharacteristic bulkCharacteristic;
    private BluetoothGattCharacteristic bulkACKCharacteristic;
    private BluetoothGattCharacteristic connectionCharacteristic;
    private BluetoothGattCharacteristic configCharacteristic;
    private BluetoothGattCharacteristic configACKCharacteristic;
    private final List<BluetoothPeripheral> scannedPeripherals;
    private final SharedPreferences prefs;
    private Profile.ProfileEnum profile;
    private final HashMap<String, TimeoutData> interactionTimeMap;
    private final HashMap<String, TimeoutData> bulkTimeMap;
    private boolean connected;
    private boolean synced;
    private boolean manual_disconnect = false;
    private final int SCAN_TIMEOUT = 3000; // Milliseconds
    private final int INTERACTION_TIMEOUT = 3000; // Milliseconds

    public BluetoothHandler( Activity activity, BluetoothCentralCallback callback ) {
        this( activity, callback, activity.getSharedPreferences( Keys.PREFS, Context.MODE_PRIVATE ) );
    }

    public BluetoothHandler( Activity activity, BluetoothCentralCallback callback, SharedPreferences prefs ) {
        central = new BluetoothCentral( activity, callback, new Handler(
                Looper.getMainLooper() ) );
        scannedPeripherals = new ArrayList<>();
        this.prefs = prefs;
        interactionTimeMap = new HashMap<>();
        bulkTimeMap = new HashMap<>();
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

    public BluetoothGattCharacteristic getBulkCharacteristic() {
        return bulkCharacteristic;
    }

    public BluetoothGattCharacteristic getConfigACKCharacteristic() {
        return configACKCharacteristic;
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
        manual_disconnect = false;
        connected = true;
    }

    public void directConnect( String UUID, BluetoothPeripheralCallback callback ) {
//        central.connectPeripheral( central.getPeripheral( UUID ), callback );
        central.autoConnectPeripheral( central.getPeripheral( UUID ), callback );
    }

    public void disconnect() {
        this.BLEPeripheral.setNotify( rssiCharacteristic, false );
        this.BLEPeripheral.setNotify( bulkCharacteristic, false );
        this.BLEPeripheral.setNotify( configACKCharacteristic, false );
        manual_disconnect = true;
        central.cancelConnection( BLEPeripheral );
    }

    public void clearForDisconnect() {
        this.BLEPeripheral = null;
        this.rssiCharacteristic = null;
        this.bulkCharacteristic = null;
        this.bulkACKCharacteristic = null;
        this.connectionCharacteristic = null;
        this.configCharacteristic = null;
        this.configACKCharacteristic = null;
        this.connected = false;
        clearMap();
    }

    private void clearMap() {
        interactionTimeMap.clear();
        bulkTimeMap.clear();
    }

    public ByteBuffer setupConfigByteBuffer() {
        ByteBuffer packetBuffer = ByteBuffer.allocate( 12 );
        packetBuffer.putFloat( getDistance() );
        packetBuffer.putInt( getMeasuredPower() );
        packetBuffer.putInt( getEnvironmentVar() );
        return packetBuffer;
    }

    public void sendConfig() {
        ByteBuffer packetBuffer = setupConfigByteBuffer();
        boolean written = BLEPeripheral.writeCharacteristic( configCharacteristic, packetBuffer.array(),
                WRITE_TYPE_DEFAULT );
        setSynced( written );
    }

    public void sendBulkACK( String message ) {
        BLEPeripheral.writeCharacteristic( bulkACKCharacteristic, message.getBytes(), WRITE_TYPE_DEFAULT );
    }

    public void setupServices( BluetoothPeripheral peripheral ) {
        if ( verifyServices( peripheral ) ) {
            this.rssiCharacteristic = peripheral.getCharacteristic( RSSI_SERVICE_ID, RSSI_CHARACTERISTIC_ID );
            this.bulkCharacteristic = peripheral.getCharacteristic( RSSI_SERVICE_ID, BULK_CHARACTERISTIC_ID );
            this.bulkACKCharacteristic = peripheral.getCharacteristic( RSSI_SERVICE_ID, BULK_ACK_CHARACTERISTIC_ID );
            this.connectionCharacteristic = peripheral.getCharacteristic( HEARTBEAT_SERVICE_ID,
                    CONN_CHARACTERISTIC_ID );
            this.configCharacteristic = peripheral.getCharacteristic( CONFIG_SERVICE_ID, CONFIG_CHARACTERISTIC_ID );
            this.configACKCharacteristic = peripheral.getCharacteristic( CONFIG_SERVICE_ID,
                    CONFIG_ACK_CHARACTERISTIC_ID );

            this.BLEPeripheral.setNotify( rssiCharacteristic, true );
            this.BLEPeripheral.setNotify( bulkCharacteristic, true );
            this.BLEPeripheral.setNotify( configACKCharacteristic, true );
        }
    }

    private boolean verifyServices( BluetoothPeripheral peripheral ) {
        // Can only implement this once device is working with ALL services
        return true;
    }

    public Date insertStartTimeAndGet( String sender, String receiver, Date startTime ) {
        return insertStartTimeHelper( interactionTimeMap, sender, receiver, startTime );
    }

    public Date insertBulkStartTimeAndGet( String sender, String receiver, Date startTime ) {
        return insertStartTimeHelper( bulkTimeMap, sender, receiver, startTime );
    }

    // For testing
    public Date insertArbStartTimeAndGet( HashMap<String, TimeoutData> map, String sender, String receiver,
            Date startTime ) {
        return insertStartTimeHelper( map, sender, receiver, startTime );
    }

    private Date insertStartTimeHelper( HashMap<String, TimeoutData> map, String sender, String receiver,
            Date startTime ) {
        String key = sender + receiver;
        if ( !map.containsKey( key ) ) {
            map.put( key, new TimeoutData( startTime, startTime ) );
        } else {
            map.get( key ).updateTime( startTime );
        }
        return map.get( key ).getStartTime();
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

    public boolean isSynced() {
        return synced;
    }

    public void setSynced( boolean synced ) {
        this.synced = synced;
    }

    public boolean isManual_disconnect() {
        return manual_disconnect;
    }
}
