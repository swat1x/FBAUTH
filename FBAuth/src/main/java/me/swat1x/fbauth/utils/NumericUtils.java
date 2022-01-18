package me.swat1x.fbauth.utils;

public class NumericUtils {

    public static int parseInt(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public static int randomInt(int min, int max) {
        return (int) (Math.random() * ((max - min) + 1)) + min;
    }

}
