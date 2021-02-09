package com.github.rywilliamson.configurator.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.github.rywilliamson.configurator.Database.DatabaseExporter;
import com.github.rywilliamson.configurator.Database.DatabaseViewModel;
import com.github.rywilliamson.configurator.Database.Entity.Device;
import com.github.rywilliamson.configurator.Database.RSSIDatabase;
import com.github.rywilliamson.configurator.Interfaces.IBackendContainer;
import com.github.rywilliamson.configurator.Interfaces.IBluetoothImplementer;
import com.github.rywilliamson.configurator.R;
import com.github.rywilliamson.configurator.Utils.BluetoothHandler;
import com.welie.blessed.BluetoothCentralCallback;
import com.welie.blessed.BluetoothPeripheral;
import com.welie.blessed.BluetoothPeripheralCallback;

public class DeviceInfoFragment extends Fragment implements IBluetoothImplementer {

    private IBackendContainer container;

    private TextView totalInfo;
    private TextView currentInfo;
    private TextView aliasInfo;

    private DatabaseViewModel db;
    private BluetoothHandler bt;

    private int oldCount = 0;
    private int curCount;
    private boolean clearing = false;

    public DeviceInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        container = (IBackendContainer) getActivity();

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
        aliasInfo = view.findViewById( R.id.tvDiAliasInfo );

        curCount = -1;

        RSSIDatabase.databaseGetExecutor.execute( () -> {
            Device device = db.getDevice( bt.getBLEPeripheral().getAddress() );
            getActivity().runOnUiThread( () -> {
                ( (TextView) view.findViewById( R.id.tvDiMacInfo ) ).setText( device.macAddress );
                ( (TextView) view.findViewById( R.id.tvDiAliasInfo ) ).setText( device.alias );
            } );
        } );

        view.findViewById( R.id.bDiExport ).setOnClickListener( this::exportClick );
        view.findViewById( R.id.bDiClear ).setOnClickListener( this::clearClick );
        view.findViewById( R.id.bDiRename ).setOnClickListener( this::renameClick );
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

    public void renameClick( View view ) {
        AlertDialog.Builder alert = new AlertDialog.Builder( getContext() );
        alert.setTitle( R.string.f_di_rename );

        final EditText input = new EditText( getContext() );
        input.setSingleLine();
        input.setHint( R.string.f_di_rename_msg );
        FrameLayout container = new FrameLayout( getContext() );
        FrameLayout.LayoutParams parameters = new FrameLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT );
        parameters.leftMargin = getResources().getDimensionPixelSize( R.dimen.rename_margin );
        parameters.rightMargin = getResources().getDimensionPixelSize( R.dimen.rename_margin );
        input.setLayoutParams( parameters );
        container.addView( input );
        alert.setView( container );

        alert.setPositiveButton( "Ok", ( dialog, which ) -> {
            db.updateAlias( bt.getBLEPeripheral().getAddress(), input.getText().toString() );
            aliasInfo.setText( input.getText().toString() );
        } );

        alert.setNegativeButton( "Cancel", ( dialog, which ) -> {
        } );

        alert.show();
    }

    public void exportClick( View view ) {
        DatabaseExporter.export( getActivity(), db, bt.getBLEPeripheral().getAddress() );
    }

    public void clearClick( View view ) {
        currentInfo.setText( "0" );
        totalInfo.setText( "0" );
        aliasInfo.setText( bt.getBLEPeripheral().getAddress() );
        oldCount = 0;
        curCount = 0;
        clearing = true;
        bt.disconnect();
    }

    public void disconnectClick( View view ) {
        Log.d("test", String.valueOf( clearing ) );
        if (clearing) {
            Toast.makeText( this.getActivity(), R.string.toast_disconnect_during_clear, Toast.LENGTH_LONG ).show();
        } else {
            container.getBluetoothHandler().disconnect();
        }
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
        public void onConnectedPeripheral( BluetoothPeripheral peripheral ) {
            clearing = false;
        }

        @Override
        public void onDisconnectedPeripheral( @NonNull BluetoothPeripheral peripheral, int status ) {
            if ( !clearing ) {
                Navigation.findNavController( DeviceInfoFragment.this.getView() ).navigate(
                        DeviceInfoFragmentDirections.actionDeviceInfoFragmentToDeviceConnectFragment2() );
            } else {
                db.clearReceiver( peripheral.getAddress() );
                container.directConnect( peripheral.getAddress() );
            }
        }

        @Override
        public void onConnectionFailed( @NonNull BluetoothPeripheral peripheral, int status ) {
            Navigation.findNavController( DeviceInfoFragment.this.getView() ).navigate(
                    DeviceInfoFragmentDirections.actionDeviceInfoFragmentToDeviceConnectFragment2() );
        }
    };

    private final BluetoothPeripheralCallback peripheralCallback = new BluetoothPeripheralCallback() {

    };
}