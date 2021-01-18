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

    @Query( "SELECT COUNT(*)" +
            "FROM (" +
            "    SELECT AVG(est_distance) AS avg_val" +
            "    FROM interaction AS i, rssi AS r" +
            "    WHERE i.receiver = :receiver AND i.receiver = r.receiver_ref AND  i.sender = r.sender_ref AND i.start_time = r.start_time_ref" +
            "    GROUP BY i.sender, i.receiver, i.start_time" +
            ")" +
            "WHERE avg_val >= :start_range AND avg_val < :end_range" )
    Integer getCountAverageDistanceInRange(String receiver, float start_range, float end_range);

    @Insert
    void insertRSSI( RSSI rssi );

    @Update
    void updateRSSI( RSSI rssi );

    @Delete
    void deleteRSSI( RSSI rssi );

    @Query( "DELETE FROM rssi WHERE receiver_ref = :receiver" )
    void deleteRSSIByReceiver( String receiver );
}
