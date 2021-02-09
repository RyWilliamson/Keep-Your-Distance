package com.github.rywilliamson.configurator.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Locale;

public class Profile {

    private static final int DEFAULT_MP = -81;
    private static final int DEFAULT_EV = 3;

    private static final int INDOOR_MP = -87;
    private static final int INDOOR_EV = 2;

    private static final int OUTDOOR_CITY_MP = -85;
    private static final int OUTDOOR_CITY_EV = 2;

    private static final int OUTDOOR_NATURE_MP = -81;
    private static final int OUTDOOR_NATURE_EV = 2;

    public enum ProfileEnum {

        INDOOR() {
            @Override
            public int getMeasuredPower() {
                return INDOOR_MP;
            }

            @Override
            public int getEnvironmentVar() {
                return INDOOR_EV;
            }
        },

        OUTDOOR_CITY() {
            @Override
            public int getMeasuredPower() {
                return OUTDOOR_CITY_MP;
            }

            @Override
            public int getEnvironmentVar() {
                return OUTDOOR_CITY_EV;
            }
        },

        OUTDOOR_NATURE() {
            @Override
            public int getMeasuredPower() {
                return OUTDOOR_NATURE_MP;
            }

            @Override
            public int getEnvironmentVar() {
                return OUTDOOR_NATURE_EV;
            }
        };

        // Default profile values (matches indoor for now)
        public int getMeasuredPower() {
            return DEFAULT_MP;
        }

        public int getEnvironmentVar() {
            return DEFAULT_EV;
        }
    }

    private static ProfileEnum fromString( String name ) {
        return ProfileEnum.valueOf( name.toUpperCase( Locale.US ) );
    }

    public static ProfileEnum convertFromReadable( String name ) {
        return fromString( name.replace( " ", "_" ) );
    }

    public static String convertToReadable( ProfileEnum name ) {
        return capitalise( name.name().replace( "_", " " ) );
    }

    private static String capitalise( String input ) {
        String[] words = input.toLowerCase( Locale.US ).split( " " );
        StringBuilder result = new StringBuilder();
        for ( String word : words ) {
            result.append( word.substring( 0, 1 ).toUpperCase( Locale.US ) )
                    .append( word.substring( 1 ) )
                    .append( " " );
        }
        return result.deleteCharAt( result.length() - 1 ).toString();
    }

    private static String fromEnum( ProfileEnum prof ) {
        return prof.name();
    }

    public static ProfileEnum getFromPreferences( Activity activity ) {
        String val = activity.getSharedPreferences( Keys.PREFS, Context.MODE_PRIVATE ).getString( Keys.PROFILE_NAME,
                "DEFAULT" );
        if ( val.equals( "DEFAULT" ) ) {
            return fromString( "INDOOR" );
        }
        return fromString( val );
    }

    public static void setProfilePreference( Activity activity, ProfileEnum prof ) {
        SharedPreferences.Editor prefs = activity.getSharedPreferences( Keys.PREFS, Context.MODE_PRIVATE ).edit();
        prefs.putString( Keys.PROFILE_NAME, fromEnum( prof ) );
        prefs.apply();
    }

}
