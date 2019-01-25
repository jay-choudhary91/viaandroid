package com.deeptruth.app.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.interfaces.adapteritemclick;
import com.deeptruth.app.android.models.videomodel;

import java.util.ArrayList;
import java.util.List;


public class videoframeadapter extends RecyclerView.Adapter<videoframeadapter.myViewHolder> {


    List<videomodel> mitemlist = new ArrayList<>();
    Context context;
    adapteritemclick mitemclick;

    public class myViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_md_info;


        public myViewHolder(View view) {
            super(view);
            txt_md_info = (TextView) view.findViewById(R.id.txt_md_info);
        }
    }

    public videoframeadapter(Context context, List<videomodel> mitemlist, adapteritemclick mitemclick){
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

    public void additems(ArrayList<videomodel> list) {
        if (list == null) {
            return;
        }
        clear();
        mitemlist.addAll(list);
        notifyDataSetChanged();
    }

    public void clear() {
        mitemlist.clear();
        this.notifyItemRangeRemoved(0, mitemlist.size());
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final myViewHolder holder, final int position) {

        String content="";
        if(mitemlist.get(position).getframeinfo().trim().isEmpty())
        {
            content=""+ mitemlist.get(position).gettitle()+" "+ mitemlist.get(position).getcurrentframenumber()+" "+
                    mitemlist.get(position).getkeytype()+":"+" "+ mitemlist.get(position).getkeyvalue();
        }
        else
        {
            content= mitemlist.get(position).getframeinfo();
        }

        holder.txt_md_info.setText(content);
    }

    @Override
    public int getItemCount() {
        return mitemlist.size();
    }

}
