package com.tkblackbelt.sync.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.AdapterView;
import com.tkblackbelt.sync.BrowserActivity;
import com.tkblackbelt.sync.MainActivity;
import com.tkblackbelt.sync.net.http.HttpSeverManager;
import com.tkblackbelt.sync.R;
import com.tkblackbelt.sync.core.adapters.ConnectionAdapter;
import com.tkblackbelt.sync.db.ArchivedConnection;
import com.tkblackbelt.sync.db.ConnectionDataSource;
import com.tkblackbelt.sync.net.http.HttpConnectionInfo;

import java.util.List;

public class SavedListFragment extends ListFragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private List<ArchivedConnection> connections;
    private ConnectionAdapter adapter;
    private ConnectionDataSource connectionDataSource;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        connectionDataSource = new ConnectionDataSource(getActivity());
        connectionDataSource.open();
        connections = connectionDataSource.getConnections();
        connectionDataSource.close();

        adapter = new ConnectionAdapter(getActivity(), R.layout.history_list_row, connections);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);
        getListView().setOnItemLongClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        connectionDataSource.open();
        List<ArchivedConnection> connections1 = connectionDataSource.getConnections();
        connections.clear();
        connections.addAll(connections1);
        connectionDataSource.close();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ArchivedConnection connection = connections.get(position);
        startBrowserActivity(connection);
    }

    /**
     * Starts an activity that will be used to browse the network location
     *
     * @param connection the connection
     */
    private void startBrowserActivity(ArchivedConnection connection) {
        Intent intent = new Intent(getActivity(), BrowserActivity.class);
        intent.putExtra(BrowserActivity.BROADCAST_CLIENT_ARG, new HttpConnectionInfo(connection.getAddress(), connection.getName()));
        intent.putExtra(BrowserActivity.HTTP_PORT_ARG, HttpSeverManager.HTTP_PORT);

        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        AlertDialog diaBox = AskOption(connections.get(position));
        diaBox.show();
        return true;
    }

    private AlertDialog AskOption(final ArchivedConnection connection) {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(getActivity())
                //set message, title, and icon
                .setTitle("Delete")
                .setMessage("Do you want to Delete " + connection.getName())
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        connectionDataSource.open();
                        connectionDataSource.deleteConnection(connection);
                        connections.remove(connection);
                        adapter.notifyDataSetChanged();
                        connectionDataSource.close();
                        dialog.dismiss();
                    }

                })

                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        return myQuittingDialogBox;

    }
}
