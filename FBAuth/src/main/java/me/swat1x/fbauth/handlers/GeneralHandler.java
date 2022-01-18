package me.swat1x.fbauth.handlers;

import com.google.common.collect.Lists;
import me.swat1x.fbauth.AuthPlugin;
import me.swat1x.fbauth.management.AuthManager;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.List;

public class GeneralHandler implements Listener {

    @EventHandler
    public void join(PostLoginEvent e) {
        AuthManager manager = AuthPlugin.getAuthManager();

        if (manager.isCorrectName(e.getPlayer())) {
            manager.loginManager().handleJoin(e.getPlayer());
        }
    }

    @EventHandler
    public void quit(PlayerDisconnectEvent e) {
        AuthPlugin.getAuthManager().loginManager().handleLeave(e.getPlayer());
    }

    private static List<String> allowCommands = Lists.newArrayList("/reg", "/register", "/рег", "/регистрация", "/l", "/login", "/л", "/логин");

    @EventHandler
    public void join(ChatEvent e) {
        ProxiedPlayer p = (ProxiedPlayer) e.getSender();
        if (AuthPlugin.getAuthManager().loginManager().getStage(p.getName()) != null) {
            if (e.isCommand()) {
                String command = e.getMessage().split(" ")[0];
                if (!allowCommands.contains(command.toLowerCase())) {
                    e.setCancelled(true);
                }
            } else {
                e.setCancelled(true);
            }
        }
    }

}
