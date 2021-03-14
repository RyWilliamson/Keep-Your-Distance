package com.github.rywilliamson.configurator.Interfaces;

import com.github.rywilliamson.configurator.DatabaseUI.DatabaseViewModel;
import com.github.rywilliamson.configurator.Utils.BluetoothHandler;

import java.io.Serializable;

public interface IBackendContainer extends Serializable {

    BluetoothHandler getBluetoothHandler();

    DatabaseViewModel getDatabaseViewModel();

    void directConnect( String mac );

    void scan();

}
