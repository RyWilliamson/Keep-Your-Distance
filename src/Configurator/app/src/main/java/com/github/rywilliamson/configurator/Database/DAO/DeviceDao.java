package com.github.rywilliamson.configurator.Database.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.github.rywilliamson.configurator.Database.Entity.Device;

import java.util.List;

@Dao
public interface DeviceDao {

    @Query( "SELECT * FROM device" )
    LiveData<List<Device>> getDeviceList();

    @Query( "SELECT * FROM device WHERE times_connected > 0" )
    List<Device> getUsedDevices();

    @Query( "SELECT * FROM device WHERE mac_address = :id" )
    Device getDeviceByID( String id );

    @Insert
    void insertDevice( Device device );

    @Query( "UPDATE device SET alias = :alias WHERE mac_address=:id" )
    void updateAlias( String id, String alias );

    @Query( "UPDATE device SET times_connected = :times_connected WHERE mac_address=:id" )
    void updateTimesConnected( String id, int times_connected );

    @Update(onConflict = OnConflictStrategy.REPLACE )
    void updateDevice( Device device );

    @Delete
    void deleteDevice( Device device );

    @Query( "DELETE FROM Device WHERE mac_address = :receiver" )
    void deleteDeviceByMac( String receiver );
}
