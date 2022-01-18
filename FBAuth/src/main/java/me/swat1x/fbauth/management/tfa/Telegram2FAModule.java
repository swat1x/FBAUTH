package me.swat1x.fbauth.management.tfa;

import me.swat1x.fbauth.AuthPlugin;
import me.swat1x.fbauth.management.AuthManager;
import me.swat1x.fbauth.management.TFAManager;
import me.swat1x.fbauth.management.config.ConfigManager;
import me.swat1x.fbauth.management.tfa.others.SocialNetworkAccount;
import me.swat1x.fbauth.management.tfa.others.TFAModule;
import me.swat1x.fbauth.utils.PlayerUtils;
import me.swat1x.fbauth.values.LoginStage;
import me.swat1x.fbauth.values.TFAType;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;

public class Telegram2FAModule extends TFAModule {

    private static Telegram2FAModule instance = null;

    public static Telegram2FAModule getInstance() {
        if (instance == null) {
            instance = new Telegram2FAModule(AuthPlugin.getAuthManager());
        }
        return instance;
    }

    private AuthManager manager;

    public Telegram2FAModule(AuthManager manager) {
        this.manager = manager;
    }

    public void onUpdate(Update update) {
        if(update.getMessage() != null){
            Message message = update.getMessage();
            long chatId = message.getChatId();
            SocialNetworkAccount user = SocialNetworkAccount.get(chatId, TFAType.TELEGRAM);
            execute(user, message.getText());
        }
    }

    public void execute(SocialNetworkAccount user, String message) {
        String[] args = message.split(" ");
        if (args[0].equalsIgnoreCase("/confirm")) {
            String linkedPlayer = manager.tfaManager().getLinkedPlayer(user.getId());
            if (linkedPlayer == null) {
                user.sendMessage("\uD83D\uDE31 К вашему акканту не привязан ни один аккаунт!");
            } else {
                if (!manager.loginManager().isStage(linkedPlayer, LoginStage.WAIT_2FA)) {
                    user.sendMessage("☹️ Пользователь оффлайн или не нуждается в подтверждении!");
                } else {
                    ProxiedPlayer player = PlayerUtils.getPlayer(linkedPlayer);
                    if (player == null) {
                        user.sendMessage("\uD83E\uDD7A По непонятной причине игрок оффлайн!");
                        return;
                    }
                    manager.loginManager().handle2FAConfirm(user, player);
                }
            }
        }
        if (args[0].equalsIgnoreCase("/kick")) {
            String linkedPlayer = manager.tfaManager().getLinkedPlayer(user.getId());
            if (linkedPlayer == null) {
                user.sendMessage("\uD83D\uDE31 К вашему акканту не привязан ни один аккаунт!");
            } else {
                if (!manager.loginManager().isStage(linkedPlayer, LoginStage.WAIT_2FA)) {
                    user.sendMessage("☹️Пользователь оффлайн!");
                } else {
                    ProxiedPlayer player = PlayerUtils.getPlayer(linkedPlayer);
                    if (player == null) {
                        user.sendMessage("\uD83E\uDD7A По непонятной причине игрок оффлайн!");
                        return;
                    }
//                    manager.loginManager().handle2FAConfirm(user, player);
                    PlayerUtils.kick(player, ConfigManager.lang().BOT_GAME_KICK);
                    user.sendMessage("\uD83D\uDC63 Пользователь успешно кикнут!");
                }
            }
        }
        if (args[0].equalsIgnoreCase("/link")) {
            String linkedPlayer = manager.tfaManager().getLinkedPlayer(user.getId());
            if (linkedPlayer != null) {
                user.sendMessage("\uD83D\uDE31 К вашему акканту уже привязан профиль - " + linkedPlayer);
            } else {
                if (args.length < 2) {
                    user.sendMessage("\uD83D\uDE24 Введите код!");
                } else {
                    String code = args[1];
                    String result = manager.tfaManager().handleConnect(user, code, TFAType.TELEGRAM);
                    if (result == null) {
                        user.sendMessage("\uD83D\uDE10 Запрос на привязку с таким кодом не найден!");
                    } else {
                        user.sendMessage("\uD83E\uDD73 Аккаунт " + result + " успешно привязан к Вашей учётной записи!");
                    }
                }
            }
        }
    }

    @Override
    public boolean has2FA(String player) {
        return false;
    }

    @Override
    public void authUser(String player, long profileId) {

    }

    @Override
    public void send2FAConfirm(String player) {

    }

    @Override
    public void handleConfirm(String player) {

    }
}
