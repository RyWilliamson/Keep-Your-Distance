package com.github.rywilliamson.configurator.Fragments;

import android.Manifest;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.le.ScanResult;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.github.rywilliamson.configurator.Interfaces.BackendContainer;
import com.github.rywilliamson.configurator.Interfaces.BluetoothImplementer;
import com.github.rywilliamson.configurator.R;
import com.github.rywilliamson.configurator.Utils.BluetoothHandler;
import com.github.rywilliamson.configurator.Utils.Keys;
import com.opencsv.CSVWriter;
import com.welie.blessed.BluetoothCentralCallback;
import com.welie.blessed.BluetoothPeripheral;
import com.welie.blessed.BluetoothPeripheralCallback;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;


public class DevExperimentFragment extends Fragment implements BluetoothImplementer {

    private Button backButton;
    private Button interactButton;
    private TextView outputTextView;

    private BackendContainer container;
    private BluetoothHandler bt;
    private List<String[]> rssiValues;
    private boolean capturing;
    private int counter;
    private final int VALUECOUNT = 250;

    public DevExperimentFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
    }

    @Override
    public void onViewCreated( @NonNull View view, @Nullable Bundle savedInstanceState ) {
        super.onViewCreated( view, savedInstanceState );
        container = DevExperimentFragmentArgs.fromBundle( getArguments() ).getContainer();
        bt = container.getBluetoothHandler();
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState ) {
        // Inflate the layout for this fragment
        View view = inflater.inflate( R.layout.fragment_dev_experiment, container, false );
        backButton = view.findViewById( R.id.f_dev_back );
        interactButton = view.findViewById( R.id.f_dev_interact );
        outputTextView = view.findViewById( R.id.f_dev_output );

        rssiValues = new ArrayList<>();
        capturing = false;

        setupButtons();

        return view;
    }

    private void setupButtons() {
        backButton.setOnClickListener( this::back );
        interactButton.setOnClickListener( this::interactPress );
    }

    private void back( View view ) {
        NavDirections action = DevExperimentFragmentDirections.actionDevExperimentFragmentToSyncDeviceFragment();
        Navigation.findNavController( view ).navigate( action );
    }

    private void interactPress( View view ) {
        if ( capturing ) {
            outputTextView.setText( R.string.f_dev_waiting );
            cancel();
            return;
        }
        start();
    }

    private void start() {
        counter = 0;
        capturing = true;
        interactButton.setText( R.string.f_dev_cancel );
        bt.getBLEPeripheral().setNotify( bt.getRssiCharacteristic(), true );
    }

    private void cancel() {
        capturing = false;
        interactButton.setText( R.string.f_dev_begin );
        bt.getBLEPeripheral().setNotify( bt.getRssiCharacteristic(), false );
    }

    private void writeCSV() {
        checkFilePermissions();
        String csv = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/output.csv";
        try {
            CSVWriter writer = new CSVWriter( new FileWriter( csv ) );
            writer.writeAll( rssiValues );
            writer.close();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    private void checkFilePermissions() {
        if ( ContextCompat.checkSelfPermission( getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( getActivity(),
                    new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE },
                    Keys.REQUEST_WRITE_EXTERNAL );
        }
    }

    @Override
    public BluetoothCentralCallback getCentralCallback() {
        return bluetoothCentralCallback;
    }

    @Override
    public BluetoothPeripheralCallback getPeripheralCallback() {
        return peripheralCallback;
    }

    private final BluetoothCentralCallback bluetoothCentralCallback = new BluetoothCentralCallback() {
        @Override
        public void onDiscoveredPeripheral( BluetoothPeripheral peripheral,
                ScanResult scanResult ) {

        }

        @Override
        public void onConnectedPeripheral( BluetoothPeripheral peripheral ) {

        }

        @Override
        public void onConnectionFailed( BluetoothPeripheral peripheral, int status ) {

        }

        @Override
        public void onDisconnectedPeripheral( BluetoothPeripheral peripheral, int status ) {

        }
    };

    private final BluetoothPeripheralCallback peripheralCallback = new BluetoothPeripheralCallback() {
        @Override
        public void onServicesDiscovered( BluetoothPeripheral peripheral ) {

        }

        @Override
        public void onCharacteristicUpdate( BluetoothPeripheral peripheral, byte[] value,
                BluetoothGattCharacteristic characteristic, int status ) {
            super.onCharacteristicUpdate( peripheral, value, characteristic, status );

            int val = ByteBuffer.wrap( value ).order( ByteOrder.LITTLE_ENDIAN ).getInt();
            if ( counter >= VALUECOUNT ) {
                Log.i( "RSSI Values", rssiValues.toString() );
                writeCSV();
                outputTextView.setText( R.string.f_dev_done );
                cancel();
                return;
            }
            rssiValues.add( new String[]{ String.valueOf( counter ), String.valueOf( val ) } );
            outputTextView.setText( getResources().getString( R.string.f_dev_capturing, counter, VALUECOUNT ) );
            counter++;
        }
    };
}