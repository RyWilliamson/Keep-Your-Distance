package com.github.rywilliamson.configurator.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.rywilliamson.configurator.Interfaces.BluetoothContainer;
import com.github.rywilliamson.configurator.R;

public class SettingsFragment extends Fragment {

    private BluetoothContainer container;
    private Button update;
    private TextView result;

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

        update.setOnClickListener( this::updateClick );

        if (container.getConnected()) {
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