package com.crowdstreaming.net;

import android.net.wifi.aware.WifiAwareManager;
import android.net.wifi.aware.WifiAwareSession;
import android.os.Handler;

public class WifiAwareSessionUtillities {

    private volatile static WifiAwareSession session;
    private static byte[] mac;
    private static WifiAwareManager manager;

    public static void setSession(WifiAwareSession session){
        WifiAwareSessionUtillities.session = session;
    }

    public static WifiAwareSession getSession(){
        return session;
    }

    public static byte[] getMac() {
        return mac;
    }

    public static void setMac(byte[] mac) {
        WifiAwareSessionUtillities.mac = mac;
    }

    public static void createSession(){
        MyAttachCallback attachCallback = new MyAttachCallback();
        manager.attach(attachCallback, new OwnIdentityChangedListener(),new Handler());
    }

    public static void restart(){
        session.close();
        createSession();
    }

    public static void setManager(WifiAwareManager manager) {
        WifiAwareSessionUtillities.manager = manager;
    }
}
