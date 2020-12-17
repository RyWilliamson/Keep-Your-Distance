package com.github.rywilliamson.configurator.Utils;

import android.os.Handler;

import java.lang.ref.WeakReference;
import java.util.Date;

public class InteractionTimeout {

    private final String key;
    private final Handler runHandler = new Handler();
    private final Date startTIme;
    private boolean lapsed;
    private final int runTime; // Milliseconds
    private final WeakReference<BluetoothHandler> handler;

    public InteractionTimeout(BluetoothHandler handler, String key, Date startTime, int runTime) {
        this.key = key;
        this.startTIme = startTime;
        this.runTime = runTime;
        this.handler = new WeakReference<>( handler );
        lapsed = false;
        runHandler.postDelayed( stopInteraction, runTime / 2 );
    }

    private final Runnable stopInteraction = new Runnable() {
        @Override
        public void run() {
            // Potential race condition here - rare occurrence
            if (handler.get() == null) {
                return;
            }
            if (lapsed) {
                handler.get().removeStartTime( key );
            } else {
                lapsed = true;
                runHandler.postDelayed( this, runTime / 2 );
            }
        }
    };

}
