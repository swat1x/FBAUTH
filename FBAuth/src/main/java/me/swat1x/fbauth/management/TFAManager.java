package me.swat1x.fbauth.management;

import com.google.common.collect.Lists;
import me.swat1x.fbauth.management.tfa.others.SocialNetworkAccount;
import me.swat1x.fbauth.utils.CacheMap;
import me.swat1x.fbauth.utils.LoggingUtils;
import me.swat1x.fbauth.utils.NumericUtils;
import me.swat1x.fbauth.values.TFAData;
import me.swat1x.fbauth.values.TFAType;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TFAManager {

    private final CacheMap<String, String> authCodes;

    private final HashMap<String, TFAData> dataMap = new HashMap<>();

    private final AuthManager manager;

    public TFAManager(AuthManager manager) {
        this.manager = manager;
        loadAllUsers();
        authCodes = new CacheMap<>(5, TimeUnit.MINUTES);
    }

    public boolean hasLink(String player) {
        return getData(player) != null;
    }

    public String getLinkedPlayer(long chatId) {
        String player = null;
        for (String s : dataMap.keySet()) {
            if (dataMap.get(s).getSocAccount().getId() == chatId) {
                player = s;
                break;
            }
        }
        return player;
    }

    private String getPlayerByCode(String code, boolean clear) {
        String player = null;
        for (String s : authCodes.keySet()) {
            if (authCodes.get(s).equalsIgnoreCase(code)) {
                if (clear) {
                    authCodes.remove(s);
                }
                player = s;
                break;
            }
        }
        return player;
    }

    public void sendConfirmRequest(ProxiedPlayer player){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(getData(player.getName()).getSocAccount().getId());
        sendMessage.setText("\uD83E\uDD78 Подтвердите вход в аккаунт "+player.getName()+" с IP "+player.getAddress().getAddress().getHostAddress());

        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        List<KeyboardRow> rows = new ArrayList<>();

        markup.setSelective(true);
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(true);

        KeyboardRow keyboardFirstRow = new KeyboardRow();

        keyboardFirstRow.add(new KeyboardButton("/confirm"));
        keyboardFirstRow.add(new KeyboardButton("/kick"));

        rows.add(keyboardFirstRow);
        markup.setKeyboard(rows);
        sendMessage.setReplyMarkup(markup);

        SocialNetworkAccount.Telegram.sendCustom(sendMessage);
    }

    public String handleConnect(SocialNetworkAccount user, String code, TFAType tfaType) {
        String player = getPlayerByCode(code, true);
        if (player == null) {
            return null;
        } else {
            linkAccount(player, user.getId(), tfaType);
            return player;
        }
    }

    public TFAData getData(String player) {
        return dataMap.getOrDefault(player, null);
    }

    private int formatBoolean(boolean b){
        return b ? 1 : 0;
    }

    private void linkAccount(String player, long id, TFAType tfaType) {
        manager.getDatabase().sync().update("INSERT INTO `2fa` (`username`, `2fa_type`, `profile_id`, `date`, `ban`) VALUES (" +
                "'" + player + "'," +
                "'" + tfaType.name() + "'," +
                "'" + id + "'," +
                "'" + System.currentTimeMillis() + "'," +
                "'" + formatBoolean(false) + "'" +
                ")");
        loadUser(player);
        LoggingUtils.info("Пользователь §b" + player + "§f привязал аккаунт §e" + tfaType.name());
    }

    public String getCachedCode(String player) {
        return authCodes.getOrDefault(player, null);
    }

    public String requestLinkCode(String player) {
        if (authCodes.containsKey(player)) {
            return null;
        }
        String code = NumericUtils.randomInt(1000, 9999) + "";
        while (authCodes.containsValue(code)) {
            code = NumericUtils.randomInt(1000, 9999) + "";
        }
        authCodes.put(player, code);
        LoggingUtils.info("Пользователь §b" + player + "§f запросил код на привязку аккаунта §7(" + code + ")");
        return code;
    }

    private void loadAllUsers() {
        dataMap.clear();
        manager.getDatabase().sync().query("SELECT * FROM `2fa`", rs -> {
            while (rs.next()) {
                String user = rs.getString("username");
                TFAData data = new TFAData(
                        user,
                        new SocialNetworkAccount(rs.getLong("profile_id"), TFAType.valueOf(rs.getString("2fa_type"))),
                        rs.getLong("date"),
                        rs.getBoolean("ban")
                );
                dataMap.put(user, data);
            }
            return null;
        });
    }

    private TFAData loadUser(String user) {
        dataMap.remove(user);
        TFAData data = manager.getDatabase().sync().query("SELECT * FROM `2fa` WHERE `username`='" + user + "'", rs -> {
            if (rs.next()) {
                return new TFAData(
                        user,
                        new SocialNetworkAccount(rs.getLong("profile_id"), TFAType.valueOf(rs.getString("2fa_type"))),
                        rs.getLong("date"),
                        rs.getBoolean("ban")
                );
            }
            return null;
        });
        if (data != null) {
            dataMap.put(user, data);
        }
        return data;
    }


}
