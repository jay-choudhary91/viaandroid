package com.deeptruth.app.android.adapter;

import android.content.Context;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class adaptermedialist extends RecyclerView.Adapter<adaptermedialist.myViewHolder> {

    Context context;
    ArrayList<video> arrayvideolist = new ArrayList<video>();
    adapteritemclick adapter;
    private int listviewheight=0,imagethumbanail_width=0,totalwidth=0;
    ViewBinderHelper binderHelper;

    public class myViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_mediatime,tv_mediadate,tv_localkey,tv_sync_status,txt_pipesign,tv_medianotes,tv_mediaduration,
                tv_valid,tv_caution,tv_unsent,tv_invalid,txt_pipesign_caution,txt_pipesign_unsent,txt_pipesign_invalid;
        EditText edtvideoname;
        RelativeLayout relative_child ;
        public ImageView img_imageshare,img_loader,img_videothumbnail,img_slide_share,img_slide_create_dir,img_slide_delete,img_scanover;
        public SwipeRevealLayout root_view;
        LinearLayout layout_share_slide,layout_delete_slide,layout_folder_slide,linearseekbarcolorview,layout_colorbar;


        public myViewHolder(View view) {
            super(view);
            tv_medianotes = (TextView) view.findViewById(R.id.tv_medianotes);
            edtvideoname = (EditText) view.findViewById(R.id.edt_videoname);
            tv_mediadate = (TextView) view.findViewById(R.id.tv_mediadate);
            img_imageshare = (ImageView) view.findViewById(R.id.img_imageshare);
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
            relative_child = (RelativeLayout) view.findViewById(R.id.relative_child);
            linearseekbarcolorview = (LinearLayout) view.findViewById(R.id.linear_seekbarcolorview);
            tv_valid = (TextView) view.findViewById(R.id.tv_valid);
            tv_caution = (TextView) view.findViewById(R.id.tv_caution);
            tv_unsent = (TextView) view.findViewById(R.id.tv_unsent);
            tv_invalid = (TextView) view.findViewById(R.id.tv_invalid);
            txt_pipesign_caution = (TextView) view.findViewById(R.id.txt_pipesign_caution);
            txt_pipesign_unsent = (TextView) view.findViewById(R.id.txt_pipesign_unsent);
            txt_pipesign_invalid = (TextView) view.findViewById(R.id.txt_pipesign_invalid);
            layout_colorbar = (LinearLayout) view.findViewById(R.id.layout_colorbar);
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
        //binderHelper.bind(holder.root_view,""+position);
        if(mediaobject.isDoenable())
        {
       //     if(! holder.root_view.isOpened())
        //        binderHelper.closeLayout(""+position);

          //  binderHelper.bind(holder.root_view,""+position);

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

            int validcount=mediaobject.getValidcount();
            int cautioncount=mediaobject.getCautioncount();
            int unsentcount=mediaobject.getUnsentcount();
            int invalidcount=mediaobject.getInvalidcount();

            ArrayList<String> arrayList = mediaobject.getMediabarcolor();
            if (mediaobject.getMediastatus().equalsIgnoreCase(config.sync_notfound))
            {
                holder.layout_colorbar.setVisibility(View.VISIBLE);
                holder.linearseekbarcolorview.setBackgroundColor(Color.RED);
                invalidcount=1;
                holder.tv_invalid.setText(config.item_invalid+" 100%");
            }

            Log.e("Status ",""+validcount+" "+cautioncount+" "+unsentcount+" "+invalidcount);
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

            if(validcount > 0 && cautioncount > 0)
                holder.txt_pipesign_caution.setVisibility(View.VISIBLE);

            if((cautioncount > 0 || validcount > 0) && unsentcount > 0){
                holder.txt_pipesign_unsent.setVisibility(View.VISIBLE);
                holder.tv_valid.setTextSize(TypedValue.COMPLEX_UNIT_SP,9);
                holder.tv_caution.setTextSize(TypedValue.COMPLEX_UNIT_SP,9);
                holder.tv_unsent.setTextSize(TypedValue.COMPLEX_UNIT_SP,9);
            }

            if((cautioncount > 0 || validcount > 0 || unsentcount > 0) && invalidcount > 0)
                holder.txt_pipesign_invalid.setVisibility(View.VISIBLE);

            /*holder.tv_valid.setText(config.item_valid);
            holder.tv_caution.setText(config.item_caution);
            holder.tv_unsent.setText(config.item_unsent);
            holder.tv_invalid.setText(config.item_invalid);*/

            if(arrayList != null && arrayList.size() > 0 && mediaobject.getColorbarview() != null )
            {
                holder.layout_colorbar.setVisibility(View.VISIBLE);
                try {
                    if(mediaobject.getColorbarview().getParent() != null)
                        ((ViewGroup)mediaobject.getColorbarview().getParent()).removeView(mediaobject.getColorbarview());

                    holder.linearseekbarcolorview.removeAllViews();
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
                holder.linearseekbarcolorview.setBackgroundColor(Color.TRANSPARENT);
                holder.linearseekbarcolorview.addView(mediaobject.getColorbarview());
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
                if(validcount == 0 && invalidcount == 0 && cautioncount == 0 && unsentcount == 0)
                    holder.layout_colorbar.setVisibility(View.GONE);
            }


            holder.edtvideoname.setText(mediaobject.getMediatitle());

            if(mediaobject.getDuration().trim().isEmpty())
            {
             //   holder.tv_mediaduration.setText("NA");
                holder.tv_mediaduration.setVisibility(View.GONE);
            }
            else
            {
                holder.tv_mediaduration.setVisibility(View.VISIBLE);
                holder.tv_mediaduration.setText(mediaobject.getDuration());
            }

            if(mediaobject.getCreatedate().trim().isEmpty())
            {

                holder.tv_mediadate.setText("NA");
                // holder.tv_mediatime.setText(mediaobject.getCreatetime());
            }
            else
            {
                holder.tv_mediadate.setText(mediaobject.getCreatedate() +" | "  + mediaobject.getCreatetime());
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
                    binderHelper.bind(holder.root_view,""+position);
                    binderHelper.closeLayout(""+position);
                    adapter.onItemClicked(mediaobject,1);
                }
            });

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
                    binderHelper.bind(holder.root_view,""+position);
                    binderHelper.closeLayout(""+position);
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
                    binderHelper.bind(holder.root_view,""+position);
                    binderHelper.closeLayout(""+position);
                    adapter.onItemClicked(mediaobject,2);
                }
            });

            holder.layout_folder_slide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    binderHelper.bind(holder.root_view,""+position);
                    binderHelper.closeLayout(""+position);
                    adapter.onItemClicked(mediaobject,6);
                }
            });


            holder.relative_child.setOnClickListener(new View.OnClickListener() {
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
        //    binderHelper.closeLayout(""+position);
      //      binderHelper.bind(holder.root_view,""+position);

            holder.root_view.getLayoutParams().height = 0;
            holder.root_view.setVisibility(View.GONE);
        }

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

    private void setseekbarlayoutcolor(LinearLayout colorbarlayout,ArrayList<String> arrayList){

        /*if(colorbarlayout != null && colorbarlayout.getChildCount() == 0)
        {

        }*/
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
                colorbarlayout.addView(view);
            }
            else
            {
                //view.setBackgroundColor(Color.parseColor(config.color_code_gray));
            }

        }
    }

}
