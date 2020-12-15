package com.github.rywilliamson.configurator.Database.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.github.rywilliamson.configurator.Database.Entity.Device;

import java.util.List;

@Dao
public interface DeviceDao {

    @Query( "SELECT * FROM device" )
    LiveData<List<Device>> getDeviceList();

    @Insert
    void insertDevice( Device device );

    @Update
    void updateDevice( Device device );

    @Delete
    void deleteDevice( Device device );
}
