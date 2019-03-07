package com.deeptruth.app.android.adapter;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.deeptruth.app.android.BuildConfig;
import com.deeptruth.app.android.R;
import com.deeptruth.app.android.interfaces.adapteritemclick;
import com.deeptruth.app.android.models.video;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by devesh on 6/8/18.
 */

public class adaptermedialist extends RecyclerView.Adapter<adaptermedialist.myViewHolder> {

    Context context;
    ArrayList<video> arrayvideolist = new ArrayList<video>();
    adapteritemclick adapter;
    private int listviewheight=0,imagethumbanail_width=0,totalwidth=0;
    ViewBinderHelper binderHelper;

    public class myViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_mediatime,tv_mediadate,tv_localkey,tv_sync_status,txt_pipesign,tv_medianotes,tv_mediaduration;
        EditText edtvideoname;
        public ImageView img_loader,img_videothumbnail,img_slide_share,img_slide_create_dir,img_slide_delete,img_scanover;
        public SwipeRevealLayout root_view;
        LinearLayout layout_share_slide,layout_delete_slide,layout_folder_slide;

        public myViewHolder(View view) {
            super(view);
            tv_medianotes = (TextView) view.findViewById(R.id.tv_medianotes);
            txt_pipesign = (TextView) view.findViewById(R.id.txt_pipesign);
            edtvideoname = (EditText) view.findViewById(R.id.edt_videoname);
            tv_mediatime = (TextView) view.findViewById(R.id.tv_mediatime);
            tv_mediadate = (TextView) view.findViewById(R.id.tv_mediadate);
            tv_localkey = (TextView) view.findViewById(R.id.tv_localkey);
            tv_sync_status = (TextView) view.findViewById(R.id.tv_sync_status);
            img_videothumbnail = (ImageView) view.findViewById(R.id.img_videothumbnail);
            img_slide_share = (ImageView) view.findViewById(R.id.img_slide_share);
            img_slide_create_dir = (ImageView) view.findViewById(R.id.img_slide_create_dir);
            img_slide_delete = (ImageView) view.findViewById(R.id.img_slide_delete);
            img_scanover = (ImageView) view.findViewById(R.id.img_scanover);
            img_loader = (ImageView) view.findViewById(R.id.img_loader);
            layout_folder_slide = view.findViewById(R.id.layout_folder_slide);
            layout_delete_slide = view.findViewById(R.id.layout_delete_slide);
            layout_share_slide = view.findViewById(R.id.layout_share_slide);
            tv_mediaduration = (TextView) view.findViewById(R.id.tv_mediaduration);
            root_view = (SwipeRevealLayout) view.findViewById(R.id.root_view);
        }
    }

    public adaptermedialist(Context context, ArrayList<video> arrayvideolist, adapteritemclick adapter,int listviewheight){
        this.context = context;
        this.arrayvideolist = arrayvideolist;
        this.adapter = adapter;
        this.listviewheight = listviewheight;
        binderHelper = new ViewBinderHelper();
        binderHelper.setOpenOnlyOne(true);
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_videolist, parent, false);

        return new myViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final myViewHolder holder, final int position) {

        final video mediaobject=arrayvideolist.get(position);
        binderHelper.closeLayout(""+position);
        binderHelper.bind(holder.root_view,""+position);
        if(mediaobject.isDoenable())
        {
            holder.img_videothumbnail.post(new Runnable() {
                @Override
                public void run() {

                    holder.img_scanover.post(new Runnable() {
                        @Override
                        public void run() {
                            imagethumbanail_width  = holder.img_videothumbnail.getWidth();
                            int img_scanoverwidth=  holder.img_scanover.getWidth();
                            totalwidth= imagethumbanail_width + (img_scanoverwidth);
                            if(totalwidth > 0)
                            {
                                TranslateAnimation animation = new TranslateAnimation(-50.0f,totalwidth,0.0f, 0.0f);
                                animation.setDuration(3000);
                                animation.setStartOffset(mediaobject.getGriditemheight()*10);
                                animation.setRepeatCount(Animation.INFINITE);
                                animation.setRepeatMode(ValueAnimator.RESTART);
                                holder.img_scanover.startAnimation(animation);

                                Animation.AnimationListener listener =new Animation.AnimationListener() {
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
                        }
                    });

                }
            });

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
                    Glide.with(context).
                            load(R.drawable.media_loader).
                            into(holder.img_loader);
                }
            }
            else
            {
                holder.img_loader.setVisibility(View.GONE);
            }

            if(mediaobject.getMediatitle().trim().isEmpty())
            {
                holder.edtvideoname.setText("No Title");
            }
            else
            {
                holder.edtvideoname.setText(mediaobject.getMediatitle());
            }

            if(mediaobject.getDuration().trim().isEmpty())
            {
             //   holder.tv_mediaduration.setText("NA");
                holder.tv_mediaduration.setVisibility(View.GONE);
            }
            else
            {
                holder.tv_mediaduration.setText(mediaobject.getDuration());
                holder.tv_mediaduration.setVisibility(View.VISIBLE);
            }

            if(mediaobject.getCreatedate().trim().isEmpty())
            {
                holder.txt_pipesign.setVisibility(View.GONE);
                holder.tv_mediadate.setText("NA");
                holder.tv_mediatime.setText(mediaobject.getCreatetime());
            }
            else
            {
                holder.txt_pipesign.setVisibility(View.VISIBLE);
                holder.tv_mediadate.setText(mediaobject.getCreatedate());
                holder.tv_mediatime.setText(mediaobject.getCreatetime());
            }


            holder.tv_medianotes.setText(mediaobject.getMedianotes());


            if(mediaobject.getVideostarttransactionid().isEmpty() ||  mediaobject.getVideostarttransactionid().equalsIgnoreCase("null")){
                holder.tv_localkey.setText("");
            }else
            {
                holder.tv_localkey.setText(mediaobject.getVideostarttransactionid());
            }


            if(mediaobject.getMediastatus().isEmpty() ||  mediaobject.getMediastatus().equalsIgnoreCase("null")){

                holder.tv_sync_status.setText("Status : pending");
            }else{

                holder.tv_sync_status.setText("Status : " + mediaobject.getMediastatus());
            }

            holder.edtvideoname.setEnabled(false);
            holder.edtvideoname.setClickable(false);
            holder.edtvideoname.setFocusable(false);

            RequestOptions requestOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL);
            requestOptions.override(200,200);
            if(! mediaobject.getmimetype().contains("audio"))
            {
                Uri uri = Uri.fromFile(new File(mediaobject.getPath()));
                Glide.with(context).load(uri).apply(requestOptions).thumbnail(0.1f).into(holder.img_videothumbnail);
            }
            else
            {
                if(! mediaobject.getThumbnailpath().trim().isEmpty())
                {
                    if(new File(mediaobject.getThumbnailpath()).exists())
                    {
                        Uri uri = Uri.fromFile(new File(mediaobject.getThumbnailpath()));
                        Glide.with(context).load(uri).apply(requestOptions).thumbnail(0.1f).into(holder.img_videothumbnail);
                    }

                }
                else
                {
                    Glide.with(context).load(R.drawable.audiothum).apply(requestOptions).thumbnail(0.1f).into(holder.img_videothumbnail);
                }

           /* holder.img_videothumbnail.setBackgroundResource(R.drawable.audiotab);*/
            }



            if(mediaobject.isSelected){
                holder.edtvideoname.setEnabled(true);
                holder.edtvideoname.setClickable(true);
                holder.edtvideoname.setFocusableInTouchMode(true);
                holder.edtvideoname.setSelection(mediaobject.getName().substring(0, mediaobject.getName().lastIndexOf(".")).length());
                holder.edtvideoname.requestFocus();
                mediaobject.setSelected(false);

                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                Editable editableText=  holder.edtvideoname.getEditableText();
                if(editableText!=null) {
                    Log.e("position",""+position);
                    holder.edtvideoname.setInputType(InputType.TYPE_CLASS_TEXT);
                    holder.edtvideoname.setEllipsize(TextUtils.TruncateAt.END);
                    holder.edtvideoname.setSingleLine();
                }
            }
            else
            {
                mediaobject.setSelected(false);
                holder.edtvideoname.setEnabled(false);
                holder.edtvideoname.setClickable(false);
                holder.edtvideoname.setKeyListener(null);
            }

            if(mediaobject.isSelected){
                holder.tv_medianotes.setEnabled(true);
                holder.tv_medianotes.setClickable(true);
                holder.tv_medianotes.setFocusableInTouchMode(true);
                holder.tv_medianotes.requestFocus();
                mediaobject.setSelected(false);

                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                Editable editableText=  holder.tv_medianotes.getEditableText();
                if(editableText!=null) {
                    Log.e("position",""+position);
                    holder.tv_medianotes.setInputType(InputType.TYPE_CLASS_TEXT);
                    holder.tv_medianotes.setEllipsize(TextUtils.TruncateAt.END);
                    holder.tv_medianotes.setSingleLine();
                }
            }
            else
            {
                mediaobject.setSelected(false);
                holder.tv_medianotes.setEnabled(false);
                holder.tv_medianotes.setClickable(false);
                holder.tv_medianotes.setKeyListener(null);
            }

            holder.layout_share_slide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.layout_share_slide.setEnabled(false);
                    new Handler().postDelayed(new Runnable()
                    {
                        public void run()
                        {
                            holder.layout_share_slide.setEnabled(true);
                        }
                    }, 1000);
                    adapter.onItemClicked(mediaobject,1);
                }
            });

            holder.layout_delete_slide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.layout_delete_slide.setEnabled(false);
                    new Handler().postDelayed(new Runnable()
                    {
                        public void run()
                        {
                            holder.layout_delete_slide.setEnabled(true);
                        }
                    }, 1000);
                    adapter.onItemClicked(mediaobject,2);
                }
            });

            holder.layout_folder_slide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapter.onItemClicked(mediaobject,6);
                }
            });


            holder.img_videothumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_reader))
                    {
                        if (! mediaobject.getMediastatus().equalsIgnoreCase(config.sync_complete))
                            return;
                    }
                    adapter.onItemClicked(mediaobject,4);
                }
            });

            double parentheight=listviewheight;
            parentheight=parentheight/4.4;
            holder.root_view.getLayoutParams().height = (int)parentheight;
        }
        else
        {
            holder.root_view.getLayoutParams().height = 0;
            holder.root_view.setVisibility(View.GONE);
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

    public void notifysingleitem(int itemposition)
    {
        if(itemposition < arrayvideolist.size())
            notifyItemChanged(itemposition);
    }

    public void filterlist(ArrayList<video> arrayvideolist) {
        this.arrayvideolist = arrayvideolist;
        notifyDataSetChanged();
    }

    public void saveStates(Bundle outState) {
        binderHelper.saveStates(outState);
    }

    /**
     * Only if you need to restore open/close state when the orientation is changed.
     * Call this method in {@link android.app.Activity#onRestoreInstanceState(Bundle)}
     */
    public void restoreStates(Bundle inState) {
        binderHelper.restoreStates(inState);
    }

    @Override
    public int getItemCount() {
        return arrayvideolist.size();
    }

}
