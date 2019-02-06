package com.deeptruth.app.android.adapter;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;

import android.net.Uri;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
    private int row_index = -1,listviewheight=0;
    HashMap<String, Bitmap> cacheBitmap;

    public class myViewHolder extends RecyclerView.ViewHolder {
        public TextView tvvideoname,tv_mediatime,tv_mediadate,tv_localkey,tv_sync_status,txt_pipesign,tv_medianotes;
        EditText edtvideoname;
        public ImageView imgshareicon,imgdeleteicon,img_videothumbnail,img_slide_share,img_slide_create_dir,img_slide_delete,img_scanover;
        public RelativeLayout root_view;

        public myViewHolder(View view) {
            super(view);
            tv_medianotes = (TextView) view.findViewById(R.id.tv_medianotes);
            tvvideoname = (TextView) view.findViewById(R.id.tv_videoname);
            txt_pipesign = (TextView) view.findViewById(R.id.txt_pipesign);
            edtvideoname = (EditText) view.findViewById(R.id.edt_videoname);
            tv_mediatime = (TextView) view.findViewById(R.id.tv_mediatime);
            tv_mediadate = (TextView) view.findViewById(R.id.tv_mediadate);
            tv_localkey = (TextView) view.findViewById(R.id.tv_localkey);
            tv_sync_status = (TextView) view.findViewById(R.id.tv_sync_status);
            imgshareicon = (ImageView) view.findViewById(R.id.img_shareicon);
            imgdeleteicon = (ImageView) view.findViewById(R.id.img_deleteicon);
            img_videothumbnail = (ImageView) view.findViewById(R.id.img_videothumbnail);
            img_slide_share = (ImageView) view.findViewById(R.id.img_slide_share);
            img_slide_create_dir = (ImageView) view.findViewById(R.id.img_slide_create_dir);
            img_slide_delete = (ImageView) view.findViewById(R.id.img_slide_delete);
            img_scanover = (ImageView) view.findViewById(R.id.img_scanover);
            root_view = (RelativeLayout) view.findViewById(R.id.root_view);
        }
    }

    public adaptermedialist(Context context, ArrayList<video> arrayvideolist, adapteritemclick adapter,int listviewheight){
        this.context = context;
        this.arrayvideolist = arrayvideolist;
        this.adapter = adapter;
        this.listviewheight = listviewheight;
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

        if(arrayvideolist.get(position).isDoenable())
        {
            TranslateAnimation animation = new TranslateAnimation(-50.0f, 1000.0f,
                    0.0f, 0.0f);
            animation.setDuration(4000);
            //animation.setStartOffset(position*100);
            animation.setRepeatCount(Animation.INFINITE);
            animation.setRepeatMode(ValueAnimator.RESTART);
            //animation.setFillAfter(true);
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

            holder.root_view.setVisibility(View.VISIBLE);
            if(arrayvideolist.get(position).getMediatitle().trim().isEmpty())
                arrayvideolist.get(position).setMediatitle(common.getfilename(arrayvideolist.get(position).getPath()));

            if(arrayvideolist.get(position).getMediatitle() != null && (! arrayvideolist.get(position).getMediatitle().equalsIgnoreCase("null")
                        && (! arrayvideolist.get(position).getMediatitle().trim().isEmpty())))
            {
                if(arrayvideolist.get(position).getMediatitle().contains("."))
                {
                    holder.edtvideoname.setText(arrayvideolist.get(position).getMediatitle().substring(0, arrayvideolist.get(position).getMediatitle().lastIndexOf(".")));
                }
                else
                {
                    holder.edtvideoname.setText(arrayvideolist.get(position).getMediatitle().substring(0, arrayvideolist.get(position).getMediatitle().trim().length()));
                }
            }

            if(arrayvideolist.get(position).getCreatedate().trim().isEmpty())
            {
                holder.txt_pipesign.setVisibility(View.GONE);
                holder.tv_mediadate.setText("NA");
                holder.tv_mediatime.setText(arrayvideolist.get(position).getCreatetime());
            }
            else
            {
                holder.txt_pipesign.setVisibility(View.VISIBLE);
                holder.tv_mediadate.setText(arrayvideolist.get(position).getCreatedate());
                holder.tv_mediatime.setText(arrayvideolist.get(position).getCreatetime());
            }


            holder.tv_medianotes.setText(arrayvideolist.get(position).getMedianotes());


            if(arrayvideolist.get(position).getVideostarttransactionid().isEmpty() ||  arrayvideolist.get(position).getVideostarttransactionid().equalsIgnoreCase("null")){
                holder.tv_localkey.setText("");
            }else
            {
                holder.tv_localkey.setText(arrayvideolist.get(position).getVideostarttransactionid());
            }


            if(arrayvideolist.get(position).getMediastatus().isEmpty() ||  arrayvideolist.get(position).getMediastatus().equalsIgnoreCase("null")){

                holder.tv_sync_status.setText("Status : pending");
            }else{

                holder.tv_sync_status.setText("Status : " + arrayvideolist.get(position).getMediastatus());
            }

            holder.edtvideoname.setEnabled(false);
            holder.edtvideoname.setClickable(false);
            holder.edtvideoname.setFocusable(false);

            if(! arrayvideolist.get(position).getmimetype().contains("audio"))
            {

                Uri uri = Uri.fromFile(new File(arrayvideolist.get(position).getPath()));
                Glide.with(context).
                        load(uri).
                        thumbnail(0.1f).
                        into(holder.img_videothumbnail);
                // holder.img_videothumbnail.setBackgroundResource(R.drawable.phototab);
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
                                into(holder.img_videothumbnail);
                    }

                }
                else
                {
                    Glide.with(context).
                            load(R.drawable.audiothum).
                            thumbnail(0.1f).
                            into(holder.img_videothumbnail);
                }

           /* holder.img_videothumbnail.setBackgroundResource(R.drawable.audiotab);*/
            }



            if(arrayvideolist.get(position).isSelected){
                holder.edtvideoname.setEnabled(true);
                holder.edtvideoname.setClickable(true);
                holder.edtvideoname.setFocusableInTouchMode(true);
                holder.edtvideoname.setSelection(arrayvideolist.get(position).getName().substring(0, arrayvideolist.get(position).getName().lastIndexOf(".")).length());
                holder.edtvideoname.requestFocus();
                arrayvideolist.get(position).setSelected(false);

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
                arrayvideolist.get(position).setSelected(false);
                holder.edtvideoname.setEnabled(false);
                holder.edtvideoname.setClickable(false);
                holder.edtvideoname.setKeyListener(null);
            }

            holder.img_slide_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapter.onItemClicked(arrayvideolist.get(position),1);
                    holder.img_slide_share.setClickable(false);
                    new Handler().postDelayed(new Runnable()
                    {
                        public void run()
                        {
                            holder.img_slide_share.setClickable(true);
                        }
                    }, 150);

                }
            });

            holder.img_slide_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapter.onItemClicked(arrayvideolist.get(position),2);
                }
            });

            holder.img_slide_create_dir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapter.onItemClicked(arrayvideolist.get(position),6);
                }
            });


            holder.img_videothumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // if(arrayvideolist.get(position).getmimetype().contains("video"))
                    adapter.onItemClicked(arrayvideolist.get(position),4);
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

    public void filterlist(ArrayList<video> arrayvideolist) {
        this.arrayvideolist = arrayvideolist;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return arrayvideolist.size();
    }

}
