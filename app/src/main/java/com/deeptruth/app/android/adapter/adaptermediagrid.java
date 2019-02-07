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
import com.deeptruth.app.android.R;
import com.deeptruth.app.android.interfaces.adapteritemclick;
import com.deeptruth.app.android.models.video;
import com.deeptruth.app.android.utils.config;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerFrameLayout;

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
        public ImageView img_mediathumbnail,img_scanover;
        public RelativeLayout rl_row_media;
        public ShimmerFrameLayout shimmer_view_container;

        public myViewHolder(View view) {
            super(view);
            tv_mediaduration = (TextView) view.findViewById(R.id.tv_mediaduration);
            img_mediathumbnail = (ImageView) view.findViewById(R.id.img_mediathumbnail);
            img_scanover = (ImageView) view.findViewById(R.id.img_scanover);
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

        if(arrayvideolist.get(position).isDoenable())
        {
            /*objectanimator = ObjectAnimator.ofFloat(holder.img_scanover,"x",300);
            objectanimator.setDuration(5000);
            objectanimator.setRepeatCount(Animation.INFINITE);
            objectanimator.setRepeatMode(ValueAnimator.RESTART);
            objectanimator.start();*/
            // Load the animation like this
            /*Animation animslide = AnimationUtils.loadAnimation(context,R.anim.slide_leftright);
            animslide.setRepeatCount(5);  // animation repeat count
            animslide.setRepeatMode(2);
            holder.img_scanover.startAnimation(animslide);*/
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
                            //animation.setStartOffset(position*100);
                            animation.setRepeatCount(Animation.INFINITE);
                            animation.setRepeatMode(ValueAnimator.RESTART);
                            //animation.setFillAfter(true);
                            holder.img_scanover.startAnimation(animation);

           /**/

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

                            if(arrayvideolist.get(position).getMediacolor().equalsIgnoreCase(config.color_green))
                            {
                                animation.setAnimationListener(listener);
                                holder.img_scanover.setVisibility(View.VISIBLE);
                                holder.img_scanover.setBackgroundResource(R.drawable.gradient_verify_green);
                            }
                            else if(arrayvideolist.get(position).getMediacolor().equalsIgnoreCase(config.color_yellow))
                            {
                                animation.setAnimationListener(listener);
                                holder.img_scanover.setVisibility(View.VISIBLE);
                                holder.img_scanover.setBackgroundResource(R.drawable.gradient_verify_yellow);
                            }
                            else if(arrayvideolist.get(position).getMediacolor().equalsIgnoreCase(config.color_red))
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
                    });
                }
            });


            holder.rl_row_media.setVisibility(View.VISIBLE);
            holder.tv_mediaduration.setText(arrayvideolist.get(position).getDuration());

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
            holder.img_scanover.getLayoutParams().height = arrayvideolist.get(position).getGriditemheight();
        }
        else
        {
            holder.rl_row_media.setVisibility(View.GONE);
            holder.img_mediathumbnail.getLayoutParams().height = 0;
            holder.img_scanover.getLayoutParams().height = 0;
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
