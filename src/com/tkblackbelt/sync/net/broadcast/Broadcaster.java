package com.tkblackbelt.sync.net.broadcast;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;

import static com.tkblackbelt.sync.core.MyLog.E;

public final class Broadcaster {

    private DatagramSocket socket;

    public Broadcaster() {
        try {
            socket = new DatagramSocket();
            socket.setBroadcast(true);
        } catch (SocketException e) {
            E("Failed to start datagram socket: " + e.getMessage());
        }
    }

    /**
     * Sends a udp packet to a ip:port combo
     *
     * @param address The send address
     * @param port    the send port
     * @param data    the data to send
     * @throws IOException
     */
    public void broadcast(InetAddress address, int port, byte[] data) throws IOException {
        DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
        socket.send(packet);
    }

    /**
     * Broadcasts the all available network interfaces.
     *
     * @param port the port to broadcast to
     * @param data the data to send
     * @throws IOException
     */
    public void broadcastToAll(int port, byte[] data) throws IOException {

        Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();

            //Don't want to send to loop back
            if (networkInterface.isLoopback() || !networkInterface.isUp())
                continue;

            broadcastToInterface(networkInterface, port, data);
        }
    }

    /**
     * Broadcasts the all addresses on an network interface
     *
     * @param networkInterface the network interface to broadcast to
     * @param port             the send port
     * @param data             the data to send
     * @throws IOException
     */
    private void broadcastToInterface(NetworkInterface networkInterface, int port, byte[] data) throws IOException {
        for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
            InetAddress broadcast = interfaceAddress.getBroadcast();
            if (broadcast == null)
                continue;

            broadcast(broadcast, port, data);
        }
    }
}















