package com.github.rywilliamson.configurator.Fragments;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.le.ScanResult;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.rywilliamson.configurator.Interfaces.BluetoothContainer;
import com.github.rywilliamson.configurator.Interfaces.BluetoothImplementer;
import com.github.rywilliamson.configurator.R;
import com.welie.blessed.BluetoothCentralCallback;
import com.welie.blessed.BluetoothPeripheral;
import com.welie.blessed.BluetoothPeripheralCallback;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


public class DevExperimentFragment extends Fragment implements BluetoothImplementer {

    private Button backButton;
    private Button interactButton;
    private TextView outputTextView;

    private BluetoothContainer container;
    private List<Integer> rssiValues;
    private boolean capturing;
    private int counter;
    private final int VALUECOUNT = 10;

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
        outputTextView.setText( R.string.f_dev_capturing );
        container.getPeripheral().setNotify( container.getRssiCharacteristic(), true );
    }

    private void cancel() {
        capturing = false;
        interactButton.setText( R.string.f_dev_begin );
        container.getPeripheral().setNotify( container.getRssiCharacteristic(), false );
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
                outputTextView.setText( R.string.f_dev_done );
                cancel();
                return;
            }
            rssiValues.add( val );
            counter++;
        }
    };
}