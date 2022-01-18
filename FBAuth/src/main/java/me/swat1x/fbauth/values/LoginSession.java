package me.swat1x.fbauth.values;

import lombok.Value;

@Value
public class LoginSession {

    String player;
    long end;

    public boolean isValid(){
        return end > System.currentTimeMillis();
    }

}
