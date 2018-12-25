package com.cryptoserver.composer.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cryptoserver.composer.R;
import com.cryptoserver.composer.interfaces.adapteritemclick;
import com.cryptoserver.composer.models.graphicalmodel;

import java.util.ArrayList;

public class encryptiondataadapter extends RecyclerView.Adapter<encryptiondataadapter.ViewHolder> {

    ArrayList<graphicalmodel> graphicaldataArrayList;
    private Context mContext;
    adapteritemclick mItemClick;

    public encryptiondataadapter(ArrayList<graphicalmodel> graphicaldataArrayList, Context mContext) {
        this.graphicaldataArrayList = graphicaldataArrayList;
        this.mContext = mContext;
       // this.mItemClick = mItemClick;
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
        final graphicalmodel graphicalmodeldata = graphicaldataArrayList.get(position);
        holder.txt_data_hash.setText(graphicalmodeldata.getGraphicalvalue());


    }

    @Override
    public int getItemCount() {
        return graphicaldataArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_data_hash;
        public ViewHolder(View itemView) {
            super(itemView);
            txt_data_hash = (TextView) itemView.findViewById(R.id.txt_data_hash);
        }
    }
}
