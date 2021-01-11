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

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.rywilliamson.configurator.Database.DatabaseViewModel;
import com.github.rywilliamson.configurator.Database.RSSIDatabase;
import com.github.rywilliamson.configurator.Fragments.GraphFragment;
import com.github.rywilliamson.configurator.Interfaces.IBackendContainer;
import com.github.rywilliamson.configurator.Interfaces.IGraphImplementer;
import com.github.rywilliamson.configurator.R;

import org.apache.commons.lang3.time.DateUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class WeeklyInteractionsFragment extends Fragment implements IGraphImplementer {

    private ArrayList<String> xLabels;
    private BarChart chart;
    private BarData data;
    private boolean first;

    private DatabaseViewModel db;


    public WeeklyInteractionsFragment() {
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
        return inflater.inflate( R.layout.fragment_weekly_interactions, container, false );
    }

    @Override
    public void onViewCreated( @NonNull View view, @Nullable Bundle savedInstanceState ) {
        super.onViewCreated( view, savedInstanceState );
        first = true;
        String mac = WeeklyInteractionsFragmentArgs.fromBundle( getArguments() ).getDeviceMac();
        chart = view.findViewById( R.id.chart );
        data = new BarData();
        chart.setData( data );
        if ( mac.equals( "" ) ) {
            ( (GraphFragment) getParentFragment().getParentFragment() ).childReady();
            return;
        }
        updateData( mac );
    }

    public void updateData( String mac ) {
        RSSIDatabase.databaseGetExecutor.execute( () -> {
            ArrayList<BarDataSet> dataSet;
            if ( !mac.equals( "FF:FF:FF:FF:FF:FF" ) ) {
                dataSet = getDataSet( mac );
            } else {
                dataSet = getTestDataSet();
            }
            if ( first ) {
                setupChart( dataSet );
                first = false;
            } else {
                setupData( dataSet );
            }
        } );
    }

    private void setupData( ArrayList<BarDataSet> dataSet ) {
        data.clearValues();
        for ( BarDataSet curSet : dataSet ) {
            data.addDataSet( curSet );
        }
        data.setValueTextSize( 18f );
        data.setValueFormatter( new IndexAxisValueFormatter() {
            @Override
            public String getFormattedValue( float value ) {
                return String.valueOf( (int) value );
            }
        } );
        chart.getXAxis().setValueFormatter( new IndexAxisValueFormatter( xLabels ) );
        chart.notifyDataSetChanged();
        requireActivity().runOnUiThread( () -> chart.animateXY( 0, 1000 ) );
        chart.invalidate();
    }

    private void setupChart( ArrayList<BarDataSet> dataSet ) {
        XAxis xAxis = chart.getXAxis();
        YAxis yAxis = chart.getAxisLeft();

        Description desc = new Description();
        desc.setEnabled( false );

        xAxis.setPosition( XAxis.XAxisPosition.BOTTOM );
        xAxis.setDrawGridLines( false );
        xAxis.setTextSize( 18f );

        chart.getAxisRight().setEnabled( false );
        yAxis.setDrawGridLines( false );
        yAxis.setTextSize( 18f );
        yAxis.setEnabled( false );
        yAxis.setAxisMinimum( 0f );

        chart.setExtraBottomOffset( 10 );
        chart.getLegend().setEnabled( false );
        chart.setDescription( desc );
        setupData( dataSet );
        requireActivity().runOnUiThread( () -> chart.animateXY( 0, 1000 ) );
        chart.invalidate();
    }

    public ArrayList<BarDataSet> getDataSet( String mac ) {
        xLabels = new ArrayList<>();
        ArrayList<BarDataSet> dataSets = new ArrayList<>();
        ArrayList<BarEntry> valueSet = new ArrayList<>();

        Date tomorrow = DateUtils.ceiling( Calendar.getInstance().getTime(), Calendar.DATE );
        Date current;
        int count;

        for ( int i = 6; i >= 0; i-- ) {
            current = DateUtils.addDays( tomorrow, -1 );
            count = db.getInteractionCountByDate( mac, current, tomorrow );
            valueSet.add( new BarEntry( i, count ) );
            xLabels.add( 0, new SimpleDateFormat( "EE", Locale.US ).format( current ) );
            Log.d( "test", count + " " + current + " " + tomorrow + " " + xLabels.get( 0 ) );
            tomorrow = current;
        }

        BarDataSet barDataSet1 = new BarDataSet( valueSet, "Interactions" );
        barDataSet1.setColor( Color.rgb( 69, 196, 255 ) );

        dataSets.add( barDataSet1 );

        return dataSets;
    }

    public ArrayList<BarDataSet> getTestDataSet() {
        ArrayList<BarDataSet> dataSets = null;

        ArrayList<BarEntry> valueSet1 = new ArrayList<>();
        valueSet1.add( new BarEntry( 0, 10 ) ); // Mon
        valueSet1.add( new BarEntry( 1, 12 ) ); // Tue
        valueSet1.add( new BarEntry( 2, 14 ) ); // Wed
        valueSet1.add( new BarEntry( 3, 9 ) ); // Thu
        valueSet1.add( new BarEntry( 4, 16 ) ); // Fri
        valueSet1.add( new BarEntry( 5, 25 ) ); // Sat
        valueSet1.add( new BarEntry( 6, 1 ) ); // Sun

        BarDataSet barDataSet1 = new BarDataSet( valueSet1, "Interactions" );
        barDataSet1.setColor( Color.rgb( 69, 196, 255 ) );

        dataSets = new ArrayList<>();
        dataSets.add( barDataSet1 );
        getTestXAxisValues();
        return dataSets;
    }

    public void getTestXAxisValues() {
        xLabels = new ArrayList<>();
        xLabels.add( "Mon" );
        xLabels.add( "Tue" );
        xLabels.add( "Wed" );
        xLabels.add( "Thu" );
        xLabels.add( "Fri" );
        xLabels.add( "Sat" );
        xLabels.add( "Sun" );
    }
}