package me.swat1x.fbauth.management.config;

import me.swat1x.fbauth.utils.LoggingUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.config.Configuration;

import java.util.List;

public class SettingsData {

    private Configuration configuration;

    public SettingsData(Configuration configuration) {
        if (configuration == null) {
            LoggingUtils.severe("Конфиг не найден! §7(Загрузка модуля настроек)");
        } else {
            this.configuration = configuration;
            loadSettings();
            LoggingUtils.info("Кеширование " + ChatColor.BLUE + "модуля настроек" + ChatColor.WHITE + " окончено");
        }
    }

    private void loadSettings() {
        DB_NAME = configuration.getString("database.name");
        DB_USER = configuration.getString("database.user");
        DB_PASSWORD = configuration.getString("database.password");
        DB_HOST = configuration.getString("database.host");

        SESSION_SIZE = configuration.getLong("settings.sessionSize") * 1000;
        AUTH_TIME = configuration.getLong("settings.authTime") * 1000;

        SERVER_AUTH = ProxyServer.getInstance().getServerInfo(configuration.getString("settings.servers.auth"));
        SERVER_LOBBY = ProxyServer.getInstance().getServerInfo(configuration.getString("settings.servers.lobby"));

        NAME_SIZE_MIN = configuration.getInt("settings.nickSize.min");
        NAME_SIZE_MAX = configuration.getInt("settings.nickSize.max");

        ADMINS = configuration.getStringList("admins");

        TG_ENABLE = configuration.getBoolean("2fa-bots.tg.enable");
        TG_USERNAME = configuration.getString("2fa-bots.tg.username");
        TG_TOKEN = configuration.getString("2fa-bots.tg.token");

        VK_ENABLE = configuration.getBoolean("2fa-bots.vk.enable");
        VK_GROUP_ID = configuration.getString("2fa-bots.vk.groupId");
        VK_TOKEN = configuration.getString("2fa-bots.vk.token");
    }

    public String DB_NAME;
    public String DB_USER;
    public String DB_PASSWORD;
    public String DB_HOST;

    public long SESSION_SIZE;
    public long AUTH_TIME;

    public ServerInfo SERVER_AUTH;
    public ServerInfo SERVER_LOBBY;

    public int NAME_SIZE_MIN;
    public int NAME_SIZE_MAX;

    public List<String> ADMINS;

    // Боты
    public boolean TG_ENABLE;
    public String TG_USERNAME;
    public String TG_TOKEN;

    public boolean VK_ENABLE;
    public String VK_GROUP_ID;
    public String VK_TOKEN;


}
