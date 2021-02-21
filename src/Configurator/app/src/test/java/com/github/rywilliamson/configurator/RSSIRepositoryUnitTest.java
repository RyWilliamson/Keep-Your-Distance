package com.github.rywilliamson.configurator;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import com.github.rywilliamson.configurator.Activities.MainActivity;
import com.github.rywilliamson.configurator.Database.Entity.Device;
import com.github.rywilliamson.configurator.Database.Entity.Interaction;
import com.github.rywilliamson.configurator.Database.Entity.RSSI;
import com.github.rywilliamson.configurator.Database.RSSIDatabase;
import com.github.rywilliamson.configurator.Database.Repository.InteractionRepository;
import com.github.rywilliamson.configurator.Database.Repository.RSSIRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;

@RunWith( RobolectricTestRunner.class )
public class RSSIRepositoryUnitTest {

    private final ActivityScenario<MainActivity> scenario = ActivityScenario.launch( MainActivity.class );
    private final Context context = ApplicationProvider.getApplicationContext();
    private RSSIDatabase database;
    private RSSIRepository repo;

    private Interaction interaction1;
    private Interaction interaction2;
    private Interaction interaction3;

    @Before
    public void initialise() {
        scenario.onActivity( activity -> {
            database = Room.inMemoryDatabaseBuilder( context, RSSIDatabase.class ).allowMainThreadQueries().build();
            RSSIDatabase.setInstance( database );
            repo = new RSSIRepository( activity.getApplication() );
            database.deviceDao().insertDevice( new Device( "A", "aliasA", 1 ) );
            database.deviceDao().insertDevice( new Device( "B", "aliasB", 1 ) );
            database.deviceDao().insertDevice( new Device( "C", "aliasC", 0 ) );
            interaction1 = new Interaction( "A", "B", new Date( 0 ), new Date( 10 ) );
            interaction2 = new Interaction( "A", "B", new Date( 11 ), new Date( 21 ) );
            interaction3 = new Interaction( "C", "A", new Date( 50 ), new Date( 90 ) );
            database.interactionDao().insertInteraction( interaction1 );
            database.interactionDao().insertInteraction( interaction2 );
            database.interactionDao().insertInteraction( interaction3 );
        } );
    }

    @Test
    public void getRSSIByID_rightIDWorks() {
        database.rssiDao().insertRSSI( new RSSI( "A", "B", new Date( 0 ), new Date( 5 ), -80, 1.67f, -81, 3 ) );
        assertThat( repo.getRSSIByID( "A", "B", new Date( 0 ), new Date( 5 ) ).timestamp.getTime() ).isEqualTo( 5 );
        assertThat( repo.getRSSIByID( interaction1, new Date( 5 ) ).timestamp.getTime() ).isEqualTo( 5 );
    }

    @Test
    public void getRSSIByID_wrongTimestampNull() {
        database.rssiDao().insertRSSI( new RSSI( "A", "B", new Date( 0 ), new Date( 5 ), -80, 1.67f, -81, 3 ) );
        assertThat( repo.getRSSIByID( "A", "B", new Date( 0 ), new Date( 6 ) ) ).isNull();
        assertThat( repo.getRSSIByID( interaction1, new Date( 6 ) ) ).isNull();
    }

    @Test
    public void getRSSIByID_wrongInteractionNull() {
        database.rssiDao().insertRSSI( new RSSI( "A", "B", new Date( 0 ), new Date( 5 ), -80, 1.67f, -81, 3 ) );
        assertThat( repo.getRSSIByID( "A", "B", new Date( 11 ), new Date( 6 ) ) ).isNull();
        assertThat( repo.getRSSIByID( interaction2, new Date( 5 ) ) ).isNull();
    }

