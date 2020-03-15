package com.crowdstreaming.ui.main;

import android.content.Context;
import android.net.wifi.aware.WifiAwareManager;
import android.os.Handler;

import com.crowdstreaming.net.Attached;
import com.crowdstreaming.net.OwnIdentityChangedListener;

public class MainPresenter {

    private MainView view;

    public MainPresenter(MainView view){
        this.view = view;
    }

    public void viewCreated(){
        WifiAwareManager wifiAwareManager = (WifiAwareManager) view.getContext().getSystemService(Context.WIFI_AWARE_SERVICE);
        Attached attached = new Attached(MainPresenter.this);
        wifiAwareManager.attach(attached, new OwnIdentityChangedListener(),new Handler());
    }

    public void error(String msg){
        this.view.showError(msg);
    }
}
