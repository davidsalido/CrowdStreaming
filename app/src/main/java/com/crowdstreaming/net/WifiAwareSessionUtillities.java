package com.crowdstreaming.net;

import android.net.wifi.aware.WifiAwareSession;

public class WifiAwareSessionUtillities {

    private static WifiAwareSession session;
    private static byte[] mac;

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
}
