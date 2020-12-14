package com.github.rywilliamson.configurator.Utils;

import android.widget.ArrayAdapter;

import java.util.List;

public class SpinnerUtils {

    public static <T> void addItem( List<T> list, ArrayAdapter<String> adapter, T data ) {
        list.add( data );
        adapter.notifyDataSetChanged();
    }
}
