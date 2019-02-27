package com.deeptruth.app.android.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.models.folder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 4/2/19.
 */

public class mediaqualityadapter extends ArrayAdapter<String> {

    private final LayoutInflater mInflater;
    private final Context mContext;
    private final ArrayList<folder> folderitems=new ArrayList<>();
    private final int mResource;
    private final List<String> items;

    public mediaqualityadapter(@NonNull Context context, @LayoutRes int resource,
                               @NonNull List objects) {
        super(context, resource, 0, objects);
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mResource = resource;
        items = objects;
    }
    @Override
    public View getDropDownView(int position, @Nullable View convertView,
                                @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @Override
    public @NonNull View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int position, View convertView, ViewGroup parent){
        final View view = mInflater.inflate(mResource, parent, false);

        TextView folder_name = (TextView) view.findViewById(R.id.folder_name);
        folder_name.setText(items.get(position));
        return view;
    }
}