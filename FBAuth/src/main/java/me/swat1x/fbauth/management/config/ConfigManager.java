package me.swat1x.fbauth.management.config;

import me.swat1x.fbauth.utils.LoggingUtils;
import net.md_5.bungee.config.Configuration;

public class ConfigManager {

    private static LangData langData = null;
    private static SettingsData settingsData = null;

    public static void loadLandAndSettingsData(Configuration configuration) {
        if (configuration == null) {
            LoggingUtils.severe("Конфиг не найден! §7(Загрузка всех модулей)");
        } else {
            LoggingUtils.info("Получил файл, начинаю кеширование настроек...");
            langData = new LangData(configuration);
            settingsData = new SettingsData(configuration);
        }
    }

    public static LangData lang() {
        return langData;
    }

    public static SettingsData settings() {
        return settingsData;
    }

}