    @Test
    public void getCountAverageDistanceInRange_singleWorks() {
        database.rssiDao().insertRSSI( new RSSI( "A", "B", new Date( 0 ), new Date( 5 ), -80, 1.67f, -81, 3 ) );
        assertThat( repo.getRSSIByID( "A", "B", new Date( 0 ), new Date( 5 ) ).timestamp.getTime() ).isEqualTo( 5 );
        assertThat( repo.getCountAverageDistanceInRange( "B", 0, 2 ) ).isEqualTo( 1 );
    }

    @Test
    public void getCountAverageDistanceInRange_multipleWorks() {
        database.rssiDao().insertRSSI( new RSSI( "A", "B", new Date( 0 ), new Date( 5 ), -80, 1.0f, -81, 3 ) );
        database.rssiDao().insertRSSI( new RSSI( "A", "B", new Date( 0 ), new Date( 6 ), -80, 2.0f, -81, 3 ) );
        database.rssiDao().insertRSSI( new RSSI( "A", "B", new Date( 0 ), new Date( 7 ), -80, 2.0f, -81, 3 ) );
        database.rssiDao().insertRSSI( new RSSI( "A", "B", new Date( 11 ), new Date( 16 ), -80, 1.0f, -81, 3 ) );
        assertThat( repo.getCountAverageDistanceInRange( "B", 0, 2 ) ).isEqualTo( 2 );
    }

    @Test
    public void getCountAverageDistanceInRange_onUpperBoundaryNotIncluded() {
        database.rssiDao().insertRSSI( new RSSI( "A", "B", new Date( 0 ), new Date( 5 ), -80, 2.0f, -81, 3 ) );
        database.rssiDao().insertRSSI( new RSSI( "A", "B", new Date( 0 ), new Date( 6 ), -80, 2.0f, -81, 3 ) );
        database.rssiDao().insertRSSI( new RSSI( "A", "B", new Date( 0 ), new Date( 7 ), -80, 2.0f, -81, 3 ) );
        assertThat( repo.getCountAverageDistanceInRange( "B", 0, 2 ) ).isEqualTo( 0 );
    }

    @Test
    public void getCountAverageDistanceInRange_onLowerBoundaryIncluded() {
        database.rssiDao().insertRSSI( new RSSI( "A", "B", new Date( 0 ), new Date( 5 ), -80, 2.0f, -81, 3 ) );
        database.rssiDao().insertRSSI( new RSSI( "A", "B", new Date( 0 ), new Date( 6 ), -80, 2.0f, -81, 3 ) );
        database.rssiDao().insertRSSI( new RSSI( "A", "B", new Date( 0 ), new Date( 7 ), -80, 2.0f, -81, 3 ) );
        assertThat( repo.getCountAverageDistanceInRange( "B", 2, 4 ) ).isEqualTo( 1 );
    }

    @Test
    public void getCountAverageDistanceInRange_ignoresOtherReceiver() {
        database.rssiDao().insertRSSI( new RSSI( "A", "B", new Date( 0 ), new Date( 5 ), -80, 2.0f, -81, 3 ) );
        database.rssiDao().insertRSSI( new RSSI( "A", "B", new Date( 0 ), new Date( 6 ), -80, 2.0f, -81, 3 ) );
        database.rssiDao().insertRSSI( new RSSI( "C", "A", new Date( 50 ), new Date( 57 ), -80, 2.0f, -81, 3 ) );
        assertThat( repo.getCountAverageDistanceInRange( "B", 1, 2.5f ) ).isEqualTo( 1 );
    }

    @Test
    public void getRSSIForReceiver_noInteraction() {
        assertThat( repo.getRSSIForReceiver( "B" ) ).isEmpty();
    }

    @Test
    public void getRSSIForReceiver_singleInteraction() {
        database.rssiDao().insertRSSI( new RSSI( "A", "B", new Date( 0 ), new Date( 5 ), -80, 2.0f, -81, 3 ) );
        database.rssiDao().insertRSSI( new RSSI( "A", "B", new Date( 0 ), new Date( 6 ), -80, 2.0f, -81, 3 ) );
        database.rssiDao().insertRSSI( new RSSI( "A", "B", new Date( 0 ), new Date( 7 ), -80, 2.0f, -81, 3 ) );
        assertThat( repo.getRSSIForReceiver( "B" ) ).hasSize( 3 );
    }

