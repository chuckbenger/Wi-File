package com.tkblackbelt.sync.fragment;


import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import com.tkblackbelt.sync.BrowserActivity;
import com.tkblackbelt.sync.R;
import com.tkblackbelt.sync.core.adapters.BroadcastClientAdapter;
import com.tkblackbelt.sync.core.client.BroadcastClient;
import com.tkblackbelt.sync.core.client.UDPBroadcastClient;
import com.tkblackbelt.sync.net.broadcast.BroadcastDataListener;
import com.tkblackbelt.sync.net.broadcast.BroadcastListener;
import com.tkblackbelt.sync.net.broadcast.DiscoveryBroadcaster;
import com.tkblackbelt.sync.net.http.HttpSeverManager;
import com.tkblackbelt.sync.util.Helper;

import java.util.ArrayList;

import static com.tkblackbelt.sync.core.MyLog.D;

public final class BroadcastListFragment extends ListFragment implements BroadcastDataListener,
        AdapterView.OnItemClickListener {

    private static final int REQUEST_ENABLE_BT = 1231;
    private static final int REQUEST_ENABLE_BT_DISC = 1232;
    private ArrayList<BroadcastClient> clients = new ArrayList<BroadcastClient>();
    private ArrayList<String> takenIpAddresses = new ArrayList<String>();
    private BroadcastClientAdapter adapter;
    private ProgressDialog connectingDialog;
    private NotificationManager notificationManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        notificationManager = (NotificationManager) getActivity().getSystemService(Activity.NOTIFICATION_SERVICE);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new BroadcastClientAdapter(getActivity(), R.layout.broadcaster_list_row, clients);
        setListAdapter(adapter);
        setListShown(false);
        getListView().setOnItemClickListener(this);
        BroadcastListener.setDataCallback(this);


        D(getClass().getName() + " created");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.client_menu, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.menu_http);
        if (item != null) {
            if (HttpSeverManager.isRunning()) {
                item.setIcon(R.drawable.ic_action_io_on);
                item.setTitle("Http Server: ON");
            } else {
                item.setIcon(R.drawable.ic_action_io);
                item.setTitle("Http Server: OFF");
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_refresh:
                clearClients();
                stopUDPListener();
                startUDPListener();
                break;
            case R.id.menu_http:
                if (HttpSeverManager.isRunning()) {
                    HttpSeverManager.stop();
                    item.setIcon(R.drawable.ic_action_io);
                    item.setTitle("Http Server: OFF");
                    cancelServerNotificationItem();
                } else {
                    if (HttpSeverManager.start()) {
                        item.setIcon(R.drawable.ic_action_io_on);
                        item.setTitle("Http Server: ON");
                        Helper.sendNotification(getActivity(), notificationManager);
                    } else {
                        Toast.makeText(getActivity(), "Failed to start file server", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
        return true;
    }

    /**
     * Cancels the http notification item
     */
    private void cancelServerNotificationItem() {
        notificationManager.cancel(0);
    }

    /**
     * Starts a udp presence broadcaster and listener
     */
    private void startUDPListener() {
        DiscoveryBroadcaster.start(getActivity());
        BroadcastListener.start();
    }

    /**
     * Stops a udp presence broadcaster and listener
     */
    private void stopUDPListener() {
        DiscoveryBroadcaster.stop();
        BroadcastListener.stop();
    }

    /**
     * Clears the client data sets
     */
    private void clearClients() {
        takenIpAddresses.clear();
        clients.clear();
        adapter.notifyDataSetChanged();
        setListShown(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        clearClients();
        updateList();
        stopUDPListener();
        D(getClass().getName() + " paused");
    }

    @Override
    public void onResume() {
        super.onResume();

        setListShown(false);
        clearClients();
        startUDPListener();
        BroadcastListener.setDataCallback(this);

        D(getClass().getName() + " resumed");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopUDPListener();
        D(getClass().getName() + " Destroyed");
    }


    @Override
    public void onBroadcastDataReceived(String ip, byte[] data) {
        if (ip.equals(Helper.getWifiIpAddress()) || ip.equals(Helper.getLocalIpAddress()))
            return;
        if (data != null && !takenIpAddresses.contains(ip)) {
            takenIpAddresses.add(ip);
            String message = new String(data).trim();
            String[] items = message.split(":");
            if (items.length == 2) {
                clients.add(new UDPBroadcastClient(items[0], items[1], ip));
                updateList();
            }
        }
    }

    /**
     * Updates the client list
     */
    private void updateList() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                setListShown(true);
            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BroadcastClient client = clients.get(position);
        connectingDialog = ProgressDialog.show(getActivity(), "",
                "Connecting. Please wait...", true, true);

        if (client instanceof UDPBroadcastClient)
            startBrowserActivity(client);
    }

    /**
     * Starts an activity that will be used to browse the network location
     *
     * @param client the client to connect to
     */
    private void startBrowserActivity(BroadcastClient client) {
        Intent intent = new Intent(getActivity(), BrowserActivity.class);
        intent.putExtra(BrowserActivity.BROADCAST_CLIENT_ARG, client.getConnectionInfo());
        intent.putExtra(BrowserActivity.HTTP_PORT_ARG, HttpSeverManager.HTTP_PORT);
        if (connectingDialog != null)
            connectingDialog.cancel();
        startActivity(intent);
    }

}
