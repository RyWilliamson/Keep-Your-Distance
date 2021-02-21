package com.github.rywilliamson.configurator;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import com.github.rywilliamson.configurator.Activities.MainActivity;
import com.github.rywilliamson.configurator.Database.Entity.Device;
import com.github.rywilliamson.configurator.Database.Entity.Interaction;
import com.github.rywilliamson.configurator.Database.RSSIDatabase;
import com.github.rywilliamson.configurator.Database.Repository.InteractionRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;

@RunWith( RobolectricTestRunner.class )
public class InteractionRepositoryUnitTest {

    private final ActivityScenario<MainActivity> scenario = ActivityScenario.launch( MainActivity.class );
    private final Context context = ApplicationProvider.getApplicationContext();
    private final RSSIDatabase database = Room.inMemoryDatabaseBuilder( context,
            RSSIDatabase.class ).allowMainThreadQueries().build();
    private InteractionRepository repo;

    @Before
    public void initialise() {
        scenario.onActivity( activity -> {
            RSSIDatabase.setInstance( database );
            repo = new InteractionRepository( activity.getApplication() );
            database.deviceDao().insertDevice( new Device( "A", "aliasA", 1 ) );
            database.deviceDao().insertDevice( new Device( "B", "aliasB", 1 ) );
        } );
    }

    @Test
    public void getInteractionsForReceiver_EmptyWorks() {
        List<Interaction> interactions = repo.getInteractionsForReceiver( "EMPTY" );
        assertThat( interactions ).isEmpty();
    }

    @Test
    public void getInteractionsForReceiver_isCorrect() {
        Date date = new Date();
        database.interactionDao().insertInteraction( new Interaction( "B", "A", date, date ) );
        database.interactionDao().insertInteraction( new Interaction( "A", "A", date, date ) );
        List<Interaction> interactions = repo.getInteractionsForReceiver( "A" );
        assertThat( interactions.size() ).isEqualTo( 2 );
    }

    @Test
    public void getInteractionsInRange_worksInRange() {
        Date date = new Date();
        database.interactionDao().insertInteraction( new Interaction( "B", "A", date, date ) );
        database.interactionDao().insertInteraction( new Interaction( "A", "A", date, date ) );
        assertThat( repo.getInteractionsInRange( "A", 0, 1 ) ).isEqualTo( 2 );
    }

    @Test
    public void getInteractionsInRange_worksOutStartRange() {
        Date date = new Date();
        database.interactionDao().insertInteraction( new Interaction( "B", "A", date, date ) );
        database.interactionDao().insertInteraction( new Interaction( "A", "A", date, date ) );
        assertThat( repo.getInteractionsInRange( "A", 1, 1 ) ).isEqualTo( 0 );
    }

    @Test
    public void getInteractionsInRange_worksOutEndRange() {
        Date date = new Date();
        database.interactionDao().insertInteraction( new Interaction( "B", "A", date, date ) );
        database.interactionDao().insertInteraction( new Interaction( "A", "A", date, date ) );
        assertThat( repo.getInteractionsInRange( "A", 0, 0 ) ).isEqualTo( 0 );
    }

    @Test
    public void getInteractionsInRange_worksBadID() {
        Date date = new Date();
        database.interactionDao().insertInteraction( new Interaction( "B", "A", date, date ) );
        database.interactionDao().insertInteraction( new Interaction( "A", "A", date, date ) );
        assertThat( repo.getInteractionCountByReceiverNow( "C" ) ).isEqualTo( 0 );
    }

    @Test
    public void getInteractionsInRange_worksGoodID() {
        Date date = new Date();
        database.interactionDao().insertInteraction( new Interaction( "B", "A", date, date ) );
        database.interactionDao().insertInteraction( new Interaction( "A", "A", date, date ) );
        assertThat( repo.getInteractionCountByReceiverNow( "A" ) ).isEqualTo( 2 );
    }

