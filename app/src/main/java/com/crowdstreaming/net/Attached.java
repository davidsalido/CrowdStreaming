package com.crowdstreaming.net;

import android.net.wifi.aware.AttachCallback;
import android.net.wifi.aware.WifiAwareSession;

import com.crowdstreaming.ui.main.MainPresenter;

public class Attached extends AttachCallback {

    private MainPresenter presenter;

    public Attached(MainPresenter presenter){
        this.presenter = presenter;
    }

    @Override
    public void onAttached(WifiAwareSession session) {
        WifiAwareSessionUtillities.setSession(session);
    }

    @Override
    public void onAttachFailed() {
        presenter.error("Ha habido un error, reinicia la aplicación");
    }
}