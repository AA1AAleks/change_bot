package com.example.demo_change_bot.service;

import com.example.demo_change_bot.config.BotConfig;
import com.example.demo_change_bot.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScope;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {
    @Autowired
    private GetExchange getExchange;
    @Autowired
    private ParseCbrAll parseCbrAll;
    private static final String HELP_TEXT = "Это тестовый бот для обучения"+ "\n"+
            "/start приветствует Вас! и начинает работу" + "\n" +
            "/usd получить курс доллара" + "\n" +
            "/eur получить курс евро" + "\n" +
            "/all получить курс всех валют";
    BotConfig botConfig;

    @Override
    public String getBotUsername() {
        return botConfig.getName();
    }
    public TelegramBot(BotConfig botConfig) {
        super(botConfig.getToken());
        this.botConfig = botConfig;
        leftMenuWindow();
    }

    private void leftMenuWindow(){

        List<BotCommand> commandList = new ArrayList<>();
        commandList.add(new BotCommand ("/start", "Приветствие и начало работы бота"));
        commandList.add(new BotCommand ("/help", "Помощь в работе бота работы бота"));
        commandList.add(new BotCommand ("/usd", "Курс доллара"));
        commandList.add(new BotCommand ("/eur", "Курс евро"));
        commandList.add(new BotCommand ("/all", "Курс всех валют в цб"));

        try {
            this.execute(new SetMyCommands(commandList, new BotCommandScopeDefault(), null));
        }catch (TelegramApiException ex){
            log.info("В листе команд ошибка " + ex.getMessage());
        }
    }


    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()){

            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText){
                case "/start" -> startCommand(chatId, update.getMessage()
                        .getChat().getFirstName());

                case "/usd" -> usdCommand(chatId);
                case "/eur" -> eurCommand(chatId);
                case "/all" -> allCurrencyCommand(chatId);
                case "/help" -> sendMassage(chatId, HELP_TEXT);
                default -> sendMassage(chatId, "Не поддерживается ! ! !");
            }

        }

    }
    private void usdCommand(long chatId){
        String formatText;
        try {
            String usd = getExchange.getUSDExchange();
            var text = "Курс доллара на %s составляет %s рублей";
            formatText = String.format(text, LocalDate.now(), usd);
        } catch (ServiceException e) {
            log.error("Ошибка получения курса доллара", e);
            formatText = "Не удалось получить текущий курс доллара. Попробуйте позже.";
        }
        sendMassage(chatId, formatText);
    }
    private void eurCommand(long chatId){
        String formatText;
        try {
            String eur = getExchange.getEURExchange();
            String text = "Курс евро на %s составляет %s рублей";
            formatText = String.format(text, LocalDate.now(), eur);
        } catch (ServiceException e) {
            log.error("Ошибка получения курса евро", e);
            formatText = "Не удалось получить текущий курс евро. Попробуйте позже.";
        }
        sendMassage(chatId, formatText);
    }
    private void allCurrencyCommand(long chatId){
        String formatText;

           String all = parseCbrAll.getDocument();
           String text = "Курс всех валют с сайте ЦБ на %s составляет %s";
           formatText = String.format(text, LocalDate.now(), all);
            log.info("-> Вывел все валюты!");

        sendMassage(chatId, formatText);

    }

    private void startCommand(long chatId, String name){
        String answer = "Привет " + name + " я чат бот курса валют";
        log.info("Ответ пользователю -> " + name);
        sendMassage(chatId, answer);
    }


    private void sendMassage(long chatId, String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);

        ReplyKeyboardMarkup replyKeyboardMarkup = getReplyKeyboardMarkup();
        message.setReplyMarkup(replyKeyboardMarkup);

        extractedException(message);
    }

    private static ReplyKeyboardMarkup getReplyKeyboardMarkup(){


        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("/all");

        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("/usd");
        row.add("/eur");

        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);

        return keyboardMarkup;

    }

    private void extractedException(SendMessage message) {
        try {
            execute(message);
        }catch (TelegramApiException ex){
            log.info("Ошибка в методе вызова" + ex.getMessage());
        }
    }

}
