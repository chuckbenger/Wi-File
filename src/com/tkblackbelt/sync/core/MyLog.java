package com.tkblackbelt.sync.core;


import android.util.Log;

public final class MyLog {

    private final static String DEBUG_NAME = "com.tkblackbelt.sync.debug";
    private final static boolean DEBUG = true;

    /**
     * Logs a debug message
     *
     * @param message the debug message
     */
    public static void D(String message) {
        if (DEBUG) {
            Log.d(DEBUG_NAME, message);
        }
    }

    /**
     * Logs a error message
     *
     * @param message the error message
     */
    public static void E(String message) {
        if (DEBUG) {
            Log.e(DEBUG_NAME, message);
        }
    }
}
