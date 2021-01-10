package com.github.rywilliamson.configurator.Fragments;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.le.ScanResult;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.github.rywilliamson.configurator.Interfaces.IBackendContainer;
import com.github.rywilliamson.configurator.Interfaces.IBluetoothImplementer;
import com.github.rywilliamson.configurator.R;
import com.github.rywilliamson.configurator.Utils.BluetoothHandler;
import com.welie.blessed.BluetoothCentralCallback;
import com.welie.blessed.BluetoothPeripheral;
import com.welie.blessed.BluetoothPeripheralCallback;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class DevSyncDeviceFragment extends Fragment implements IBluetoothImplementer {

    private IBackendContainer container;
    private BluetoothHandler bt;

    private Button syncButton;
    private Button experimentButton;

    public DevSyncDeviceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        container = (IBackendContainer) getActivity();
        bt = container.getBluetoothHandler();
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState ) {
        // Inflate the layout for this fragment
        View view = inflater.inflate( R.layout.fragment_dev_sync_device, container, false );
        syncButton = view.findViewById( R.id.fSyncConnectButton );
        experimentButton = view.findViewById( R.id.fSyncToExperimentButton );
        setupButtons();
        return view;
    }

    private void setupButtons() {
        syncButton.setOnClickListener( this::connectToDevice );
        experimentButton.setOnClickListener( this::moveToExperiment );
    }

    private void connectToDevice( View view ) {
        bt.checkBLEPermissions( this.getActivity() );
        bt.getCentral().scanForPeripheralsWithNames( new String[]{ "ESP32" } );
    }

    private void moveToExperiment( View view ) {
        NavDirections action = DevSyncDeviceFragmentDirections.actionSyncDeviceFragmentToDevExperiment( container );
        Navigation.findNavController( view ).navigate( action );
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
            experimentButton.setVisibility( VISIBLE );
        }

        @Override
        public void onConnectionFailed( BluetoothPeripheral peripheral, int status ) {
            experimentButton.setVisibility( GONE );
        }

        @Override
        public void onDisconnectedPeripheral( BluetoothPeripheral peripheral, int status ) {
            experimentButton.setVisibility( GONE );
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
        }
    };
}