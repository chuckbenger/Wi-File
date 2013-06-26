package com.tkblackbelt.sync.net.broadcast;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import static com.tkblackbelt.sync.core.MyLog.D;
import static com.tkblackbelt.sync.core.MyLog.E;

public class BroadcastListenerThread implements Runnable {

    private DatagramSocket socket;
    private boolean running;
    private byte[] buffer = new byte[256];
    private BroadcastDataListener dataCallback;

    /**
     * Shuts down the broadcast listener thread
     */
    public void stop() {
        running = false;
        socket.close();
    }

    /**
     * Starts the datagram socket
     *
     * @param port the port to listen on
     */
    public void start(int port) {
        try {
            socket = new DatagramSocket(port);
            running = true;
        } catch (SocketException e) {
            E("Failed to start udp listener: " + e.getMessage());
        }
    }

    public void setDataCallback(BroadcastDataListener dataCallback) {
        this.dataCallback = dataCallback;
    }

    @Override
    public void run() {
        if (isRunning())
            D("Listener thread started");
        while (isRunning()) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                if (dataCallback != null)
                    dataCallback.onBroadcastDataReceived(packet.getAddress().getHostAddress(), packet.getData().clone());

            } catch (IOException e) {
                running = false;
                E("UDP Receive failed " + e.getMessage());
            }
        }
        D("Listener thread shutdown or never started");
    }

    public boolean isRunning() {
        return running;
    }
}
