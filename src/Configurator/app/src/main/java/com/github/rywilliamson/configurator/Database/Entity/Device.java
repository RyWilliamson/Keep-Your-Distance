package com.github.rywilliamson.configurator.Database.Entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Device {
    @PrimaryKey
    @ColumnInfo( name = "mac_address" )
    @NonNull
    public String macAddress;

    @ColumnInfo( name = "alias" )
    @NonNull
    public String alias;

    @ColumnInfo( name = "times_connected" )
    @NonNull
    public Integer times_connected;

    public Device( @NonNull String macAddress, @NonNull String alias, int times_connected ) {
        this.macAddress = macAddress;
        this.alias = alias;
        this.times_connected = times_connected;
    }

    @Ignore
    @NonNull
    @Override
    public String toString() {
        return alias;
    }
}
