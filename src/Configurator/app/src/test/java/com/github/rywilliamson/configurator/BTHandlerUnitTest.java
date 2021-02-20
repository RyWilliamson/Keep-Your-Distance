package com.github.rywilliamson.configurator;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import com.github.rywilliamson.configurator.Activities.MainActivity;
import com.github.rywilliamson.configurator.Utils.BluetoothHandler;
import com.github.rywilliamson.configurator.Utils.Keys;
import com.github.rywilliamson.configurator.Utils.Profile;
import com.github.rywilliamson.configurator.Utils.TimeoutData;
import com.welie.blessed.BluetoothCentralCallback;
import com.welie.blessed.BluetoothPeripheral;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import java.nio.ByteBuffer;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith( RobolectricTestRunner.class )
public class BTHandlerUnitTest {
    private final ActivityScenario<MainActivity> scenario = ActivityScenario.launch( MainActivity.class );
    private final Context context = ApplicationProvider.getApplicationContext();
    private final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( context );
    private final BluetoothCentralCallback testCB = new BluetoothCentralCallback() {
    };
    @Mock
    BluetoothPeripheral mockPeripheral = mock( BluetoothPeripheral.class );
    BluetoothHandler handler;


    private BluetoothHandler getHandler() {
        AtomicReference<BluetoothHandler> handler = new AtomicReference<>();
        scenario.onActivity( activity -> {
            handler.set( new BluetoothHandler( activity, testCB, prefs ) );
        } );
        return handler.get();
    }

    @Before
    public void initialise() {
        handler = getHandler();
        when( mockPeripheral.getAddress() ).thenReturn( "00:00:00:00:00:00" );
    }

    @Test
    public void isConnected_isCorrect() {
        assertThat( handler.isConnected() ).isFalse();
        handler.onConnect( mockPeripheral );
        assertThat( handler.isConnected() ).isTrue();
    }

    @Test
    public void getPrevMac_defaultIsCorrect() {
        assertThat( handler.getPrevMac() ).isEmpty();
    }

    @Test
    public void getPrevMac_changedIsCorrect() {
        handler.onConnect( mockPeripheral );
        assertThat( handler.getPrevMac() ).isEqualTo( "00:00:00:00:00:00" );
    }

    @Test
    public void setupConfigByteBuffer_isCorrect() {
        ByteBuffer checkBuffer = ByteBuffer.allocate( 12 );
        checkBuffer.putFloat( 1.0f );
        checkBuffer.putInt( -81 );
        checkBuffer.putInt( 3 );
        assertThat( handler.setupConfigByteBuffer() ).isEqualTo( checkBuffer );
    }

    @Test
    public void sendConfig_setsSynced() {
        assertThat( handler.isSynced() ).isFalse();
        when( mockPeripheral.writeCharacteristic( any( BluetoothGattCharacteristic.class ), any( byte[].class ),
                anyInt() ) ).thenReturn( true );
        handler.onConnect( mockPeripheral );
        handler.sendConfig();
        assertThat( handler.isSynced() ).isTrue();
    }

    @Test
    public void insertStartTimeHelper_isFirstInsertCorrect() {
        HashMap<String, TimeoutData> map = new HashMap<>();
        Date date = new Date();
        assertThat( handler.insertArbStartTimeAndGet( map, "s", "r", date ) ).isEqualTo( date );
        assertThat( Objects.requireNonNull( map.get( "sr" ) ).getStartTime() ).isEqualTo( date );
    }

    @Test
    public void insertStartTimeHelper_isSecondInsertCorrect() {
        HashMap<String, TimeoutData> map = new HashMap<>();
        Date date = new Date();
        assertThat( handler.insertArbStartTimeAndGet( map, "s", "r", date ) ).isEqualTo( date );
        Date newdate = new Date();
        assertThat( handler.insertArbStartTimeAndGet( map, "s", "r", newdate ).getTime() ).isEqualTo( date.getTime() );
        assertThat( Objects.requireNonNull( map.get( "sr" ) ).getStartTime().getTime() ).isEqualTo( date.getTime() );
    }

    @Test
    public void insertStartTimeHelper_isMultiReceiverCorrect() {
        HashMap<String, TimeoutData> map = new HashMap<>();
        Date date = new Date();
        assertThat( handler.insertArbStartTimeAndGet( map, "s", "r", date ) ).isEqualTo( date );
        Date newdate = new Date();
        assertThat( handler.insertArbStartTimeAndGet( map, "s1", "r1", newdate ).getTime() ).isEqualTo(
                newdate.getTime() );
        assertThat( Objects.requireNonNull( map.get( "sr" ) ).getStartTime().getTime() ).isEqualTo( date.getTime() );
        assertThat( Objects.requireNonNull( map.get( "s1r1" ) ).getStartTime().getTime() ).isEqualTo(
                newdate.getTime() );
    }

    @Test
    public void setProfile_changesPrefs() {
        scenario.onActivity( activity -> {
            handler.setProfile( activity, Profile.ProfileEnum.OUTDOOR_NATURE );
            assertThat( activity.getSharedPreferences( Keys.PREFS, Context.MODE_PRIVATE ).getString( Keys.PROFILE_NAME,
                    "" ) ).isEqualTo( "OUTDOOR_NATURE" );
        } );
    }

    @Test
    public void setDistance_changesPrefs() {
        handler.setDistance( 1.5f );
        assertThat( prefs.getFloat( Keys.DISTANCE, 1.0f ) ).isEqualTo( 1.5f );
    }

}
