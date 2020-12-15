package com.github.rywilliamson.configurator.Database.Entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.TypeConverters;

import com.github.rywilliamson.configurator.Database.Converters;

import java.util.Date;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        foreignKeys = {
                @ForeignKey(
                        entity = Device.class,
                        parentColumns = "mac_address",
                        childColumns = "sender",
                        onDelete = CASCADE
                ),
                @ForeignKey(
                        entity = Device.class,
                        parentColumns = "mac_address",
                        childColumns = "receiver",
                        onDelete = CASCADE
                ),
        },
        primaryKeys = { "sender", "receiver", "start_time" }
)
@TypeConverters( Converters.class )
public class Interaction {
    @ColumnInfo( name = "sender" )
    @NonNull
    public String sender;

    @ColumnInfo( name = "receiver", index = true )
    @NonNull
    public String receiver;

    @ColumnInfo( name = "start_time" )
    @NonNull
    public Date startTime;

    @ColumnInfo( name = "end_time" )
    @NonNull
    public Date endTime;

    public Interaction( @NonNull String sender, @NonNull String receiver, @NonNull Date startTime,
            @NonNull Date endTime ) {
        this.sender = sender;
        this.receiver = receiver;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
