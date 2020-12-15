package com.github.rywilliamson.configurator.Database.Repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.github.rywilliamson.configurator.Database.DAO.InteractionDao;
import com.github.rywilliamson.configurator.Database.Entity.Interaction;
import com.github.rywilliamson.configurator.Database.RSSIDatabase;

import java.util.List;

public class InteractionRepository {

    private final InteractionDao mInteractionDao;
    private final LiveData<List<Interaction>> mAllInteractions;

    public InteractionRepository( Application application) {
        RSSIDatabase db = RSSIDatabase.getInstance( application );
        mInteractionDao = db.interactionDao();
        mAllInteractions = mInteractionDao.getInteractionList();
    }

    public LiveData<List<Interaction>> getAllInteractions() {
        return mAllInteractions;
    }

    public void insert(Interaction interaction) {
        RSSIDatabase.databaseWriteExecutor.execute(() -> {
            mInteractionDao.insertInteraction( interaction );
        } );
    }

}
