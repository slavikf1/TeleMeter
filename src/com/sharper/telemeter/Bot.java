package com.sharper.telemeter;

import com.sharper.meter.Meter;
import com.sharper.meter.Readings;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


public class Bot extends TelegramLongPollingBot {
    final int RECONNECT_PAUSE = 10000;
    String userName;
    String token;
    MeterReader reader;

    public Bot(MeterReader reader, String userName, String token){
        super();
        this.reader = reader;
        this.token = token;
        this.userName = userName;
    }


    public void updateOnRecieved(Update update){
        System.out.println("Update received");
    }

    @Override
    public String getBotUsername() {
        return userName;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {

        try {
            Readings readings = reader.getReadings();
        String message = "День: " + readings.getDay() + "\nНочь: " + readings.getNight()+"\n"+
                "Мощность: " + readings.getPower()/1000 + "\nТок: "  + readings.getCurrent();
        sendMsg(update.getMessage().getChatId().toString(), message);
            System.out.println("sending update");
        }
        catch (Exception e){}
    }


    public synchronized void sendMsg(String chatId, String s){
        SendMessage sendMessage= new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(s);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {

        }



    }
}
