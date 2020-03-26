package com.crowdstreaming.ui.avaliablesstreamings;

import android.net.ConnectivityManager;

import java.io.File;

public interface AvaliablesStreamingsView {

    public void addDevice(String device);

    public ConnectivityManager getConnectivityManager();


    public void cambiarVista();

    public void saveFile();


}
