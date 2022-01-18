package me.swat1x.fbauth.utils;


import org.springframework.security.crypto.bcrypt.BCrypt;

public class HashUtils {

    public static String secureString(String s){
        return BCrypt.hashpw(s, BCrypt.gensalt(10));
    }

    public static boolean isEquals(String s, String hashed){
        return BCrypt.checkpw(s, hashed);
    }

}
