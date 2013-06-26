package com.tkblackbelt.sync.core.client;


import com.tkblackbelt.sync.net.http.HttpConnectionInfo;

public class UDPBroadcastClient extends BroadcastClient {

    private String model;
    private String manufacturer;
    private String ip;

    public UDPBroadcastClient(String model, String manufacturer, String ip) {
        this.model = model;
        this.manufacturer = manufacturer;
        this.ip = ip;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getIp() {
        return ip;
    }

    @Override
    public HttpConnectionInfo getConnectionInfo() {
        return new HttpConnectionInfo(ip, model);
    }

    @Override
    public String getName() {
        return model;
    }
}
