package com.github.rywilliamson.configurator;

import android.widget.ArrayAdapter;

import com.github.rywilliamson.configurator.Utils.SpinnerUtils;
import com.github.rywilliamson.configurator.Utils.TimeoutData;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;

@RunWith( RobolectricTestRunner.class )
public class TimeoutDataUnitTest {

    Date startTime;

    @Before
    public void initialise() {
        startTime = new Date();
    }

    @Test
    public void getStartTime_worksInitially() {
        assertThat( new TimeoutData(startTime, startTime).getStartTime() ).isEqualTo( startTime );
    }

    @Test
    public void getStartTime_worksUpdateClose() {
        TimeoutData data = new TimeoutData(startTime, startTime);
        Date newdate = new Date();
        data.updateTime( newdate );
        assertThat( data.getStartTime() ).isEqualTo( startTime );
    }

    @Test
    public void getStartTime_worksUpdateFar() {
        TimeoutData data = new TimeoutData(startTime, startTime);
        Date newdate = new Date(startTime.getTime() + 4000);
        data.updateTime( newdate );
        assertThat( data.getStartTime() ).isEqualTo( newdate );
    }

}
