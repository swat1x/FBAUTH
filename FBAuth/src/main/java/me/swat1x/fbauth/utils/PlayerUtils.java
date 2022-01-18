package me.swat1x.fbauth.utils;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PlayerUtils {

    public static ProxiedPlayer getPlayer(String name) {
        return ProxyServer.getInstance().getPlayer(name);
    }

    public static void sendMessage(ProxiedPlayer player, String string) {
        player.sendMessage(new ComponentBuilder(string).create());
    }

    public static void sendMessage(String player, String string) {
        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(player);
        if (proxiedPlayer != null) {
            proxiedPlayer.sendMessage(new ComponentBuilder(string).create());
        }
    }

    public static void sendMessage(ChatMessageType chatMessageType, ProxiedPlayer player, String string) {
        player.sendMessage(chatMessageType, new ComponentBuilder(string).create());
    }

    public static void sendMessage(ChatMessageType chatMessageType, String player, String string) {
        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(player);
        if (proxiedPlayer != null) {
            proxiedPlayer.sendMessage(chatMessageType, new ComponentBuilder(string).create());
        }
    }

    public static void kick(ProxiedPlayer player, String string) {
        player.disconnect(new ComponentBuilder(string).create());
    }

    public static void kick(String player, String string) {
        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(player);
        if (proxiedPlayer != null) {
            proxiedPlayer.disconnect(new ComponentBuilder(string).create());
        }
    }

}
