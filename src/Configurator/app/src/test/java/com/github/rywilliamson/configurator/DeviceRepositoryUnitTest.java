package com.github.rywilliamson.configurator;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import com.github.rywilliamson.configurator.Activities.MainActivity;
import com.github.rywilliamson.configurator.Database.Entity.Device;
import com.github.rywilliamson.configurator.Database.RSSIDatabase;
import com.github.rywilliamson.configurator.Database.Repository.DeviceRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;

@RunWith( RobolectricTestRunner.class )
public class DeviceRepositoryUnitTest {

    private final ActivityScenario<MainActivity> scenario = ActivityScenario.launch( MainActivity.class );
    private final Context context = ApplicationProvider.getApplicationContext();
    private final RSSIDatabase database = Room.inMemoryDatabaseBuilder( context,
            RSSIDatabase.class ).allowMainThreadQueries().build();
    private DeviceRepository repo;

    @Before
    public void initialise() {
        scenario.onActivity( activity -> {
            RSSIDatabase.setInstance( database );
            repo = new DeviceRepository( activity.getApplication() );
        } );
    }

    @Test
    public void getDevice_nothingOnWrongID() {
        assertThat( repo.getDevice( "WRONG" ) ).isNull();
    }

    @Test
    public void getDevice_deviceOnRightID() {
        Device device = new Device( "RIGHT", "", 0 );
        database.deviceDao().insertDevice( device );
        assertThat( repo.getDevice( "RIGHT" ).macAddress ).isEqualTo( device.macAddress );
    }

    @Test
    public void insert_firstInsertWorks() {
        RSSIDatabase.databaseWriteExecutor = Executors.newFixedThreadPool( 1 );
        Device device = new Device( "id", "alias", 0 );
        repo.insert( device );
        RSSIDatabase.databaseWriteExecutor.shutdown();
        try {
            RSSIDatabase.databaseWriteExecutor.awaitTermination( 5000, TimeUnit.MILLISECONDS );
        } catch (InterruptedException e) {
            assertThat( true ).isFalse();
        }
        assertThat( repo.getDevice( "id" ).times_connected ).isEqualTo( 0 );
    }

    @Test
    public void insert_secondInsertUpdates() {
        RSSIDatabase.databaseWriteExecutor = Executors.newFixedThreadPool( 1 );
        Device device = new Device( "id", "alias", 0 );
        repo.insert( device );
        repo.insert( device );
        RSSIDatabase.databaseWriteExecutor.shutdown();
        try {
            RSSIDatabase.databaseWriteExecutor.awaitTermination( 5000, TimeUnit.MILLISECONDS );
        } catch (InterruptedException e) {
            assertThat( true ).isFalse();
        }
        assertThat( repo.getDevice( "id" ).times_connected ).isEqualTo( 1 );
    }

    @Test
    public void insert_separateInsertWorks() {
        RSSIDatabase.databaseWriteExecutor = Executors.newFixedThreadPool( 1 );
        Device device1 = new Device( "id1", "alias", 0 );
        Device device2 = new Device( "id2", "alias", 0 );
        repo.insert( device1 );
        repo.insert( device2 );
        RSSIDatabase.databaseWriteExecutor.shutdown();
        try {
            RSSIDatabase.databaseWriteExecutor.awaitTermination( 5000, TimeUnit.MILLISECONDS );
        } catch (InterruptedException e) {
            assertThat( true ).isFalse();
        }
        assertThat( repo.getDevice( "id1" ).times_connected ).isEqualTo( 0 );
        assertThat( repo.getDevice( "id2" ).times_connected ).isEqualTo( 0 );
    }

    @Test
    public void insertScanned_insertWorks() {
        RSSIDatabase.databaseWriteExecutor = Executors.newFixedThreadPool( 1 );
        Device device = new Device( "id", "alias", 0 );
        repo.insertScanned( device );
        Device device2 = new Device( "id", "alias", 50 );
        repo.insertScanned( device2 );
        RSSIDatabase.databaseWriteExecutor.shutdown();
        try {
            RSSIDatabase.databaseWriteExecutor.awaitTermination( 5000, TimeUnit.MILLISECONDS );
        } catch (InterruptedException e) {
            assertThat( true ).isFalse();
        }
        assertThat( repo.getDevice( "id" ).times_connected ).isEqualTo( 0 );
    }

