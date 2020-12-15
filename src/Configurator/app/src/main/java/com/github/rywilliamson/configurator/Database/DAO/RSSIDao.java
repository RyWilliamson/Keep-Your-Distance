package com.github.rywilliamson.configurator.Database.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.github.rywilliamson.configurator.Database.Entity.RSSI;

import java.util.List;

@Dao
public interface RSSIDao {
    @Query( "Select * from rssi" )
    List<RSSI> getRSSIList();

    @Insert
    void insertRSSI( RSSI rssi );

    @Update
    void updateRSSI( RSSI rssi );

    @Delete
    void deleteRSSI( RSSI rssi );
}
