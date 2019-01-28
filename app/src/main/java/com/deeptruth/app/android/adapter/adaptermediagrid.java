package com.deeptruth.app.android.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.deeptruth.app.android.R;
import com.deeptruth.app.android.interfaces.adapteritemclick;
import com.deeptruth.app.android.models.video;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by devesh on 6/8/18.
 */

public class adaptermediagrid extends RecyclerView.Adapter<adaptermediagrid.myViewHolder> {

    Context context;
    ArrayList<video> arrayvideolist = new ArrayList<video>();
    adapteritemclick adapter;
    public class myViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_mediaduration;
        public ImageView img_mediathumbnail;
        public RelativeLayout rl_row_media;
        public Button btnedit;

        public myViewHolder(View view) {
            super(view);
            tv_mediaduration = (TextView) view.findViewById(R.id.tv_mediaduration);
            img_mediathumbnail = (ImageView) view.findViewById(R.id.img_mediathumbnail);
            rl_row_media = (RelativeLayout) view.findViewById(R.id.rl_row_media);
        }
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_gridmedialist, parent, false);

        return new myViewHolder(itemView);
    }

    public adaptermediagrid(Context context, ArrayList<video> arrayvideolist, adapteritemclick adapter){
        this.context = context;
        this.arrayvideolist = arrayvideolist;
        this.adapter = adapter;
    }

    @Override
    public void onBindViewHolder(@NonNull final myViewHolder holder, final int position) {

        if(arrayvideolist.get(position).isDoenable())
        {
            holder.rl_row_media.setVisibility(View.VISIBLE);
            if (arrayvideolist.get(position).getmimetype().contains("image/"))
            {
                holder.tv_mediaduration.setText("");
            }
            else
            {
                holder.tv_mediaduration.setText(arrayvideolist.get(position).getDuration());
            }

            if(! arrayvideolist.get(position).getmimetype().contains("audio"))
            {

                Uri uri = Uri.fromFile(new File(arrayvideolist.get(position).getPath()));
                Glide.with(context).
                        load(uri).
                        thumbnail(0.1f).
                        into(holder.img_mediathumbnail);
            }
            else
            {
                if(! arrayvideolist.get(position).getThumbnailpath().trim().isEmpty())
                {
                    if(new File(arrayvideolist.get(position).getThumbnailpath()).exists())
                    {
                        Uri uri = Uri.fromFile(new File(arrayvideolist.get(position).getThumbnailpath()));
                        Glide.with(context).
                                load(uri).
                                thumbnail(0.1f).
                                into(holder.img_mediathumbnail);
                    }

                }
                else
                {
                    Glide.with(context).
                            load(R.drawable.audiothum).
                            thumbnail(0.1f).
                            into(holder.img_mediathumbnail);
                }
            }

            holder.img_mediathumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // if(arrayvideolist.get(position).getmimetype().contains("video"))
                    adapter.onItemClicked(arrayvideolist.get(position),4);
                }
            });
            holder.img_mediathumbnail.getLayoutParams().height = arrayvideolist.get(position).getGriditemheight();
        }
        else
        {
            holder.rl_row_media.setVisibility(View.GONE);
            holder.img_mediathumbnail.getLayoutParams().height = 0;
        }
    }

    public void filterlist(ArrayList<video> arrayvideolist) {
        this.arrayvideolist = arrayvideolist;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return arrayvideolist.size();
    }

}