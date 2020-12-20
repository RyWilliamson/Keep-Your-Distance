package com.github.rywilliamson.configurator.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.rywilliamson.configurator.Interfaces.BackendContainer;
import com.github.rywilliamson.configurator.Interfaces.BluetoothImplementer;
import com.github.rywilliamson.configurator.R;
import com.github.rywilliamson.configurator.Utils.SpinnerUtils;
import com.welie.blessed.BluetoothCentralCallback;
import com.welie.blessed.BluetoothPeripheral;
import com.welie.blessed.BluetoothPeripheralCallback;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment implements BluetoothImplementer {

    private BackendContainer container;
    private Button update;
    private TextView result;
    private ImageView image;

    private Spinner distSpinner;
    private ArrayAdapter<String> distAdapter;
    private List<String> distList;

    private Spinner profileSpinner;
    private ArrayAdapter<String> profileAdapter;
    private List<String> profileList;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        container = (BackendContainer) getActivity();
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState ) {
        // Inflate the layout for this fragment
        return inflater.inflate( R.layout.fragment_settings, container, false );
    }

    @Override
    public void onViewCreated( @NonNull View view, @Nullable Bundle savedInstanceState ) {
        super.onViewCreated( view, savedInstanceState );

        update = view.findViewById( R.id.bSUpdate );
        result = view.findViewById( R.id.tvSResult );
        image = view.findViewById( R.id.ivConnected );

        distSpinner = view.findViewById( R.id.spSDistance );
        distList = new ArrayList<>();
        distAdapter = new ArrayAdapter<>( view.getContext(), R.layout.mac_address_item, distList );
        distAdapter.setDropDownViewResource( R.layout.mac_address_item );
        distSpinner.setAdapter( distAdapter );

        SpinnerUtils.addItem( distList, distAdapter, "1.0" );
        SpinnerUtils.addItem( distList, distAdapter, "1.5" );
        SpinnerUtils.addItem( distList, distAdapter, "2.0" );

        profileSpinner = view.findViewById( R.id.spSProfile );
        profileList = new ArrayList<>();
        profileAdapter = new ArrayAdapter<>( view.getContext(),
                R.layout.mac_address_item, profileList );
        profileAdapter.setDropDownViewResource( R.layout.mac_address_item );
        profileSpinner.setAdapter( profileAdapter );

        SpinnerUtils.addItem( profileList, profileAdapter, "Indoor" );
        SpinnerUtils.addItem( profileList, profileAdapter, "City Outdoor" );
        SpinnerUtils.addItem( profileList, profileAdapter, "Nature Outdoor" );

        update.setOnClickListener( this::updateClick );

        if ( container.getBluetoothHandler().isConnected() ) {
            updateComponents( View.VISIBLE, View.INVISIBLE, R.drawable.ic_connected );
        } else {
            updateComponents( View.GONE, View.GONE, R.drawable.ic_not_connected );
        }
    }

    public void updateClick( View view ) {
        result.setVisibility( View.VISIBLE );
    }

    private void updateComponents( final int updateVal, final int resultVal, final int icon ) {
        update.setVisibility( updateVal );
        result.setVisibility( resultVal );
        image.setImageResource( icon );
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
        public void onConnectedPeripheral( @NonNull BluetoothPeripheral peripheral ) {
            updateComponents( View.VISIBLE, View.INVISIBLE, R.drawable.ic_connected );
        }

        @Override
        public void onDisconnectedPeripheral( @NonNull BluetoothPeripheral peripheral, int status ) {
            updateComponents( View.GONE, View.GONE, R.drawable.ic_not_connected );
        }
    };

    private final BluetoothPeripheralCallback peripheralCallback = new BluetoothPeripheralCallback() {

    };
}