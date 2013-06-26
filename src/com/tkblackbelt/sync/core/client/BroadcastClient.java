package com.tkblackbelt.sync.core.client;


import com.tkblackbelt.sync.net.http.HttpConnectionInfo;


/**
 * Base class to represent all types of broadcast clients
 */
public abstract class BroadcastClient {

    /**
     * Get the connection information for a http connection from a broadcast client
     *
     * @return returns the http connection information
     */
    public abstract HttpConnectionInfo getConnectionInfo();

    /**
     * Returns the name of the broadcast client
     *
     * @return
     */
    public abstract String getName();

}
