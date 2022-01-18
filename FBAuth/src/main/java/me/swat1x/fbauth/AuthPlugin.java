package me.swat1x.fbauth;

import me.swat1x.fbauth.commands.AdminCommand;
import me.swat1x.fbauth.commands.AuthCommands;
import me.swat1x.fbauth.commands.TFACommands;
import me.swat1x.fbauth.database.Database;
import me.swat1x.fbauth.database.SQLManager;
import me.swat1x.fbauth.handlers.GeneralHandler;
import me.swat1x.fbauth.management.AuthManager;
import me.swat1x.fbauth.management.CommandManager;
import me.swat1x.fbauth.management.FileManager;
import me.swat1x.fbauth.management.LoginManager;
import me.swat1x.fbauth.management.config.ConfigManager;
import me.swat1x.fbauth.management.config.SettingsData;
import me.swat1x.fbauth.management.tfa.others.SocialNetworkAccount;
import me.swat1x.fbauth.utils.LoggingUtils;
import me.swat1x.fbauth.utils.NumericUtils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.Configuration;

public final class AuthPlugin extends Plugin {

    private static AuthManager authManager;

    public static AuthManager getAuthManager() {
        return authManager;
    }

    private static Plugin plugin;

    public static Plugin getPlugin() {
        return plugin;
    }

    private static Database database;

    public static Database getDatabase() {
        return database;
    }

    @Override
    public void onEnable() {
        plugin = this;

        // Регистрация всего что возможно
        registerHandlers(getPlugin());
        registerServices(getPlugin());
        registerCommands(getPlugin());
    }

    private void registerHandlers(Plugin plugin) {
        PluginManager pm = ProxyServer.getInstance().getPluginManager();

        pm.registerListener(plugin, new GeneralHandler());
    }

    private void registerServices(Plugin plugin) {
        new FileManager(plugin).loadConfig();
        loadConfigModule();
        loadDatabase();
        SocialNetworkAccount.registerBots();
        authManager = new AuthManager(getDatabase());

        new LoginManager.NotifyTask(this, authManager);
    }

    private void registerCommands(Plugin plugin) {
        CommandManager commandManager = new CommandManager(plugin);

        commandManager.registerCommand(new AuthCommands());
        commandManager.registerCommand(new AdminCommand());
        commandManager.registerCommand(new TFACommands(authManager));
    }

    public static void reloadConfig(){
        LoggingUtils.info("Начинаю перезагрузку конфигов...");
        new FileManager(AuthPlugin.getPlugin()).loadConfig();
        Configuration configuration = FileManager.getConfigFile();
        ConfigManager.loadLandAndSettingsData(configuration);
        LoggingUtils.info("Перезагрузка конфигов прошла успешно!");
    }

    private void loadConfigModule() {
        Configuration configuration = FileManager.getConfigFile();
        if (configuration != null) {
            LoggingUtils.info("Начинаю загрузку модуля конфига...");
            ConfigManager.loadLandAndSettingsData(configuration);
        } else {
            LoggingUtils.severe("Конфиг не найден! §7(Начальная подгрузка)");
        }
    }

    private void loadDatabase() {
        LoggingUtils.info("Начинаю подсоединение к базе данных...");
        SettingsData data = ConfigManager.settings();
        database = new Database(
                data.DB_HOST.split(":")[0],
                NumericUtils.parseInt(data.DB_HOST.split(":")[1], 3306),
                data.DB_NAME,
                data.DB_USER,
                data.DB_PASSWORD
        );
        LoggingUtils.info("База данных успешно подключена!");
        SQLManager.createTables(database);
    }
}
