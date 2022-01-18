package me.swat1x.fbauth.management.tfa.others;

import me.swat1x.fbauth.AuthPlugin;
import me.swat1x.fbauth.management.AuthManager;
import me.swat1x.fbauth.management.config.ConfigManager;
import me.swat1x.fbauth.management.tfa.Telegram2FAModule;
import me.swat1x.fbauth.utils.LoggingUtils;
import me.swat1x.fbauth.values.TFAType;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

public class SocialNetworkAccount {

    public static void registerBots() {
        // Регистрация Telegram бота
        if (ConfigManager.settings().TG_ENABLE) {
            ApiContextInitializer.init();
            try {
                new TelegramBotsApi().registerBot(Telegram.getInstance());
                LoggingUtils.info("Бот 2fa "+ConfigManager.lang().TG_COLOR_NAME+"§f успешно подключен!");
            } catch (TelegramApiRequestException e) {
                LoggingUtils.severe("Данные от бота введены неправильно");
            }
        }
    }

    private final long id;
    private final TFAType tfaType;

    public TFAType get2FAType(){
        return tfaType;
    }

    public long getId(){
        return id;
    }

    public static SocialNetworkAccount get(long id, TFAType tfaType){
        return new SocialNetworkAccount(id, tfaType);
    }

    public SocialNetworkAccount(long id, TFAType tfaType) {
        this.id = id;
        this.tfaType = tfaType;
    }

    /* <-- Отправка сообщений в разных соц сетях -->*/
    public void sendMessage(String message) {
        if (tfaType == TFAType.TELEGRAM) {
            Telegram.getInstance().sendMessage(id, message);
        }
    }

    public void sendMessage(SendMessage message) {
        if (tfaType == TFAType.TELEGRAM) {
            Telegram.sendCustom(message);
        }
    }

    public static class Telegram extends TelegramLongPollingBot {

        public static void sendCustom(SendMessage sendMessage){
            try {
                getInstance().sendMessage(sendMessage);
            } catch (TelegramApiException e) {

            }
        }

        private static Telegram instance;

        public static Telegram getInstance(){
            if(instance == null){
                instance = new Telegram(AuthPlugin.getAuthManager());
            }
            return instance;
        }

        private AuthManager manager;

        public Telegram(AuthManager manager) {
            this.manager = manager;
        }

        public void sendMessage(long id, String message) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.enableMarkdown(true);
            sendMessage.setChatId(id);
            sendMessage.setText(message);
            sendCustom(sendMessage);
        }

        @Override
        public void onUpdateReceived(Update update) {
            Telegram2FAModule.getInstance().onUpdate(update);
        }

        @Override
        public String getBotUsername() {
            return ConfigManager.settings().TG_USERNAME;
        }

        @Override
        public String getBotToken() {
            return ConfigManager.settings().TG_TOKEN;
        }
    }

}
