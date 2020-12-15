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

    public void insert(Device device) {
        RSSIDatabase.databaseWriteExecutor.execute(() -> {
            mDeviceDao.insertDevice( device );
        } );
    }

}
