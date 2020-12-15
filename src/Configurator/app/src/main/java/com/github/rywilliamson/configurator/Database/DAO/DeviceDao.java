package com.github.rywilliamson.configurator.Database.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.github.rywilliamson.configurator.Database.Entity.Device;

import java.util.List;

@Dao
public interface DeviceDao {

    @Query( "Select * from device" )
    List<Device> getDeviceList();

    @Insert
    void insertDevice( Device device );

    @Update
    void updateDevice( Device device );

    @Delete
    void deleteDevice( Device device );
}
