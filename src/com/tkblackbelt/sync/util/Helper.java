package com.tkblackbelt.sync.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import com.tkblackbelt.sync.MainActivity;
import com.tkblackbelt.sync.R;
import com.tkblackbelt.sync.net.http.HttpSeverManager;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import static com.tkblackbelt.sync.core.MyLog.D;

public class Helper {

    private static String localIP = null;
    private static String wifiIP = null;

    /**
     * Returns a string in the format of device model:manufacturer
     *
     * @return returns the device model and manufacturer
     */
    public static String getDeviceName() {
        return Build.MODEL + ":" + Build.MANUFACTURER;
    }

    /**
     * Returns the wi-fi broadcast address
     *
     * @param context the app context
     * @return returns an the broadcast address
     * @throws IOException
     */
    public static InetAddress getBroadcastAddress(Context context) throws IOException {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();
        // handle null somehow

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }

    public static String getWifiIpAddress() {
        if(wifiIP != null)
            return wifiIP;

        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en
                    .hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                if (intf.getName().contains("wlan")) {
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
                            .hasMoreElements();) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()
                                && (inetAddress.getAddress().length == 4)) {
                            D(inetAddress.getHostAddress());
                            wifiIP = inetAddress.getHostAddress();
                            return wifiIP;
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            D(ex.toString());
        }
        return null;
    }

    public static String getLocalIpAddress() {
        if(localIP != null)
            return localIP;

        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address)) {
                        localIP = inetAddress.getHostAddress().toString();
                        return localIP;
                    }
                }
            }
        } catch (SocketException ex) {
            D(ex.toString());
        }
        return "";
    }

    /**
     * Starts a notification item letting the user know the http server is running
     * @param context
     * @param notificationManager
     */
    public static void sendNotification(Context context, NotificationManager notificationManager) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);


        Notification noti = new Notification.Builder(context)
                .setContentTitle("WI-File")
                .setContentText("running at http://" + Helper.getWifiIpAddress() + ":" + HttpSeverManager.HTTP_PORT)
                .setSmallIcon(R.drawable.ic_launcher)
                .setOngoing(true)
                .setContentIntent(pIntent).build();

        notificationManager.notify(0, noti);
    }
}
