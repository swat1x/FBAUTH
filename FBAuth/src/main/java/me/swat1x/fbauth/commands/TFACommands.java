package me.swat1x.fbauth.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import me.swat1x.fbauth.management.AuthManager;
import me.swat1x.fbauth.management.config.ConfigManager;
import me.swat1x.fbauth.utils.PlayerUtils;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class TFACommands extends BaseCommand {

    private AuthManager manager;
    public TFACommands(AuthManager manager){
        this.manager = manager;
    }

    @CommandAlias("link")
    public void execute(ProxiedPlayer player){
        if(manager.tfaManager().hasLink(player.getName())){
            PlayerUtils.sendMessage(player, ConfigManager.lang().BOT_GAME_ALREADY_LINKED);
            return;
        }
        String code = manager.tfaManager().requestLinkCode(player.getName());
        if(code == null){
            PlayerUtils.sendMessage(player, ConfigManager.lang().BOT_GAME_ALREADY_REQUEST_CODE.replace("%code", manager.tfaManager().getCachedCode(player.getName())));
            return;
        }
        PlayerUtils.sendMessage(player, ConfigManager.lang().BOT_GAME_READY_TO_CONNECT.replace("%code", code));
    }

}
