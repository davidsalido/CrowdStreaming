package com.crowdstreaming.net;

import android.net.wifi.aware.DiscoverySessionCallback;
import android.net.wifi.aware.PeerHandle;
import android.widget.Toast;

public abstract class OwnDiscoverySessionCallback extends DiscoverySessionCallback {

    private PeerHandle peerHandle;
    private int portToUse;
    private byte[] otherIp;

    public static final String[] MESSAGES = {"ACK", "CONNECTION", "IP", "PORT"};

    @Override
    public void onMessageSendSucceeded(int messageId){
        super.onMessageSendSucceeded(messageId);
    }


    public int getPortToUse() {
        return portToUse;
    }

    public void setPortToUse(int portToUse) {
        this.portToUse = portToUse;
    }

    public PeerHandle getPeerHandle() {
        return peerHandle;
    }

    public void setPeerHandle(PeerHandle peerHandle) {
        this.peerHandle = peerHandle;
    }

    public byte[] getOtherIp() {
        return otherIp;
    }

    public void setOtherIp(byte[] otherIp) {
        this.otherIp = otherIp;
    }
}
