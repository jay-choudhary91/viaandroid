package com.cryptoserver.composer.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cryptoserver.composer.R;
import com.cryptoserver.composer.interfaces.adapteritemclick;
import com.cryptoserver.composer.models.graphicalmodel;
import com.cryptoserver.composer.models.metricmodel;
import com.cryptoserver.composer.utils.common;

import java.util.ArrayList;

public class graphicaldataadapter extends RecyclerView.Adapter<graphicaldataadapter.ViewHolder> {

    ArrayList<graphicalmodel> graphicaldataArrayList;
    private Context mContext;
    adapteritemclick mItemClick;

    public graphicaldataadapter(ArrayList<graphicalmodel> graphicaldataArrayList, Context mContext) {
        this.graphicaldataArrayList = graphicaldataArrayList;
        this.mContext = mContext;
       // this.mItemClick = mItemClick;
    }

    @NonNull
    @Override
    public graphicaldataadapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_graphical, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull graphicaldataadapter.ViewHolder holder, int position) {
        final graphicalmodel graphicalmodeldata = graphicaldataArrayList.get(position);
        holder.tv_graphicaltitle.setText(graphicalmodeldata.getGraphicalkeyname());
        holder.tv_graphicalvalue.setText(graphicalmodeldata.getGraphicalvalue());

        holder.tv_graphicaltitle.setVisibility(View.VISIBLE);
        if(graphicalmodeldata.getGraphicalkeyname().toString().trim().isEmpty())
            holder.tv_graphicaltitle.setVisibility(View.GONE);

        if(graphicalmodeldata.getGraphicalvalue().trim().isEmpty() || graphicalmodeldata.getGraphicalvalue().equalsIgnoreCase("null"))
            holder.tv_graphicalvalue.setText("NA");
    }

    @Override
    public int getItemCount() {
        return graphicaldataArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_graphicaltitle,tv_graphicalvalue;
        public ViewHolder(View itemView) {
            super(itemView);
            tv_graphicaltitle = (TextView) itemView.findViewById(R.id.txt_graphicaltitle);
            tv_graphicalvalue = (TextView) itemView.findViewById(R.id.txt_graphicalvalue);
        }
    }
}
