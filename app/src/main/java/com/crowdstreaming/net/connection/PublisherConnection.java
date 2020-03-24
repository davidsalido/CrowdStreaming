package com.crowdstreaming.net.connection;

import android.net.ConnectivityManager;
import android.net.NetworkSpecifier;
import android.net.wifi.aware.PeerHandle;
import android.net.wifi.aware.PublishDiscoverySession;
import android.net.wifi.aware.WifiAwareNetworkSpecifier;

public class PublisherConnection extends AbstractConnection {

    private PublishDiscoverySession discoverySession;
    private PeerHandle peerHandle;
    public boolean connected = true;

    public PublisherConnection(ConnectivityManager connectivityManager, PublishDiscoverySession discoverySession, PeerHandle peerHandle) {
        super(connectivityManager);
        this.discoverySession = discoverySession;
        this.peerHandle = peerHandle;
    }

    @Override
    public NetworkSpecifier createNetworkSpecifier(int port) {
        return new WifiAwareNetworkSpecifier.Builder(discoverySession,peerHandle)
                .setPskPassphrase("crowdstreaming")
                .setPort(port)
                .setTransportProtocol(132) //SCTP
                .build();
    }

    @Override
    public void sendIp(byte[] ip) {
        discoverySession.sendMessage(peerHandle, 0, getIpv6().getAddress());
    }

    @Override
    public void connectToPublisher(int port, int backlog) {
        connected = true;
    }

    @Override
    public boolean getConnected() {
        return connected;
    }

}
