package com.tkblackbelt.sync.net.broadcast;


public interface BroadcastDataListener {

    /**
     * Callback for when UDP data has been received
     *
     * @param data the data received
     */
    public void onBroadcastDataReceived(String ip, byte[] data);
}
