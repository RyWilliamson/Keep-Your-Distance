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

import com.github.rywilliamson.configurator.Interfaces.BluetoothContainer;
import com.github.rywilliamson.configurator.R;
import com.github.rywilliamson.configurator.Utils.SpinnerUtils;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment {

    private BluetoothContainer container;
    private Button update;
    private TextView result;

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
        container = (BluetoothContainer) getActivity();
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
        ImageView image = view.findViewById( R.id.ivConnected );

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

        if ( container.getConnected() ) {
            update.setVisibility( View.VISIBLE );
            result.setVisibility( View.INVISIBLE );
            image.setImageResource( R.drawable.ic_connected );

        } else {
            update.setVisibility( View.GONE );
            result.setVisibility( View.GONE );
            image.setImageResource( R.drawable.ic_not_connected );
        }
    }

    public void updateClick( View view ) {
        result.setVisibility( View.VISIBLE );
    }
}