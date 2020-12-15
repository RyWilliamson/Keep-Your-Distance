package com.github.rywilliamson.configurator.Database.Repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.github.rywilliamson.configurator.Database.DAO.RSSIDao;
import com.github.rywilliamson.configurator.Database.Entity.RSSI;
import com.github.rywilliamson.configurator.Database.RSSIDatabase;

import java.util.List;

public class RSSIRepository {

    private final RSSIDao mRSSIDao;
    private final LiveData<List<RSSI>> mAllRSSI;

    public RSSIRepository( Application application) {
        RSSIDatabase db = RSSIDatabase.getInstance( application );
        mRSSIDao = db.rssiDao();
        mAllRSSI = mRSSIDao.getRSSIList();
    }

    public LiveData<List<RSSI>> getAllRSSI() {
        return mAllRSSI;
    }

    public void insert(RSSI rssi) {
        RSSIDatabase.databaseWriteExecutor.execute(() -> {
            mRSSIDao.insertRSSI( rssi );
        } );
    }

}
