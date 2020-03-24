package com.crowdstreaming.ui.avaliablesstreamings;

import android.net.wifi.aware.WifiAwareSession;

import com.crowdstreaming.net.Subscriber;
import com.crowdstreaming.net.WifiAwareSessionUtillities;

import java.io.IOException;

public class AvaliablesStreamingsPresenter {

    private AvaliablesStreamingsView view;
    public static Subscriber subscriber;

    public AvaliablesStreamingsPresenter(AvaliablesStreamingsView view) {
        this.view = view;
    }

    public void viewCreated(){
        WifiAwareSession session = WifiAwareSessionUtillities.getSession();
        subscriber = new Subscriber(this, view.getConnectivityManager());
        session.subscribe(Subscriber.CONFIGSUBS,subscriber,null);
    }
    public void realizaConexion(){
        subscriber.realizaConexion();
    }

    public void addDevice(String device){
        view.addDevice(device);
    }

    public void cambiarVista(){
        view.cambiarVista();
    }


    public void saveFile() {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    subscriber.saveFile(view.carpeta());
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    public Subscriber getSubscriber(){
        return subscriber;
    }
}
