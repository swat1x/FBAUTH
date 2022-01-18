package me.swat1x.fbauth.database.builder;

public class ColumnType {

    public static String LONG = "LONG";

    public static String VARCHAR(int size) {
        return "VARCHAR(" + size + ")";
    }

    public static String LONGTEXT = "LONGTEXT";
    public static String INT = "INT";
    public static String DOUBLE = "DOUBLE";
    public static String BOOLEAN = "BOOLEAN";

}
