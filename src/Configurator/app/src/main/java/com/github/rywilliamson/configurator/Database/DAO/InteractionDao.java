package com.github.rywilliamson.configurator.Database.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.TypeConverters;
import androidx.room.Update;

import com.github.rywilliamson.configurator.Database.Converters;
import com.github.rywilliamson.configurator.Database.Entity.Interaction;

import java.util.Date;
import java.util.List;

@Dao
public interface InteractionDao {
    @Query( "SELECT * FROM interaction" )
    LiveData<List<Interaction>> getInteractionList();

    @Query( "SELECT * FROM interaction WHERE sender = :sender AND receiver = :receiver AND start_time = :start" )
    Interaction getInteractionByID( String sender, String receiver, Date start );

    @Query( "SELECT COUNT(receiver) FROM interaction WHERE receiver = :receiver" )
    LiveData<Integer> getInteractionCountForReceiver( String receiver );

    @Query( "SELECT * FROM interaction WHERE receiver = :receiver" )
    List<Interaction> getInteractionsForReceiver( String receiver );

    @TypeConverters( Converters.class )
    @Query( "SELECT COUNT(*) FROM interaction WHERE receiver = :receiver AND" +
            "(end_time - start_time) >= :start_range AND " +
            "(end_time - start_time) < :end_range" )
    Integer getInteractionsInRange( String receiver, long start_range, long end_range );

    @Query( "SELECT COUNT(*) FROM interaction WHERE receiver = :receiver" )
    Integer getInteractionCountByReceiverNow( String receiver );

    @TypeConverters( Converters.class )
    @Query( "SELECT MAX(start_time) FROM interaction WHERE receiver = :receiver" )
    Date getLastDate( String receiver );

    @TypeConverters( Converters.class )
    @Query( "SELECT MIN(start_time) FROM interaction WHERE receiver = :receiver" )
    Date getFirstDate( String receiver );

    @TypeConverters( Converters.class )
    @Query( "SELECT COUNT(receiver) FROM interaction WHERE receiver = :receiver AND start_time > :start AND start_time < :end" )
    Integer getInteractionCountByDate( String receiver, Date start, Date end );

    @Insert
    void insertInteraction( Interaction interaction );

    @Update
    void updateInteraction( Interaction interaction );

    @Delete
    void deleteInteraction( Interaction interaction );

    @Query( "DELETE FROM interaction WHERE receiver = :receiver" )
    void deleteInteractionByReceiver( String receiver );
}
