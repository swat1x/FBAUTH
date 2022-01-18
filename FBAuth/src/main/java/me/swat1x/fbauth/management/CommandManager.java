package me.swat1x.fbauth.management;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.BungeeCommandManager;
import co.aikar.commands.Locales;
import co.aikar.commands.RegisteredCommand;
import me.swat1x.fbauth.utils.LoggingUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.List;
import java.util.Locale;

public class CommandManager {

    private BungeeCommandManager cm;
    public CommandManager(Plugin plugin){
        cm = new BungeeCommandManager(plugin);
        cm.getLocales().setDefaultLocale(Locales.RUSSIAN.stripExtensions());
    }

    public void registerCommand(BaseCommand command){
        // Регистрация команды
        cm.registerCommand(command);

        // Логирование
        List<RegisteredCommand> commandsName = command.getRegisteredCommands();
        String ending = (commandsName.size() != 1 ? "ы" : "а");
        LoggingUtils.info("Зарегистрирован"+ending+" команд"+ending+" в классе "+ChatColor.YELLOW+command.getName().toUpperCase());
    }


}
