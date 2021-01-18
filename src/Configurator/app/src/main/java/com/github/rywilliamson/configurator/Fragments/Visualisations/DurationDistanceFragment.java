package com.github.rywilliamson.configurator.Fragments.Visualisations;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.rywilliamson.configurator.Database.DatabaseViewModel;
import com.github.rywilliamson.configurator.Database.RSSIDatabase;
import com.github.rywilliamson.configurator.Interfaces.IBackendContainer;
import com.github.rywilliamson.configurator.Interfaces.IGraphImplementer;
import com.github.rywilliamson.configurator.R;

import java.util.ArrayList;

public class DurationDistanceFragment extends Fragment implements IGraphImplementer {

    private PieChart durationChart;
    private PieChart distanceChart;
    private PieData durationData;
    private PieData distanceData;

    ArrayList<Integer> colours = new ArrayList<>();

    private boolean first;

    private DatabaseViewModel db;

    public DurationDistanceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        IBackendContainer container = (IBackendContainer) getActivity();
        db = container.getDatabaseViewModel();
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState ) {
        // Inflate the layout for this fragment
        return inflater.inflate( R.layout.fragment_duration_distance, container, false );
    }

    @Override
    public void onViewCreated( @NonNull View view, @Nullable Bundle savedInstanceState ) {
        super.onViewCreated( view, savedInstanceState );
        durationChart = view.findViewById( R.id.duration_chart );
        distanceChart = view.findViewById( R.id.distance_chart );

        first = true;
        String mac = DurationDistanceFragmentArgs.fromBundle( getArguments() ).getDeviceMac();
        durationData = initData( durationChart );
        distanceData = initData( distanceChart );
        setupColours();
        view.post( () -> updateData( mac ) );
    }

    public void updateData( String mac ) {
        RSSIDatabase.databaseGetExecutor.execute( () -> {
            PieDataSet durationSet;
            PieDataSet distanceSet;
            if ( !mac.equals( "FF:FF:FF:FF:FF:FF" ) ) {
                durationSet = getDurationDataSet( mac );
                distanceSet = getDistanceDataSet( mac );
            } else {
                durationSet = getDurationTestDataSet();
                distanceSet = getDistanceTestDataSet();
            }
            if ( first ) {
                setupDurationChart( durationSet );
                setupDistanceChart( distanceSet );
                first = false;
            } else {
                setupDurationData( durationSet );
                setupDistanceData( distanceSet );
            }
        } );
    }

    private PieData initData( PieChart chart ) {
        PieData data = new PieData();
        return data;
    }

    private void setupData( PieChart chart, PieData data, PieDataSet dataSet ) {
        data.clearValues();
        data.addDataSet( dataSet );
        data.setValueFormatter( new PercentFormatter( chart ) );

        chart.notifyDataSetChanged();
        requireActivity().runOnUiThread( () -> chart.animateY( 1000, Easing.EaseInOutQuad ) );
    }

    private void setupDurationData( PieDataSet durationDataSet ) {
        setupData( durationChart, durationData, durationDataSet );
    }

    private void setupDistanceData( PieDataSet distanceDataSet ) {
        setupData( distanceChart, distanceData, distanceDataSet );
    }

    private void setupChart( PieChart chart, PieData data, PieDataSet dataSet, int string_id ) {
        Description desc = new Description();
        desc.setEnabled( false );
        chart.setDescription( desc );

        chart.setDrawCenterText( true );
        chart.setCenterText( getResources().getString( string_id ) );
        chart.setCenterTextSize( 17f );
        chart.setUsePercentValues( true );
        chart.getLegend().setTextSize( 14f );
        chart.getLegend().setHorizontalAlignment( Legend.LegendHorizontalAlignment.CENTER );

        setupData( chart, data, dataSet );
        chart.setData( data );
        chart.invalidate();
    }

    private void setupDurationChart( PieDataSet durationDataSet ) {
        setupChart( durationChart, durationData, durationDataSet, R.string.f_g_duration );
    }

    private void setupDistanceChart( PieDataSet distanceDataSet ) {
        setupChart( distanceChart, distanceData, distanceDataSet, R.string.f_g_distance );
    }

    private PieDataSet getTestDataSet( int slices, float range, int string_id, String[] labels ) {
        ArrayList<PieEntry> entries = new ArrayList<>();

        for ( int i = 0; i < slices; i++ ) {
            entries.add( new PieEntry( (float) ( ( Math.random() * range ) + range / 5 ), labels[i] ) );
        }

        PieDataSet dataSet = new PieDataSet( entries, "" );
        dataSet.setValueTextSize( 16f );
        dataSet.setSliceSpace( 3f );
        dataSet.setSelectionShift( 5f );

        dataSet.setColors( colours );
        return dataSet;
    }

    private PieDataSet getDurationDataSet( String mac ) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        String[] labels = getDurationLabels();

        int count;
        for (int i = 0; i < 6; i++) {
            long offset = i * 60_000;
            count = db.getInteractionsInRange( mac, offset, offset + 59_999 );
            if (count != 0) {
                entries.add( new PieEntry( count, labels[i] ) );
            }
        }

        PieDataSet dataSet = new PieDataSet( entries, "" );
        dataSet.setValueTextSize( 16f );
        dataSet.setSliceSpace( 3f );
        dataSet.setSelectionShift( 5f );

        dataSet.setColors( colours );
        return dataSet;
    }

    private PieDataSet getDurationTestDataSet() {
        return getTestDataSet( 6, 20f, R.string.f_g_duration, getDurationLabels() );
    }

    private PieDataSet getDistanceDataSet( String mac ) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        String[] labels = getDistanceLabels();

        int count;
        for (int i = 0; i < 5; i++) {
            float offset = i * 0.5f;
            count = db.getCountAverageDistanceInRange( mac, offset, offset + 0.49f );
            Log.d("test", String.valueOf( count ) );
            if (count != 0) {
                entries.add( new PieEntry( count, labels[i] ) );
            }
        }

        PieDataSet dataSet = new PieDataSet( entries, "" );
        dataSet.setValueTextSize( 16f );
        dataSet.setSliceSpace( 3f );
        dataSet.setSelectionShift( 5f );

        dataSet.setColors( colours );
        return dataSet;
    }

    private PieDataSet getDistanceTestDataSet() {
        return getTestDataSet( 5, 20f, R.string.f_g_distance, getDistanceLabels() );
    }

    private void setupColours() {
        // Tol_light colour palette from
        // https://gist.github.com/JoachimGoedhart/b2f91652de2b9e3b393c6c28be843e00
        colours.add( Color.rgb( 238, 102, 119 ) );
        colours.add( Color.rgb( 34, 136, 51 ) );
        colours.add( Color.rgb( 68, 119, 170 ) );
        colours.add( Color.rgb( 204, 187, 68 ) );
        colours.add( Color.rgb( 102, 204, 238 ) );
        colours.add( Color.rgb( 170, 51, 119 ) );
        colours.add( Color.rgb( 187, 187, 187 ) );
    }

    private String[] getDurationLabels() {
        return new String[]{
                "0-1",
                "1-2",
                "2-3",
                "3-4",
                "4-5",
                ">5"
        };
    }

    private String[] getDistanceLabels() {
        return new String[]{
                "0-50",
                "50-100",
                "100-150",
                "150-200",
                ">200"
        };
    }
}