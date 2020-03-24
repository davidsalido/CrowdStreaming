package com.crowdstreaming.net.connection;

import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class OwnNetworkCallback extends ConnectivityManager.NetworkCallback {

    private AbstractConnection connection;

    public OwnNetworkCallback(AbstractConnection connection){
        this.connection = connection;
    }

    @Override
    public void onAvailable(Network network) {
        super.onAvailable(network);
    }

    @Override
    public void onLosing(Network network, int maxMsToLive) {
        super.onLosing(network, maxMsToLive);
    }

    @Override
    public void onLost(Network network) {
        super.onLost(network);
    }

    @Override
    public void onUnavailable() {
        super.onUnavailable();

    }

    @Override
    public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
        super.onCapabilitiesChanged(network,networkCapabilities);
    }

    @Override
    public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
        super.onLinkPropertiesChanged(network,linkProperties);

        try {
            NetworkInterface awareNi = NetworkInterface.getByName(linkProperties.getInterfaceName());

            Enumeration<InetAddress> Addresses = awareNi.getInetAddresses();
            while (Addresses.hasMoreElements()) {
                InetAddress addr = Addresses.nextElement();
                if (addr instanceof Inet6Address && addr.isLinkLocalAddress()) {
                    Inet6Address ipv6 = Inet6Address.getByAddress("WifiAware", addr.getAddress(), awareNi);
                    connection.setIpv6(ipv6);
                    connection.sendIp(addr.getAddress());
                    break;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        connection.connectToPublisher(0,3);

    }

}
