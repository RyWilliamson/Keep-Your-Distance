package com.github.rywilliamson.configurator.Database.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.github.rywilliamson.configurator.Database.Entity.Interaction;

import java.util.List;

@Dao
public interface InteractionDao {
    @Query( "SELECT * FROM interaction" )
    LiveData<List<Interaction>> getInteractionList();

    @Insert
    void insertInteraction( Interaction interaction );

    @Update
    void updateInteraction( Interaction interaction );

    @Delete
    void deleteInteraction( Interaction interaction );
}
