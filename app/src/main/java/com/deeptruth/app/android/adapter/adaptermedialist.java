package com.deeptruth.app.android.adapter;

import android.content.Context;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.deeptruth.app.android.BuildConfig;
import com.deeptruth.app.android.R;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.interfaces.adapteritemclick;
import com.deeptruth.app.android.models.video;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by devesh on 6/8/18.
 */

public class adaptermedialist extends RecyclerSwipeAdapter<adaptermedialist.myViewHolder> {

    Context context;
    ArrayList<video> arrayvideolist = new ArrayList<video>();
    adapteritemclick adapter;
    private int listviewheight=0,imagethumbanail_width=0,totalwidth=0;
    ViewBinderHelper binderHelper;

    public class myViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_mediatime,tv_mediadate,tv_localkey,tv_sync_status,txt_pipesign,tv_medianotes,tv_mediaduration,
                tv_valid,tv_caution,tv_unsent,tv_invalid,txt_pipesign_caution,txt_pipesign_unsent,txt_pipesign_invalid,
                tv_framecounts,txt_videoname,tv_size_location;
        RelativeLayout relative_child ;
        public ImageView img_imageshare,img_loader,img_videothumbnail,img_slide_share,img_slide_create_dir,img_slide_delete,img_scanover,
                img_slide_save,img_slide_publish,img_slide_send;
        public SwipeLayout root_view;
        LinearLayout layout_share_slide,layout_delete_slide,layout_folder_slide,linearseekbarcolorview,layout_update_labels,layout_share_send
                ,layout_share_publish,layout_share_save;
        ProgressBar progressmediasync;
        LinearLayout delete_layout,share_layout;

