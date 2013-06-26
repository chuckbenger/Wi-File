package com.tkblackbelt.sync.core.adapters;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.tkblackbelt.sync.R;
import com.tkblackbelt.sync.db.ArchivedConnection;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class ConnectionAdapter extends ArrayAdapter<ArchivedConnection> {

    private Context context;
    private int layoutResourceId;
    private List<ArchivedConnection> data = null;

    public ConnectionAdapter(Context context, int layoutResourceId, List<ArchivedConnection> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        MyConnectionHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new MyConnectionHolder();
            holder.txtTitle = (TextView) row.findViewById(R.id.txtHistoryTitle);

            row.setTag(holder);
        } else {
            holder = (MyConnectionHolder) row.getTag();
        }

        ArchivedConnection connection = data.get(position);

        String format = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(new Date(connection.getLastConnected()));
        holder.txtTitle.setText(connection.getName() + "@" + connection.getAddress() + " on " + format);

        return row;
    }

    static class MyConnectionHolder {
        TextView txtTitle;
        TextView txtTime;
    }
}
