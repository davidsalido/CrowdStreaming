package com.crowdstreaming.net;

import android.net.wifi.aware.PeerHandle;
import android.net.wifi.aware.PublishConfig;
import android.net.wifi.aware.PublishDiscoverySession;
import android.os.Build;

import com.crowdstreaming.ui.streaming.StreamingPresenter;

import java.util.List;

public class Publisher extends OwnDiscoverySessionCallback {

    public static final PublishConfig CONFIGPUBL = new PublishConfig.Builder().setServiceName("CrowdStreaming").build();

    private PublishDiscoverySession session;

    private StreamingPresenter presenter;

    public Publisher(StreamingPresenter presenter){
        this.presenter = presenter;
    }

    @Override
    public void onPublishStarted(PublishDiscoverySession session) {
        super.onPublishStarted(session);
        this.session = session;
        presenter.showMessage("Sesión publisher creada");
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
        else if(messageString.equals("ack")){
            this.setPeerHandle(peerHandle);
            //realizarConexion(publishDiscoverySession,peerHandle);
            //publishDiscoverySession.sendMessage(peerHandle,1,"realiza conexion".getBytes());
        }
        else if(messageString.contains("PORT:")){
            this.setPortToUse(Integer.parseInt(messageString.split(":")[1]));
        }
        else{
            this.setOtherIp(message);
        }

    }

    public PublishDiscoverySession getSession() {
        return session;
    }

    public void setSession(PublishDiscoverySession session) {
        this.session = session;
    }
}
