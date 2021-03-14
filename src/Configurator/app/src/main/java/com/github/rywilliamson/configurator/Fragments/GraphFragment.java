package com.github.rywilliamson.configurator.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;

import com.github.rywilliamson.configurator.DatabaseUI.DatabaseViewModel;
import com.github.rywilliamson.configurator.Database.Entity.Device;
import com.github.rywilliamson.configurator.Database.RSSIDatabase;
import com.github.rywilliamson.configurator.Interfaces.IBackendContainer;
import com.github.rywilliamson.configurator.Interfaces.IBluetoothImplementer;
import com.github.rywilliamson.configurator.Interfaces.IGraphImplementer;
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

    private Spinner deviceSpinner;
    private ArrayAdapter<Device> deviceAdapter;
    private List<Device> deviceList;
    private final Device fakeDevice = new Device( "FF:FF:FF:FF:FF:FF", "Test Device", 0 );

    private IBackendContainer container;
    private DatabaseViewModel dbViewModel;
    private boolean graphInitFlag;
    private boolean deviceInitFlag;

    private ImageView image;
    private IGraphImplementer current;

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

        if (dbViewModel == null) {
            container = (IBackendContainer) getActivity();
            dbViewModel = container.getDatabaseViewModel();
        }

        deviceList = new ArrayList<>();
        RSSIDatabase.databaseGetExecutor.execute( () -> {
            List<Device> devices = dbViewModel.getUsedDevices();
            for ( Device device : devices ) {
                if ( dbViewModel.getInteractionCountByReceiverNow( device.macAddress ) != 0 ) {
                    deviceList.add( device );
                }
            }
            deviceList.add( fakeDevice );

            getActivity().runOnUiThread( () -> {
                deviceSpinner = view.findViewById( R.id.spDSelector );
                deviceAdapter = new ArrayAdapter<>( view.getContext(), R.layout.mac_address_item, deviceList );
                deviceAdapter.setDropDownViewResource( R.layout.mac_address_item );
                deviceAdapter.notifyDataSetChanged();
                deviceSpinner.setAdapter( deviceAdapter );

                deviceInitFlag = true;
                deviceSpinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected( AdapterView<?> parent, View view, int position, long id ) {
                        if ( deviceInitFlag ) {
                            deviceInitFlag = false;
                            return;
                        }

                        String mac = deviceList.get( position ).macAddress;
                        getCurrentImplementer().updateData( mac );
                    }

                    @Override
                    public void onNothingSelected( AdapterView<?> parent ) {
                        // Do Nothing
                    }
                } );
            } );
        } );

        graphSpinner = view.findViewById( R.id.spGSelector );
        graphList = new ArrayList<>();
        graphAdapter = new ArrayAdapter<>( view.getContext(),
                R.layout.mac_address_item, graphList );
        graphAdapter.setDropDownViewResource( R.layout.mac_address_item );
        graphSpinner.setAdapter( graphAdapter );

        SpinnerUtils.addItem( graphList, graphAdapter, "Weekly Interactions" );
        SpinnerUtils.addItem( graphList, graphAdapter, "Duration & Distance" );
        SpinnerUtils.addItem( graphList, graphAdapter, "Interactions Over Time" );

        graphInitFlag = true;
        graphSpinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected( AdapterView<?> parent, View view, int position, long id ) {
                if ( graphInitFlag ) {
                    graphInitFlag = false;
                    return;
                }

                String mac = ( (Device) deviceSpinner.getSelectedItem() ).macAddress;
                switch ( position ) {
                    case 0:
                        NavGraphGraphDirections.ActionGlobalWeeklyInteractionsFragment action =
                                NavGraphGraphDirections.actionGlobalWeeklyInteractionsFragment();
                        action.setDeviceMac( mac );
                        Navigation.findNavController( getActivity(), R.id.GraphNavigator ).navigate( action );
                        break;
                    case 1:
                        NavGraphGraphDirections.ActionGlobalTotalInteractionFragment action2 =
                                NavGraphGraphDirections.actionGlobalTotalInteractionFragment();
                        action2.setDeviceMac( mac );
                        Navigation.findNavController( getActivity(), R.id.GraphNavigator ).navigate( action2 );
                        break;
                    case 2:
                        NavGraphGraphDirections.ActionGlobalInteractionsOverTimeFragment action3 =
                                NavGraphGraphDirections.actionGlobalInteractionsOverTimeFragment();
                        action3.setDeviceMac( mac );
                        Navigation.findNavController( getActivity(), R.id.GraphNavigator ).navigate( action3 );
                        break;
                    default:
                        Log.d( GRAPH_SPINNER, "Invalid fragment selection - something went very wrong." );
                        return;
                }
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

    public void childReady() {
        if ( deviceList.size() > 0 ) {
            getCurrentImplementer().updateData( deviceList.get( 0 ).macAddress );
        } else {
            Handler handler = new Handler( Looper.getMainLooper() );
            handler.post( () -> {
                while ( deviceList.size() < 1 ) {
                    Log.d( "Graph", "Waiting until device list is populated." );
                }
                getActivity().runOnUiThread( () -> {
                    getCurrentImplementer().updateData( deviceList.get( 0 ).macAddress );
                } );
            } );
        }

    }

    private IGraphImplementer getCurrentImplementer() {
        FragmentManager manager = getChildFragmentManager();
        Fragment frag = manager.findFragmentById( R.id.GraphNavigator );
        return (IGraphImplementer) frag.getChildFragmentManager().getFragments().get( 0 );
    }
}