package com.cryptoserver.composer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cryptoserver.composer.R;
import com.cryptoserver.composer.interfaces.adapteritemclick;
import com.cryptoserver.composer.models.metricmodel;
import com.cryptoserver.composer.models.videomodel;
import com.cryptoserver.composer.utils.common;

import java.util.ArrayList;
import java.util.List;


public class drawermetricesadapter extends RecyclerView.Adapter<drawermetricesadapter.myViewHolder> {


    List<metricmodel> mitemlist = new ArrayList<>();
    Context context;
    adapteritemclick mitemclick;

    public class myViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_info;


        public myViewHolder(View view) {
            super(view);
            txt_info = (TextView) view.findViewById(R.id.txt_md_info);
        }
    }

    public drawermetricesadapter(Context context, List<metricmodel> mitemlist){
        this.context = context;
        this.mitemlist = mitemlist;
        this.mitemclick = mitemclick;
    }


    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.videoview, parent, false);

        return new myViewHolder(itemView);
    }

    public void clear() {
        mitemlist.clear();
        this.notifyItemRangeRemoved(0, mitemlist.size());
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final myViewHolder holder, final int position) {

        final metricmodel metricModel = mitemlist.get(position);

        String content="";
        String key=common.metric_display(metricModel.getMetricTrackKeyName());

        content=metricModel.getMetricTrackValue();
        if(metricModel.getMetricTrackValue().trim().isEmpty() || metricModel.getMetricTrackValue().equalsIgnoreCase("null"))
            content="NA";

        holder.txt_info.setText(key+" - "+content);
    }

    @Override
    public int getItemCount() {
        return mitemlist.size();
    }

}
