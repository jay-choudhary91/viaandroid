package com.deeptruth.app.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.interfaces.adapteritemclick;
import com.deeptruth.app.android.models.metricmodel;
import com.deeptruth.app.android.utils.common;


import java.util.ArrayList;

/**
 * Created by devesh on 20/4/18.
 */

public class Itemmetricadapter extends RecyclerView.Adapter<Itemmetricadapter.ContactViewHolder> {

    ArrayList<metricmodel> metricItemArrayList;
    private Context mContext;
    int row_index;
    adapteritemclick mItemClick;

    public Itemmetricadapter(Context mContext, ArrayList<metricmodel> metricItemArrayList, adapteritemclick mItemClick) {
        this.metricItemArrayList = metricItemArrayList;
        this.mContext = mContext;
        this.mItemClick = mItemClick;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_matric_item, parent, false);

        return new ContactViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final ContactViewHolder holder, final int position) {

        final metricmodel metricModel = metricItemArrayList.get(position);

        holder.tvItemName.setText(common.metric_display(metricModel.getMetricTrackKeyName()));
        holder.tv_itemValue.setText(metricModel.getMetricTrackValue());
        if(metricModel.getMetricTrackValue().trim().isEmpty() || metricModel.getMetricTrackValue().equalsIgnoreCase("null"))
            holder.tv_itemValue.setText("NA");

        holder.imgSelected.setImageResource(metricModel.isSelected() ? R.drawable.selectedicon : R.drawable.unselectedicon);

        if(metricModel.isSelected())
        {
            holder.tv_itemValue.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.tv_itemValue.setVisibility(View.GONE);
        }

        holder.rootlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(metricModel.isSelected())
                {
                    metricModel.setSelected(false);
                    metricModel.setTag("unchecked");
                    holder.imgSelected.setImageResource(metricModel.isSelected() ? R.drawable.selectedicon : R.drawable.unselectedicon);

                    holder.tv_itemValue.setVisibility(View.GONE);
                }
                else if(metricModel.getMetricTrackValue().trim().isEmpty())
                {
                    metricModel.setTag("checked");
                    metricModel.setSelected(true);
                    holder.imgSelected.setImageResource(metricModel.isSelected() ? R.drawable.selectedicon : R.drawable.unselectedicon);
                    holder.tv_itemValue.setVisibility(View.VISIBLE);
                    mItemClick.onItemClicked(metricModel.getMetricTrackKeyName());
                }
                else
                {
                    holder.tv_itemValue.setVisibility(View.VISIBLE);
                    metricModel.setSelected(true);
                    metricModel.setTag("checked");
                    holder.imgSelected.setImageResource(metricModel.isSelected() ? R.drawable.selectedicon : R.drawable.unselectedicon);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return metricItemArrayList.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {

        ImageView imgSelected;
        TextView tvItemName;
        TextView tv_itemValue;
        RelativeLayout rootlayout;

        public ContactViewHolder(View itemView) {
            super(itemView);
            tvItemName = (TextView) itemView.findViewById(R.id.tv_itemName);
            tv_itemValue = (TextView) itemView.findViewById(R.id.tv_itemValue);
            imgSelected = (ImageView) itemView.findViewById(R.id.img_selected);
            rootlayout = (RelativeLayout) itemView.findViewById(R.id.rootlayout);
        }
    }
}
