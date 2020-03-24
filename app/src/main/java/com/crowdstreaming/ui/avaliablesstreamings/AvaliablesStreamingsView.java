package com.crowdstreaming.ui.avaliablesstreamings;

import android.net.ConnectivityManager;

import java.io.File;

public interface AvaliablesStreamingsView {

    public void addDevice(String device);

    public ConnectivityManager getConnectivityManager();

    public File carpeta();

    public void cambiarVista();

}