        public myViewHolder(View view) {
            super(view);
            tv_medianotes = (TextView) view.findViewById(R.id.tv_medianotes);
            txt_videoname = (TextView) view.findViewById(R.id.txt_videoname);
            tv_mediadate = (TextView) view.findViewById(R.id.tv_mediadate);
            img_imageshare = (ImageView) view.findViewById(R.id.img_imageshare);
            tv_localkey = (TextView) view.findViewById(R.id.tv_localkey);
            tv_sync_status = (TextView) view.findViewById(R.id.tv_sync_status);
            img_videothumbnail = (ImageView) view.findViewById(R.id.img_videothumbnail);
            img_slide_share = (ImageView) view.findViewById(R.id.img_slide_share);
            img_slide_create_dir = (ImageView) view.findViewById(R.id.img_slide_create_dir);
            img_slide_delete = (ImageView) view.findViewById(R.id.img_slide_delete);
            img_scanover = (ImageView) view.findViewById(R.id.img_scanover);
            img_slide_save = (ImageView) view.findViewById(R.id.img_slide_save);
            img_slide_publish = (ImageView) view.findViewById(R.id.img_slide_publish);
            img_slide_send = (ImageView) view.findViewById(R.id.img_slide_send);
            img_loader = (ImageView) view.findViewById(R.id.img_loader);
            layout_folder_slide = view.findViewById(R.id.layout_folder_slide);
            layout_delete_slide = view.findViewById(R.id.layout_delete_slide);
            layout_share_send = view.findViewById(R.id.layout_share_send);
            layout_share_publish = view.findViewById(R.id.layout_share_publish);
            layout_share_save = view.findViewById(R.id.layout_share_save);
            layout_share_slide = view.findViewById(R.id.layout_share_slide);
            tv_mediaduration = (TextView) view.findViewById(R.id.tv_mediaduration);
            root_view = (SwipeLayout) view.findViewById(R.id.root_view);
            relative_child = (RelativeLayout) view.findViewById(R.id.relative_child);
            linearseekbarcolorview = (LinearLayout) view.findViewById(R.id.linear_seekbarcolorview);
            tv_valid = (TextView) view.findViewById(R.id.tv_valid);
            tv_caution = (TextView) view.findViewById(R.id.tv_caution);
            tv_unsent = (TextView) view.findViewById(R.id.tv_unsent);
            tv_invalid = (TextView) view.findViewById(R.id.tv_invalid);
            txt_pipesign_caution = (TextView) view.findViewById(R.id.txt_pipesign_caution);
            txt_pipesign_unsent = (TextView) view.findViewById(R.id.txt_pipesign_unsent);
            txt_pipesign_invalid = (TextView) view.findViewById(R.id.txt_pipesign_invalid);
            tv_framecounts = (TextView) view.findViewById(R.id.tv_framecounts);
            tv_size_location = (TextView) view.findViewById(R.id.tv_size_location);
            layout_update_labels = (LinearLayout) view.findViewById(R.id.layout_update_labels);
            progressmediasync = (ProgressBar) view.findViewById(R.id.progressmediasync);
            delete_layout = (LinearLayout) view.findViewById(R.id.delete_layout);
            share_layout = (LinearLayout) view.findViewById(R.id.share_layout);

            try {
                if(img_slide_save.getDrawable() != null)
                    DrawableCompat.setTint(img_slide_save.getDrawable(), ContextCompat.getColor(applicationviavideocomposer.getactivity(), R.color.white));
            }catch (Exception e)
            {
                e.printStackTrace();
            }

            try {
                if(img_slide_publish.getDrawable() != null)
                    DrawableCompat.setTint(img_slide_publish.getDrawable(), ContextCompat.getColor(applicationviavideocomposer.getactivity(), R.color.white));
            }catch (Exception e)
            {
                e.printStackTrace();
            }

            try {
                if(img_slide_send.getDrawable() != null)
                    DrawableCompat.setTint(img_slide_send.getDrawable(), ContextCompat.getColor(applicationviavideocomposer.getactivity(), R.color.white));
            }catch (Exception e)
            {
                e.printStackTrace();
            }
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
                .inflate(R.layout.adapter_medialist, parent, false);

        return new myViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final myViewHolder holder, final int position) {

        final video mediaobject=arrayvideolist.get(position);
        holder.root_view.setShowMode(SwipeLayout.ShowMode.PullOut);

        holder.root_view.addDrag(SwipeLayout.DragEdge.Right,holder.delete_layout);
        holder.root_view.addDrag(SwipeLayout.DragEdge.Left,holder.share_layout);



        //binderHelper.bind(holder.root_view,""+position);

        /*if( holder.root_view.getOpenStatus().)
            binderHelper.closeLayout(""+position);*/



        //  binderHelper.bind(holder.root_view,""+position);

        if(BuildConfig.FLAVOR.contains(config.build_flavor_reader))
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

        int validcount=mediaobject.getValidcount();
        int cautioncount=mediaobject.getCautioncount();
        int unsentcount=mediaobject.getUnsentcount();
        int invalidcount=mediaobject.getInvalidcount();
        if(common.isdevelopermodeenable())
        {
            holder.tv_framecounts.setVisibility(View.VISIBLE);
            holder.tv_framecounts.setText(mediaobject.getFrameuploadstatus());
        }
        else
        {
            holder.tv_framecounts.setVisibility(View.INVISIBLE);
        }
        try {

            holder.linearseekbarcolorview.removeAllViews();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        ArrayList<String> arrayList = mediaobject.getMediabarcolor();
        if (mediaobject.getMediastatus().equalsIgnoreCase(config.sync_notfound))
        {
            holder.layout_update_labels.setVisibility(View.VISIBLE);
            holder.linearseekbarcolorview.setBackgroundColor(Color.RED);
            invalidcount=1;
            holder.tv_invalid.setText(config.item_invalid+" 100%");
        }

        holder.tv_valid.setVisibility(View.VISIBLE);
        holder.tv_caution.setVisibility(View.VISIBLE);
        holder.tv_unsent.setVisibility(View.VISIBLE);
        holder.tv_invalid.setVisibility(View.VISIBLE);

        holder.tv_valid.setTextSize(TypedValue.COMPLEX_UNIT_SP,11);
        holder.tv_caution.setTextSize(TypedValue.COMPLEX_UNIT_SP,11);
        holder.tv_unsent.setTextSize(TypedValue.COMPLEX_UNIT_SP,11);
        holder.tv_invalid.setTextSize(TypedValue.COMPLEX_UNIT_SP,11);

        if(validcount == 0)
            holder.tv_valid.setVisibility(View.GONE);

        if(cautioncount == 0)
            holder.tv_caution.setVisibility(View.GONE);

        if(unsentcount == 0)
            holder.tv_unsent.setVisibility(View.GONE);

        if(invalidcount == 0)
            holder.tv_invalid.setVisibility(View.GONE);

        holder.txt_pipesign_caution.setVisibility(View.GONE);
        holder.txt_pipesign_unsent.setVisibility(View.GONE);
        holder.txt_pipesign_invalid.setVisibility(View.GONE);

        if(validcount > 0 && cautioncount > 0){
            holder.tv_valid.setTextSize(TypedValue.COMPLEX_UNIT_SP,11);
            holder.tv_caution.setTextSize(TypedValue.COMPLEX_UNIT_SP,11);
            holder.txt_pipesign_caution.setVisibility(View.VISIBLE);
        }

        if(cautioncount > 0 && unsentcount > 0){
            holder.tv_unsent.setTextSize(TypedValue.COMPLEX_UNIT_SP,11);
            holder.tv_caution.setTextSize(TypedValue.COMPLEX_UNIT_SP,11);
        }
        if(validcount > 0 && unsentcount > 0){
            holder.tv_valid.setTextSize(TypedValue.COMPLEX_UNIT_SP,11);
            holder.tv_unsent.setTextSize(TypedValue.COMPLEX_UNIT_SP,11);
        }

        if(validcount >0 && unsentcount > 0 && unsentcount > 0 ){
            holder.tv_valid.setTextSize(TypedValue.COMPLEX_UNIT_SP,9);
            holder.tv_caution.setTextSize(TypedValue.COMPLEX_UNIT_SP,9);
            holder.tv_unsent.setTextSize(TypedValue.COMPLEX_UNIT_SP,9);
        }

        if(unsentcount > 0)
            holder.progressmediasync.setVisibility(View.VISIBLE);
        else
            holder.progressmediasync.setVisibility(View.INVISIBLE);

        if((cautioncount > 0 || validcount > 0) && unsentcount > 0){
            holder.txt_pipesign_unsent.setVisibility(View.VISIBLE);
        }

        if((cautioncount > 0 || validcount > 0 || unsentcount > 0) && invalidcount > 0)
            holder.txt_pipesign_invalid.setVisibility(View.VISIBLE);

        if(arrayList != null && arrayList.size() > 0)
        {
            holder.layout_update_labels.setVisibility(View.VISIBLE);
            holder.linearseekbarcolorview.setBackgroundColor(Color.TRANSPARENT);
            if(mediaobject.getColorsectionsarray() != null && mediaobject.getColorsectionsarray().size() > 0)
            {
                for(int i=0;i<mediaobject.getColorsectionsarray().size();i++)
                {
                    String item=mediaobject.getColorsectionsarray().get(i);
                    if(! item.trim().isEmpty())
                    {
                        String[] itemarray=item.split(",");
                        if(itemarray.length >= 2)
                        {
                            String writecolor=itemarray[0];
                            String weight=itemarray[1];
                            if(! weight.trim().isEmpty())
                                holder.linearseekbarcolorview.addView(getmediaseekbarbackgroundview(weight,writecolor));
                        }
                    }
                }
            }

            holder.linearseekbarcolorview.invalidate();
            holder.linearseekbarcolorview.requestLayout();

            holder.tv_valid.setText(config.item_valid+" "+common.getcolorprogresspercentage(validcount,arrayList.size()));
            holder.tv_caution.setText(config.item_caution+" "+ common.getcolorprogresspercentage(cautioncount,arrayList.size()));
            holder.tv_unsent.setText(config.item_unsent+" "+ common.getcolorprogresspercentage(unsentcount,arrayList.size()));
            holder.tv_invalid.setText(config.item_invalid+" "+ common.getcolorprogresspercentage(invalidcount,arrayList.size()));
        }
        else
        {
            Log.e("else case ","case2");
            holder.layout_update_labels.setVisibility(View.VISIBLE);
            holder.tv_unsent.setVisibility(View.VISIBLE);

            holder.tv_unsent.setText(config.item_unsent+" 100%");
            holder.progressmediasync.setVisibility(View.VISIBLE);
        }

        holder.txt_videoname.setText(mediaobject.getMediatitle());
        if(mediaobject.getDuration().trim().isEmpty())
        {
            //   holder.tv_mediaduration.setText("NA");
            holder.tv_mediaduration.setVisibility(View.INVISIBLE);
        }
        else
        {
            holder.tv_mediaduration.setVisibility(View.VISIBLE);
            holder.tv_mediaduration.setText(mediaobject.getDuration());
        }

        if(mediaobject.getCreatedate().trim().isEmpty())
            holder.tv_mediadate.setText("NA");
        else
            holder.tv_mediadate.setText(mediaobject.getCreatedate() +", "  + mediaobject.getCreatetime());

        if(mediaobject.getfilesize().trim().isEmpty())
            holder.tv_size_location.setText("NA");
        else
            holder.tv_size_location.setText(mediaobject.getfilesize());

        if(mediaobject.getMedialocation().trim().isEmpty())
            holder.tv_size_location.append(" | NA");
        else
            holder.tv_size_location.append(" | "  + mediaobject.getMedialocation());

        holder.tv_medianotes.setText(mediaobject.getMedianotes());

        if(mediaobject.getVideostarttransactionid().isEmpty() ||
                mediaobject.getVideostarttransactionid().equalsIgnoreCase("null"))
            holder.tv_localkey.setText("");
        else
            holder.tv_localkey.setText(mediaobject.getVideostarttransactionid());

        if(mediaobject.getMediastatus().isEmpty() ||  mediaobject.getMediastatus().equalsIgnoreCase("null"))
            holder.tv_sync_status.setText("Status : pending");
        else
            holder.tv_sync_status.setText("Status : " + mediaobject.getMediastatus());

            /*holder.edtvideoname.setEnabled(false);
            holder.edtvideoname.setClickable(false);
            holder.edtvideoname.setFocusable(false);*/

        RequestOptions requestOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.override(200,200);
        if(! mediaobject.getmimetype().contains("audio"))
        {
            Uri uri = Uri.fromFile(new File(mediaobject.getPath()));
            Glide.with(context).load(uri).apply(requestOptions).thumbnail(0.1f).into(holder.img_videothumbnail);
        }
        else
        {
            holder.img_videothumbnail.setBackgroundColor(context.getResources().getColor(R.color.black));
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
        }

        holder.img_imageshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.img_imageshare.setEnabled(false);
                new Handler().postDelayed(new Runnable()
                {
                    public void run()
                    {
                        holder.img_imageshare.setEnabled(true);
                    }
                }, 1000);
                adapter.onItemClicked(mediaobject,1);
                holder.root_view.close();
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
                holder.root_view.close();
            }
        });

        holder.layout_share_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                adapter.onItemClicked(mediaobject,5);
                holder.root_view.close();

            }
        });

        holder.layout_share_publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.onItemClicked(mediaobject,6);
                holder.root_view.close();


            }
        });

        holder.layout_share_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.onItemClicked(mediaobject,7);
                holder.root_view.close();
            }
        });

        holder.relative_child.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(BuildConfig.FLAVOR.contains(config.build_flavor_reader))
                {
                    if (! mediaobject.getMediastatus().equalsIgnoreCase(config.sync_complete))
                        return;
                }
                adapter.onItemClicked(mediaobject,4);
                holder.root_view.close();
            }
        });

        double parentheight=listviewheight;
        Log.e("parentheight",""+listviewheight);
        parentheight=parentheight/4.3;
        holder.root_view.getLayoutParams().height = (int)parentheight;

        /*if(mediaobject.isDoenable())
        {

        }
        else
        {

            holder.root_view.getLayoutParams().height = 0;
            holder.root_view.setVisibility(View.GONE);
        }*/

        //binderHelper.bind(holder.root_view,""+position);

        /*if(mediaobject.isDoenable())
        {
            if(! holder.root_view.isOpened())
                 binderHelper.closeLayout(""+position);

            binderHelper.bind(holder.root_view,""+position);
        }
        else
        {
            binderHelper.closeLayout(""+position);
            binderHelper.bind(holder.root_view,""+position);
        }*/

        mItemManger.bindView(holder.itemView, position);

    }

    public View getmediaseekbarbackgroundview(String weight,String color)
    {
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT,Float.parseFloat(weight));
        View view = new View(applicationviavideocomposer.getactivity());
        view.setLayoutParams(param);
        if((! color.isEmpty()))
        {
            view.setBackgroundColor(Color.parseColor(common.getcolorbystring(color)));
        }
        else
        {
            view.setBackgroundColor(Color.parseColor(config.color_code_white_transparent));
        }
        return view;
    }

    public void notifyitems(ArrayList<video> arrayvideolist,int selectedlisttype)
    {
        ArrayList<video> localarray=new ArrayList<>();
        for(int i=0;i<arrayvideolist.size();i++)
        {
            if(! arrayvideolist.get(i).ismediapublished() && selectedlisttype == 0)
                localarray.add(arrayvideolist.get(i));
            else if(arrayvideolist.get(i).ismediapublished() && selectedlisttype == 1)
                localarray.add(arrayvideolist.get(i));
        }
        this.arrayvideolist=localarray;
        notifyDataSetChanged();
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

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.root_view;
    }

}
