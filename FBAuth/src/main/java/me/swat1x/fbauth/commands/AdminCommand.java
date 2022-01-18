package me.swat1x.fbauth.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import me.swat1x.fbauth.AuthPlugin;
import me.swat1x.fbauth.management.AuthManager;
import me.swat1x.fbauth.management.config.ConfigManager;
import me.swat1x.fbauth.utils.PlayerUtils;
import me.swat1x.fbauth.utils.TextUtils;
import me.swat1x.fbauth.values.PasswordData;
import me.swat1x.fbauth.values.UserData;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class AdminCommand extends BaseCommand {

    @CommandAlias("auth")
    public void executeAuth(CommandSender sender, String[] args){
        if(sender instanceof ProxiedPlayer){
            if(!ConfigManager.settings().ADMINS.contains(sender.getName())){
                PlayerUtils.sendMessage(sender.getName(), ConfigManager.lang().NO_ADMIN);
                return;
            }
        }
        if(args.length == 0){
            sendInfo(sender);
            return;
        }

        if(args[0].equalsIgnoreCase("setpw")){
            setPw(sender, args);
            return;
        }
        if(args[0].equalsIgnoreCase("info")){
            info(sender, args);
            return;
        }
        if(args[0].equalsIgnoreCase("reload")){
            reload(sender);
            return;
        }
        sendInfo(sender);
    }

    private void setPw(CommandSender sender, String[] args){
        AuthManager manager = AuthPlugin.getAuthManager();
        if(args.length < 3){
            sender.sendMessage("§fСменить пароль игроку §7- §e/auth setpw <игрок> <новый пароль>");
            return;
        }
        String player = args[1];
        PasswordData pd = manager.getOrLoadPassword(player);
        if(pd == null){
            sender.sendMessage("§cТакой игрок не зарегистрирован");
            return;
        }
        String newPassword = args[2];
        manager.changePassword(player, newPassword);
        sender.sendMessage("Пароль игрока §e"+player+"§f изменён на §b"+newPassword);
    }

    private void info(CommandSender sender, String[] args){
        AuthManager manager = AuthPlugin.getAuthManager();
        if(args.length < 2){
            sender.sendMessage("§fИнформация об игроке §7- §e/auth info <игрок>");
            return;
        }
        String player = args[1];
        PasswordData pd = manager.getOrLoadPassword(player);
        if(pd == null){
            sender.sendMessage("§cТакой игрок не зарегистрирован");
            return;
        }
        UserData userData = manager.getOrLoadUserData(player);
        sender.sendMessage("Информация о пользователе §b"+player);
        sender.sendMessage("");
        sender.sendMessage("Последний айпи §e"+userData.getIp());
        sender.sendMessage("Айпи регистрации §e"+userData.getRegIp());
        sender.sendMessage("Последний вход в аккаунт §e"+ TextUtils.getTimeLabel(userData.getLastJoin(), System.currentTimeMillis())+" назад");
        sender.sendMessage("Зарегистрировался §e"+ TextUtils.getTimeLabel(userData.getRegData(), System.currentTimeMillis())+" назад");
    }

    private void reload(CommandSender sender){
        AuthPlugin.reloadConfig();
        sender.sendMessage("Конфиг успешно перезагружен!");
    }

    private void sendInfo(CommandSender sender){
        sender.sendMessage(" ");
        sender.sendMessage("§7-----(§a§lF§e§lB§f§lAuth§7)-----");
        sender.sendMessage(" ");

        sender.sendMessage("§fСменить пароль игроку §7- §e/auth setpw <игрок> <новый пароль>");
        sender.sendMessage("§fИнформация об игроке §7- §e/auth info <игрок>");
        sender.sendMessage("§fПерезагрузка конфигов §7- §e/auth reload §7(Лучше будет перезапуск BungeeCord)");

        sender.sendMessage(" ");
        sender.sendMessage("§7-----(§a§lF§e§lB§f§lAuth§7)-----");
        sender.sendMessage(" ");
    }

}
