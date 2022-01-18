package me.swat1x.fbauth.utils;

import me.swat1x.fbauth.AuthPlugin;

public class LoggingUtils {

    public static void info(String s){
        AuthPlugin.getPlugin().getLogger().info("§f"+s);
    }

    public static void severe(String s){
        AuthPlugin.getPlugin().getLogger().severe("§f"+s);
    }

    public static void warning(String s){
        AuthPlugin.getPlugin().getLogger().warning("§f"+s);
    }

}
