package me.swat1x.fbauth.management;

import me.swat1x.fbauth.commands.TFACommands;
import me.swat1x.fbauth.database.Database;
import me.swat1x.fbauth.management.config.ConfigManager;
import me.swat1x.fbauth.management.config.LangData;
import me.swat1x.fbauth.management.config.SettingsData;
import me.swat1x.fbauth.utils.HashUtils;
import me.swat1x.fbauth.utils.LoggingUtils;
import me.swat1x.fbauth.utils.PlayerUtils;
import me.swat1x.fbauth.values.LoginSession;
import me.swat1x.fbauth.values.PasswordData;
import me.swat1x.fbauth.values.UserData;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.ResultSet;
import java.util.HashMap;

public class AuthManager {

    private final LoginManager loginManager;
    private final TFAManager tfaManager;

    public LoginManager loginManager() {
        return loginManager;
    }

    public TFAManager tfaManager() {
        return tfaManager;
    }

    private final HashMap<String, LoginSession> sessionMap = new HashMap<>();
    private final HashMap<String, PasswordData> passwordMap = new HashMap<>();
    private final HashMap<String, UserData> dataMap = new HashMap<>();
    private final HashMap<String, String> exactNamesMap = new HashMap<>();

    private final Database database;

    public Database getDatabase() {
        return database;
    }

    public AuthManager(Database database) {
        this.database = database;
        loginManager = new LoginManager(this);
        tfaManager = new TFAManager(this);
    }

    /*     Рабочий процесс     */

    public boolean isCorrectName(PendingConnection player) {
        SettingsData settings = ConfigManager.settings();
        LangData lang = ConfigManager.lang();

        int size = player.getName().length();
        if (size < settings.NAME_SIZE_MIN || size > settings.NAME_SIZE_MAX) {
            player.disconnect(lang.WRONG_NICK_SIZE);
            return false;
        }
        String normal = isNormalCaseNick(player.getName());
        if (normal != null) {
            player.disconnect(lang.WRONG_NICK_CASE.replace("%name", normal));
            return false;
        }
        return true;
    }

    public boolean isCorrectName(ProxiedPlayer player) {
        SettingsData settings = ConfigManager.settings();
        LangData lang = ConfigManager.lang();

        int size = player.getName().length();
        if (size < settings.NAME_SIZE_MIN || size > settings.NAME_SIZE_MAX) {
            PlayerUtils.kick(player, lang.WRONG_NICK_SIZE);
            return false;
        }
        String normal = isNormalCaseNick(player.getName());
        if (normal != null) {
            PlayerUtils.kick(player, lang.WRONG_NICK_CASE.replace("%name", normal));
            return false;
        }
        return true;
    }

    public boolean isRegister(String playerName) {
        return haveDatabaseRaw(playerName);
    }

    public void registerPlayer(ProxiedPlayer player, String password) {
        if (isRegister(player.getName())) {
            return;
        }
        registerUser(player.getName(), password, player.getAddress().getAddress().getHostAddress());
        LoggingUtils.info("Зарегистрирован новый пользователь §b" + player.getName() + "§f!");
        player.connect(ConfigManager.settings().SERVER_LOBBY);
    }

