package me.swat1x.fbauth.management.tfa.others;

public interface TFAModuleImpl {

    boolean has2FA(String player);

    void authUser(String player, long profileId);

    void send2FAConfirm(String player);

    void handleConfirm(String player);

}
