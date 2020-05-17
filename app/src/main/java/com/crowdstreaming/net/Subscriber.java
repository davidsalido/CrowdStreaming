package com.crowdstreaming.net;

import android.icu.util.Output;
import android.net.ConnectivityManager;
import android.net.wifi.aware.PeerHandle;
import android.net.wifi.aware.SubscribeConfig;
import android.net.wifi.aware.SubscribeDiscoverySession;

import com.crowdstreaming.net.connection.AbstractConnection;
import com.crowdstreaming.net.connection.SubscriberConnection;
import com.crowdstreaming.ui.avaliablesstreamings.AvaliablesStreamingsView;
import com.crowdstreaming.ui.watchstreaming.SubscriberObserver;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Subscriber extends OwnDiscoverySessionCallback {

    public static final SubscribeConfig CONFIGSUBS = new SubscribeConfig.Builder().setServiceName("CrowdStreaming").build();

    private SubscribeDiscoverySession session;

    private AvaliablesStreamingsView view;
    private SubscriberObserver observer;

    private Socket clientSocket;

    public Subscriber(AvaliablesStreamingsView view){
        this.view = view;
    }

    @Override
    public void onSubscribeStarted(SubscribeDiscoverySession session) {
        super.onSubscribeStarted(session);
        this.session = session;
    }

    @Override
    public void onMessageSendFailed(@SuppressWarnings("unused") int messageId) {
        super.onMessageSendFailed(messageId);
        System.out.println("No ha llegado");
    }

    @Override
    public void onServiceDiscovered(PeerHandle peerHandle, byte[] serviceSpecificInfo, List<byte[]> matchFilter) {
        super.onServiceDiscovered(peerHandle,serviceSpecificInfo,matchFilter);
        this.setPeerHandle(peerHandle);
        session.sendMessage(peerHandle,1,"identifyrequest".getBytes());

    }


    @Override
    public void onMessageReceived(PeerHandle peerHandle, byte[] message) {
        super.onMessageReceived(peerHandle,message);

        String messageString = new String(message);

        if(messageString.contains("identifyresponse")){
            String [] s = messageString.split(";");
            view.addDevice(s[2] , s[1], peerHandle);
        }
        else if(messageString.equals("connectionresponse")){
            setConnection(new SubscriberConnection(view.getConnectivityManager(),session, peerHandle));
            getConnection().connect(session, peerHandle);
            view.saveFile();
            session.sendMessage(peerHandle,1,"startstreaming".getBytes());
        }
        else if(messageString.contains("PORT:")){
            setPortToUse(Integer.parseInt(messageString.split(":")[1]));
        }
        else if(messageString.contains("streamingstarted")){
            System.out.println("Vista cambiar");
            view.cambiarVista();
        }
        else {
            setOtherIp(message);
        }

    }

    public void setObserver(SubscriberObserver observer){
        this.observer = observer;
    }

    public void realizaConexion(PeerHandle peerHandle){
        session.sendMessage(peerHandle,1,"connectionrequest".getBytes());
    }


    public AbstractConnection getConnection(){
        return super.getConnection();
    }

    private OutputStream nodoTransito;

    public void saveFile(File archivo) throws IOException {
        ServerSocket serverSocket = getConnection().getServerSocket();

        clientSocket = serverSocket.accept();

        DataInputStream in = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));

        byte[] metadata = new byte[4096];
        byte[] buffer = new byte[4096];
        int read;
        int totalRead = 0;
        FileOutputStream fos = new FileOutputStream(archivo);

        while ((read = in.read(buffer)) > 0) {
            if(totalRead == 0){
                read = 4096;
                metadata = buffer.clone();
            }
            if(nodoTransito != null){
                if(metadata != null){
                    nodoTransito.write(metadata,0,4096);
                    metadata = null;
                }
                System.out.println("Mandando");
                nodoTransito.write(buffer,0,read);
            }
            fos.write(buffer,0,read);
            totalRead += read;
        }
        if(observer != null)
            observer.stopStreaming();
    }

    public void setNodoTransito(OutputStream nodoTransito){
        this.nodoTransito = nodoTransito;
    }

    public void closeSocket(){
        //todo
    }
}
