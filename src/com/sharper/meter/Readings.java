package com.sharper.meter;

import org.checkerframework.checker.units.qual.C;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;
import org.influxdb.annotation.TimeColumn;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

@Measurement(name = "power")
public class Readings {

    private int serialNum;

    public void setTime(Date date) {
        this.time = date.toInstant();
    }

    @TimeColumn
    @Column(name = "time")
    private Instant time;
    private Date date;
    @Column(name = "day")
    private float day;
    @Column(name = "night")
    private float night;
    @Column(name = "current")
    private float current;
    @Column(name = "power")
    private float power;
    @Column(name = "voltage")
    private float voltage;

    public Readings(int serialNum, float day, float night, float current, float power, float voltage) {
        this.serialNum = serialNum;
        this.date = new Date();
        this.day = day;
        this.night = night;
        this.current = current;
        this.power = power;
        this.voltage = voltage;
        this.time = this.date.toInstant();
    }


    public int getSerialNum() {
        return serialNum;
    }

    public String getDateString() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return formatter.format(this.date);
    }

    public Date getDate() {
        return date;
    }

    public float getDay() {
        return day;
    }

    public float getNight() {
        return night;
    }

    public float getCurrent() {
        return current;
    }

    public float getPower() {
        return power;
    }

    public float getVoltage() {
        return voltage;
    }

    public String toString(){
        return serialNum + " on " + getDateString() + ": " + "day: " + day + " night: "+night + " Voltage: "+ voltage +
                " Current: " + current +" Power: "+ power/1000;
    }
}
