package com.deeptruth.app.android.adapter;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.deeptruth.app.android.BuildConfig;
import com.deeptruth.app.android.R;
import com.deeptruth.app.android.interfaces.adapteritemclick;
import com.deeptruth.app.android.models.video;
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
    ObjectAnimator objectanimator = null;
    int imagethumbnail_width,imagescanover_width;
    float totalwidth;
    public class myViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_mediaduration;
        public ImageView img_mediathumbnail,img_scanover,img_loader;
        public RelativeLayout rl_row_media;
        public ShimmerFrameLayout shimmer_view_container;

        public myViewHolder(View view) {
            super(view);
            tv_mediaduration = (TextView) view.findViewById(R.id.tv_mediaduration);
            img_mediathumbnail = (ImageView) view.findViewById(R.id.img_mediathumbnail);
            img_scanover = (ImageView) view.findViewById(R.id.img_scanover);
            img_loader = (ImageView) view.findViewById(R.id.img_loader);
            rl_row_media = (RelativeLayout) view.findViewById(R.id.rl_row_media);
            shimmer_view_container = (ShimmerFrameLayout) view.findViewById(R.id.shimmer_view_container);
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
            holder.img_mediathumbnail.post(new Runnable() {
                @Override
                public void run() {

                    imagethumbnail_width = holder.img_mediathumbnail.getWidth();
                    holder.img_scanover.post(new Runnable() {
                        @Override
                        public void run() {
                            imagescanover_width = holder.img_scanover.getWidth();
                            totalwidth = imagescanover_width + imagethumbnail_width;

                            TranslateAnimation animation = new TranslateAnimation(-50.0f, totalwidth,
                                    0.0f, 0.0f);
                            animation.setDuration(3000);
                            animation.setStartOffset(mediaobject.getGriditemheight()*10);
                            animation.setRepeatCount(Animation.INFINITE);
                            animation.setRepeatMode(ValueAnimator.RESTART);
                            holder.img_scanover.startAnimation(animation);

                            Animation.AnimationListener listener=new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {
                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {
                                    animation.setStartOffset(5000);
                                }
                            };

                            if(mediaobject != null)
                            {
                                if(mediaobject.getMediacolor().equalsIgnoreCase(config.color_green))
                                {
                                    animation.setAnimationListener(listener);
                                    holder.img_scanover.setVisibility(View.VISIBLE);
                                    holder.img_scanover.setBackgroundResource(R.drawable.gradient_verify_green);
                                }
                                else if(mediaobject.getMediacolor().equalsIgnoreCase(config.color_yellow))
                                {
                                    animation.setAnimationListener(listener);
                                    holder.img_scanover.setVisibility(View.VISIBLE);
                                    holder.img_scanover.setBackgroundResource(R.drawable.gradient_verify_yellow);
                                }
                                else if(mediaobject.getMediacolor().equalsIgnoreCase(config.color_red))
                                {
                                    animation.setAnimationListener(listener);
                                    holder.img_scanover.setVisibility(View.VISIBLE);
                                    holder.img_scanover.setBackgroundResource(R.drawable.gradient_verify_red);
                                }
                                else
                                {
                                    holder.img_scanover.setBackgroundResource(0);
                                    holder.img_scanover.setVisibility(View.GONE);
                                }
                            }


                        }
                    });
                }
            });


            holder.rl_row_media.setVisibility(View.VISIBLE);
            holder.tv_mediaduration.setText(mediaobject.getDuration());

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
            holder.img_scanover.getLayoutParams().height = mediaobject.getGriditemheight();
        }
        else
        {
            holder.rl_row_media.setVisibility(View.GONE);
            holder.img_mediathumbnail.getLayoutParams().height = 0;
            holder.img_scanover.getLayoutParams().height = 0;
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

    public void filterlist(ArrayList<video> arrayvideolist) {
        this.arrayvideolist = arrayvideolist;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return arrayvideolist.size();
    }

}
