package com.github.rywilliamson.configurator.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.github.rywilliamson.configurator.Database.DatabaseViewModel;
import com.github.rywilliamson.configurator.Interfaces.IBackendContainer;
import com.github.rywilliamson.configurator.Interfaces.IBluetoothImplementer;
import com.github.rywilliamson.configurator.NavGraphGraphDirections;
import com.github.rywilliamson.configurator.R;
import com.github.rywilliamson.configurator.Utils.SpinnerUtils;
import com.welie.blessed.BluetoothCentralCallback;
import com.welie.blessed.BluetoothPeripheral;
import com.welie.blessed.BluetoothPeripheralCallback;

import java.util.ArrayList;
import java.util.List;

import static com.github.rywilliamson.configurator.Utils.Keys.GRAPH_SPINNER;

public class GraphFragment extends Fragment implements IBluetoothImplementer {

    private Spinner graphSpinner;
    private ArrayAdapter<String> graphAdapter;
    private List<String> graphList;
    private IBackendContainer container;
    private DatabaseViewModel dbViewModel;
    private boolean graphInitFlag;

    private ImageView image;

    public GraphFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        container = (IBackendContainer) getActivity();
        dbViewModel = container.getDatabaseViewModel();
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

        graphInitFlag = true;
        graphSpinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected( AdapterView<?> parent, View view, int position, long id ) {
                if ( graphInitFlag ) {
                    graphInitFlag = false;
                    return;
                }

                NavDirections action = null;
                switch (position) {
                    case 0:
                        action = NavGraphGraphDirections.actionGlobalWeeklyInteractionsFragment();
                        break;
                    case 1:
                        action = NavGraphGraphDirections.actionGlobalTotalInteractionFragment();
                        break;
                    case 2:
                        action = NavGraphGraphDirections.actionGlobalInteractionsOverTimeFragment();
                        break;
                    default:
                        Log.d(GRAPH_SPINNER, "Invalid fragment selection - something went very wrong.");
                        return;
                }
                Navigation.findNavController( getActivity(), R.id.GraphNavigator ).navigate( action );
            }

            @Override
            public void onNothingSelected( AdapterView<?> parent ) {
                // Do Nothing
            }
        } );

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