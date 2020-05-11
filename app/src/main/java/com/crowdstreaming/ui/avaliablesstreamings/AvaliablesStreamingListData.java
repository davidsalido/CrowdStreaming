package com.crowdstreaming.ui.avaliablesstreamings;

import android.net.wifi.aware.PeerHandle;

public class AvaliablesStreamingListData {

    private String name, mac;
    private PeerHandle peerHandle;

    public AvaliablesStreamingListData(String name, String mac, PeerHandle peerHandle){
        this.name = name;
        this.mac = mac;
        this.peerHandle = peerHandle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public PeerHandle getPeerHandle() {
        return peerHandle;
    }

    public void setPeerHandle(PeerHandle peerHandle) {
        this.peerHandle = peerHandle;
    }
}
