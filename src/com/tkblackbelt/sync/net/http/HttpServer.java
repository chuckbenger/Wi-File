package com.tkblackbelt.sync.net.http;

import java.io.File;
import java.io.IOException;

public class HttpServer extends NanoHTTPD {

    private boolean running;


    /**
     * Starts a HTTP server to given port.<p>
     * Throws an IOException if the socket is already in use
     */
    public HttpServer(int port, File wwwroot) throws IOException {
        super(port, wwwroot);
        running = true;
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    public void stop() {
        super.stop();
        running = false;
    }
}







