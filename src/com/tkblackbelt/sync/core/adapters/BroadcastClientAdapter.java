package com.tkblackbelt.sync.core.adapters;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.tkblackbelt.sync.R;
import com.tkblackbelt.sync.core.client.BroadcastClient;

import java.util.ArrayList;

public final class BroadcastClientAdapter extends ArrayAdapter<BroadcastClient> {

    private Context context;
    private int layoutResourceId;
    private ArrayList<BroadcastClient> data = null;

    public BroadcastClientAdapter(Context context, int layoutResourceId, ArrayList<BroadcastClient> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        BroadcastHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new BroadcastHolder();
            holder.txtTitle = (TextView) row.findViewById(R.id.txtTitle);

            row.setTag(holder);
        } else {
            holder = (BroadcastHolder) row.getTag();
        }

        BroadcastClient client = data.get(position);
        holder.txtTitle.setText(client.getName());

        return row;
    }

    static class BroadcastHolder {
        TextView txtTitle;
    }
}
