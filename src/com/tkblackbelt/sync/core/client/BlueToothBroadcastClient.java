package com.tkblackbelt.sync.core.client;


import android.bluetooth.BluetoothDevice;
import com.tkblackbelt.sync.net.http.HttpConnectionInfo;

public class BlueToothBroadcastClient extends BroadcastClient {

    private BluetoothDevice device;
    private String httpIP;

    public BlueToothBroadcastClient(BluetoothDevice device) {
        this.device = device;
    }

    public void setHttpIP(String httpIP) {
        this.httpIP = httpIP;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    @Override
    public HttpConnectionInfo getConnectionInfo() {
        return new HttpConnectionInfo(httpIP, device.getName());
    }

    @Override
    public String getName() {
        return device.getName();
    }
}
