package com.deeptruth.app.android.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.interfaces.adapteritemclick;
import com.deeptruth.app.android.models.arraycontainer;
import com.deeptruth.app.android.models.graphicalmodel;
import com.deeptruth.app.android.models.metricmodel;

import java.util.ArrayList;

public class encryptiondataadapter extends RecyclerView.Adapter<encryptiondataadapter.ViewHolder> {

    ArrayList<arraycontainer> arrayitemlist;
    private Context mContext;

    public encryptiondataadapter(ArrayList<arraycontainer> arrayitemlist, Context mContext) {
        this.arrayitemlist = arrayitemlist;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public encryptiondataadapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_encryption, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull encryptiondataadapter.ViewHolder holder, int position) {
        final arraycontainer modeldata = arrayitemlist.get(position);
        holder.txt_metahash.setText(modeldata.getMetahash());
        holder.txt_mediahash.setText(modeldata.getValuehash());
        holder.txt_blockchainid.setText(modeldata.getVideostarttransactionid());
        holder.txt_hashformula.setText(modeldata.getHashmethod());
        ArrayList<metricmodel> metalist=modeldata.getMetricItemArraylist();
        for(int i=0;i<metalist.size();i++)
        {
            if(metalist.get(i).getMetricTrackKeyName().equalsIgnoreCase("devicedate"))
                holder.txt_date.setText(metalist.get(i).getMetricTrackValue());

            if(metalist.get(i).getMetricTrackKeyName().equalsIgnoreCase("sequencestarttime"))
                holder.txt_time.setText(metalist.get(i).getMetricTrackValue());

            if(metalist.get(i).getMetricTrackKeyName().equalsIgnoreCase("sequenceendtime"))
                holder.txt_time.setText(holder.txt_time.getText()+" - "+metalist.get(i).getMetricTrackValue());
        }
    }

    @Override
    public int getItemCount() {
        //return arrayitemlist.size();
        return arrayitemlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_date,txt_time,txt_blockchainid,txt_hashformula,txt_mediahash,txt_metahash;
        public ViewHolder(View itemView) {
            super(itemView);
            txt_date = (TextView) itemView.findViewById(R.id.txt_date);
            txt_time = (TextView) itemView.findViewById(R.id.txt_time);
            txt_blockchainid = (TextView) itemView.findViewById(R.id.txt_blockchainid);
            txt_hashformula = (TextView) itemView.findViewById(R.id.txt_hashformula);
            txt_mediahash = (TextView) itemView.findViewById(R.id.txt_mediahash);
            txt_metahash = (TextView) itemView.findViewById(R.id.txt_metahash);
        }
    }
}
