package com.sharper.telemeter;

import com.sharper.meter.Meter;
import org.apache.commons.codec.DecoderException;

public class MeterReader implements Runnable {
    public static volatile boolean isReading;

    private Meter meter;

    public MeterReader(Meter meter){
        this.meter = meter;
    }

    public void run() {

        while (isReading){

        }

        isReading = true;
        try {
            System.out.println(meter.getReadings());
        } catch (DecoderException | InterruptedException e) {
            e.printStackTrace();
        }
        isReading = false;

    }
}
