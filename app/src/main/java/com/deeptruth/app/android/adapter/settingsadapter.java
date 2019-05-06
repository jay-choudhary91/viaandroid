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
import com.deeptruth.app.android.models.pair;

import java.util.ArrayList;
import java.util.List;


public class settingsadapter extends RecyclerView.Adapter<settingsadapter.MyViewHolder> {


    List<pair> mItemList = new ArrayList<>();
    Context context;
    adapteritemclick mItemClick;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvKeyName,tvKeyValue;
        public LinearLayout root_view;


        public MyViewHolder(View view) {
            super(view);
            tvKeyName = (TextView) view.findViewById(R.id.tv_key_name);
            tvKeyValue = (TextView) view.findViewById(R.id.tv_key_value);
            root_view = (LinearLayout) view.findViewById(R.id.root_view);
        }
    }

    public settingsadapter(Context context, List<pair> mItemList, adapteritemclick mItemClick){
        this.context = context;
        this.mItemList = mItemList;
        this.mItemClick = mItemClick;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_adapter_setting, parent, false);

        return new MyViewHolder(itemView);
    }

    public void addItems(ArrayList<pair> list) {
        if (list == null) {
            return;
        }
        clear();
        mItemList.addAll(list);
        notifyDataSetChanged();
    }

    public void clear() {
        mItemList.clear();
        this.notifyItemRangeRemoved(0, mItemList.size());
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.tvKeyName.setText(mItemList.get(position).getKeyName().toString());
        holder.tvKeyValue.setText(mItemList.get(position).getKeyValue().toString());

        holder.root_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemClick.onItemClicked(mItemList.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

}
