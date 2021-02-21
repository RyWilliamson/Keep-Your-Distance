package com.github.rywilliamson.configurator;

import androidx.test.core.app.ActivityScenario;

import com.github.rywilliamson.configurator.Activities.MainActivity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Date;

import static com.google.common.truth.Truth.assertThat;

@RunWith( RobolectricTestRunner.class )
public class MainActivityUnitTest {

    private final ActivityScenario<MainActivity> scenario = ActivityScenario.launch( MainActivity.class );
    // Mac of 000000000000, rssi of 1, offset of 1ms
    private final byte[] data = new byte[]{
            (byte) 0x30, (byte) 0x30, (byte) 0x30, (byte) 0x30, (byte) 0x30, (byte) 0x30,
            (byte) 0x30, (byte) 0x30, (byte) 0x30, (byte) 0x30, (byte) 0x30, (byte) 0x30,
            (byte) 0x01, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };

    @Test
    public void extendMac_isCorrect() {
        scenario.onActivity( activity -> {
            assertThat( activity.extendMac( "000000000000" ) ).isEqualTo( "00:00:00:00:00:00" );
        } );
    }

    @Test
    public void calculateTimeFromData_notBulkWorks() {
        scenario.onActivity( activity -> {
            Date now = new Date();
            assertThat( activity.calculateTimeFromData( data, false, now ) ).isEqualTo( now );
        } );
    }

    @Test
    public void calculateTimeFromData_isBulkWorks() {
        scenario.onActivity( activity -> {
            Date now = new Date();
            assertThat( activity.calculateTimeFromData( data, true, now ).getTime() ).isEqualTo( now.getTime() - 1 );
        } );
    }
}
