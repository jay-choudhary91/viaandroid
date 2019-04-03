package com.deeptruth.app.android.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.interfaces.adapteritemclick;
import com.deeptruth.app.android.models.mediatype;

import java.util.ArrayList;
import java.util.List;


public class adaptercomposemediatype extends RecyclerView.Adapter<adaptercomposemediatype.myViewHolder> {

    List<mediatype> mitemlist = new ArrayList<mediatype>();
    Context context;
    adapteritemclick mitemclick;
    int parentwidth=0;

    public class myViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_mediatype;
        public LinearLayout root_view;

        public myViewHolder(View view) {
            super(view);
            txt_mediatype = (TextView) view.findViewById(R.id.txt_mediatype);
            root_view = (LinearLayout) view.findViewById(R.id.root_view);
        }
    }

    public adaptercomposemediatype(Context context, List<mediatype> mitemlist, int parentwidth, adapteritemclick mitemclick){
        this.context = context;
        this.mitemlist = mitemlist;
        this.mitemclick = mitemclick;
        this.parentwidth = parentwidth;
    }


    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_composemediatype, parent, false);
        itemView.getLayoutParams().width=parentwidth/5;
        return new myViewHolder(itemView);
    }

    public void clear() {
        mitemlist.clear();
        this.notifyItemRangeRemoved(0, mitemlist.size());
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final myViewHolder holder, final int position) {
        holder.txt_mediatype.setText(mitemlist.get(position).getMedianame());
        if(mitemlist.get(position).isIsmediaselected())
        {
            holder.txt_mediatype.setTextColor(context.getResources().getColor(R.color.wave_blue));
        }
        else
        {
            holder.txt_mediatype.setTextColor(context.getResources().getColor(R.color.white));
        }
        holder.root_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mitemclick != null)
                    mitemclick.onItemClicked(null,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mitemlist.size();
    }

}
