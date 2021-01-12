package com.github.rywilliamson.configurator.Database.Repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.github.rywilliamson.configurator.Database.DAO.InteractionDao;
import com.github.rywilliamson.configurator.Database.Entity.Interaction;
import com.github.rywilliamson.configurator.Database.RSSIDatabase;

import java.util.Date;
import java.util.List;

public class InteractionRepository {

    private final InteractionDao mInteractionDao;
    private final LiveData<List<Interaction>> mAllInteractions;
    private LiveData<Integer> mInteractionCount;

    public InteractionRepository( Application application ) {
        RSSIDatabase db = RSSIDatabase.getInstance( application );
        mInteractionDao = db.interactionDao();
        mAllInteractions = mInteractionDao.getInteractionList();
        mInteractionCount = mInteractionDao.getInteractionCountForReceiver( "" );
    }

    public LiveData<List<Interaction>> getAllInteractions() {
        return mAllInteractions;
    }

    public LiveData<Integer> getInteractionCount() {
        return mInteractionCount;
    }

    public List<Interaction> getInteractionsForReceiver( String receiver ) {
        return mInteractionDao.getInteractionsForReceiver( receiver );
    }

    public Date getLastDate(String receiver) {
        return mInteractionDao.getLastDate( receiver );
    }

    public Date getFirstDate(String receiver) {
        return mInteractionDao.getFirstDate( receiver );
    }

    public void setInteractionCountReceiver(String receiver) {
        mInteractionCount = mInteractionDao.getInteractionCountForReceiver( receiver );
    }

    public Interaction getInteractionByID( String sender, String receiver, Date start ) {
        return mInteractionDao.getInteractionByID( sender, receiver, start );
    }

    public Integer getInteractionCountByDate(String receiver, Date start, Date end) {
        return mInteractionDao.getInteractionCountByDate( receiver, start, end );
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

    public void deleteByReceiver( String receiver ) {
        RSSIDatabase.databaseWriteExecutor.execute( () -> {
            mInteractionDao.deleteInteractionByReceiver( receiver );
        } );
    }

}
