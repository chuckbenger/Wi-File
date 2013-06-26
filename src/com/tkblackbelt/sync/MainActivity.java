package com.tkblackbelt.sync;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.widget.Toast;
import com.tkblackbelt.sync.fragment.BroadcastListFragment;
import com.tkblackbelt.sync.fragment.SavedListFragment;
import com.tkblackbelt.sync.net.http.HttpConnectionInfo;
import com.tkblackbelt.sync.util.Helper;
import com.tkblackbelt.sync.net.http.HttpSeverManager;

import static android.nfc.NdefRecord.createMime;
import static com.tkblackbelt.sync.core.MyLog.D;


public class MainActivity extends FragmentActivity implements ActionBar.TabListener, NfcAdapter.CreateNdefMessageCallback {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private NfcAdapter nfcAdapter;

    private boolean handledNFC = false;
    private NotificationManager notificationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        setupNFC();
        initView();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        cm.setNetworkPreference(ConnectivityManager.TYPE_WIFI);

        if (HttpSeverManager.start()) {
            Helper.sendNotification(this, notificationManager);
        } else {
            Toast.makeText(this, "Failed to start file server", Toast.LENGTH_LONG).show();
        }


        D(getClass().getName() + " onCreate");
    }

    /**
     * Initializes the nfs system
     */
    private void setupNFC() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        // Register callback
        nfcAdapter.setNdefPushMessageCallback(this, this);
    }


    /**
     * Initializes the view of the activity
     */
    private void initView() {
        final ActionBar actionBar = getActionBar();
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            actionBar.addTab(actionBar.newTab()
                    .setText(mSectionsPagerAdapter.getPageTitle(i))
                    .setTabListener(this));
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        D("OnNewIntent");

        setIntent(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check to see that the Activity started due to an Android Beam
        if (!handledNFC && NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
            handledNFC = true;
        }

        D(getClass().getName() + " onResume");
    }



    @Override
    protected void onStop() {
        super.onStop();
        //server.stop();
        D(getClass().getName() + " onStop");
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {

        NdefMessage msg = new NdefMessage(
                new NdefRecord[]{createMime(
                        "text/plain", (Helper.getWifiIpAddress() + ":" + Helper.getDeviceName()).getBytes())
                        /**
                         * The Android Application Record (AAR) is commented out. When a device
                         * receives a push with an AAR in it, the application specified in the AAR
                         * is guaranteed to run. The AAR overrides the tag dispatch system.
                          * You can add it back in to guarantee that this
                         * activity starts when receiving a beamed message. For now, this code
                         * uses the tag dispatch system.
                         */
                        //,NdefRecord.createApplicationRecord("com.example.android.beam")
                });

        return msg;
    }

    /**
     * Parses the NDEF Message from the intent and prints to the TextView
     */
    private void processIntent(Intent intent) {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        // record 0 contains the MIME type, record 1 is the AAR, if present
        String data = new String(msg.getRecords()[0].getPayload());
        String[] parts = data.split(":");
        startBrowserActivity(parts[0], parts[1]);
    }

    /**
     * Starts an activity that will be used to browse the network location
     *
     * @param ip the ip address to connect to
     */
    private void startBrowserActivity(String ip, String name) {
        Intent intent = new Intent(this, BrowserActivity.class);
        intent.putExtra(BrowserActivity.BROADCAST_CLIENT_ARG, new HttpConnectionInfo(ip, name));
        intent.putExtra(BrowserActivity.HTTP_PORT_ARG, HttpSeverManager.HTTP_PORT);
        startActivity(intent);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = new BroadcastListFragment();
                    break;
                case 1:
                    fragment = new SavedListFragment();
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase();
                case 1:
                    return getString(R.string.title_section2).toUpperCase();
            }
            return null;
        }
    }
}
