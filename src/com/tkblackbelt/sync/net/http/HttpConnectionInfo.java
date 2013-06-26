package com.tkblackbelt.sync.net.http;

import java.io.Serializable;

/**
 * Meta data class for holding the address of an http server to connect to and the name of the device
 */
public class HttpConnectionInfo implements Serializable {

    private String address;
    private String name;

    public HttpConnectionInfo(String address, String name) {
        this.address = address;
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }
}
