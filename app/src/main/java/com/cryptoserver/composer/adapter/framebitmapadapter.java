package com.cryptoserver.composer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cryptoserver.composer.R;
import com.cryptoserver.composer.applicationviavideocomposer;
import com.cryptoserver.composer.interfaces.adapteritemclick;
import com.cryptoserver.composer.models.frame;
import com.cryptoserver.composer.models.videomodel;

import java.util.ArrayList;
import java.util.List;


public class framebitmapadapter extends RecyclerView.Adapter<framebitmapadapter.myViewHolder> {


    List<frame> mitemlist = new ArrayList<>();
    Context context;
    adapteritemclick mitemclick;
    int parentwidth=0;

    public class myViewHolder extends RecyclerView.ViewHolder {
        public ImageView img_item;
        public View view_empty;


        public myViewHolder(View view) {
            super(view);
            img_item = (ImageView) view.findViewById(R.id.img_item);
            view_empty = (View) view.findViewById(R.id.view_empty);
        }
    }

    public framebitmapadapter(Context context, List<frame> mitemlist, int parentwidth,adapteritemclick mitemclick){
        this.context = context;
        this.mitemlist = mitemlist;
        this.mitemclick = mitemclick;
        this.parentwidth = parentwidth;
    }


    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_frame, parent, false);

        return new myViewHolder(itemView);
    }

    public void additems(ArrayList<frame> list) {
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

        if(mitemlist.get(position).isIsheaderfooter())
        {
            LinearLayout.LayoutParams param=new LinearLayout.LayoutParams(parentwidth/2,50);
            holder.view_empty.setLayoutParams(param);
            holder.view_empty.setVisibility(View.VISIBLE);
            holder.img_item.setVisibility(View.GONE);
        }
        else
        {
            int img_height = (int) applicationviavideocomposer.getactivity().getResources().getDimension(R.dimen.image_height);
            final LinearLayout.LayoutParams param=new LinearLayout.LayoutParams(100, img_height);
            holder.img_item.setLayoutParams(param);
            holder.view_empty.setVisibility(View.GONE);
            holder.img_item.setVisibility(View.VISIBLE);

           /* holder.img_item.post(new Runnable() {
                @Override
                public void run() {
                }
            });
*/
            if(mitemlist.get(position).getBitmap() != null)
                holder.img_item.setImageBitmap(mitemlist.get(position).getBitmap());
        }
    }

    @Override
    public int getItemCount() {
        return mitemlist.size();
    }

}
