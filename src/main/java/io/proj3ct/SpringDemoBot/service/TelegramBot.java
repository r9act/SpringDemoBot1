package io.proj3ct.SpringDemoBot.service;

import io.proj3ct.SpringDemoBot.config.BotConfig;
import io.proj3ct.SpringDemoBot.model.User;
import io.proj3ct.SpringDemoBot.model.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Slf4j //библиотека логгирования
@Component //позволит автоматически создавать экземпляр Spring
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private UserRepository userRepository;
    final BotConfig config;

    private static final String HELP_TEXT = "For help call 911\n\n" +
            "For other help call 119";

    public TelegramBot(BotConfig config) { //конструктор
        this.config = config;
        List<BotCommand> listofCommands = new ArrayList<>();
        listofCommands.add(new BotCommand("/start", "get a welcome message"));
        listofCommands.add(new BotCommand("/mydata", "get user data"));
        listofCommands.add(new BotCommand("/deletedata", "delete user data"));
        listofCommands.add(new BotCommand("/help", "how to use this bot"));
        listofCommands.add(new BotCommand("/settings", "set you preferences"));
        try {
            this.execute(new SetMyCommands(listofCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot's command list: " + e.getMessage());
        }


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

                    registerUser(update.getMessage());                                      //событие 1 (регистрация пользователья) -- инфа для БД

                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName()); //событие 2 (ответ)

                    break;
                case "/help":

                    helpCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;

                default:

                    sendMessage(chatId, "Not available yet!");
            }
        }
    }

    private void registerUser(Message msg) {
        if (userRepository.findById(msg.getChatId()).isEmpty()){
            var chatId = msg.getChatId();
            var chat = msg.getChat();

            User user = new User();

            user.setChatId(chatId);
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setUserName(chat.getUserName());
            user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));

            userRepository.save(user);
            log.info("user saved: " + user);

        }

    }

    private void startCommandReceived(long chatId, String name) {

        String answer = "Hi, " + name + ", I love you";

        log.info("Replied to user: " + name);                   //лог в уровень ИНФО

        sendMessage(chatId, answer);
    }

    private void helpCommandReceived(long chatId, String name) {


        log.info("Helped user: " + name);                   //лог в уровень ИНФО

        sendMessage(chatId, HELP_TEXT);
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
