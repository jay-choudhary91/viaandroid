package com.deeptruth.app.android.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.interfaces.adapteritemclick;
import com.deeptruth.app.android.models.mediatypeoptions;
import com.deeptruth.app.android.models.video;

import java.util.ArrayList;

/**
 * Created by devesh on 21/8/19.
 */

public class adaptermediaoptionstype extends RecyclerView.Adapter<adaptermediaoptionstype.myViewHolder> {
    ArrayList<mediatypeoptions> arraymediatypeoption = new ArrayList<>();
    adapteritemclick adapter;
    Context context;


    public class myViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;
        TextView txt_options;
        ImageView img_updownarrow;

        public myViewHolder(View view) {
            super(view);
            linearLayout = (LinearLayout) view.findViewById(R.id.layout_typeoption);
            txt_options = (TextView) view.findViewById(R.id.txt_optiontype);
            img_updownarrow = (ImageView) view.findViewById(R.id.img_updownarrow);
        }
    }

    public adaptermediaoptionstype(Context context ,ArrayList<mediatypeoptions> arraymediatypeoption,adapteritemclick adapter){
        this.context = context;
        this.arraymediatypeoption = arraymediatypeoption;
        this.adapter = adapter;
    }
    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_mediaoptions, parent, false);

        return new adaptermediaoptionstype.myViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {


        final mediatypeoptions mediaobject=arraymediatypeoption.get(position);
        Log.e("getMediatypename",mediaobject.getMediatypename());
        holder.txt_options.setText(mediaobject.getMediatypename());
    }

    @Override
    public int getItemCount() {
        Log.e("arraylistsize",""+arraymediatypeoption.size());
        return arraymediatypeoption.size();
    }
}
