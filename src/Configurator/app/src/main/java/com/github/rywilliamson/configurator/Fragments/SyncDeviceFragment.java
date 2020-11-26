package com.github.rywilliamson.configurator.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.github.rywilliamson.configurator.Interfaces.BluetoothContainer;
import com.github.rywilliamson.configurator.R;

public class SyncDeviceFragment extends Fragment {

    private BluetoothContainer container;

    private Button syncButton;
    private Button experimentButton;

    public SyncDeviceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        container = (BluetoothContainer) getActivity();
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState ) {
        // Inflate the layout for this fragment
        View view = inflater.inflate( R.layout.fragment_sync_device, container, false );
        syncButton = view.findViewById( R.id.fSyncConnectButton );
        experimentButton = view.findViewById( R.id.fSyncToExperimentButton );
        setupButtons();
        return view;
    }

    private void setupButtons() {
        syncButton.setOnClickListener( this::connectToDevice );
        experimentButton.setOnClickListener( this::moveToExperiment );
        Log.d("test", "test");
    }

    private void connectToDevice( View view ) {
        Log.d("test", "test");
        container.getCentral().scanForPeripheralsWithNames( new String[]{ "ESP32" } );
    }

    private void moveToExperiment( View view ) {
        NavDirections action = SyncDeviceFragmentDirections.actionSyncDeviceFragmentToDevExperiment(
                container );
        Navigation.findNavController( view ).navigate( action );
    }
}