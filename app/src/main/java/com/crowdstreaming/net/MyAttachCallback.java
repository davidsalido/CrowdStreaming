package com.crowdstreaming.net;

import android.net.wifi.aware.AttachCallback;
import android.net.wifi.aware.WifiAwareSession;

public class MyAttachCallback extends AttachCallback {


    public MyAttachCallback(){

    }

    @Override
    public void onAttached(WifiAwareSession session) {
        WifiAwareSessionUtillities.setSession(session);
    }

    @Override
    public void onAttachFailed() {
        System.out.println("Ha habido un error, reinicia la aplicación");
    }
}