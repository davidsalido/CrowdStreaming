package com.crowdstreaming.ui.avaliablesstreamings;

import android.net.ConnectivityManager;
import android.net.wifi.aware.PeerHandle;

public interface AvaliablesStreamingsView {

    public void addDevice(String device, String mac, PeerHandle peerHandle);

    public ConnectivityManager getConnectivityManager();


    public void cambiarVista();

    public void saveFile();



}
