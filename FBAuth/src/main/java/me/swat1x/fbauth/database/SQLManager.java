package me.swat1x.fbauth.database;


import me.swat1x.fbauth.database.builder.ColumnType;

import java.sql.ResultSet;

public class SQLManager {

    public static void createTables(Database database) {

        /*       Таблица данных о пользователях       */
        Database.tableBuilder("auth")
                .appendColumn("username", ColumnType.VARCHAR(24))
                .appendColumn("password", ColumnType.VARCHAR(100))
                .appendColumn("register_ip", ColumnType.VARCHAR(45))
                .appendColumn("ip", ColumnType.VARCHAR(45))
                .appendColumn("session", ColumnType.LONG)
                .appendColumn("last_join", ColumnType.LONG)
                .appendColumn("reg_date", ColumnType.LONG)
                .build(database);

        Database.tableBuilder("2fa")
                .appendColumn("username", ColumnType.VARCHAR(24))
                .appendColumn("2fa_type", ColumnType.VARCHAR(10))
                .appendColumn("profile_id", ColumnType.LONG)
                .appendColumn("date", ColumnType.LONG)
                .appendColumn("ban", ColumnType.BOOLEAN)
                .build(database);

    }

}
