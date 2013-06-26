package com.tkblackbelt.sync.core.adapters;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.tkblackbelt.sync.R;
import com.tkblackbelt.sync.net.http.parse.DownloadFolder;
import com.tkblackbelt.sync.net.http.parse.Downloadable;

import java.util.List;

public class MyFileAdapter extends ArrayAdapter<Downloadable> {

    private Context context;
    private int layoutResourceId;
    private List<Downloadable> data = null;

    public MyFileAdapter(Context context, int layoutResourceId, List<Downloadable> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        MyFileHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new MyFileHolder();
            holder.txtTitle = (TextView) row.findViewById(R.id.txtFileName);
            holder.image = (ImageView) row.findViewById(R.id.imgType);

            row.setTag(holder);
        } else {
            holder = (MyFileHolder) row.getTag();
        }

        Downloadable myFile = data.get(position);

        holder.txtTitle.setText(myFile.getName());

        if (myFile instanceof DownloadFolder)
            holder.image.setBackground(context.getResources().getDrawable(R.drawable.ic_action_folder_closed));
        else
            holder.image.setBackground(context.getResources().getDrawable(R.drawable.ic_action_document));

        return row;
    }

    static class MyFileHolder {
        ImageView image;
        TextView txtTitle;
    }
}
