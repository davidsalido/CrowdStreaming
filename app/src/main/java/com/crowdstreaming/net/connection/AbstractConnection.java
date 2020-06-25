package com.crowdstreaming.net.connection;

import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.NetworkSpecifier;
import android.net.wifi.aware.DiscoverySession;
import android.net.wifi.aware.PeerHandle;

import java.io.IOException;
import java.net.Inet6Address;
import java.net.ServerSocket;

public abstract class AbstractConnection {

    private ServerSocket serverSocket;
    private NetworkSpecifier networkSpecifier;
    private ConnectivityManager connectivityManager;
    private Inet6Address ipv6;
    private int portToUse;

    public AbstractConnection(ConnectivityManager connectivityManager){
        this.connectivityManager = connectivityManager;
    }


    public void connect(DiscoverySession discoverySession, final PeerHandle peerHandle){
        try {

            ServerSocket ss = new ServerSocket(0);
            int port = ss.getLocalPort();
            networkSpecifier = createNetworkSpecifier(port);

            NetworkRequest networkRequest = new NetworkRequest.Builder()
                    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI_AWARE)
                    .setNetworkSpecifier(networkSpecifier)
                    .build();

            connectivityManager.requestNetwork(networkRequest, new OwnNetworkCallback(this));

            while (!getConnected()){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract NetworkSpecifier createNetworkSpecifier(int port);

    public abstract void sendIp(byte[] ip);

    public abstract void connectToPublisher(final int port, final int backlog);

    public abstract boolean getConnected();

    public Inet6Address getIpv6() {
        return ipv6;
    }

    public void setIpv6(Inet6Address ipv6) {
        this.ipv6 = ipv6;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public NetworkSpecifier getNetworkSpecifier() {
        return networkSpecifier;
    }

    public void setNetworkSpecifier(NetworkSpecifier networkSpecifier) {
        this.networkSpecifier = networkSpecifier;
    }

    public ConnectivityManager getConnectivityManager() {
        return connectivityManager;
    }

    public void setConnectivityManager(ConnectivityManager connectivityManager) {
        this.connectivityManager = connectivityManager;
    }

    public int getPortToUse() {
        return portToUse;
    }

    public void setPortToUse(int portToUse) {
        this.portToUse = portToUse;
    }
}
