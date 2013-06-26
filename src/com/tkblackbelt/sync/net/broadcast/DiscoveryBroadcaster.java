package com.tkblackbelt.sync.net.broadcast;

import android.content.Context;
import com.tkblackbelt.sync.util.Helper;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static com.tkblackbelt.sync.core.MyLog.D;
import static com.tkblackbelt.sync.core.MyLog.E;

/**
 * Manager class for starting and stopping a udp broadcast sender
 */
public final class DiscoveryBroadcaster {

    private final static int BROADCAST_PORT = 9878;
    private final static int BROADCAST_DELAY_MS = 2000;
    private final static int INITIAL_BROADCAST_DELAY_MS = 2000;
    private static DiscoveryTask discoveryTask;
    private static Timer discoveryTimer;
    private static boolean isRunning;
    private static Context context;

    /**
     * Starts the broadcast discovery task
     */
    public static void start(Context mcontext) {
        if (!isRunning) {
            context = mcontext;
            D("Broadcast timer started");
            discoveryTimer = new Timer();
            discoveryTask = new DiscoveryTask();
            discoveryTimer.schedule(discoveryTask, INITIAL_BROADCAST_DELAY_MS, BROADCAST_DELAY_MS);
            isRunning = true;
        }
    }

    /**
     * Stops the broadcast discovery task
     */
    public static void stop() {
        if (isRunning) {
            D("Broadcast timer stopped");
            discoveryTimer.cancel();
            discoveryTask.cancel();
            isRunning = false;
        }
    }

    private static class DiscoveryTask extends TimerTask {

        private static final Broadcaster broadcaster = new Broadcaster();
        private static byte[] deviceName = Helper.getDeviceName().getBytes();

        @Override
        public void run() {
            try {
                broadcaster.broadcast(Helper.getBroadcastAddress(context), BROADCAST_PORT, deviceName);
                broadcaster.broadcastToAll(BROADCAST_PORT, deviceName);
            } catch (IOException e) {
                E("Failed to send datagram packet to " + e.getMessage());
            }
        }
    }
}











