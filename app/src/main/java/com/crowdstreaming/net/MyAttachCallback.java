package com.crowdstreaming.net;

import android.net.wifi.aware.AttachCallback;
import android.net.wifi.aware.WifiAwareSession;

import com.crowdstreaming.ui.main.MainView;

public class MyAttachCallback extends AttachCallback {


    public MyAttachCallback(){

    }

    @Override
    public void onAttached(WifiAwareSession session) {
        System.out.println("Jeje ha funcado");
        WifiAwareSessionUtillities.setSession(session);
    }

    @Override
    public void onAttachFailed() {
        System.out.println("Ha habido un error, reinicia la aplicación");
    }
}