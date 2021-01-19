package com.github.rywilliamson.configurator.Database;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.rywilliamson.configurator.Database.Entity.Device;
import com.github.rywilliamson.configurator.Database.Entity.Interaction;
import com.github.rywilliamson.configurator.Database.Entity.RSSI;
import com.github.rywilliamson.configurator.R;
import com.github.rywilliamson.configurator.Utils.Keys;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

import static android.os.Environment.DIRECTORY_DOCUMENTS;

public class DatabaseExporter {

    public static void export( Activity activity, DatabaseViewModel db, String mac ) {
        RSSIDatabase.databaseGetExecutor.execute( () -> {
            WeakReference<Activity> activityRef = new WeakReference<>( activity );

            if ( !gainFilePermissions( activityRef ) ) {
                makeToast( activityRef, R.string.toast_no_file_perms, Toast.LENGTH_SHORT );
                return;
            }

            String dir = createStorageFolders( mac );
            if ( dir.equals( "" ) ) {
                makeToast( activityRef, R.string.toast_export_folder_fail, Toast.LENGTH_SHORT );
                return;
            }

            if ( !writeDeviceToCSV( db, dir, mac ) ) {
                makeToast( activityRef, R.string.toast_export_device_fail, Toast.LENGTH_SHORT );
                return;
            }

            if ( !writeInteractionToCSV( db, dir, mac ) ) {
                makeToast( activityRef, R.string.toast_export_interaction_fail, Toast.LENGTH_SHORT );
                return;
            }

            if ( !writeRSSIToCSV( db, dir, mac ) ) {
                makeToast( activityRef, R.string.toast_export_rssi_fail, Toast.LENGTH_SHORT );
                return;
            }

            makeToast( activityRef, R.string.toast_export_success, Toast.LENGTH_SHORT );
        } );
    }

    private static void makeToast( WeakReference<Activity> activity, int string_id, int length ) {
        activity.get().runOnUiThread( () -> {
            Toast.makeText( activity.get().getBaseContext(), string_id, length ).show();
        } );
    }

    private static String createStorageFolders( String mac ) {
        File folder = new File( Environment.getExternalStoragePublicDirectory( DIRECTORY_DOCUMENTS ),
                "Keep-Your-Distance/" + mac.replace( ":", "-" ) + "/" );
        if ( !folder.exists() ) {
            if ( !folder.mkdirs() ) {
                return "";
            }
        }

        return folder.getAbsolutePath();
    }

    private static boolean writeDeviceToCSV( DatabaseViewModel db, String dir, String mac ) {
        String csv = dir + "/device.csv";
        Device device = db.getDevice( mac );
        String[] headers = { "mac_address", "alias", "times_connected" };
        String[] record = { device.macAddress, device.alias, device.times_connected.toString() };

        try {
            CSVWriter writer = new CSVWriter( new FileWriter( csv ) );
            writer.writeNext( headers );
            writer.writeNext( record );
            writer.close();
        } catch ( IOException e ) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private static boolean writeInteractionToCSV( DatabaseViewModel db, String dir, String mac ) {
        String csv = dir + "/interaction.csv";
        List<Interaction> interactions = db.getInteractionsForReceiver( mac );
        String[] headers = { "sender", "receiver", "start_time", "end_time" };

        try {
            CSVWriter writer = new CSVWriter( new FileWriter( csv ) );
            writer.writeNext( headers );
            for ( Interaction interaction : interactions ) {
                String[] record = { interaction.sender, interaction.receiver, String.valueOf(
                        interaction.startTime.getTime() ), String.valueOf( interaction.endTime.getTime() ) };
                writer.writeNext( record );
            }
            writer.close();
        } catch ( IOException e ) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private static boolean writeRSSIToCSV( DatabaseViewModel db, String dir, String mac ) {
        String csv = dir + "/rssi.csv";
        List<RSSI> rssis = db.getRSSIForReceiver( mac );
        String[] headers = {    "sender_ref", "receiver_ref", "start_time_ref", "timestamp", "value",
                                "est_distance", "measured_power", "environment_var" };

        try {
            CSVWriter writer = new CSVWriter( new FileWriter( csv ) );
            writer.writeNext( headers );
            for (RSSI rssi : rssis) {
                String[] record = { rssi.senderRef, rssi.receiverRef,
                                    String.valueOf( rssi.startTimeRef.getTime() ),
                                    String.valueOf( rssi.timestamp.getTime() ),
                                    String.valueOf( rssi.value ),
                                    String.valueOf( rssi.estDistance ),
                                    String.valueOf( rssi.measuredPower ),
                                    String.valueOf( rssi.environmentVar ) };
                writer.writeNext( record );
            }
            writer.close();
        } catch ( IOException e ) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private static boolean gainFilePermissions( WeakReference<Activity> activity ) {
        if ( !checkFilePermissions( activity ) ) {
            ActivityCompat.requestPermissions( activity.get(),
                    new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE },
                    Keys.REQUEST_WRITE_EXTERNAL );
            return false;
        }
        return true;
    }

    private static boolean checkFilePermissions( WeakReference<Activity> activity ) {
        return ContextCompat.checkSelfPermission( activity.get(), Manifest.permission.WRITE_EXTERNAL_STORAGE ) ==
                PackageManager.PERMISSION_GRANTED;
    }

}
