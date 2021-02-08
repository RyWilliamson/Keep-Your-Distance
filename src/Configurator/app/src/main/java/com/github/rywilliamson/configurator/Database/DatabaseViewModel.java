package com.github.rywilliamson.configurator.Database;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.github.rywilliamson.configurator.Database.Entity.Device;
import com.github.rywilliamson.configurator.Database.Entity.Interaction;
import com.github.rywilliamson.configurator.Database.Entity.RSSI;
import com.github.rywilliamson.configurator.Database.Repository.DeviceRepository;
import com.github.rywilliamson.configurator.Database.Repository.InteractionRepository;
import com.github.rywilliamson.configurator.Database.Repository.RSSIRepository;

import java.util.Date;
import java.util.List;

public class DatabaseViewModel extends AndroidViewModel {

    private final DeviceRepository mDeviceRepository;
    private final InteractionRepository mInteractionRepository;
    private final RSSIRepository mRSSIRepository;

    private final LiveData<List<Device>> mAllDevices;

    private final LiveData<List<Interaction>> mAllInteractions;
    private LiveData<Integer> mInteractionCount;

    private final LiveData<List<RSSI>> mAllRSSI;

    public DatabaseViewModel( Application application ) {
        super( application );
        mDeviceRepository = new DeviceRepository( application );
        mInteractionRepository = new InteractionRepository( application );
        mRSSIRepository = new RSSIRepository( application );

        mAllDevices = mDeviceRepository.getAllDevices();
        mAllInteractions = mInteractionRepository.getAllInteractions();
        mAllRSSI = mRSSIRepository.getAllRSSI();
        mInteractionRepository.setInteractionCountReceiver( "" );
        mInteractionCount = mInteractionRepository.getInteractionCount();
    }

    // Reads for Device
    public LiveData<List<Device>> getAllDevices() {
        return mAllDevices;
    }

    public Device getDevice( String id ) {
        return mDeviceRepository.getDevice( id );
    }

    public List<Device> getUsedDevices() {
        return mDeviceRepository.getUsedDevices();
    }

    // Reads for Interactions
    public LiveData<List<Interaction>> getAllInteractions() {
        return mAllInteractions;
    }

    public Interaction getInteraction( String sender, String receiver, Date start ) {
        return mInteractionRepository.getInteractionByID( sender, receiver, start );
    }

    public List<Interaction> getInteractionsForReceiver( String id ) {
        return mInteractionRepository.getInteractionsForReceiver( id );
    }

    public Integer getInteractionCountByReceiverNow( String receiver ) {
        return mInteractionRepository.getInteractionCountByReceiverNow( receiver );
    }

    public Date getLastInteractionDate( String id ) {
        return mInteractionRepository.getLastDate( id );
    }

    public Date getFirstInteractionDate( String id ) {
        return mInteractionRepository.getFirstDate( id );
    }

    public Integer getInteractionCountByDate( String id, Date start, Date end ) {
        return mInteractionRepository.getInteractionCountByDate( id, start, end );
    }

    public Integer getInteractionsInRange( String id, long start_range, long end_range ) {
        return mInteractionRepository.getInteractionsInRange( id, start_range, end_range );
    }

    public LiveData<Integer> getConnectedInteractionCount() {
        return mInteractionCount;
    }

    // Reads for RSSI
    public LiveData<List<RSSI>> getAllRSSI() {
        return mAllRSSI;
    }

    public RSSI getRSSI( Interaction interaction, Date timestamp ) {
        return mRSSIRepository.getRSSIByID( interaction, timestamp );
    }

    public Integer getCountAverageDistanceInRange(String receiver, float start_range, float end_range) {
        return mRSSIRepository.getCountAverageDistanceInRange( receiver, start_range, end_range );
    }

    public List<RSSI> getRSSIForReceiver( String receiver ) {
        return mRSSIRepository.getRSSIForReceiver( receiver );
    }

    // Writes for Device
    public void insert( Device device ) {
        mDeviceRepository.insert( device );
    }

    public void insertScanned( Device device ) {
        mDeviceRepository.insertScanned( device );
    }

    public void updateAlias( String id, String alias ) {
        mDeviceRepository.updateAlias( id, alias );
    }

    // Writes for Insert
    public void insert( Interaction interaction ) {
        mInteractionRepository.insert( interaction );
    }

    public void setInteractionCountReceiver( String receiver ) {
        mInteractionRepository.setInteractionCountReceiver( receiver );
        mInteractionCount = mInteractionRepository.getInteractionCount();
    }

    // Writes for RSSI
    public void insert( RSSI rssi, boolean linked ) {
        if ( linked ) {
            mRSSIRepository.insert( rssi, mInteractionRepository );
        } else {
            mRSSIRepository.insert( rssi );
        }
    }

    // Deletes
    public void clearReceiver( String receiver ) {
        Log.d("test", "Clear mac: " + receiver);
        mRSSIRepository.deleteByReceiver( receiver );
        mInteractionRepository.deleteByReceiver( receiver );
        mDeviceRepository.deleteByMac( receiver );
    }
}
