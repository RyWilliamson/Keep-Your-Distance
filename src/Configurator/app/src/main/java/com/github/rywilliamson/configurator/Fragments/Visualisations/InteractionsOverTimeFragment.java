package com.github.rywilliamson.configurator.Fragments.Visualisations;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.rywilliamson.configurator.Interfaces.IGraphImplementer;
import com.github.rywilliamson.configurator.R;

import java.util.ArrayList;

public class InteractionsOverTimeFragment extends Fragment implements IGraphImplementer {

    private ArrayList<String> xLabels;

    private BarChart chart;
    private BarData data;
    private boolean first;

    public InteractionsOverTimeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState ) {
        // Inflate the layout for this fragment
        return inflater.inflate( R.layout.fragment_interactions_over_time, container, false );
    }

    public void updateData(String mac) {

    }
}