package com.sharper.dao;

import com.sharper.meter.Readings;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBException;
import org.influxdb.InfluxDBFactory;
import org.influxdb.InfluxDBIOException;
import org.influxdb.dto.Point;

public class InfluxReadingsDAO implements ReadingsDAO {

    private InfluxDB influxDB;
    private String url;
    private String username;
    private String password;
    private String database;

    public InfluxReadingsDAO(String url, String username, String password, String database) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.database = database;
        System.out.println("DAO Creadted");
    }

    public void createReadings (Readings readings) throws InfluxDBIOException{
        Point point = Point.measurementByPOJO(readings.getClass()).addFieldsFromPOJO(readings).build();

            influxDB.write(point);
            System.out.println("point written: " + point.lineProtocol());

    }

    public void connect() throws InfluxDBException {

           influxDB = InfluxDBFactory.connect(url, username, password);
           influxDB.setDatabase(database);
           System.out.println("DB connected");

    }

    public void close(){
        try {
            influxDB.close();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }

    }


}