    @Test
    public void getLastDate_isCorrect() {
        database.interactionDao().insertInteraction( new Interaction( "B", "A", new Date( 0 ), new Date( 0 ) ) );
        database.interactionDao().insertInteraction( new Interaction( "B", "A", new Date( 1 ), new Date( 1 ) ) );
        assertThat( repo.getLastDate( "A" ).getTime() ).isEqualTo( 1 );
    }

    @Test
    public void getFirstDate_isCorrect() {
        database.interactionDao().insertInteraction( new Interaction( "B", "A", new Date( 0 ), new Date( 0 ) ) );
        database.interactionDao().insertInteraction( new Interaction( "B", "A", new Date( 1 ), new Date( 1 ) ) );
        assertThat( repo.getFirstDate( "A" ).getTime() ).isEqualTo( 0 );
    }

    @Test
    public void getInteractionByID_wrongIDWorks() {
        assertThat( repo.getInteractionByID( "C", "C", new Date( 0 ) ) ).isNull();
    }

    @Test
    public void getInteractionByID_rightIDWorks() {
        database.interactionDao().insertInteraction( new Interaction( "B", "A", new Date( 0 ), new Date( 0 ) ) );
        Interaction retrieved = repo.getInteractionByID( "B", "A", new Date( 0 ) );
        assertThat( retrieved.sender ).isEqualTo( "B" );
        assertThat( retrieved.receiver ).isEqualTo( "A" );
        assertThat( retrieved.startTime.getTime() ).isEqualTo( 0 );
    }

    @Test
    public void getInteractionByID_rightIDMultipleWorks() {
        database.interactionDao().insertInteraction( new Interaction( "B", "A", new Date( 0 ), new Date( 0 ) ) );
        database.interactionDao().insertInteraction( new Interaction( "B", "A", new Date( 1 ), new Date( 1 ) ) );
        Interaction retrieved = repo.getInteractionByID( "B", "A", new Date( 0 ) );
        assertThat( retrieved.sender ).isEqualTo( "B" );
        assertThat( retrieved.receiver ).isEqualTo( "A" );
        assertThat( retrieved.startTime.getTime() ).isEqualTo( 0 );
        retrieved = repo.getInteractionByID( "B", "A", new Date( 1 ) );
        assertThat( retrieved.sender ).isEqualTo( "B" );
        assertThat( retrieved.receiver ).isEqualTo( "A" );
        assertThat( retrieved.startTime.getTime() ).isEqualTo( 1 );
    }

    @Test
    public void getInteractionCountByDate_wrongIDWorks() {
        assertThat( repo.getInteractionCountByDate( "C", new Date( 0 ), new Date( 0 ) ) ).isEqualTo( 0 );
    }

    @Test
    public void getInteractionCountByDate_correctIDWorks() {
        database.interactionDao().insertInteraction( new Interaction( "B", "A", new Date( 1 ), new Date( 1 ) ) );
        database.interactionDao().insertInteraction( new Interaction( "A", "A", new Date( 1 ), new Date( 1 ) ) );
        assertThat( repo.getInteractionCountByDate( "A", new Date( 0 ), new Date( 2 ) ) ).isEqualTo( 2 );
    }

    @Test
    public void getInteractionCountByDate_rangeSeparationWorks() {
        database.interactionDao().insertInteraction( new Interaction( "B", "A", new Date( 1 ), new Date( 1 ) ) );
        database.interactionDao().insertInteraction( new Interaction( "A", "A", new Date( 3 ), new Date( 3 ) ) );
        assertThat( repo.getInteractionCountByDate( "A", new Date( 0 ), new Date( 2 ) ) ).isEqualTo( 1 );
    }

    @Test
    public void insertHelper_firstInsertWorks() {
        repo.insertHelper( new Interaction( "B", "A", new Date( 1 ), new Date( 1 ) ) );
        RSSIDatabase.databaseWriteExecutor.shutdown();
        assertThat( repo.getInteractionByID( "B", "A", new Date( 1 ) ).endTime.getTime() ).isEqualTo( 1 );
    }

