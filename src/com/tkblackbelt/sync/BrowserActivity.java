package com.tkblackbelt.sync;

import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.tkblackbelt.sync.core.adapters.MyFileAdapter;
import com.tkblackbelt.sync.db.ConnectionDataSource;
import com.tkblackbelt.sync.net.http.HtmlDownloadTask;
import com.tkblackbelt.sync.net.http.HttpConnectionInfo;
import com.tkblackbelt.sync.net.http.HttpSeverManager;
import com.tkblackbelt.sync.net.http.parse.DownloadFile;
import com.tkblackbelt.sync.net.http.parse.DownloadFolder;
import com.tkblackbelt.sync.net.http.parse.Downloadable;

import java.util.ArrayList;
import java.util.Stack;

import static com.tkblackbelt.sync.core.MyLog.D;


public class BrowserActivity extends Activity implements HtmlDownloadTask.HtmlDownloadListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    public final static String BROADCAST_CLIENT_ARG = "com.tkblackbelt.sync.broadcastarg";
    public final static String HTTP_PORT_ARG = "com.tkblackbelt.sync.http_port";

    private ConnectionDataSource connectionDataSource;
    private boolean isFirstLaunch = true;
    private TextView listHeader;
    private ListView fileListView;
    private HttpConnectionInfo client;
    private ArrayList<Downloadable> files = new ArrayList<Downloadable>();
    private MyFileAdapter adapter;
    private String fullAddress;
    private Stack<DownloadFolder> folderStack = new Stack<DownloadFolder>();
    private int httpPort;
    private DownloadManager downloadManager;
    private ProgressDialog loadingDialog;
    private ConnectivityManager connManager;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        parseExtras();
        setupList();

        connectionDataSource = new ConnectionDataSource(this);

        downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

        DownloadFolder root = new DownloadFolder("", "/");
        folderStack.push(root);

        fullAddress = "http://" + client.getAddress() + ":" + httpPort + "/";
        setTitle(client.getName() + "@" + client.getAddress());

        downloadHtml();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.browser_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_exit:
                finish();
                break;
            case R.id.menu_refresh:
                downloadHtml();
                break;
        }
        return true;
    }

    /**
     * Sets up the file list view
     */
    private void setupList() {
        fileListView = (ListView) findViewById(R.id.listViewFiles);
        fileListView.addHeaderView(inflateListHeader());
        adapter = new MyFileAdapter(this, R.layout.file_list_row, files);
        fileListView.setAdapter(adapter);
        fileListView.setOnItemClickListener(this);
        fileListView.setOnItemLongClickListener(this);
    }

    /**
     * Inflates the list header and find the directory list field
     *
     * @return returns the list header view
     */
    private View inflateListHeader() {
        View header = View.inflate(this, R.layout.browser_header, null);
        listHeader = (TextView) header.findViewById(R.id.txtCurrentDirectory);
        return header;
    }

    /**
     * Parses any extras that are passed into the activity
     */
    private void parseExtras() {
        Intent intent = getIntent();
        client = (HttpConnectionInfo) intent.getSerializableExtra(BROADCAST_CLIENT_ARG);
        httpPort = intent.getIntExtra(HTTP_PORT_ARG, 8080);
    }

    /**
     * Executes a task that will download and parse the html from the specified page
     */
    private void downloadHtml() {
        files.clear();
        adapter.notifyDataSetChanged();
        loadingDialog = ProgressDialog.show(this, "", "Loading", true, true);
        DownloadFolder folder = folderStack.peek();
        HtmlDownloadTask task = new HtmlDownloadTask(fullAddress + folder.getPath(), this);
        task.execute();
    }

    @Override
    public void onHtmlReceived(ArrayList<Downloadable> resultSet) {
        if (isFirstLaunch) {
            addConnectionToDB(client.getAddress());
            isFirstLaunch = false;
        }
        if (loadingDialog != null)
            loadingDialog.cancel();
        files.addAll(resultSet);
        adapter.notifyDataSetChanged();
        DownloadFolder folder = folderStack.peek();
        listHeader.setText(folder.getName());
    }

    /**
     * Adds the input ip address to the list of connections
     *
     * @param fullAddress the ip address of the connection
     */
    private void addConnectionToDB(String fullAddress) {
        connectionDataSource.open();
        connectionDataSource.createElseUpdate(client.getName(), fullAddress, HttpSeverManager.HTTP_PORT);
        connectionDataSource.close();
    }

    @Override
    public void onHtmlDownloadError() {
        if (loadingDialog != null)
            loadingDialog.cancel();
        Toast.makeText(this, "Failed to get directory.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Downloadable file = files.get(position - 1);
        if (file instanceof DownloadFolder) {
            folderStack.push((DownloadFolder) file);
            downloadHtml();
        } else {
            startDownload((DownloadFile) file, "");
        }
    }


    @Override
    public void onBackPressed() {
        folderStack.pop();
        if (folderStack.empty())
            finish();
        else
            downloadHtml();
    }

    /**
     * Downloads the input file to the downloads directory
     *
     * @param file the file to download
     */
    private void startDownload(DownloadFile file, String subDirectory) {
        Uri uri = Uri.parse(fullAddress + file.getPath());

        Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .mkdirs();


        downloadManager.enqueue(new DownloadManager.Request(uri)
                .setTitle(file.getName())
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS + subDirectory,
                        file.getName()));
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Downloadable file = files.get(position - 1);
        if (file instanceof DownloadFolder) {
            showDownloadDialog((DownloadFolder) file);
        } else {
            startDownload((DownloadFile) file, "");
        }
        return true;
    }

    /**
     * Shows a dialog to the user to prompt for a folder download
     */
    private void showDownloadDialog(final DownloadFolder folder) {
        final Dialog dialog = new Dialog(this);
        dialog.setTitle("Download");
        dialog.setContentView(R.layout.download_dialog);
        final ListView list = (ListView) dialog.findViewById(R.id.listViewDownload);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        D("download");
                        downloadFolder(folder, false);
                        break;
                    case 1:
                        D("download & sub");
                        downloadFolder(folder, true);
                        break;
                }
                dialog.cancel();
            }
        });
        dialog.show();
    }

    private void downloadFolder(DownloadFolder folder, boolean recursive) {
        FolderDownloader folderDownloader = new FolderDownloader(folder.getPath(), folder.getName(), recursive);
        folderDownloader.download();
    }

    private class FolderDownloader implements HtmlDownloadTask.HtmlDownloadListener {

        private String folder;
        private String name;
        private boolean recursive;

        private FolderDownloader(String folder, String name, Boolean recursive) {
            this.folder = folder;
            this.name = name;
            this.recursive = recursive;
        }

        public void download() {
            HtmlDownloadTask downloadTask = new HtmlDownloadTask(fullAddress + folder, this);
            downloadTask.execute();
        }

        @Override
        public void onHtmlReceived(ArrayList<Downloadable> resultSet) {
            for (Downloadable item : resultSet) {
                if (item instanceof DownloadFile) {
                    DownloadFile file = (DownloadFile) item;
                    startDownload(file, "/" + name);
                } else if (recursive) {
                    DownloadFolder nextFolder = (DownloadFolder) item;
                    FolderDownloader downloader = new FolderDownloader(nextFolder.getPath(), name + "/" + nextFolder.getName(), recursive);
                    downloader.download();
                }
            }
        }

        @Override
        public void onHtmlDownloadError() {
            Toast.makeText(BrowserActivity.this, "Access Denied", Toast.LENGTH_SHORT).show();
        }
    }
}