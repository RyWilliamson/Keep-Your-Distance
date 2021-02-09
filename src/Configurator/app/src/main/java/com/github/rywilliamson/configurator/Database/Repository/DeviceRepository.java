package com.github.rywilliamson.configurator.Database.Repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.github.rywilliamson.configurator.Database.DAO.DeviceDao;
import com.github.rywilliamson.configurator.Database.Entity.Device;
import com.github.rywilliamson.configurator.Database.RSSIDatabase;

import java.util.List;

public class DeviceRepository {

    private final DeviceDao mDeviceDao;
    private final LiveData<List<Device>> mAllDevices;

    public DeviceRepository( Application application ) {
        RSSIDatabase db = RSSIDatabase.getInstance( application );
        mDeviceDao = db.deviceDao();
        mAllDevices = mDeviceDao.getDeviceList();
    }

    public LiveData<List<Device>> getAllDevices() {
        return mAllDevices;
    }

    public Device getDevice( String id ) {
        return mDeviceDao.getDeviceByID( id );
    }

    public List<Device> getUsedDevices() {
        return mDeviceDao.getUsedDevices();
    }

    public void insert( Device device ) {
        RSSIDatabase.databaseWriteExecutor.execute( () -> {
            Device qDevice = mDeviceDao.getDeviceByID( device.macAddress );
            if ( qDevice == null ) {
                mDeviceDao.insertDevice( device );
            } else {
                qDevice.times_connected++;
                mDeviceDao.updateDevice( qDevice );
            }
        } );
    }

    public void updateAlias( String id, String alias ) {
        RSSIDatabase.databaseWriteExecutor.execute( () -> {
            mDeviceDao.updateAlias( id, alias );
        } );
    }

    public void updateTimesConnected( String id, int times_connected ) {
        RSSIDatabase.databaseWriteExecutor.execute( () -> {
            mDeviceDao.updateTimesConnected( id, times_connected );
        } );
    }

    public void insertScanned( Device device ) {
        RSSIDatabase.databaseWriteExecutor.execute( () -> {
            Device qDevice = mDeviceDao.getDeviceByID( device.macAddress );
            if ( qDevice == null ) {
                mDeviceDao.insertDevice( device );
            }
        } );
    }

    public void deleteByMac( String receiver ) {
        RSSIDatabase.databaseWriteExecutor.execute( () -> {
            mDeviceDao.deleteDeviceByMac( receiver );
        } );
    }

}