    @Test
    public void insertHelper_secondInsertUpdates() {
        repo.insertHelper( new Interaction( "B", "A", new Date( 1 ), new Date( 1 ) ) );
        repo.insertHelper( new Interaction( "B", "A", new Date( 1 ), new Date( 2 ) ) );
        assertThat( repo.getInteractionByID( "B", "A", new Date( 1 ) ).endTime.getTime() ).isEqualTo( 2 );
    }

    @Test
    public void insert_isCorrect() {
        RSSIDatabase.databaseWriteExecutor = Executors.newFixedThreadPool( 1 );
        repo.insert( new Interaction( "B", "A", new Date( 1 ), new Date( 1 ) ) );
        RSSIDatabase.databaseWriteExecutor.shutdown();
        try {
            RSSIDatabase.databaseWriteExecutor.awaitTermination( 5000, TimeUnit.MILLISECONDS );
        } catch ( InterruptedException e ) {
            assertThat( true ).isFalse();
        }
        assertThat( repo.getInteractionByID( "B", "A", new Date( 1 ) ).endTime.getTime() ).isEqualTo( 1 );
    }

    @Test
    public void insert_isCorrectSingle() {
        RSSIDatabase.databaseWriteExecutor = Executors.newFixedThreadPool( 1 );
        repo.insert( new Interaction( "B", "A", new Date( 1 ), new Date( 1 ) ) );
        RSSIDatabase.databaseWriteExecutor.shutdown();
        try {
            RSSIDatabase.databaseWriteExecutor.awaitTermination( 5000, TimeUnit.MILLISECONDS );
        } catch ( InterruptedException e ) {
            assertThat( true ).isFalse();
        }
        assertThat( repo.getInteractionByID( "B", "A", new Date( 1 ) ).endTime.getTime() ).isEqualTo( 1 );
        RSSIDatabase.databaseWriteExecutor = Executors.newFixedThreadPool( 1 );
        repo.deleteByReceiver( "A" );
        RSSIDatabase.databaseWriteExecutor.shutdown();
        try {
            RSSIDatabase.databaseWriteExecutor.awaitTermination( 5000, TimeUnit.MILLISECONDS );
        } catch ( InterruptedException e ) {
            assertThat( true ).isFalse();
        }
        assertThat( repo.getInteractionByID( "B", "A", new Date( 1 ) ) ).isNull();
    }

    @Test
    public void insert_isCorrectMultiple() {
        RSSIDatabase.databaseWriteExecutor = Executors.newFixedThreadPool( 1 );
        repo.insert( new Interaction( "B", "A", new Date( 1 ), new Date( 1 ) ) );
        repo.insert( new Interaction( "A", "A", new Date( 1 ), new Date( 1 ) ) );
        RSSIDatabase.databaseWriteExecutor.shutdown();
        try {
            RSSIDatabase.databaseWriteExecutor.awaitTermination( 5000, TimeUnit.MILLISECONDS );
        } catch ( InterruptedException e ) {
            assertThat( true ).isFalse();
        }
        assertThat( repo.getInteractionByID( "B", "A", new Date( 1 ) ).endTime.getTime() ).isEqualTo( 1 );
        assertThat( repo.getInteractionByID( "A", "A", new Date( 1 ) ).endTime.getTime() ).isEqualTo( 1 );
        RSSIDatabase.databaseWriteExecutor = Executors.newFixedThreadPool( 1 );
        repo.deleteByReceiver( "A" );
        RSSIDatabase.databaseWriteExecutor.shutdown();
        try {
            RSSIDatabase.databaseWriteExecutor.awaitTermination( 5000, TimeUnit.MILLISECONDS );
        } catch ( InterruptedException e ) {
            assertThat( true ).isFalse();
        }
        assertThat( repo.getInteractionByID( "B", "A", new Date( 1 ) ) ).isNull();
    }

}
