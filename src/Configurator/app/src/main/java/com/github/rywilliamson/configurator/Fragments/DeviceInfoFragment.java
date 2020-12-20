package com.github.rywilliamson.configurator.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.github.rywilliamson.configurator.Database.DatabaseViewModel;
import com.github.rywilliamson.configurator.Interfaces.BackendContainer;
import com.github.rywilliamson.configurator.Interfaces.BluetoothImplementer;
import com.github.rywilliamson.configurator.R;
import com.github.rywilliamson.configurator.Utils.BluetoothHandler;
import com.welie.blessed.BluetoothCentralCallback;
import com.welie.blessed.BluetoothPeripheral;
import com.welie.blessed.BluetoothPeripheralCallback;

public class DeviceInfoFragment extends Fragment implements BluetoothImplementer {

    private BackendContainer container;

    private TextView totalInfo;
    private TextView currentInfo;

    private DatabaseViewModel db;
    private BluetoothHandler bt;

    private int oldCount = 0;
    private int curCount;

    public DeviceInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        container = (BackendContainer) getActivity();

        bt = container.getBluetoothHandler();
        db = container.getDatabaseViewModel();
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState ) {
        // Inflate the layout for this fragment
        return inflater.inflate( R.layout.fragment_device_info, container, false );
    }

    @Override
    public void onViewCreated( @NonNull View view, @Nullable Bundle savedInstanceState ) {
        super.onViewCreated( view, savedInstanceState );

        totalInfo = view.findViewById( R.id.tvDiTotalInfo );
        currentInfo = view.findViewById( R.id.tvDiCurrentInfo );
        curCount = -1;

        ( (TextView) view.findViewById( R.id.tvDiMacInfo ) ).setText( bt.getBLEPeripheral().getAddress() );

        view.findViewById( R.id.bDiExport ).setOnClickListener( this::exportClick );
        view.findViewById( R.id.bDiClear ).setOnClickListener( this::clearClick );
        view.findViewById( R.id.bDiDisconnect ).setOnClickListener( this::disconnectClick );

        db.setInteractionCountReceiver( bt.getBLEPeripheral().getAddress() );
        db.getConnectedInteractionCount().observe( getActivity(), count -> {
            if ( oldCount != count ) {
                totalInfo.setText( String.valueOf( count ) );
                currentInfo.setText( String.valueOf( ++curCount ) );
                oldCount = count;
            }
        } );


    }

    public void exportClick( View view ) {

    }

    public void clearClick( View view ) {
        currentInfo.setText( "0" );
        totalInfo.setText( "0" );
    }

    public void disconnectClick( View view ) {
        container.getBluetoothHandler().disconnect();
    }

    @Override
    public BluetoothCentralCallback getCentralCallback() {
        return centralCallback;
    }

    @Override
    public BluetoothPeripheralCallback getPeripheralCallback() {
        return peripheralCallback;
    }

    private final BluetoothCentralCallback centralCallback = new BluetoothCentralCallback() {
        @Override
        public void onDisconnectedPeripheral( @NonNull BluetoothPeripheral peripheral, int status ) {
            Navigation.findNavController( DeviceInfoFragment.this.getView() ).navigate(
                    DeviceInfoFragmentDirections.actionDeviceInfoFragmentToDeviceConnectFragment2() );
        }
    };

    private final BluetoothPeripheralCallback peripheralCallback = new BluetoothPeripheralCallback() {

    };
}