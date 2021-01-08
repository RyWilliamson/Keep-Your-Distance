package com.github.rywilliamson.configurator.Utils;

import java.util.Date;

public class TimeoutData {

    private Date startTime;
    private Date endTime;

    public TimeoutData( BluetoothHandler handler, Date startTime, Date endTime ) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void updateTime( Date time ) {
        if (time.getTime() - endTime.getTime() > 3000) {
            startTime = time;
        }
        endTime = time;
    }
}
