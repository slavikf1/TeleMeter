package com.sharper.dao;

import com.sharper.meter.Readings;

public interface ReadingsDAO {

    void createReadings(Readings readings);
    void connect();
    void close();



}
