package com.crowdstreaming.net;

import android.net.wifi.aware.DiscoverySessionCallback;
import android.net.wifi.aware.PeerHandle;
import android.net.wifi.aware.PublishDiscoverySession;
import android.net.wifi.aware.SubscribeConfig;
import android.net.wifi.aware.SubscribeDiscoverySession;
import android.widget.Toast;

import com.crowdstreaming.net.OwnDiscoverySessionCallback;
import com.crowdstreaming.ui.avaliablesstreamings.AvaliablesStreamingsPresenter;

import java.util.List;

public class Subscriber extends OwnDiscoverySessionCallback {

    public static final SubscribeConfig CONFIGSUBS = new SubscribeConfig.Builder().setServiceName("CrowdStreaming").build();

    private SubscribeDiscoverySession session;

    private AvaliablesStreamingsPresenter presenter;

    public Subscriber(AvaliablesStreamingsPresenter presenter){
        this.presenter = presenter;
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
        System.out.println("Movil descubierto");
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
        else if(messageString.equals("realiza conexion")){
            //realizarConexion(subscribeDiscoverySession,peerHandle);
        }
        else if(messageString.contains("PORT:")){
            setPortToUse(Integer.parseInt(messageString.split(":")[1]));
        }
        else {
            setOtherIp(message);
        }

    }


    public SubscribeDiscoverySession getSession() {
        return session;
    }

    public void setSession(SubscribeDiscoverySession session) {
        this.session = session;
    }
}
