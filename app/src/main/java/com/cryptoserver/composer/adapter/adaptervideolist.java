package com.cryptoserver.composer.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cryptoserver.composer.R;
import com.cryptoserver.composer.interfaces.AdapterItemClick;
import com.cryptoserver.composer.models.video;

import java.util.ArrayList;

/**
 * Created by devesh on 6/8/18.
 */

public class adaptervideolist extends  RecyclerView.Adapter<adaptervideolist.myViewHolder> {

    Context context;
    ArrayList<video> arrayvideolist = new ArrayList<video>();
    AdapterItemClick adapter;

    public class myViewHolder extends RecyclerView.ViewHolder {
        public TextView tvvideoname,tvvideocreatedate,tvvideoduration,tvvideodescription;
        public ImageView imgvideothumbnail,imgshareicon,imgdeleteicon;

        public myViewHolder(View view) {
            super(view);
            tvvideoname = (TextView) view.findViewById(R.id.tv_videoname);
            tvvideocreatedate = (TextView) view.findViewById(R.id.tv_videocreatedate);
            tvvideoduration = (TextView) view.findViewById(R.id.tv_videoduration);
            tvvideodescription = (TextView) view.findViewById(R.id.tv_videodescription);
            imgvideothumbnail = (ImageView) view.findViewById(R.id.img_videothumbnail);
            imgshareicon = (ImageView) view.findViewById(R.id.img_shareicon);
            imgdeleteicon = (ImageView) view.findViewById(R.id.img_deleteicon);
        }
    }

    public adaptervideolist(Context context, ArrayList<video> arrayvideolist, AdapterItemClick adapter){
        this.context = context;
        this.arrayvideolist = arrayvideolist;
        this.adapter = adapter;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_videolist, parent, false);

        return new myViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, final int position) {

        holder.tvvideoname.setText(arrayvideolist.get(position).getName());
        holder.tvvideocreatedate.setText(arrayvideolist.get(position).getCreatedate());
        holder.tvvideoduration.setText("Duration : " +arrayvideolist.get(position).getDuration());
        holder.tvvideodescription.setText(arrayvideolist.get(position).getMd5());

        holder.imgshareicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.onItemClicked(arrayvideolist.get(position),1);
            }
        });

        holder.imgdeleteicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.onItemClicked(arrayvideolist.get(position),2);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayvideolist.size();
    }
}
