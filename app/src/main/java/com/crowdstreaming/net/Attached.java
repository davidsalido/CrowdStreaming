package com.crowdstreaming.net;

import android.net.wifi.aware.AttachCallback;
import android.net.wifi.aware.WifiAwareSession;

import com.crowdstreaming.ui.main.MainView;

public class Attached extends AttachCallback {

    private MainView view;

    public Attached(MainView view){
        this.view = view;
    }

    @Override
    public void onAttached(WifiAwareSession session) {
        WifiAwareSessionUtillities.setSession(session);
    }

    @Override
    public void onAttachFailed() {
        view.showError("Ha habido un error, reinicia la aplicación");
    }
}