package com.crowdstreaming.net;

import android.net.ConnectivityManager;
import android.net.wifi.aware.PeerHandle;
import android.net.wifi.aware.PublishConfig;
import android.net.wifi.aware.PublishDiscoverySession;
import android.os.Build;

import com.crowdstreaming.net.connection.PublisherConnection;
import com.crowdstreaming.ui.streaming.StreamingView;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet6Address;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

public class Publisher extends OwnDiscoverySessionCallback {

    public static final PublishConfig CONFIGPUBL = new PublishConfig.Builder().setServiceName("CrowdStreaming").build();

    private PublishDiscoverySession session;

    private StreamingView view;

    private ConnectivityManager connectivityManager;

    public Publisher(StreamingView view, ConnectivityManager connectivityManager){
        this.view = view;
        this.connectivityManager = connectivityManager;
    }

    @Override
    public void onPublishStarted(PublishDiscoverySession session) {
        super.onPublishStarted(session);
        this.session = session;
        //view.showMessage("Sesión publisher creada");
    }

    @Override
    public void onServiceDiscovered(PeerHandle peerHandle, byte[] serviceSpecificInfo, List<byte[]> matchFilter) {
        super.onServiceDiscovered(peerHandle,serviceSpecificInfo,matchFilter);
        this.setPeerHandle(peerHandle);
    }

    @Override
    public void onMessageReceived(PeerHandle peerHandle, byte[] message) {
        super.onMessageReceived(peerHandle,message);

        String messageString = new String(message);

        if(messageString.equals("identifyrequest")){
            byte[] mac = WifiAwareSessionUtillities.getMac();
            String macAddress = String.format("%02x:%02x:%02x:%02x:%02x:%02x", mac[0], mac[1], mac[2], mac[3], mac[4], mac[5]);
            String response = "identifyresponse;" + macAddress + ";" + Build.MODEL;
            session.sendMessage(peerHandle,1,response.getBytes());
        }
        else if(messageString.equals("connectionrequest")){
            this.setPeerHandle(peerHandle);
            setConnection(new PublisherConnection(connectivityManager,session,peerHandle));
            getConnection().connect(session,peerHandle);
            session.sendMessage(peerHandle,1,"connectionresponse".getBytes());

        }
        else if(messageString.contains("PORT:")){
            this.setPortToUse(Integer.parseInt(messageString.split(":")[1]));
        }
        else if(messageString.contains("startstreaming")){
            view.startStreamingPublic();
        }
        else{
            String ipAddr = null;
            try {
                ipAddr = Inet6Address.getByAddress(message).toString();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            this.setOtherIp(message);
        }

    }

    public PublishDiscoverySession getSession() {
        return session;
    }

    public void setSession(PublishDiscoverySession session) {
        this.session = session;
    }

    public Inet6Address getAddress() throws UnknownHostException {
        return Inet6Address.getByAddress("WifiAwareHost",getOtherIp(), getConnection().getIpv6().getScopedInterface());
    }

    public int getPort(){
        return getPortToUse();
    }


    public void clientSendFile(final File file) {
        Runnable clientTask = new Runnable() {
            @Override
            public void run() {
                byte[] buffer = new byte[4096];
                int bytesRead;
                Socket clientSocket = null;
                InputStream is = null;
                OutputStream outs = null;
                try {
                    String ipAddr = null;
                    try {
                        ipAddr = Inet6Address.getByAddress(getOtherIp()).toString();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }

                    Inet6Address address = Inet6Address.getByAddress("WifiAwareHost",getOtherIp(), getConnection().getIpv6().getScopedInterface());
                    clientSocket = new Socket( address , getPortToUse() );
                    outs = clientSocket.getOutputStream();

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                try {

                    InputStream in = new FileInputStream(file);
                    int count;
                    int totalSent = 0;
                    DataOutputStream dos = new DataOutputStream(outs);

                    while ((count = in.read(buffer))>0){
                        totalSent += count;
                        dos.write(buffer, 0, count);
                    }
                    in.close();
                    dos.close();
                } catch(IOException e){
                    e.printStackTrace();
                }

            }
        };
        Thread clientThread = new Thread(clientTask);
        clientThread.start();

    }

    public void callSubscriber(){
        session.sendMessage(getPeerHandle(),1,"streamingstarted".getBytes());
    }

}
