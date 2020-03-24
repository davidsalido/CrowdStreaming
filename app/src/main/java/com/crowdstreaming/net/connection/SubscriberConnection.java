package com.crowdstreaming.net.connection;

import android.net.ConnectivityManager;
import android.net.NetworkSpecifier;
import android.net.wifi.aware.PeerHandle;
import android.net.wifi.aware.SubscribeDiscoverySession;
import android.net.wifi.aware.WifiAwareNetworkSpecifier;

import java.io.IOException;
import java.net.ServerSocket;

public class SubscriberConnection extends AbstractConnection {

    private SubscribeDiscoverySession discoverySession;
    private PeerHandle peerHandle;
    private boolean connected = false;

    public SubscriberConnection(ConnectivityManager connectivityManager, SubscribeDiscoverySession discoverySession, PeerHandle peerHandle) {
        super(connectivityManager);
        this.discoverySession = discoverySession;
        this.peerHandle = peerHandle;
    }

    @Override
    public NetworkSpecifier createNetworkSpecifier(int port) {
        return new WifiAwareNetworkSpecifier.Builder(discoverySession,peerHandle)
                .setPskPassphrase("crowdstreaming")
                .build();
    }

    @Override
    public void sendIp(byte[] ip) {
        discoverySession.sendMessage(peerHandle, 0, getIpv6().getAddress());
    }

    @Override
    public void connectToPublisher(int port, int backlog) {
        try {
            setServerSocket(new ServerSocket(port, backlog, getIpv6()));

            String portToSend = "PORT:" + getServerSocket().getLocalPort();
            discoverySession.sendMessage(peerHandle, 0, portToSend.getBytes());
            setPortToUse(getServerSocket().getLocalPort());
            connected = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean getConnected() {
        return connected;
    }
}