    public boolean hasSession(String playerName) {
        LoginSession session = getOrLoadSession(playerName);
        if (session != null) {
            if (session.getEnd() < System.currentTimeMillis()) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public void handleLogin(ProxiedPlayer player) {
        setIp(player.getName(), player.getAddress().getAddress().getHostAddress());
        setLastJoin(player.getName(), System.currentTimeMillis());
        loadUserData(player.getName());
        LoggingUtils.info("Пользователь §e" + player.getName() + "§f успешно авторизировался!");
    }

    public void updateSession(String playerName) {
        setSession(playerName, System.currentTimeMillis() + ConfigManager.settings().SESSION_SIZE);
        loadSession(playerName);
    }

    public void changePassword(String playerName, String newPassword) {
        setPassword(playerName, newPassword);
        updatePassword(playerName);
    }

    public void updatePassword(String playerName) {
        setSession(playerName, System.currentTimeMillis());
        loadPassword(playerName);
    }

    public LoginSession getOrLoadSession(String playerName) {
        return sessionMap.getOrDefault(getExactName(playerName), loadSession(playerName));
    }

    public PasswordData getOrLoadPassword(String playerName) {
        return passwordMap.getOrDefault(getExactName(playerName), loadPassword(playerName));
    }

    public UserData getOrLoadUserData(String playerName) {
        return dataMap.getOrDefault(getExactName(playerName), loadUserData(playerName));
    }

    public String isNormalCaseNick(String playerName) {
        String exact = getExactName(playerName);
        if (playerName.equals(exact)) {
            return null;
        } else {
            return exact;
        }
    }

    /*     Backend приколюхи     */

    private LoginSession loadSession(String playerName) {
        if (!haveDatabaseRaw(playerName)) {
            return null;
        }
        long sessionLong = database.sync().query("SELECT * FROM `auth` WHERE `username`='" + playerName + "'", resultSet -> {
            if (resultSet.next()) {
                return resultSet.getLong("session");
            }
            return -1L;
        });
        LoginSession session = new LoginSession(playerName, sessionLong);
        sessionMap.remove(playerName);
        sessionMap.put(playerName, session);
        return session;
    }

    private void setIp(String playerName, String ip) {
        database.sync().update("UPDATE `auth` SET `ip`='" + ip + "' WHERE `username`='" + getExactName(playerName) + "'");
    }

    private void setLastJoin(String playerName, long time) {
        database.sync().update("UPDATE `auth` SET `last_join`='" + time + "' WHERE `username`='" + getExactName(playerName) + "'");
    }

    private void setSession(String playerName, long session) {
        database.sync().update("UPDATE `auth` SET `session`='" + session + "' WHERE `username`='" + getExactName(playerName) + "'");
    }

    private void setPassword(String playerName, String password) {
        String hashPass = HashUtils.secureString(password);
        database.sync().update("UPDATE `auth` SET `password`='" + hashPass + "' WHERE `username`='" + getExactName(playerName) + "'");
    }

    private PasswordData loadPassword(String playerName) {
        if (!haveDatabaseRaw(playerName)) {
            return null;
        }
        String password = database.sync().query("SELECT * FROM `auth` WHERE `username`='" + playerName + "'", resultSet -> {
            if (resultSet.next()) {
                return resultSet.getString("password");
            }
            return null;
        });
        PasswordData passwordData = new PasswordData(playerName, password);
        passwordMap.remove(playerName);
        passwordMap.put(playerName, passwordData);
        return passwordData;
    }

    private int getAlts(String ip) {
        return database.sync().query("SELECT * FROM `auth` WHETE `register_ip`='" + ip + "'", ResultSet::getFetchSize);
    }

    private UserData loadUserData(String playerName) {
        if (!haveDatabaseRaw(playerName)) {
            return null;
        }

        UserData userData = database.sync().query("SELECT * FROM `auth` WHERE `username`='" + playerName + "'", resultSet -> {
            if (resultSet.next()) {
                return new UserData(
                        resultSet.getString("ip"),
                        resultSet.getString("register_ip"),
                        resultSet.getLong("last_join"),
                        resultSet.getLong("reg_date")
                );
            }
            return null;
        });
        dataMap.remove(playerName);
        dataMap.put(playerName, userData);
        return userData;
    }

    private String getExactName(String playerName) {
        if (exactNamesMap.containsKey(playerName.toLowerCase())) {
            return exactNamesMap.get(playerName.toLowerCase());
        }
        String name = database.sync().query("SELECT * FROM `auth`", resultSet -> {
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                if (username.equalsIgnoreCase(playerName)) {
                    return username;
                }
            }
            return playerName;
        });
        exactNamesMap.remove(playerName.toLowerCase());
        exactNamesMap.put(playerName.toLowerCase(), name);
        return name;
    }

    private boolean haveDatabaseRaw(String playerName) {
        return database.sync().query("SELECT * FROM `auth` WHERE `username`='" + playerName + "'", ResultSet::next);
    }

    private void registerUser(String username, String password, String ip) {
        database.sync().update("INSERT INTO `auth` (`username`, `password`, `register_ip`, `ip`, `session`, `last_join`, `reg_date`) VALUES (" +
                "'" + username + "' ," +
                "'" + HashUtils.secureString(password) + "' ," +
                "'" + ip + "' ," +
                "'" + ip + "' ," +
                "'" + (System.currentTimeMillis() + ConfigManager.settings().SESSION_SIZE) + "' ," +
                "'" + System.currentTimeMillis() + "' ," +
                "'" + System.currentTimeMillis() + "'" +
                ")");
    }


}
