package com.tkblackbelt.sync.net.broadcast;


public final class BroadcastListener {

    private final static int LISTEN_PORT = 9878;
    private static BroadcastListenerThread listenerThread;

    /**
     * Starts the broadcast listener thread
     */
    public static void start() {
        if (listenerThread == null)
            listenerThread = new BroadcastListenerThread();

        if (!listenerThread.isRunning()) {
            listenerThread.start(LISTEN_PORT);
            Thread thread = new Thread(listenerThread);
            thread.start();
        }
    }

    /**
     * Set's the method that will receive callback data when a broadcast is received
     *
     * @param callback the callback method or null to have no trigger
     */
    public static void setDataCallback(BroadcastDataListener callback) {
        if (listenerThread != null)
            listenerThread.setDataCallback(callback);
    }

    /**
     * Stops the broadcast listener thread
     */
    public static void stop() {
        if (listenerThread == null || !listenerThread.isRunning())
            return;

        listenerThread.stop();
    }
}
