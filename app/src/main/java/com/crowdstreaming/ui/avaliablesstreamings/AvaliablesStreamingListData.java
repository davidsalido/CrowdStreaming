package com.crowdstreaming.ui.avaliablesstreamings;

public class AvaliablesStreamingListData {

    private String name, mac;

    public AvaliablesStreamingListData(String name, String mac){
        this.name = name;
        this.mac = mac;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
}
