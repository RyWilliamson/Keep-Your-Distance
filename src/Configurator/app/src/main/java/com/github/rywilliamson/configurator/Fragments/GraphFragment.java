package com.github.rywilliamson.configurator.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

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

public class GraphFragment extends Fragment implements BluetoothImplementer {

    private Spinner graphSpinner;
    private ArrayAdapter<String> graphAdapter;
    private List<String> graphList;
    private BackendContainer container;

    private ImageView image;

    public GraphFragment() {
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
        return inflater.inflate( R.layout.fragment_graph, container, false );
    }

    @Override
    public void onViewCreated( @NonNull View view, @Nullable Bundle savedInstanceState ) {
        super.onViewCreated( view, savedInstanceState );

        image = view.findViewById( R.id.ivConnected );

        graphSpinner = view.findViewById( R.id.spGSelector );
        graphList = new ArrayList<>();
        graphAdapter = new ArrayAdapter<>( view.getContext(),
                R.layout.mac_address_item, graphList );
        graphAdapter.setDropDownViewResource( R.layout.mac_address_item );
        graphSpinner.setAdapter( graphAdapter );

        SpinnerUtils.addItem( graphList, graphAdapter, "Weekly Interactions" );
        SpinnerUtils.addItem( graphList, graphAdapter, "Total Interactions" );
        SpinnerUtils.addItem( graphList, graphAdapter, "Interactions Over Time" );

        if ( container.getBluetoothHandler().isConnected() ) {
            image.setImageResource( R.drawable.ic_connected );
        } else {
            image.setImageResource( R.drawable.ic_not_connected );
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
        public void onConnectedPeripheral( @NonNull BluetoothPeripheral peripheral ) {
            image.setImageResource( R.drawable.ic_connected );
        }

        @Override
        public void onDisconnectedPeripheral( @NonNull BluetoothPeripheral peripheral, int status ) {
            image.setImageResource( R.drawable.ic_not_connected );
        }
    };

    private final BluetoothPeripheralCallback peripheralCallback = new BluetoothPeripheralCallback() {

    };
}