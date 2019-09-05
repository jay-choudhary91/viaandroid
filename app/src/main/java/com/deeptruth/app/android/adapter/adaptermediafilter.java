package com.deeptruth.app.android.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.interfaces.adapteritemclick;
import com.deeptruth.app.android.models.mediafilteroptions;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;

import java.util.ArrayList;

/**
 * Created by devesh on 21/8/19.
 */

public class adaptermediafilter extends RecyclerView.Adapter<adaptermediafilter.myViewHolder> {
    ArrayList<mediafilteroptions> arraymediafilter = new ArrayList<>();
    adapteritemclick adapter;
    Context context;
    int screenwidth=0;


    public class myViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layout_typeoption,root_view;
        TextView txt_options;
        ImageView img_updownarrow;

        public myViewHolder(View view) {
            super(view);
            root_view = (LinearLayout) view.findViewById(R.id.root_view);
            layout_typeoption = (LinearLayout) view.findViewById(R.id.layout_typeoption);
            txt_options = (TextView) view.findViewById(R.id.txt_optiontype);
            img_updownarrow = (ImageView) view.findViewById(R.id.img_updownarrow);
        }
    }

    public adaptermediafilter(Context context , ArrayList<mediafilteroptions> arraymediafilter, adapteritemclick adapter,
                              int screenwidth){
        this.context = context;
        this.arraymediafilter = arraymediafilter;
        this.adapter = adapter;
        this.screenwidth = screenwidth;
    }
    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_mediafilteroptions, parent, false);

        return new adaptermediafilter.myViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {

        LinearLayout.LayoutParams param=null;
        float width=screenwidth;
        double newwidth=0;

        final mediafilteroptions mediaobject=arraymediafilter.get(position);
        holder.txt_options.setText(mediaobject.getmediafiltername());

        if(mediaobject.getmediafiltername().equalsIgnoreCase(config.filter_date))
            newwidth=width/4;
        else if(mediaobject.getmediafiltername().equalsIgnoreCase(config.filter_location))
            newwidth=width/4;
        else
            newwidth=width/6;

        if(mediaobject.isfilterselected())
        {
            holder.img_updownarrow.setVisibility(View.VISIBLE);
            if(mediaobject.isascending())
                holder.img_updownarrow.setImageResource(R.drawable.ic_uparrow);
            else
                holder.img_updownarrow.setImageResource(R.drawable.ic_downarrow);

            holder.txt_options.setTypeface(applicationviavideocomposer.semiboldfonttype);
            holder.txt_options.setTextSize(12);
            common.underlinespan(holder.txt_options,mediaobject.getmediafiltername());
        }
        else
        {
            holder.img_updownarrow.setVisibility(View.GONE);
            holder.txt_options.setTypeface(applicationviavideocomposer.comfortaaregular);
            holder.txt_options.setTextSize(11);
            common.removeunderline(holder.txt_options,mediaobject.getmediafiltername());
        }

        holder.layout_typeoption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.onItemClicked(mediaobject , 1);
            }
        });

        param=new LinearLayout.LayoutParams((int)newwidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        holder.root_view.setLayoutParams(param);
    }

    @Override
    public int getItemCount() {
        return arraymediafilter.size();
    }
}
