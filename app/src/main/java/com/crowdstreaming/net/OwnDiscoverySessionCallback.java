package com.crowdstreaming.net;

import android.net.wifi.aware.DiscoverySessionCallback;
import android.net.wifi.aware.PeerHandle;
import android.widget.Toast;

import com.crowdstreaming.net.connection.AbstractConnection;

public abstract class OwnDiscoverySessionCallback extends DiscoverySessionCallback {

    private PeerHandle peerHandle;
    private int portToUse;
    private byte[] otherIp;
    private AbstractConnection connection;

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

    public AbstractConnection getConnection() {
        return connection;
    }

    public void setConnection(AbstractConnection connection) {
        this.connection = connection;
    }
}
