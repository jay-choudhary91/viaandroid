package com.cryptoserver.composer.adapter;

import android.content.Context;
import android.graphics.Bitmap;

import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cryptoserver.composer.R;
import com.cryptoserver.composer.interfaces.adapteritemclick;
import com.cryptoserver.composer.models.video;
import com.cryptoserver.composer.utils.common;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

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
        public TextView tvvideoname,tvvideocreatedate,tvvideoduration,tv_localkey,tv_sync_status,txt_pipesign,tv_medianotes;
        EditText edtvideoname;
        public ImageView imgshareicon,imgdeleteicon,img_videothumbnail,img_slide_share,img_slide_create_dir,img_slide_delete;
        public RelativeLayout root_view;

        public myViewHolder(View view) {
            super(view);
            tv_medianotes = (TextView) view.findViewById(R.id.tv_medianotes);
            tvvideoname = (TextView) view.findViewById(R.id.tv_videoname);
            txt_pipesign = (TextView) view.findViewById(R.id.txt_pipesign);
            edtvideoname = (EditText) view.findViewById(R.id.edt_videoname);
            tvvideocreatedate = (TextView) view.findViewById(R.id.tv_videocreatedate);
            tvvideoduration = (TextView) view.findViewById(R.id.tv_videoduration);
            tv_localkey = (TextView) view.findViewById(R.id.tv_localkey);
            tv_sync_status = (TextView) view.findViewById(R.id.tv_sync_status);
            imgshareicon = (ImageView) view.findViewById(R.id.img_shareicon);
            imgdeleteicon = (ImageView) view.findViewById(R.id.img_deleteicon);
            img_videothumbnail = (ImageView) view.findViewById(R.id.img_videothumbnail);
            img_slide_share = (ImageView) view.findViewById(R.id.img_slide_share);
            img_slide_create_dir = (ImageView) view.findViewById(R.id.img_slide_create_dir);
            img_slide_delete = (ImageView) view.findViewById(R.id.img_slide_delete);
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

            holder.tvvideocreatedate.setText(arrayvideolist.get(position).getCreatetime());
            holder.tvvideoduration.setText(arrayvideolist.get(position).getCreatedate());

           /* String datetime = arrayvideolist.get(position).getCreatedate();
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ENGLISH);
            final Date startdate;
            try {
                startdate = format.parse(datetime);
                String time = new SimpleDateFormat("hh:mm:ss aa").format(startdate);
                String filecreateddate = new SimpleDateFormat("MM/dd/yyyy").format(startdate);

                holder.tvvideocreatedate.setText(time);
                holder.tvvideoduration.setText(filecreateddate);

            } catch (ParseException e) {
                e.printStackTrace();
            }*/

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

           /* if (arrayvideolist.get(position).getmimetype().contains("image/"))
            {
                holder.tvvideoduration.setText("");
                holder.txt_pipesign.setVisibility(View.GONE);
            }
            else
            {
                holder.txt_pipesign.setVisibility(View.VISIBLE);
                holder.tvvideoduration.setText(arrayvideolist.get(position).getDuration());
            }*/

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
