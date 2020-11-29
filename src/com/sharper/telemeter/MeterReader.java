package com.sharper.telemeter;

import com.sharper.dao.ReadingsDAO;
import com.sharper.meter.Meter;
import com.sharper.meter.Readings;
import org.apache.commons.codec.DecoderException;

public class MeterReader implements Runnable {
    public static volatile boolean isReading; //busy flag

    private Meter meter; //utilizing the same meter
    private ReadingsDAO dao;

    public MeterReader(Meter meter, ReadingsDAO dao){

        this.meter = meter;
        this.dao = dao;
    }

    public void run() {
        if (isReading) System.out.println("requested while blocked... pending for freeing up"); //diagnostic message
        while (isReading){
        }
        isReading = true; //setting up busy flag

        try {
            Readings out = meter.getReadings();
            dao.connect();
                dao.createReadings(out);
            dao.close();
            System.out.println("\n"+out+"\n");

        } catch (DecoderException | InterruptedException e) {
            e.printStackTrace();
        }
        isReading = false; //releasing busy flag

    }

    public Readings getReadings() throws DecoderException, InterruptedException {
        Readings result;
        if (isReading) System.out.println("requested while blocked... returning values once read"); //diagnosting message
            while (isReading){

            }
            isReading = true;
            result = meter.getReadings();
            isReading = false;
            return  result;

    }
}
