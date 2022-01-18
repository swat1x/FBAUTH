package me.swat1x.fbauth.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import me.swat1x.fbauth.AuthPlugin;
import me.swat1x.fbauth.management.AuthManager;
import me.swat1x.fbauth.management.config.ConfigManager;
import me.swat1x.fbauth.utils.PlayerUtils;
import me.swat1x.fbauth.values.LoginStage;
import me.swat1x.fbauth.values.PasswordData;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class AuthCommands extends BaseCommand {

    @CommandAlias("reg|register|рег|регистрация")
    public void executeRegister(ProxiedPlayer player, String[] args) {
        AuthManager manager = AuthPlugin.getAuthManager();
        if(manager.loginManager().getStage(player.getName()) == null){
            PlayerUtils.sendMessage(player, ConfigManager.lang().ALREADY_LOGIN);
            return;
        }
        if (manager.loginManager().isStage(player.getName(), LoginStage.REGISTER)) {
            if (args.length == 0) {
                PlayerUtils.sendMessage(player, ConfigManager.lang().INSERT_PASSWORD);
            } else {
                String password = args[0];
                manager.registerPlayer(player, password);
                PlayerUtils.sendMessage(player, ConfigManager.lang().SUCCESS_REGISTER);
                player.sendMessage(ConfigManager.lang().NEW_PASSWORD(password));
            }
        } else {
            if (!manager.loginManager().isStage(player.getName(), LoginStage.LOGIN)) {
                PlayerUtils.sendMessage(player, ConfigManager.lang().ALREADY_REGISTER);
            }
        }
    }

    @CommandAlias("l|login|л|логин")
    public void executeLogin(ProxiedPlayer player, String[] args) {
        AuthManager manager = AuthPlugin.getAuthManager();
        if(manager.loginManager().getStage(player.getName()) == null){
            PlayerUtils.sendMessage(player, ConfigManager.lang().ALREADY_LOGIN);
            return;
        }
        if (manager.loginManager().isStage(player.getName(), LoginStage.LOGIN)) {
            if (args.length == 0) {
                PlayerUtils.sendMessage(player, ConfigManager.lang().INSERT_PASSWORD);
            } else {
                String password = args[0];
                PasswordData passwordData = manager.getOrLoadPassword(player.getName());
                if (passwordData.isTrue(password)) {
                    manager.loginManager().login(player);
                } else {
                    PlayerUtils.sendMessage(player, ConfigManager.lang().WRONG_PASSWORD);
                }
            }
        }
    }

    @CommandAlias("cp|changepassword|сп|сменитьпароль")
    public void executeChangePassword(ProxiedPlayer player, String[] args) {
        AuthManager manager = AuthPlugin.getAuthManager();
        if(args.length < 2){
            PlayerUtils.sendMessage(player, ConfigManager.lang().CHANGE_PASSWORD);
            return;
        }
        String oldPassword = args[0];
        String newPassword = args[1];
        if(!manager.getOrLoadPassword(player.getName()).isTrue(oldPassword)){
            PlayerUtils.sendMessage(player, ConfigManager.lang().WRONG_PASSWORD);
            return;
        }
        manager.changePassword(player.getName(), newPassword);
        player.sendMessage(ConfigManager.lang().NEW_PASSWORD(newPassword));
    }

}
