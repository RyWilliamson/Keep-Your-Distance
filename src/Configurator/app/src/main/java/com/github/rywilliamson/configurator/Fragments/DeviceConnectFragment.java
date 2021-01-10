package com.github.rywilliamson.configurator.Fragments;

import android.bluetooth.le.ScanResult;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.github.rywilliamson.configurator.Database.DatabaseViewModel;
import com.github.rywilliamson.configurator.Interfaces.IBackendContainer;
import com.github.rywilliamson.configurator.Interfaces.IBluetoothImplementer;
import com.github.rywilliamson.configurator.R;
import com.github.rywilliamson.configurator.Utils.BluetoothHandler;
import com.github.rywilliamson.configurator.Utils.Keys;
import com.github.rywilliamson.configurator.Utils.SpinnerUtils;
import com.welie.blessed.BluetoothCentralCallback;
import com.welie.blessed.BluetoothPeripheral;
import com.welie.blessed.BluetoothPeripheralCallback;

import java.util.ArrayList;
import java.util.List;

public class DeviceConnectFragment extends Fragment implements IBluetoothImplementer {

    private TextView prevMac;
    private Spinner macSpinner;
    private ArrayAdapter<String> macAdapter;
    private List<String> macList;
    private IBackendContainer container;
    private BluetoothHandler bt;
    private DatabaseViewModel db;

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

        container = (IBackendContainer) getActivity();
        bt = container.getBluetoothHandler();
        db = container.getDatabaseViewModel();

        macSpinner = view.findViewById( R.id.spDcMacs );
        macList = new ArrayList<>();
        macAdapter = new ArrayAdapter<>( view.getContext(), R.layout.mac_address_item, macList );
        macAdapter.setDropDownViewResource( R.layout.mac_address_item );
        macSpinner.setAdapter( macAdapter );

        prevMac = view.findViewById( R.id.tvDcMacText );
        prevMac.setText( bt.getPrevMac() );

        view.findViewById( R.id.bDcConnect ).setOnClickListener( this::connectClick );
        view.findViewById( R.id.bDcReconnect ).setOnClickListener( this::reconnectClick );
        view.findViewById( R.id.bDcScan ).setOnClickListener( this::scanClick );
    }

    public void connectClick( View view ) {
        if ( macList != null && !macList.isEmpty() ) {
            container.directConnect( macSpinner.getSelectedItem().toString() );
        }
    }

    public void reconnectClick( View view ) {
        String mac = prevMac.getText().toString();
        if ( !mac.equals( "" ) ) {
            container.directConnect( mac );
        }
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
        public void onDiscoveredPeripheral( BluetoothPeripheral peripheral, @NonNull ScanResult scanResult ) {
            Log.d( Keys.CONNECTION_CENTRAL, "Adding item for: " + peripheral.getAddress() );
            SpinnerUtils.addItem( macList, macAdapter, peripheral.getAddress() );
        }

        @Override
        public void onConnectedPeripheral( @NonNull BluetoothPeripheral peripheral ) {
            Log.d( Keys.CONNECTION_CENTRAL, "Connected! Switching View to Device Info" );
//            String mac = peripheral.getAddress();
//            db.insert( new Device( mac, mac, 1 ) );
            Navigation.findNavController( DeviceConnectFragment.this.getView() ).navigate(
                    DeviceConnectFragmentDirections.actionDeviceConnectFragmentToDeviceInfoFragment2() );
        }

        @Override
        public void onConnectionFailed( BluetoothPeripheral peripheral, int status ) {
            Log.d( Keys.CONNECTION_CENTRAL, "Not Connected to " + peripheral.getAddress() );
            Toast.makeText( getContext(), R.string.toast_no_connect, Toast.LENGTH_SHORT ).show();
        }

        @Override
        public void onScanFailed( int errorCode ) {
            super.onScanFailed( errorCode );
            Log.d( Keys.CONNECTION_CENTRAL, "Scan Failed with error code: " + errorCode );
            Toast.makeText( getContext(), R.string.toast_scan_fail, Toast.LENGTH_LONG ).show();
        }

        @Override
        public void onDisconnectedPeripheral( @NonNull BluetoothPeripheral peripheral, int status ) {

        }
    };

    private final BluetoothPeripheralCallback peripheralCallback = new BluetoothPeripheralCallback() {
    };
}