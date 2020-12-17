package com.github.rywilliamson.configurator.Database.Repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.github.rywilliamson.configurator.Database.DAO.InteractionDao;
import com.github.rywilliamson.configurator.Database.Entity.Interaction;
import com.github.rywilliamson.configurator.Database.RSSIDatabase;

import java.util.Date;
import java.util.List;

public class InteractionRepository {

    private final InteractionDao mInteractionDao;
    private final LiveData<List<Interaction>> mAllInteractions;

    public InteractionRepository( Application application ) {
        RSSIDatabase db = RSSIDatabase.getInstance( application );
        mInteractionDao = db.interactionDao();
        mAllInteractions = mInteractionDao.getInteractionList();
    }

    public LiveData<List<Interaction>> getAllInteractions() {
        return mAllInteractions;
    }

    public Interaction getInteractionByID( String sender, String receiver, Date start ) {
        return mInteractionDao.getInteractionByID( sender, receiver, start );
    }

    public void insert( Interaction interaction ) {
        RSSIDatabase.databaseWriteExecutor.execute( () -> {
            insertHelper( interaction );
        } );
    }

    public void insertHelper(Interaction interaction) {
        Interaction qInteraction = mInteractionDao.getInteractionByID( interaction.sender, interaction.receiver,
                interaction.startTime );
        if ( qInteraction == null ) {
            mInteractionDao.insertInteraction( interaction );
        } else {
            qInteraction.endTime = interaction.endTime;
            mInteractionDao.updateInteraction( qInteraction );
        }
    }

}
