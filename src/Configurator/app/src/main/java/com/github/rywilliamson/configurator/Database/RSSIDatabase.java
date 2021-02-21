package com.github.rywilliamson.configurator.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.github.rywilliamson.configurator.Database.DAO.DeviceDao;
import com.github.rywilliamson.configurator.Database.DAO.InteractionDao;
import com.github.rywilliamson.configurator.Database.DAO.RSSIDao;
import com.github.rywilliamson.configurator.Database.Entity.Device;
import com.github.rywilliamson.configurator.Database.Entity.Interaction;
import com.github.rywilliamson.configurator.Database.Entity.RSSI;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database( entities = { Device.class, Interaction.class, RSSI.class }, exportSchema = false, version = 1 )
@TypeConverters( Converters.class )
public abstract class RSSIDatabase extends RoomDatabase {
    private static final String DB_NAME = "rssi_db";
    private static RSSIDatabase instance;
    private static final int NO_THREADS = 1;
    public static ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool( NO_THREADS );
    public static ExecutorService databaseGetExecutor = Executors.newSingleThreadExecutor();

    // Uses Singleton pattern
    public static synchronized RSSIDatabase getInstance( Context context ) {
        if ( instance == null ) {

            // Threading lock
            synchronized ( RSSIDatabase.class ) {
                // Data race check
                if ( instance == null ) {
                    instance = Room.databaseBuilder( context.getApplicationContext(), RSSIDatabase.class, DB_NAME )
                            .fallbackToDestructiveMigration().build();
                }
            }
        }
        return instance;
    }

    public static synchronized void setInstance( RSSIDatabase database ) {
        instance = database;
    }

    public abstract DeviceDao deviceDao();

    public abstract InteractionDao interactionDao();

    public abstract RSSIDao rssiDao();
}
