package com.sharper.dao;

import com.sharper.meter.Readings;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
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
    }

    public void createReadings(Readings readings){
        Point point = Point.measurementByPOJO(readings.getClass()).addFieldsFromPOJO(readings).build();
        System.out.println("point created");
        System.out.println(point.lineProtocol());
        try {
            influxDB.write(point);
            System.out.println("point written");
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public void connect(){

        try{
           influxDB = InfluxDBFactory.connect(url, username, password);
           influxDB.setDatabase(database);
           System.out.println("DB connected");

        }
        catch (Exception e){
            System.out.println(e.getMessage());

        }
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