    @Test
    public void getRSSIForReceiver_multipleInteractions() {
        database.rssiDao().insertRSSI( new RSSI( "A", "B", new Date( 0 ), new Date( 5 ), -80, 2.0f, -81, 3 ) );
        database.rssiDao().insertRSSI( new RSSI( "A", "B", new Date( 0 ), new Date( 6 ), -80, 2.0f, -81, 3 ) );
        database.rssiDao().insertRSSI( new RSSI( "A", "B", new Date( 11 ), new Date( 17 ), -80, 2.0f, -81, 3 ) );
        assertThat( repo.getRSSIForReceiver( "B" ) ).hasSize( 3 );
    }

    @Test
    public void getRSSIForReceiver_IgnoresOtherReceiver() {
        database.rssiDao().insertRSSI( new RSSI( "A", "B", new Date( 0 ), new Date( 5 ), -80, 2.0f, -81, 3 ) );
        database.rssiDao().insertRSSI( new RSSI( "A", "B", new Date( 0 ), new Date( 6 ), -80, 2.0f, -81, 3 ) );
        database.rssiDao().insertRSSI( new RSSI( "C", "A", new Date( 50 ), new Date( 57 ), -80, 2.0f, -81, 3 ) );
        assertThat( repo.getRSSIForReceiver( "B" ) ).hasSize( 2 );
    }

    @Test
    public void insertOnlyRSSI_isCorrectSingle() {
        RSSIDatabase.databaseWriteExecutor = Executors.newFixedThreadPool( 1 );
        repo.insert( new RSSI( "A", "B", new Date( 0 ), new Date( 5 ), -80, 2.0f, -81, 3 ) );
        RSSIDatabase.databaseWriteExecutor.shutdown();
        try {
            RSSIDatabase.databaseWriteExecutor.awaitTermination( 5000, TimeUnit.MILLISECONDS );
        } catch ( InterruptedException e ) {
            assertThat( true ).isFalse();
        }
        assertThat( repo.getRSSIByID( interaction1, new Date( 5 ) ).timestamp.getTime() ).isEqualTo( 5 );
    }

    @Test
    public void insertOnlyRSSI_isCorrectMulti() {
        RSSIDatabase.databaseWriteExecutor = Executors.newFixedThreadPool( 1 );
        repo.insert( new RSSI( "A", "B", new Date( 0 ), new Date( 5 ), -80, 2.0f, -81, 3 ) );
        RSSIDatabase.databaseWriteExecutor.shutdown();
        try {
            RSSIDatabase.databaseWriteExecutor.awaitTermination( 5000, TimeUnit.MILLISECONDS );
        } catch ( InterruptedException e ) {
            assertThat( true ).isFalse();
        }
        assertThat( repo.getRSSIByID( interaction1, new Date( 5 ) ).timestamp.getTime() ).isEqualTo( 5 );
        RSSIDatabase.databaseWriteExecutor = Executors.newFixedThreadPool( 1 );
        repo.insert( new RSSI( "A", "B", new Date( 0 ), new Date( 6 ), -80, 2.0f, -81, 3 ) );
        RSSIDatabase.databaseWriteExecutor.shutdown();
        try {
            RSSIDatabase.databaseWriteExecutor.awaitTermination( 5000, TimeUnit.MILLISECONDS );
        } catch ( InterruptedException e ) {
            assertThat( true ).isFalse();
        }
        assertThat( repo.getRSSIByID( interaction1, new Date( 6 ) ).timestamp.getTime() ).isEqualTo( 6 );
    }

