package me.swat1x.fbauth.management.config;

import me.swat1x.fbauth.utils.AdvancedMessageBuilder;
import me.swat1x.fbauth.utils.LoggingUtils;
import me.swat1x.fbauth.utils.TextUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.config.Configuration;

import java.util.List;

public class LangData {

    private Configuration configuration;

    public LangData(Configuration configuration) {
        if (configuration == null) {
            LoggingUtils.severe("Конфиг не найден! §7(Загрузка языкового модуля)");
        } else {
            this.configuration = configuration;
            loadMessages();
            LoggingUtils.info("Кеширование " + ChatColor.BLUE + "языкового модуля" + ChatColor.WHITE + " окончено");
        }
    }

    private void loadMessages() {
        NO_ADMIN = getMessage("messages.noAdmin");

        REGISTER = getMessage("messages.register");
        LOGIN = getMessage("messages.login");
        TFA = getMessage("messages.2fa");
        CHANGE_PASSWORD = getMessage("messages.changePassword");

        HAS_SESSION = getMessage("messages.hasSession");
        INSERT_PASSWORD = getMessage("messages.insertPassword");
        SUCCESS_REGISTER = getMessage("messages.successRegister");
        SUCCESS_LOGIN = getMessage("messages.successLogin");
        ALREADY_REGISTER = getMessage("messages.alreadyRegister");
        ALREADY_LOGIN = getMessage("messages.alreadyLogin");
        WRONG_PASSWORD = getMessage("messages.wrongPassword");
        WRONG_NICK_SIZE = getMessage("messages.wrongNameSize");
        WRONG_NICK_CASE = getMessage("messages.wrongNameCase");
        NO_TIME_TO_AUTH = getMessage("messages.noTimeToAuth");
        AB_TIME = getMessage("messages.actionBarTime");
        AB_AUTH = getMessage("messages.actionBarAuth");
        NEW_PASSWORD_STRING = getMessage("messages.newPassword");
        NEW_PASSWORD_HOVER = getList("messages.newPassword-hover");

        BOT_GAME_LOGIN_CONFIRM = getMessage("2fa-bots.messages.game.loginConfirm");
        BOT_GAME_READY_TO_CONNECT = getMessage("2fa-bots.messages.game.readyToConnect");
        BOT_GAME_SUCCESS_CONNECT = getMessage("2fa-bots.messages.game.successConnect");
        BOT_GAME_ALREADY_REQUEST_CODE = getMessage("2fa-bots.messages.game.alreadyRequestCode");
        BOT_GAME_ALREADY_LINKED = getMessage("2fa-bots.messages.game.alreadyLinked");
        BOT_GAME_KICK = getMessage("2fa-bots.messages.game.kick");

        TG_COLOR_NAME = getMessage("2fa-bots.tg.colorName");
        VK_COLOR_NAME = getMessage("2fa-bots.vk.colorName");
    }

    private String getMessage(String path) {
        return TextUtils.translateColors(configuration.getString(path));
    }

    private List<String> getList(String path) {
        return TextUtils.translateColors(configuration.getStringList(path));
    }

    public String NO_ADMIN;

    public String REGISTER;
    public String LOGIN;
    public String TFA;
    public String CHANGE_PASSWORD;

    public String HAS_SESSION;
    public String INSERT_PASSWORD;
    public String SUCCESS_REGISTER;
    public String SUCCESS_LOGIN;
    public String ALREADY_REGISTER;
    public String ALREADY_LOGIN;
    public String WRONG_PASSWORD;
    public String WRONG_NICK_SIZE;
    public String WRONG_NICK_CASE;
    public String NO_TIME_TO_AUTH;
    public String AB_TIME;
    public String AB_AUTH;
    public String BOT_GAME_LOGIN_CONFIRM;

    public String BOT_GAME_READY_TO_CONNECT;
    public String BOT_GAME_SUCCESS_CONNECT;
    public String BOT_GAME_ALREADY_REQUEST_CODE;
    public String BOT_GAME_ALREADY_LINKED;
    public String BOT_GAME_KICK;

    public String TG_COLOR_NAME;
    public String VK_COLOR_NAME;

    private String NEW_PASSWORD_STRING;
    private List<String> NEW_PASSWORD_HOVER;

    public TextComponent NEW_PASSWORD(String password) {
        return AdvancedMessageBuilder.get().append(NEW_PASSWORD_STRING,
                TextUtils.replace(NEW_PASSWORD_HOVER, "%password", password)
        ).build();
    }

}
