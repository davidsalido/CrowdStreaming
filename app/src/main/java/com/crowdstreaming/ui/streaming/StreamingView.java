package com.crowdstreaming.ui.streaming;

import android.net.ConnectivityManager;

public interface StreamingView {

    public void showMessage(String msg);

    public ConnectivityManager getConnectivityManager();

}
