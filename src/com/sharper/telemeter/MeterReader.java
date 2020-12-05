package com.sharper.telemeter;

import com.sharper.dao.ReadingsDAO;
import com.sharper.meter.Meter;
import com.sharper.meter.ReadingException;
import com.sharper.meter.Readings;
import org.apache.commons.codec.DecoderException;
import org.influxdb.InfluxDBException;

import java.util.Iterator;
import java.util.LinkedList;

public class MeterReader implements Runnable {
    public static volatile boolean isReading; //busy flag

    private Meter meter; //utilizing the same meter
    private ReadingsDAO dao;
    private volatile LinkedList <Readings> ReadingsQueue;

    public MeterReader(Meter meter, ReadingsDAO dao){
        this.meter = meter;
        this.dao = dao;
        ReadingsQueue = new LinkedList<>();
        isReading = false;
    }

    public void run() {
        if (isReading) System.out.println("requested while blocked... pending for freeing up"); //diagnostic message
        Readings out;
        synchronized (meter) {
            while (isReading) {
                try {
                    meter.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            try {
                isReading = true; //setting up busy flag
                out = meter.getReadings();
                System.out.println("\nRead from meter: " + out + "\n");
            } catch (ReadingException | DecoderException | InterruptedException e) {
                System.out.println("Meter Reader: Exception caught" + " " + e.getMessage() + " ignoring readings");

                //NO More filtering based on last readings:
                //meter.lastReadings.setTime(new Date()); //updating time for current for last readings
                out = meter.lastReadings;
                //System.out.println("\n"+ "Last known values are" + meter.lastReadings+"\n");
            }

            finally {
                isReading = false; //releasing busy flag
                try {
                    meter.notifyAll();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //notifying other threads
            }
        }

        try {
            System.out.println("inside 1st TRY block");
            dao.connect();
            dao.createReadings(out);

            //Переписать - в случае потери соедениния после записи последнего значения, оно будет добавлено в очередь в
            //обоаботчике  - это приведет к задвоению значений.
            if (ReadingsQueue.size() > 0 ){
                System.out.println("writing the queue to the DB: " + ReadingsQueue.size() + " elements:\n");
                int counter = 0;
                for (Iterator <Readings> iterator = ReadingsQueue.iterator(); iterator.hasNext();) {
                    Readings next = iterator.next();
                    dao.createReadings(next);
                    iterator.remove();
                    counter ++;
                }
                System.out.println("Wrote the Queue to the DB: " + counter + " items");
                System.out.println("Remaining Queue size: " + ReadingsQueue.size() + "\n");
            }
        }

        catch (InfluxDBException exception){
            System.out.println("Exception caught: " + exception.getMessage());
            ReadingsQueue.add(out);
            System.out.println("wrote Readings to the queue... Queue size "  + ReadingsQueue.size() );
        }

        finally {
            dao.close();
        }
    }

    public Readings getReadings() throws DecoderException, InterruptedException {
        Readings result;
        synchronized (meter) {
            if (isReading)
                System.out.println("requested while blocked... returning values once read"); //diagnosting message
            while (isReading) {
                try {
                    meter.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            isReading = true;
            try {
                result = meter.getReadings();
            } catch (ReadingException e) {
                System.out.println("MeterReader: Exception caught" + " " + e.getMessage());
                System.out.println("MeterReader: Returning last known values");
                return meter.lastReadings;
            } finally {
                isReading = false;
                meter.notifyAll();
            }
            return result;
        }
    }
}
