package com.github.rywilliamson.configurator.Database.Repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.github.rywilliamson.configurator.Database.DAO.InteractionDao;
import com.github.rywilliamson.configurator.Database.DAO.RSSIDao;
import com.github.rywilliamson.configurator.Database.Entity.Interaction;
import com.github.rywilliamson.configurator.Database.Entity.RSSI;
import com.github.rywilliamson.configurator.Database.RSSIDatabase;

import java.util.Date;
import java.util.List;

public class RSSIRepository {

    private final RSSIDao mRSSIDao;
    private final LiveData<List<RSSI>> mAllRSSI;

    public RSSIRepository( Application application ) {
        RSSIDatabase db = RSSIDatabase.getInstance( application );
        mRSSIDao = db.rssiDao();
        mAllRSSI = mRSSIDao.getRSSIList();
    }

    public LiveData<List<RSSI>> getAllRSSI() {
        return mAllRSSI;
    }

    public RSSI getRSSIByID(String sender, String receiver, Date start, Date timestamp) {
        return mRSSIDao.getRSSIByID( sender, receiver, start, timestamp );
    }

    public List<RSSI> getRSSIForReceiver( String receiver ) {
        return mRSSIDao.getRSSIForReceiver( receiver );
    }

    public Integer getCountAverageDistanceInRange(String receiver, float start_range, float end_range) {
        return mRSSIDao.getCountAverageDistanceInRange( receiver, start_range, end_range );
    }

    public RSSI getRSSIByID( Interaction interaction, Date timestamp) {
        return mRSSIDao.getRSSIByID( interaction.sender, interaction.receiver, interaction.startTime, timestamp );
    }

    public void insert( RSSI rssi ) {
        RSSIDatabase.databaseWriteExecutor.execute( () -> {
            mRSSIDao.insertRSSI( rssi );
        } );
    }

    public void insert(RSSI rssi, InteractionRepository repo) {
        RSSIDatabase.databaseWriteExecutor.execute( () -> {
            repo.insertHelper(
                    new Interaction(rssi.senderRef, rssi.receiverRef, rssi.startTimeRef, rssi.timestamp)
            );
            mRSSIDao.insertRSSI( rssi );
        } );
    }

    public void deleteByReceiver( String receiver ) {
        RSSIDatabase.databaseWriteExecutor.execute( () -> {
            mRSSIDao.deleteRSSIByReceiver( receiver );
        } );
    }

}
