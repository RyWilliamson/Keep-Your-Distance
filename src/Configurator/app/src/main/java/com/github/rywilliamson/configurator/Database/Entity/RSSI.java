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
        foreignKeys = @ForeignKey(
                entity = Interaction.class,
                parentColumns = { "sender", "receiver", "start_time" },
                childColumns = { "sender_ref", "receiver_ref", "start_time_ref" },
                onDelete = CASCADE
        ),
        primaryKeys = { "sender_ref", "receiver_ref", "start_time_ref", "timestamp" }
)
@TypeConverters( Converters.class )
public class RSSI {
    @ColumnInfo( name = "sender_ref" )
    @NonNull
    public String senderRef;

    @ColumnInfo( name = "receiver_ref" )
    @NonNull
    public String receiverRef;

    @ColumnInfo( name = "start_time_ref" )
    @NonNull
    public Date startTimeRef;

    @ColumnInfo( name = "timestamp" )
    @NonNull
    public Date timestamp;

    @ColumnInfo( name = "value" )
    @NonNull
    public Integer value;

    @ColumnInfo( name = "est_distance" )
    @NonNull
    public Float estDistance;

    @ColumnInfo( name = "measured_power" )
    @NonNull
    public Integer measuredPower;

    @ColumnInfo( name = "environment_var" )
    @NonNull
    public Integer environmentVar;

    public RSSI( @NonNull String senderRef, @NonNull String receiverRef, @NonNull Date startTimeRef,
            @NonNull Date timestamp, int value, float estDistance, int measuredPower, int environmentVar ) {
        this.senderRef = senderRef;
        this.receiverRef = receiverRef;
        this.startTimeRef = startTimeRef;
        this.timestamp = timestamp;
        this.value = value;
        this.estDistance = estDistance;
        this.measuredPower = measuredPower;
        this.environmentVar = environmentVar;
    }
}
