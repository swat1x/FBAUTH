package me.swat1x.fbauth.values;

import lombok.Value;
import me.swat1x.fbauth.utils.HashUtils;

@Value
public class PasswordData {

    String player;
    String hashedPassword;

    public boolean isTrue(String password){
        return HashUtils.isEquals(password, hashedPassword);
    }

}
