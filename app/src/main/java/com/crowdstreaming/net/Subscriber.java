package com.crowdstreaming.net;

import android.net.ConnectivityManager;
import android.net.wifi.aware.DiscoverySessionCallback;
import android.net.wifi.aware.PeerHandle;
import android.net.wifi.aware.PublishDiscoverySession;
import android.net.wifi.aware.SubscribeConfig;
import android.net.wifi.aware.SubscribeDiscoverySession;
import android.os.Environment;
import android.widget.Toast;

import com.crowdstreaming.net.OwnDiscoverySessionCallback;
import com.crowdstreaming.net.connection.AbstractConnection;
import com.crowdstreaming.net.connection.SubscriberConnection;
import com.crowdstreaming.ui.avaliablesstreamings.AvaliablesStreamingsPresenter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.Inet6Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Subscriber extends OwnDiscoverySessionCallback {

    public static final SubscribeConfig CONFIGSUBS = new SubscribeConfig.Builder().setServiceName("CrowdStreaming").build();

    private SubscribeDiscoverySession session;

    private AvaliablesStreamingsPresenter presenter;

    private ConnectivityManager connectivityManager;

    public Subscriber(AvaliablesStreamingsPresenter presenter, ConnectivityManager connectivityManager){
        this.presenter = presenter;
        this.connectivityManager = connectivityManager;
    }

    @Override
    public void onSubscribeStarted(SubscribeDiscoverySession session) {
        super.onSubscribeStarted(session);
        this.session = session;
    }

    @Override
    public void onServiceDiscovered(PeerHandle peerHandle, byte[] serviceSpecificInfo, List<byte[]> matchFilter) {
        super.onServiceDiscovered(peerHandle,serviceSpecificInfo,matchFilter);
        this.setPeerHandle(peerHandle);
        session.sendMessage(peerHandle,1,"identifyrequest".getBytes());
        //MainActivity.this.subscribeDiscoverySession.sendMessage(peerHandle,1,"ack".getBytes()); //Mensaje de ack

    }


    @Override
    public void onMessageReceived(PeerHandle peerHandle, byte[] message) {
        super.onMessageReceived(peerHandle,message);

        String messageString = new String(message);

        if(messageString.contains("identifyresponse")){
            String [] s = messageString.split(";");
            presenter.addDevice(s[2] + " - " + s[1]);
        }
        else if(messageString.equals("connectionresponse")){
            setConnection(new SubscriberConnection(connectivityManager,session, peerHandle));
            getConnection().connect(session, peerHandle);
            System.out.println("ha llegado");
            presenter.saveFile();
            presenter.cambiarVista();
        }
        else if(messageString.contains("PORT:")){
            setPortToUse(Integer.parseInt(messageString.split(":")[1]));
        }
        else {
            setOtherIp(message);
        }

    }

    public void realizaConexion(){
        session.sendMessage(getPeerHandle(),1,"connectionrequest".getBytes());
    }

    public SubscribeDiscoverySession getSession() {
        return session;
    }

    public void setSession(SubscribeDiscoverySession session) {
        this.session = session;
    }

    public byte[] portToBytes(int port){
        byte[] data = new byte [2];
        data[0] = (byte) (port & 0xFF);
        data[1] = (byte) ((port >> 8) & 0xFF);
        return data;
    }

    public AbstractConnection getConnection(){
        return super.getConnection();
    }


    public void saveFile(File carpeta) throws IOException, InterruptedException {
        while (true) {

            ServerSocket serverSocket = getConnection().getServerSocket();

            Socket clientSocket = serverSocket.accept();

            DataInputStream in = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));

            byte[] buffer = new byte[4096];
            int read;
            int totalRead = 0;
            FileOutputStream fos = new FileOutputStream(carpeta + "/salida.mp4");

            while ((read = in.read(buffer)) > 0) {

                fos.write(buffer,0,read);
                totalRead += read;
            }

        }
    }
}