    @Test
    public void updateAlias_validUpdateWorks() {
        RSSIDatabase.databaseWriteExecutor = Executors.newFixedThreadPool( 1 );
        Device device = new Device( "id", "alias", 0 );
        repo.insert( device );
        repo.updateAlias( "id", "alias2" );
        RSSIDatabase.databaseWriteExecutor.shutdown();
        try {
            RSSIDatabase.databaseWriteExecutor.awaitTermination( 5000, TimeUnit.MILLISECONDS );
        } catch (InterruptedException e) {
            assertThat( true ).isFalse();
        }
        assertThat( repo.getDevice( "id" ).alias ).isEqualTo( "alias2" );
    }

    @Test
    public void updateAlias_invalidUpdateDoesNotInsert() {
        RSSIDatabase.databaseWriteExecutor = Executors.newFixedThreadPool( 1 );
        repo.updateAlias( "id", "alias2" );
        RSSIDatabase.databaseWriteExecutor.shutdown();
        try {
            RSSIDatabase.databaseWriteExecutor.awaitTermination( 5000, TimeUnit.MILLISECONDS );
        } catch (InterruptedException e) {
            assertThat( true ).isFalse();
        }
        assertThat( repo.getDevice( "id" ) ).isNull();
    }

    @Test
    public void updateTimesConnected_validUpdateWorks() {
        RSSIDatabase.databaseWriteExecutor = Executors.newFixedThreadPool( 1 );
        Device device = new Device( "id", "alias", 0 );
        repo.insert( device );
        repo.updateTimesConnected( "id", 50 );
        RSSIDatabase.databaseWriteExecutor.shutdown();
        try {
            RSSIDatabase.databaseWriteExecutor.awaitTermination( 5000, TimeUnit.MILLISECONDS );
        } catch (InterruptedException e) {
            assertThat( true ).isFalse();
        }
        assertThat( repo.getDevice( "id" ).times_connected ).isEqualTo( 50 );
    }

    @Test
    public void updateTimesConnected_invalidUpdateDoesNotInsert() {
        RSSIDatabase.databaseWriteExecutor = Executors.newFixedThreadPool( 1 );
        repo.updateTimesConnected( "id", 50 );
        RSSIDatabase.databaseWriteExecutor.shutdown();
        try {
            RSSIDatabase.databaseWriteExecutor.awaitTermination( 5000, TimeUnit.MILLISECONDS );
        } catch (InterruptedException e) {
            assertThat( true ).isFalse();
        }
        assertThat( repo.getDevice( "id" ) ).isNull();
    }

    @Test
    public void deleteByMac_deleteExisting() {
        RSSIDatabase.databaseWriteExecutor = Executors.newFixedThreadPool( 1 );
        Device device = new Device( "id", "alias", 0 );
        repo.insert( device );
        RSSIDatabase.databaseWriteExecutor.shutdown();
        try {
            RSSIDatabase.databaseWriteExecutor.awaitTermination( 5000, TimeUnit.MILLISECONDS );
        } catch (InterruptedException e) {
            assertThat( true ).isFalse();
        }
        assertThat( repo.getDevice( "id" ).macAddress ).isEqualTo( device.macAddress );
        RSSIDatabase.databaseWriteExecutor = Executors.newFixedThreadPool( 1 );
        repo.deleteByMac( device.macAddress );
        RSSIDatabase.databaseWriteExecutor.shutdown();
        try {
            RSSIDatabase.databaseWriteExecutor.awaitTermination( 5000, TimeUnit.MILLISECONDS );
        } catch (InterruptedException e) {
            assertThat( true ).isFalse();
        }
        assertThat( repo.getDevice( "id" ) ).isNull();
    }

    @Test
    public void deleteByMac_deleteNotExisting() {
        RSSIDatabase.databaseWriteExecutor = Executors.newFixedThreadPool( 1 );
        repo.deleteByMac( "WRONG" );
        RSSIDatabase.databaseWriteExecutor.shutdown();
        try {
            RSSIDatabase.databaseWriteExecutor.awaitTermination( 5000, TimeUnit.MILLISECONDS );
        } catch (InterruptedException e) {
            assertThat( true ).isFalse();
        }
        assertThat( repo.getDevice( "WRONG" ) ).isNull();
    }

//    @After
//    public void teardown() {
//        database.close();
//    }

}
