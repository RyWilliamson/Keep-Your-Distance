package com.github.rywilliamson.configurator.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.rywilliamson.configurator.Interfaces.BluetoothContainer;
import com.github.rywilliamson.configurator.Interfaces.BluetoothImplementer;
import com.github.rywilliamson.configurator.R;
import com.welie.blessed.BluetoothCentralCallback;
import com.welie.blessed.BluetoothPeripheralCallback;

public class DeviceInfoFragment extends Fragment implements BluetoothImplementer {

    private BluetoothContainer container;

    private TextView totalInfo;
    private TextView currentInfo;

    public DeviceInfoFragment() {
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
        return inflater.inflate( R.layout.fragment_device_info, container, false );
    }

    @Override
    public void onViewCreated( @NonNull View view, @Nullable Bundle savedInstanceState ) {
        super.onViewCreated( view, savedInstanceState );

        totalInfo = view.findViewById( R.id.tvDiTotalInfo );
        currentInfo = view.findViewById( R.id.tvDiCurrentInfo );

        view.findViewById( R.id.bDiExport ).setOnClickListener( this::exportClick );
        view.findViewById( R.id.bDiClear ).setOnClickListener( this::clearClick );
        view.findViewById( R.id.bDiDisconnect ).setOnClickListener( this::disconnectClick );
    }

    public void exportClick(View view) {

    }

    public void clearClick(View view) {
        totalInfo.setText("0");
        currentInfo.setText("0");
    }

    public void disconnectClick(View view) {
        container.setConnected( false );
        Navigation.findNavController( view ).navigate(
                DeviceInfoFragmentDirections.actionDeviceInfoFragmentToDeviceConnectFragment2() );
    }

    @Override
    public BluetoothCentralCallback getCentralCallback() {
        return null;
    }

    @Override
    public BluetoothPeripheralCallback getPeripheralCallback() {
        return null;
    }
}