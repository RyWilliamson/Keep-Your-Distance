package com.github.rywilliamson.configurator;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;

import com.github.rywilliamson.configurator.Activities.MainActivity;
import com.github.rywilliamson.configurator.Utils.Keys;
import com.github.rywilliamson.configurator.Utils.Profile;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static com.google.common.truth.Truth.assertThat;

@RunWith( RobolectricTestRunner.class )
public class ProfileUnitTest {
    private final ActivityScenario<MainActivity> scenario = ActivityScenario.launch( MainActivity.class );
    Profile.ProfileEnum profile;

    @Test
    public void indoorInit_isCorrectValues() {
        assertThat( Profile.ProfileEnum.INDOOR.getEnvironmentVar() ).isEqualTo( 2 );
        assertThat( Profile.ProfileEnum.INDOOR.getMeasuredPower() ).isEqualTo( -78 );
    }

    @Test
    public void outdoorCityInit_isCorrectValues() {
        assertThat( Profile.ProfileEnum.OUTDOOR_CITY.getEnvironmentVar() ).isEqualTo( 2 );
        assertThat( Profile.ProfileEnum.OUTDOOR_CITY.getMeasuredPower() ).isEqualTo( -77 );
    }

    @Test
    public void outdoorNatureInit_isCorrectValues() {
        assertThat( Profile.ProfileEnum.OUTDOOR_NATURE.getEnvironmentVar() ).isEqualTo( 2 );
        assertThat( Profile.ProfileEnum.OUTDOOR_NATURE.getMeasuredPower() ).isEqualTo( -75 );
    }

    @Test
    public void convertFromReadable_worksOnIndoor() {
        assertThat( Profile.convertFromReadable( "INDOOR" ) ).isEqualTo( Profile.ProfileEnum.INDOOR );
    }

    @Test
    public void convertFromReadable_worksOnOutdoorCity() {
        assertThat( Profile.convertFromReadable( "OUTDOOR CITY" ) ).isEqualTo( Profile.ProfileEnum.OUTDOOR_CITY );
    }

    @Test
    public void convertFromReadable_worksOnOutdoorNature() {
        assertThat( Profile.convertFromReadable( "OUTDOOR NATURE" ) ).isEqualTo( Profile.ProfileEnum.OUTDOOR_NATURE );
    }

    @Test( expected = IllegalArgumentException.class )
    public void convertFromReadable_failsOnOther() {
        assertThat( Profile.convertFromReadable( "WRONG" ) );
    }

    @Test
    public void convertToReadable_worksOnIndoor() {
        assertThat( Profile.convertToReadable( Profile.ProfileEnum.INDOOR ) ).isEqualTo( "Indoor" );
    }

    @Test
    public void convertToReadable_worksOnOutdoorCity() {
        assertThat( Profile.convertToReadable( Profile.ProfileEnum.OUTDOOR_CITY ) ).isEqualTo( "Outdoor City" );
    }

    @Test
    public void convertToReadable_worksOnOutdoorNature() {
        assertThat( Profile.convertToReadable( Profile.ProfileEnum.OUTDOOR_NATURE ) ).isEqualTo( "Outdoor Nature" );
    }

    @Test
    public void setProfilePreference_isCorrect() {
        scenario.onActivity( activity -> {
            Profile.setProfilePreference( activity, Profile.ProfileEnum.OUTDOOR_NATURE );
            assertThat( activity.getSharedPreferences( Keys.PREFS, Context.MODE_PRIVATE ).getString( Keys.PROFILE_NAME,
                    "" ) ).isEqualTo( "OUTDOOR_NATURE" );
        } );
    }

    @Test( expected = IllegalArgumentException.class )
    public void getFromPreference_failsOnWrongValue() {
        scenario.onActivity( activity -> {
            activity.getSharedPreferences( Keys.PREFS, Context.MODE_PRIVATE ).edit().putString( Keys.PROFILE_NAME,
                    "WRONG" ).apply();
            assertThat( Profile.getFromPreferences( activity ) );
        } );
    }

    @Test
    public void getFromPreference_IndoorOnDefault() {
        scenario.onActivity( activity -> {
            assertThat( Profile.getFromPreferences( activity ) ).isEqualTo( Profile.ProfileEnum.INDOOR );
        } );
    }

    @Test
    public void getFromPreference_worksOnIndoor() {
        scenario.onActivity( activity -> {
            Profile.setProfilePreference( activity, Profile.ProfileEnum.INDOOR );
            assertThat( Profile.getFromPreferences( activity ) ).isEqualTo( Profile.ProfileEnum.INDOOR );
        } );
    }

    @Test
    public void getFromPreference_worksOnOutdoorCity() {
        scenario.onActivity( activity -> {
            Profile.setProfilePreference( activity, Profile.ProfileEnum.OUTDOOR_CITY );
            assertThat( Profile.getFromPreferences( activity ) ).isEqualTo( Profile.ProfileEnum.OUTDOOR_CITY );
        } );
    }

    @Test
    public void getFromPreference_worksOnOutdoorNature() {
        scenario.onActivity( activity -> {
            Profile.setProfilePreference( activity, Profile.ProfileEnum.OUTDOOR_NATURE );
            assertThat( Profile.getFromPreferences( activity ) ).isEqualTo( Profile.ProfileEnum.OUTDOOR_NATURE );
        } );
    }

}
