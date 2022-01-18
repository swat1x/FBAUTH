package me.swat1x.fbauth.management;

import me.swat1x.fbauth.AuthPlugin;
import me.swat1x.fbauth.management.config.ConfigManager;
import me.swat1x.fbauth.management.tfa.others.SocialNetworkAccount;
import me.swat1x.fbauth.utils.PlayerUtils;
import me.swat1x.fbauth.values.LoginSession;
import me.swat1x.fbauth.values.LoginStage;
import me.swat1x.fbauth.values.TFAType;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class LoginManager {

    private final HashMap<String, Long> joinMap = new HashMap<>();

    private final HashMap<String, LoginStage> stageMap = new HashMap<>();

    public HashMap<String, LoginStage> getStageMap() {
        return stageMap;
    }

    private final AuthManager authManager;

    public LoginManager(AuthManager authManager) {
        this.authManager = authManager;
    }

    public void handleJoin(ProxiedPlayer player) {
        LoginSession ls = authManager.getOrLoadSession(player.getName());
        joinMap.remove(player.getName());
        joinMap.put(player.getName(), System.currentTimeMillis());
        if (ls != null) {
            if (authManager.getOrLoadUserData(player.getName()).getIp().equals(player.getAddress().getAddress().getHostAddress())) {
                if (ls.isValid()) {
                    bypassPlayer(player, false);
                    PlayerUtils.sendMessage(player, ConfigManager.lang().HAS_SESSION);
                    return;
                }
            }
            PlayerUtils.sendMessage(player, ConfigManager.lang().LOGIN);
            setStage(player.getName(), LoginStage.LOGIN);
        } else {
            PlayerUtils.sendMessage(player, ConfigManager.lang().REGISTER);
            setStage(player.getName(), LoginStage.REGISTER);
        }
    }

    public void handleLeave(ProxiedPlayer player) {
        joinMap.remove(player.getName());
        stageMap.remove(player.getName());
    }

    public LoginStage getStage(String player) {
        return stageMap.getOrDefault(player, null);
    }

    public boolean isStage(String player, LoginStage stage) {
        LoginStage playerStage = getStage(player);
        if (playerStage != null) {
            return playerStage == stage;
        }
        return false;
    }

    public void setStage(String player, LoginStage stage) {
        stageMap.remove(player);
        stageMap.put(player, stage);
    }

    public void bypassPlayer(ProxiedPlayer player, boolean updateSession) {
        joinMap.remove(player.getName());
        stageMap.remove(player.getName());
        PlayerUtils.sendMessage(ChatMessageType.ACTION_BAR, player, ConfigManager.lang().AB_AUTH);
        ProxyServer.getInstance().getScheduler().schedule(AuthPlugin.getPlugin(), () ->
                player.connect(ConfigManager.settings().SERVER_LOBBY), 100, TimeUnit.MILLISECONDS);
        authManager.handleLogin(player);
        if (updateSession) {
            authManager.updateSession(player.getName());
        }
        else{
            PlayerUtils.sendMessage(player, ConfigManager.lang().SUCCESS_LOGIN);
        }
    }

    public void login(ProxiedPlayer player) {
        if (authManager.tfaManager().hasLink(player.getName())) {
            setStage(player.getName(), LoginStage.WAIT_2FA);
            authManager.tfaManager().sendConfirmRequest(player);
        } else {
            bypassPlayer(player, true);
        }
    }

    public void handle2FAConfirm(SocialNetworkAccount sUser,ProxiedPlayer player) {
        PlayerUtils.sendMessage(player, ConfigManager.lang().BOT_GAME_LOGIN_CONFIRM);
        bypassPlayer(player, true);
        sUser.sendMessage("ℹ На ваш аккаунт "+player.getName()+" был произведен вход с IP адреса: "+player.getAddress().getAddress().getHostAddress());
    }

    public static class NotifyTask implements Runnable {

        private final AuthManager manager;

        public NotifyTask(Plugin plugin, AuthManager manager) {
            this.manager = manager;
            ProxyServer.getInstance().getScheduler().schedule(plugin, this, 0, 1, TimeUnit.SECONDS);
        }

        private int getTimeToKick(String player) {
            long joinTime = manager.loginManager().joinMap.getOrDefault(player, -1L);
            long time = joinTime + ConfigManager.settings().AUTH_TIME - System.currentTimeMillis();
            return (int) (time / 1000);
        }

        private int i = 1;

        @Override
        public void run() {
            LoginManager loginManager = manager.loginManager();
            for (String s : loginManager.getStageMap().keySet()) {
                int timeLeft = getTimeToKick(s);
                if (timeLeft <= 0) {
                    loginManager.stageMap.remove(s);
                    loginManager.joinMap.remove(s);
                    PlayerUtils.kick(s, ConfigManager.lang().NO_TIME_TO_AUTH);
                    return;
                }
                boolean send = false;
                if (i >= 3) {
                    i = 1;
                    send = true;
                }
                PlayerUtils.sendMessage(ChatMessageType.ACTION_BAR, s, ConfigManager.lang().AB_TIME.replace("%time", timeLeft + ""));
                if (send) {
                    LoginStage stage = loginManager.getStageMap().get(s);
                    if (stage == LoginStage.LOGIN) {
                        PlayerUtils.sendMessage(s, ConfigManager.lang().LOGIN);
                    } else {
                        if (stage == LoginStage.REGISTER) {
                            PlayerUtils.sendMessage(s, ConfigManager.lang().REGISTER);
                        } else {
                            PlayerUtils.sendMessage(s, ConfigManager.lang().TFA.replace("%linkType", (
                                    manager.tfaManager().getData(s).getSocAccount().get2FAType() == TFAType.TELEGRAM ? ConfigManager.lang().TG_COLOR_NAME : ConfigManager.lang().VK_COLOR_NAME)));
                        }
                    }
                }
                i++;
            }
        }
    }


}
