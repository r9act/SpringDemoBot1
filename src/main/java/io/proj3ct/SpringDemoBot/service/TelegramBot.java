package io.proj3ct.SpringDemoBot.service;

import io.proj3ct.SpringDemoBot.config.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j //библиотека логгирования
@Component //позволит автоматически создавать экземпляр Spring
public class TelegramBot extends TelegramLongPollingBot {

    final BotConfig config;

    public TelegramBot(BotConfig config) { //конструктор
        this.config = config;
    }


    @Override
    public String getBotUsername() {                    //API имя предоставим (аннотация @Data создала автоматически сеттер-геттер в классе BotConfig)
        return config.getBotName();
    }

    @Override
    public String getBotToken() {                       //API ключ предоставим (аннотация @Data создала автоматически сеттер-геттер в классе BotConfig)
        return config.getToken();
    }


    @Override
    public void onUpdateReceived(Update update) {       //что делать боту, когда ему написали (есть еще WebHook)
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();        //чтобы бот нас мог идентифицировать - получаем чат ID

            switch (messageText) {
                case "/start":

                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;

                default:

                    sendMessage(chatId, "Not available yet!");
            }
        }
    }

    private void startCommandReceived(long chatId, String name) {

        String answer = "Hi, " + name + ", I love you";

        log.info("Replied to user: " + name);                   //лог в уровень ИНФО

        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try {                                           //тут нужен, т.к. execute может выкинуть Exception
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());     //лог в уровень ЕРРОР
        }
    }
}
