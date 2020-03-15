package com.crowdstreaming.ui.streaming;

import android.net.wifi.aware.WifiAwareSession;

import com.crowdstreaming.net.Publisher;
import com.crowdstreaming.net.WifiAwareSessionUtillities;

public class StreamingPresenter {

    private StreamingView view;
    private Publisher publisher;

    public StreamingPresenter(StreamingView view){
        this.view = view;
    }

    public void recButton(){
        WifiAwareSession session = WifiAwareSessionUtillities.getSession();
        publisher = new Publisher(this);
        session.publish(Publisher.CONFIGPUBL,publisher,null);
    }

    public void showMessage(String msg){
        view.showMessage(msg);
    }
}
