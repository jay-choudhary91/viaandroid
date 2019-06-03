package com.deeptruth.app.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.interfaces.adapteritemclick;
import com.deeptruth.app.android.models.celltowermodel;
import com.deeptruth.app.android.models.pair;

import java.util.ArrayList;
import java.util.List;


public class toweritemadapter extends RecyclerView.Adapter<toweritemadapter.MyViewHolder> {

    ArrayList<celltowermodel> celltowers = new ArrayList<>();
    Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_cellid,tv_mcc,tv_mnc,tv_iso,tv_country,tv_countrycode,tv_network;
        public LinearLayout root_view,layout_header;


        public MyViewHolder(View view) {
            super(view);
            tv_cellid = (TextView) view.findViewById(R.id.tv_cellid);
            tv_mcc = (TextView) view.findViewById(R.id.tv_mcc);
            tv_mnc = (TextView) view.findViewById(R.id.tv_mnc);
            tv_iso = (TextView) view.findViewById(R.id.tv_iso);
            tv_country = (TextView) view.findViewById(R.id.tv_country);
            tv_countrycode = (TextView) view.findViewById(R.id.tv_countrycode);
            tv_network = (TextView) view.findViewById(R.id.tv_network);
            root_view = (LinearLayout) view.findViewById(R.id.root_view);
            layout_header = (LinearLayout) view.findViewById(R.id.layout_header);
        }
    }

    public toweritemadapter(Context context, ArrayList<celltowermodel> mItemList){
        this.context = context;
        this.celltowers = mItemList;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_adapter_tower_item, parent, false);

        return new MyViewHolder(itemView);
    }

    public void addItems(ArrayList<celltowermodel> list) {
        if (list == null) {
            return;
        }
        clear();
        celltowers.addAll(list);
        notifyDataSetChanged();
    }

    public void clear() {
        celltowers.clear();
        this.notifyItemRangeRemoved(0, celltowers.size());
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.tv_mcc.setText(""+celltowers.get(position).getMcc());
        holder.tv_cellid.setText(""+celltowers.get(position).getCid());
        holder.tv_mnc.setText(""+celltowers.get(position).getMnc());
        holder.tv_iso.setText(""+celltowers.get(position).getIso());
        holder.tv_country.setText(""+celltowers.get(position).getCountry());
        holder.tv_countrycode.setText(""+celltowers.get(position).getCountrycode());
        holder.tv_network.setText(""+celltowers.get(position).getNetwork());
        if(position == 0)
            holder.layout_header.setVisibility(View.VISIBLE);
        else
            holder.layout_header.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return celltowers.size();
    }

}
