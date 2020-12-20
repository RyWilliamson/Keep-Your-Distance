package com.github.rywilliamson.configurator.Database.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.github.rywilliamson.configurator.Database.Entity.Interaction;

import java.util.Date;
import java.util.List;

@Dao
public interface InteractionDao {
    @Query( "SELECT * FROM interaction" )
    LiveData<List<Interaction>> getInteractionList();

    @Query( "SELECT * FROM interaction WHERE sender = :sender AND receiver = :receiver AND start_time = :start" )
    Interaction getInteractionByID(String sender, String receiver, Date start);

    @Query ("SELECT COUNT(receiver) FROM interaction WHERE receiver = :receiver")
    LiveData<Integer> getInteractionCountForReceiver(String receiver);

    @Insert
    void insertInteraction( Interaction interaction );

    @Update
    void updateInteraction( Interaction interaction );

    @Delete
    void deleteInteraction( Interaction interaction );

    @Query( "DELETE FROM interaction WHERE receiver = :receiver" )
    void deleteInteractionByReceiver( String receiver );
}
