package com.crowdstreaming.ui.avaliablesstreamings;

import android.net.ConnectivityManager;

import java.io.File;

public interface AvaliablesStreamingsView {

    public void addDevice(String device, String mac);

    public ConnectivityManager getConnectivityManager();


    public void cambiarVista();

    public void saveFile();


}
