package com.crowdstreaming.ui.avaliablesstreamings;

import android.net.wifi.aware.WifiAwareSession;

import com.crowdstreaming.net.Subscriber;
import com.crowdstreaming.net.WifiAwareSessionUtillities;

public class AvaliablesStreamingsPresenter {

    private AvaliablesStreamingsView view;
    private Subscriber subscriber;

    public AvaliablesStreamingsPresenter(AvaliablesStreamingsView view) {
        this.view = view;
    }

    public void viewCreated(){
        WifiAwareSession session = WifiAwareSessionUtillities.getSession();
        subscriber = new Subscriber(this);
        session.subscribe(Subscriber.CONFIGSUBS,subscriber,null);
    }

    public void addDevice(String device){
        view.addDevice(device);
    }
}
