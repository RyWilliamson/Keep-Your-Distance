package com.github.rywilliamson.configurator.Fragments.Visualisations;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.Transformer;
import com.github.rywilliamson.configurator.Database.DatabaseViewModel;
import com.github.rywilliamson.configurator.Database.RSSIDatabase;
import com.github.rywilliamson.configurator.Interfaces.IBackendContainer;
import com.github.rywilliamson.configurator.Interfaces.IGraphImplementer;
import com.github.rywilliamson.configurator.R;

import org.apache.commons.lang3.time.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class InteractionsOverTimeFragment extends Fragment implements IGraphImplementer {

    private ArrayList<String> xLabels;

    private LineChart chart;
    private LineData data;
    private boolean first;
    private LinearGradient gradient;

    private DatabaseViewModel db;

    public InteractionsOverTimeFragment() {
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
        return inflater.inflate( R.layout.fragment_interactions_over_time, container, false );
    }

    @Override
    public void onViewCreated( @NonNull View view, @Nullable Bundle savedInstanceState ) {
        super.onViewCreated( view, savedInstanceState );
        first = true;
        String mac = InteractionsOverTimeFragmentArgs.fromBundle( getArguments() ).getDeviceMac();
        chart = view.findViewById( R.id.trend_chart );
        data = new LineData();
        chart.setData( data );
        view.post( () -> updateData( mac ) );
    }

    public void updateData( String mac ) {
        RSSIDatabase.databaseGetExecutor.execute( () -> {
            ArrayList<LineDataSet> dataSet;
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

    private void setupData( ArrayList<LineDataSet> dataSet ) {
        data.clearValues();
        for ( LineDataSet curSet : dataSet ) {
            data.addDataSet( curSet );
        }
        data.setValueTextSize( 18f );
        data.setDrawValues( false );
        //chart.getXAxis().setValueFormatter( new IndexAxisValueFormatter( xLabels ) );
        chart.notifyDataSetChanged();
        requireActivity().runOnUiThread( () -> chart.animateXY( 1000, 0 ) );
        setupGradient( dataSet.get( 0 ) );
    }

    private void setupChart( ArrayList<LineDataSet> dataSet ) {
        XAxis xAxis = chart.getXAxis();
        YAxis yAxis = chart.getAxisLeft();

        Description desc = new Description();
        desc.setEnabled( false );
        chart.setDescription( desc );

        xAxis.setPosition( XAxis.XAxisPosition.BOTTOM );
        xAxis.setDrawGridLines( false );
        xAxis.setTextSize( 18f );

        chart.getAxisRight().setEnabled( false );
        yAxis.setDrawGridLines( false );
        yAxis.setTextSize( 18f );
        yAxis.setAxisMinimum( 0f );
        //yAxis.setAxisMaximum( 150f );

        chart.setExtraBottomOffset( 10 );
        chart.setScaleEnabled( false );
        chart.getLegend().setEnabled( false );
        setupData( dataSet );

        chart.invalidate();
    }

    private ArrayList<LineDataSet> getDataSet( String mac ) {
        ArrayList<LineDataSet> dataSets;
        ArrayList<Entry> valueSet = new ArrayList<>();


        Date last = DateUtils.ceiling( db.getLastInteractionDate( mac ), Calendar.DATE );
        Date first = DateUtils.truncate( db.getFirstInteractionDate( mac ), Calendar.DATE );
        Date current;
        Date prev = first;
        int count;
        long difference = ( last.getTime() - first.getTime() ) / ( 24 * 60 * 60 * 1000 );
        Log.d( "test", String.valueOf( difference ) );
        for ( int i = 0; i < difference; i++ ) {
            current = DateUtils.addDays( prev, 1 );
            count = db.getInteractionCountByDate( mac, prev, current );
            valueSet.add( new Entry( i, count ) );
            prev = current;
        }

        dataSets = new ArrayList<>();
        dataSets.add( createLineData( valueSet ) );
        return dataSets;
    }

    private ArrayList<LineDataSet> getTestDataSet() {
        ArrayList<LineDataSet> dataSets;
        ArrayList<Entry> valueSet = new ArrayList<>();

        float modifier = 0;
        for ( int i = 10; i < 6000; i += 10 ) {
            float average = 0;
            for ( int j = 0; j < 10; j++ ) {
                float val = (int) ( Math.random() * 4 ) + modifier;
                average += val;
            }
            average = average / 10;
            valueSet.add( new Entry( i / 10, average ) );
            modifier = modifier + i / 15000f;
        }

        dataSets = new ArrayList<>();
        dataSets.add( createLineData( valueSet ) );
        return dataSets;
    }

    private LineDataSet createLineData(ArrayList<Entry> entries) {
        LineDataSet lineDataSet = new LineDataSet( entries, "Interactions" );
        lineDataSet.setDrawCircles( false );
        lineDataSet.setMode( LineDataSet.Mode.CUBIC_BEZIER );
        lineDataSet.setCubicIntensity( 0.1f );
        lineDataSet.setLineWidth( 5f );
        return lineDataSet;
    }

    private void setupGradient( LineDataSet dataSet ) {
        Transformer trans = chart.getTransformer( dataSet.getAxisDependency() );
        float dangerPoint = 8;
        float dangerY0 = (float) trans.getPixelForValues( 0, dangerPoint ).y;
        float dangerY1 = (float) trans.getPixelForValues( 0, dangerPoint + 1 ).y;
        LinearGradient gradient = new LinearGradient( 0f, dangerY1, 0f, dangerY0,
                Color.rgb( 220, 50, 32 ),
                Color.rgb( 0, 90, 181 ),
                Shader.TileMode.CLAMP
        );

        chart.getRenderer().getPaintRender().setShader( gradient );
    }
}