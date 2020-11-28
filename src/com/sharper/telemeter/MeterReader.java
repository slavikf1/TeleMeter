package com.sharper.telemeter;

import com.sharper.meter.Meter;
import com.sharper.meter.Readings;
import org.apache.commons.codec.DecoderException;

public class MeterReader implements Runnable {
    public static volatile boolean isReading;

    private Meter meter;

    public MeterReader(Meter meter){
        this.meter = meter;
    }

    public void run() {
        if (isReading) System.out.println("requested while blocked... pending for freeing up");
        while (isReading){
        }

        isReading = true;
        try {
            System.out.println("\n"+meter.getReadings()+"\n");
        } catch (DecoderException | InterruptedException e) {
            e.printStackTrace();
        }
        isReading = false;

    }

    public Readings getReadings() throws DecoderException, InterruptedException {
        Readings result;
        if (isReading){
            System.out.println("requested while blocked... returning values once read");
            while (isReading){
            }
            return meter.lastReadings;
        }
        else {
            isReading = true;
            result = meter.getReadings();
            isReading = false;
            return  result;
        }


    }
}
