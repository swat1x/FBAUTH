package me.swat1x.fbauth.values;

import lombok.Value;
import me.swat1x.fbauth.management.tfa.others.SocialNetworkAccount;

@Value
public class TFAData {

    String player;
    SocialNetworkAccount socAccount;
    long date;
    boolean ban;

}
