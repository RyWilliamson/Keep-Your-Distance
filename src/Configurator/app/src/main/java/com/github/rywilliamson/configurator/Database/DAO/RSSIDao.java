package com.github.rywilliamson.configurator.Database.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.github.rywilliamson.configurator.Database.Entity.RSSI;

import java.util.Date;
import java.util.List;

@Dao
public interface RSSIDao {
    @Query( "SELECT * FROM rssi" )
    LiveData<List<RSSI>> getRSSIList();

    @Query( "SELECT * FROM rssi WHERE sender_ref = :sender AND receiver_ref = :receiver AND start_time_ref = :start AND timestamp = :timestamp" )
    RSSI getRSSIByID(String sender, String receiver, Date start, Date timestamp);

    @Insert
    void insertRSSI( RSSI rssi );

    @Update
    void updateRSSI( RSSI rssi );

    @Delete
    void deleteRSSI( RSSI rssi );
}
