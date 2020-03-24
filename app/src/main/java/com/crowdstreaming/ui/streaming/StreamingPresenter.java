package com.crowdstreaming.ui.streaming;

import android.net.wifi.aware.WifiAwareSession;
import android.os.Environment;

import com.crowdstreaming.net.Publisher;
import com.crowdstreaming.net.WifiAwareSessionUtillities;

import java.io.File;

public class StreamingPresenter {

    private StreamingView view;
    private Publisher publisher;

    public StreamingPresenter(StreamingView view){
        this.view = view;
    }

    public void recButton(){
        WifiAwareSession session = WifiAwareSessionUtillities.getSession();
        publisher = new Publisher(this, view.getConnectivityManager());
        session.publish(Publisher.CONFIGPUBL,publisher,null);
    }

    public void sendFile(File file){
        publisher.clientSendFile(file);
    }

    public Publisher getPublisher(){
        return publisher;
    }

    public void showMessage(String msg){
        view.showMessage(msg);
    }
}
