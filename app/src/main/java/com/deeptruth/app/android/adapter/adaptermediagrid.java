package com.deeptruth.app.android.adapter;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.deeptruth.app.android.BuildConfig;
import com.deeptruth.app.android.R;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.interfaces.adapteritemclick;
import com.deeptruth.app.android.models.video;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.squareup.picasso.Picasso;

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
        public ImageView img_mediathumbnail,img_loader;
        public RelativeLayout rl_row_media;
        LinearLayout linearseekbarcolorview;

        public myViewHolder(View view) {
            super(view);
            tv_mediaduration = (TextView) view.findViewById(R.id.tv_mediaduration);
            img_mediathumbnail = (ImageView) view.findViewById(R.id.img_mediathumbnail);
            img_loader = (ImageView) view.findViewById(R.id.img_loader);
            rl_row_media = (RelativeLayout) view.findViewById(R.id.rl_row_media);
            linearseekbarcolorview = (LinearLayout) view.findViewById(R.id.linear_seekbarcolorview);
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

        final video mediaobject=arrayvideolist.get(position);
        if(mediaobject.isDoenable())
        {
            holder.rl_row_media.setVisibility(View.VISIBLE);
            holder.tv_mediaduration.setText(mediaobject.getDuration());

            if(BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_composer))
            {
                ArrayList<String> arrayList = mediaobject.getMediabarcolor();
                if(arrayList != null && arrayList.size() > 0 && mediaobject.getColorbarview() != null ) {
                    holder.linearseekbarcolorview.setVisibility(View.VISIBLE);
                    try {
                        if (mediaobject.getColorbarview().getParent() != null)
                            ((ViewGroup) mediaobject.getColorbarview().getParent()).removeView(mediaobject.getColorbarview());

                        holder.linearseekbarcolorview.removeAllViews();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    holder.linearseekbarcolorview.addView(mediaobject.getColorbarview());
                    holder.linearseekbarcolorview.invalidate();
                    holder.linearseekbarcolorview.requestLayout();
                }
                else
                {
                    holder.linearseekbarcolorview.setVisibility(View.INVISIBLE);
                }
                /*ArrayList<String> arrayList = mediaobject.getMediabarcolor();
                if(arrayList != null && arrayList.size() > 0 )
                {
                    holder.linearseekbarcolorview.setVisibility(View.VISIBLE);
                    setseekbarlayoutcolor(holder.linearseekbarcolorview,arrayList);
                }
                else
                {
                    holder.linearseekbarcolorview.setVisibility(View.INVISIBLE);
                }*/
            }
            else
            {
                holder.linearseekbarcolorview.setVisibility(View.GONE);
                holder.tv_mediaduration.setPadding(0,0,20,5);
            }

            if(BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_reader))
            {
                if(mediaobject.getMediastatus().equalsIgnoreCase(config.sync_complete) ||
                        mediaobject.getMediastatus().equalsIgnoreCase(config.sync_notfound))
                {
                    holder.img_loader.setVisibility(View.GONE);
                }
                else
                {
                    holder.img_loader.setVisibility(View.VISIBLE);
                    Glide.with(context).load(R.drawable.media_loader).into(holder.img_loader);
                }
            }
            else
            {
                holder.img_loader.setVisibility(View.GONE);
            }

            RequestOptions requestOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL);
            requestOptions.override(200,mediaobject.getGriditemheight());
            if(! mediaobject.getmimetype().contains(config.item_audio.toLowerCase()))
            {
                holder.img_mediathumbnail.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                Uri uri = Uri.fromFile(new File(mediaobject.getPath()));
                Glide.with(context).load(uri).apply(requestOptions).thumbnail(0.1f).into(holder.img_mediathumbnail);
                /*Picasso.get().load(new File(mediaobject.getPath())).resize(50, 50)
                    .centerCrop().into(holder.img_mediathumbnail);*/
            }
            else
            {
                holder.img_mediathumbnail.setBackgroundColor(context.getResources().getColor(R.color.dark_blue_solid_a));
                if(! mediaobject.getThumbnailpath().trim().isEmpty())
                {
                    if(new File(mediaobject.getThumbnailpath()).exists())
                    {
                        Uri uri = Uri.fromFile(new File(mediaobject.getThumbnailpath()));
                        Glide.with(context).load(uri).apply(requestOptions).thumbnail(0.1f).into(holder.img_mediathumbnail);
                    }
                }
                else
                {
                    Glide.with(context).load(R.drawable.audiothum).apply(requestOptions).thumbnail(0.1f).into(holder.img_mediathumbnail);
                }
            }

            holder.img_mediathumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    if(BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_reader))
                    {
                        if (! mediaobject.getMediastatus().equalsIgnoreCase(config.sync_complete))
                            return;
                    }
                    adapter.onItemClicked(mediaobject,4);
                }
            });
            holder.img_mediathumbnail.getLayoutParams().height = mediaobject.getGriditemheight();
        }
        else
        {
            holder.rl_row_media.setVisibility(View.GONE);
            holder.img_mediathumbnail.getLayoutParams().height = 0;
        }
    }

    public void notifyitems(ArrayList<video> arrayvideolist)
    {
        ArrayList<video> localarray=new ArrayList<>();
        for(int i=0;i<arrayvideolist.size();i++)
        {
            if(arrayvideolist.get(i).isDoenable())
            {
                localarray.add(arrayvideolist.get(i));
            }
        }
        this.arrayvideolist=localarray;
        notifyDataSetChanged();
    }

    private void setseekbarlayoutcolor(LinearLayout colorbarlayout,ArrayList<String> arrayList){
        try
        {
            colorbarlayout.removeAllViews();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        for(int i=0 ; i<arrayList.size();i++)
        {
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,1.0f);
            param.leftMargin=0;
            View view = new View(applicationviavideocomposer.getactivity());
            view.setLayoutParams(param);
            if(arrayList.get(i) != null && (! arrayList.get(i).isEmpty()))
            {
                view.setBackgroundColor(Color.parseColor(common.getcolorbystring( arrayList.get(i))));
            }
            else
            {
                view.setBackgroundColor(Color.parseColor(config.color_code_gray));
            }
            colorbarlayout.addView(view);
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
