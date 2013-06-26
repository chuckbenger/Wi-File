package com.tkblackbelt.sync.net.http;


import java.io.File;
import java.io.IOException;

import static com.tkblackbelt.sync.core.MyLog.E;

public class HttpSeverManager {

    private static HttpServer server;
    public final static int HTTP_PORT = 3323;

    /**
     * Starts the http server
     * @return Returns true if started else false
     */
    public static boolean start() {

        try {
            if (server == null || !server.isRunning())
                server = new HttpServer(HTTP_PORT, new File("/"));
        } catch (IOException e) {
            E("Failed to start http server " + e.getMessage());
            return false;
        }
        return server.isRunning();
    }

    /**
     * Stops the http server
     */
    public static void stop() {
        if(server != null)
            server.stop();
    }

    /**
     * Check if the http server is running
     * @return
     */
    public static boolean isRunning() {
        if(server == null)
            return false;
        else
            return server.isRunning();
    }
}

