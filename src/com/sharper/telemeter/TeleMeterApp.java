package com.sharper.telemeter;

import com.sharper.meter.Meter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TeleMeterApp {

    public static void main(String[] args) {

        String comPort = "ttyUSB0";
        String serial = "39037291";

        Meter meter = new Meter(comPort,serial);
        MeterReader reader = new MeterReader(meter);

        ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
        ScheduledFuture <?> scheduledFuture = ses.scheduleAtFixedRate(reader, 1, 10, TimeUnit.SECONDS);
        System.out.println("Program run");
    }
}
