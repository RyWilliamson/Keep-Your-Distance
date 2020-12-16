package com.github.rywilliamson.configurator.Database;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.github.rywilliamson.configurator.Database.Entity.Device;
import com.github.rywilliamson.configurator.Database.Entity.Interaction;
import com.github.rywilliamson.configurator.Database.Entity.RSSI;
import com.github.rywilliamson.configurator.Database.Repository.DeviceRepository;
import com.github.rywilliamson.configurator.Database.Repository.InteractionRepository;
import com.github.rywilliamson.configurator.Database.Repository.RSSIRepository;

import java.util.List;

public class DatabaseViewModel extends AndroidViewModel {

    private final DeviceRepository mDeviceRepository;
    private final InteractionRepository mInteractionRepository;
    private final RSSIRepository mRSSIRepository;

    private final LiveData<List<Device>> mAllDevices;

    private final LiveData<List<Interaction>> mAllInteractions;
    private final LiveData<List<RSSI>> mAllRSSI;

    public DatabaseViewModel( Application application ) {
        super( application );
        mDeviceRepository = new DeviceRepository( application );
        mInteractionRepository = new InteractionRepository( application );
        mRSSIRepository = new RSSIRepository( application );

        mAllDevices = mDeviceRepository.getAllDevices();
        mAllInteractions = mInteractionRepository.getAllInteractions();
        mAllRSSI = mRSSIRepository.getAllRSSI();
    }

    // Reads for Device
    public LiveData<List<Device>> getAllDevices() {
        return mAllDevices;
    }

    public Device getDevice( String id ) {
        return mDeviceRepository.getDevice( id );
    }

    // Reads for Interactions
    public LiveData<List<Interaction>> getAllInteractions() {
        return mAllInteractions;
    }

    // Reads for RSSI
    public LiveData<List<RSSI>> getAllRSSI() {
        return mAllRSSI;
    }

    // Writes for
    public void insert( Device device ) {
        mDeviceRepository.insert( device );
    }

    public void insertScanned(Device device) {
        mDeviceRepository.insertScanned( device );
    }

    public void insert( Interaction interaction ) {
        mInteractionRepository.insert( interaction );
    }

    public void insert( RSSI rssi ) {
        mRSSIRepository.insert( rssi );
    }
}
