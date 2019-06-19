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
import com.deeptruth.app.android.models.mediatype;
import com.deeptruth.app.android.models.synclogmodel;

import java.util.ArrayList;
import java.util.List;


public class adaptersynclogs extends RecyclerView.Adapter<adaptersynclogs.myViewHolder> {

    ArrayList<synclogmodel> mitemlist = new ArrayList<>();
    Context context;
    adapteritemclick mitemclick;

    public class myViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_token,txt_mediakey,txt_localkey,txt_mediatranid,txt_syncdate;

        public myViewHolder(View view) {
            super(view);
            txt_token = (TextView) view.findViewById(R.id.txt_token);
            txt_mediakey = (TextView) view.findViewById(R.id.txt_mediakey);
            txt_localkey = (TextView) view.findViewById(R.id.txt_localkey);
            txt_mediatranid = (TextView) view.findViewById(R.id.txt_mediatranid);
            txt_syncdate = (TextView) view.findViewById(R.id.txt_syncdate);
        }
    }

    public adaptersynclogs(Context context, ArrayList<synclogmodel> mitemlist, adapteritemclick mitemclick){
        this.context = context;
        this.mitemlist = mitemlist;
        this.mitemclick = mitemclick;
    }


    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_synclog, parent, false);

        return new myViewHolder(itemView);
    }

    public void clear() {
        mitemlist.clear();
        this.notifyItemRangeRemoved(0, mitemlist.size());
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final myViewHolder holder, final int position) {

        holder.txt_token.setText(""+mitemlist.get(position).getToken());
        holder.txt_mediakey.setText(""+mitemlist.get(position).getMediakey());
        holder.txt_localkey.setText(""+mitemlist.get(position).getLocalkey());
        holder.txt_mediatranid.setText(""+mitemlist.get(position).getMediastarttransactionid());
        holder.txt_syncdate.setText(""+mitemlist.get(position).getSync_date());
    }

    @Override
    public int getItemCount() {
        return mitemlist.size();
    }

}
