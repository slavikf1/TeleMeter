package com.sharper.telemeter;

import com.sharper.dao.InfluxReadingsDAO;
import com.sharper.dao.ReadingsDAO;
import com.sharper.meter.Meter;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TeleMeterApp {

    public static void main(String[] args) {

        Properties properties = new Properties(); //getting port, telefragm credentials from a file

        try {
            InputStream inputStream = ClassLoader.getSystemResourceAsStream("config.properties");
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String comPort = properties.getProperty("port");
        String serial = properties.getProperty("serial");
        String telegramUserName = properties.getProperty("botname");
        String telegramToken = properties.getProperty("token");
        String databaseUrl = properties.getProperty("databaseUrl");
        String userName = properties.getProperty("user");
        String password = properties.getProperty("password");
        String database = properties.getProperty("database");

        InfluxReadingsDAO influxDAO = new InfluxReadingsDAO(databaseUrl, userName, password, database);


        Meter meter = new Meter(comPort,serial);
        MeterReader reader = new MeterReader(meter, influxDAO);


        ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
        ScheduledFuture <?> scheduledFuture = ses.scheduleAtFixedRate(reader, 10, 10, TimeUnit.SECONDS);
        //System.out.println("Schedule run... starting a bot");

        try{
            TelegramBotsApi botsApi= new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new Bot(reader, telegramUserName, telegramToken));

        }
        catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
}
