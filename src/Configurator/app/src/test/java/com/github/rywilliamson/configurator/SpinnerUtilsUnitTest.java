package com.github.rywilliamson.configurator;

import android.widget.ArrayAdapter;

import com.github.rywilliamson.configurator.Utils.SpinnerUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;

@RunWith( RobolectricTestRunner.class )
public class SpinnerUtilsUnitTest {

    private List<Integer> list;
    @Mock
    private ArrayAdapter<Integer> adapter;

    @Before
    public void initialise() {
        list = new ArrayList<>();
        adapter = mock( ArrayAdapter.class );
    }

    @Test
    public void addItem_firstAddWorks() {
        SpinnerUtils.addItem( list, adapter, 1 );
        assertThat( list ).contains( 1 );
        assertThat( list ).containsNoDuplicates();
    }

    @Test
    public void addItem_separateAdd() {
        SpinnerUtils.addItem( list, adapter, 1 );
        SpinnerUtils.addItem( list, adapter, 2 );
        assertThat( list ).contains( 1 );
        assertThat( list ).contains( 2 );
        assertThat( list ).containsNoDuplicates();
    }

    @Test
    public void addItem_duplicateNoAdd() {
        SpinnerUtils.addItem( list, adapter, 1 );
        SpinnerUtils.addItem( list, adapter, 1 );
        assertThat( list ).contains( 1 );
        assertThat( list ).containsNoDuplicates();
    }

}