    @Test
    public void insertRSSI_isCorrectSingle() {
        scenario.onActivity( activity -> {
            InteractionRepository interactionRepo = new InteractionRepository( activity.getApplication() );
            Interaction interaction = new Interaction( "B", "C", new Date( 0 ), new Date( 10 ) );
            RSSIDatabase.databaseWriteExecutor = Executors.newFixedThreadPool( 1 );
            repo.insert( new RSSI( "B", "C", new Date( 0 ), new Date( 5 ), -80, 2.0f, -81, 3 ), interactionRepo );
            RSSIDatabase.databaseWriteExecutor.shutdown();
            try {
                RSSIDatabase.databaseWriteExecutor.awaitTermination( 5000, TimeUnit.MILLISECONDS );
            } catch ( InterruptedException e ) {
                assertThat( true ).isFalse();
            }
            assertThat( interactionRepo.getInteractionByID( "B", "C", new Date(0) ).receiver ).isEqualTo( interaction.receiver );
            assertThat( repo.getRSSIByID( interaction, new Date( 5 ) ).timestamp.getTime() ).isEqualTo( 5 );
        } );
    }

    @Test
    public void delete_isCorrectSingle() {
        RSSIDatabase.databaseWriteExecutor = Executors.newFixedThreadPool( 1 );
        repo.insert( new RSSI( "A", "B", new Date( 0 ), new Date( 5 ), -80, 2.0f, -81, 3 ) );
        RSSIDatabase.databaseWriteExecutor.shutdown();
        try {
            RSSIDatabase.databaseWriteExecutor.awaitTermination( 5000, TimeUnit.MILLISECONDS );
        } catch ( InterruptedException e ) {
            assertThat( true ).isFalse();
        }
        assertThat( repo.getRSSIByID( interaction1, new Date( 5 ) ).timestamp.getTime() ).isEqualTo( 5 );
        RSSIDatabase.databaseWriteExecutor = Executors.newFixedThreadPool( 1 );
        repo.deleteByReceiver( "B" );
        RSSIDatabase.databaseWriteExecutor.shutdown();
        try {
            RSSIDatabase.databaseWriteExecutor.awaitTermination( 5000, TimeUnit.MILLISECONDS );
        } catch ( InterruptedException e ) {
            assertThat( true ).isFalse();
        }
        assertThat( repo.getRSSIByID( interaction1, new Date( 5 ) ) ).isNull();
    }

    @Test
    public void delete_isCorrectMultiple() {
        RSSIDatabase.databaseWriteExecutor = Executors.newFixedThreadPool( 1 );
        repo.insert( new RSSI( "A", "B", new Date( 0 ), new Date( 5 ), -80, 2.0f, -81, 3 ) );
        repo.insert( new RSSI( "A", "B", new Date( 0 ), new Date( 6 ), -80, 2.0f, -81, 3 ) );
        RSSIDatabase.databaseWriteExecutor.shutdown();
        try {
            RSSIDatabase.databaseWriteExecutor.awaitTermination( 5000, TimeUnit.MILLISECONDS );
        } catch ( InterruptedException e ) {
            assertThat( true ).isFalse();
        }
        assertThat( repo.getRSSIByID( interaction1, new Date( 5 ) ).timestamp.getTime() ).isEqualTo( 5 );
        assertThat( repo.getRSSIByID( interaction1, new Date( 6 ) ).timestamp.getTime() ).isEqualTo( 6 );
        RSSIDatabase.databaseWriteExecutor = Executors.newFixedThreadPool( 1 );
        repo.deleteByReceiver( "B" );
        RSSIDatabase.databaseWriteExecutor.shutdown();
        try {
            RSSIDatabase.databaseWriteExecutor.awaitTermination( 5000, TimeUnit.MILLISECONDS );
        } catch ( InterruptedException e ) {
            assertThat( true ).isFalse();
        }
        assertThat( repo.getRSSIByID( interaction1, new Date( 5 ) ) ).isNull();
        assertThat( repo.getRSSIByID( interaction1, new Date( 6 ) ) ).isNull();
    }

    @After
    public void teardown() {
        database.close();
    }

}
