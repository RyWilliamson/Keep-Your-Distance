package com.github.rywilliamson.configurator.Utils;

import java.util.UUID;

public class CustomCharacteristics {

    // Generated from https://www.guidgenerator.com/

    // Services
    public static final UUID HEARTBEAT_SERVICE_ID = UUID.fromString( "4fafc201-1fb5-459e-8fcc-c5c9c331914b" );
    public static final UUID RSSI_SERVICE_ID = UUID.fromString( "f23ab3c8-506a-41ec-90e0-e6cbb6304e03" );
    public static final UUID CONFIG_SERVICE_ID = UUID.fromString( "24c2285b-4f54-4af7-a021-b86ea4374345" );

    // Heartbeat Characteristics
    public static final UUID CONN_CHARACTERISTIC_ID = UUID.fromString( "beb5483e-36e1-4688-b7f5-ea07361b26a8" );

    // RSSI Characteristics
    public static final UUID RSSI_CHARACTERISTIC_ID = UUID.fromString( "3f237eb3-99b4-4bbd-9475-f2e7b39ac899" );
    public static final UUID BULK_CHARACTERISTIC_ID = UUID.fromString( "930b4bb6-42b0-4be9-9272-04662bfce45a" );
    public static final UUID BULK_ACK_CHARACTERISTIC_ID = UUID.fromString( "b017e190-8d1e-4438-b33a-0d584bac7523" );

    // Configuration Characteristics
    public static final UUID CONFIG_CHARACTERISTIC_ID = UUID.fromString( "757affde-78ab-49d6-84a5-16193ad80b13" );
    public static final UUID CONFIG_ACK_CHARACTERISTIC_ID = UUID.fromString( "41a8c415-7ad6-4efd-8638-9d5d504039ce" );
}
