package com.github.rywilliamson.configurator.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.github.rywilliamson.configurator.Database.DAO.DeviceDao;
import com.github.rywilliamson.configurator.Database.DAO.InteractionDao;
import com.github.rywilliamson.configurator.Database.DAO.RSSIDao;
import com.github.rywilliamson.configurator.Database.Entity.Device;
import com.github.rywilliamson.configurator.Database.Entity.Interaction;
import com.github.rywilliamson.configurator.Database.Entity.RSSI;

@Database( entities = { Device.class, Interaction.class, RSSI.class }, exportSchema = false, version = 1 )
public abstract class RSSIDatabase extends RoomDatabase {
    private static final String DB_NAME = "rssi_db";
    private static RSSIDatabase instance;

    public static synchronized RSSIDatabase getInstance( Context context ) {
        if ( instance == null ) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    RSSIDatabase.class,
                    DB_NAME
                ).fallbackToDestructiveMigration().build();
        }
        return instance;
    }

    public abstract DeviceDao deviceDao();
    public abstract InteractionDao interactionDao();
    public abstract RSSIDao rssiDao();
}
