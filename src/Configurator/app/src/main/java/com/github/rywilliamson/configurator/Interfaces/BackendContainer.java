package com.github.rywilliamson.configurator.Interfaces;

import com.github.rywilliamson.configurator.Database.DatabaseViewModel;
import com.github.rywilliamson.configurator.Utils.BluetoothHandler;

import java.io.Serializable;

public interface BackendContainer extends Serializable {

    BluetoothHandler getBluetoothHandler();

    DatabaseViewModel getDatabaseViewModel();

    void directConnect( String mac );

    void scan();

}
