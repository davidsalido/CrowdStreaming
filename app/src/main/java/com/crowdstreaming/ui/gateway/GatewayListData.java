package com.crowdstreaming.ui.gateway;

import android.graphics.Bitmap;

public class GatewayListData {

    private String title, onionUrl, logoUrl;


    public GatewayListData(String title,String onionUrl, String logoUrl) {
        this.title = title;
        this.onionUrl = onionUrl;
        this.logoUrl = logoUrl;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOnionUrl() {
        return onionUrl;
    }

    public void setOnionUrl(String onionUrl) {
        this.onionUrl = onionUrl;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }
}