package com.sharper.telemeter;

import com.sharper.dao.ReadingsDAO;
import com.sharper.meter.Meter;
import com.sharper.meter.ReadingException;
import com.sharper.meter.Readings;
import org.apache.commons.codec.DecoderException;

import java.util.Date;

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

        catch (ReadingException e) {
            System.out.println("Meter Reader: Exception caught" + " "+ e.getMessage() + " writing last known value to DB");
            meter.lastReadings.setTime(new Date()); //updating time for current for last readings
            dao.connect();
                dao.createReadings(meter.lastReadings);
            dao.close();
            System.out.println("\n"+ "Last known values are" + meter.lastReadings+"\n");
        }
        isReading = false; //releasing busy flag

    }

    public Readings getReadings() throws DecoderException, InterruptedException {
        Readings result;
        if (isReading) System.out.println("requested while blocked... returning values once read"); //diagnosting message
            while (isReading){

            }
            isReading = true;
            try {
                result = meter.getReadings();
            }
            catch (ReadingException e){
                System.out.println("MeterReader: Exception caught" + " " + e.getMessage());
                System.out.println("MeterReader: Returning last known values");
                isReading = false;
                return meter.lastReadings;
            }

            isReading = false;
            return  result;

    }
}
