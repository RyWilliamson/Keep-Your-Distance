package com.github.rywilliamson.configurator.Fragments;

import android.bluetooth.le.ScanResult;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.github.rywilliamson.configurator.Interfaces.BluetoothContainer;
import com.github.rywilliamson.configurator.Interfaces.BluetoothImplementer;
import com.github.rywilliamson.configurator.R;
import com.github.rywilliamson.configurator.Utils.Keys;
import com.github.rywilliamson.configurator.Utils.SpinnerUtils;
import com.welie.blessed.BluetoothCentralCallback;
import com.welie.blessed.BluetoothPeripheral;
import com.welie.blessed.BluetoothPeripheralCallback;

import java.util.ArrayList;
import java.util.List;

public class DeviceConnectFragment extends Fragment implements BluetoothImplementer {

    private Spinner macSpinner;
    private ArrayAdapter<String> macAdapter;
    private List<String> macList;
    private BluetoothContainer container;

    public DeviceConnectFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState ) {
        return inflater.inflate( R.layout.fragment_device_connect, container, false );
    }

    @Override
    public void onViewCreated( @NonNull View view, @Nullable Bundle savedInstanceState ) {
        super.onViewCreated( view, savedInstanceState );

        container = (BluetoothContainer) getActivity();

        macSpinner = view.findViewById( R.id.spDcMacs );
        macList = new ArrayList<>();
        macAdapter = new ArrayAdapter<>( view.getContext(),
                R.layout.mac_address_item, macList );
        macAdapter.setDropDownViewResource( R.layout.mac_address_item );
        macSpinner.setAdapter( macAdapter );

        view.findViewById( R.id.bDcConnect ).setOnClickListener( this::connectClick );
        view.findViewById( R.id.bDcReconnect ).setOnClickListener( this::reconnectClick );
        view.findViewById( R.id.bDcScan ).setOnClickListener( this::scanClick );

//        SpinnerUtils.addItem( macList, macAdapter, "00:11:22:33:FF:EE" );
//        SpinnerUtils.addItem( macList, macAdapter, "00:11:22:33:FF:ED" );
    }

    public void connectClick( View view ) {
        if ( macList != null && !macList.isEmpty() ) {
            container.directConnect( macSpinner.getSelectedItem().toString() );
        }
    }

    public void reconnectClick( View view ) {
        //container.setConnected( true );
//        Navigation.findNavController( view ).navigate(
//                DeviceConnectFragmentDirections.actionDeviceConnectFragmentToDeviceInfoFragment2() );
    }

    public void scanClick( View view ) {
        macList.clear();
        container.scan();
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
        public void onDiscoveredPeripheral( BluetoothPeripheral peripheral, ScanResult scanResult ) {
            Log.d( Keys.CONNECTION_CENTRAL, "Adding item for: " + peripheral.getAddress() );
            SpinnerUtils.addItem( macList, macAdapter, peripheral.getAddress() );
        }

        @Override
        public void onConnectedPeripheral( BluetoothPeripheral peripheral ) {
            Log.d( Keys.CONNECTION_CENTRAL, "Connected! Switching View to Device Info" );
            Navigation.findNavController( DeviceConnectFragment.this.getView() ).navigate(
                    DeviceConnectFragmentDirections.actionDeviceConnectFragmentToDeviceInfoFragment2() );
        }
    };

    private final BluetoothPeripheralCallback peripheralCallback = new BluetoothPeripheralCallback() {

    };
}