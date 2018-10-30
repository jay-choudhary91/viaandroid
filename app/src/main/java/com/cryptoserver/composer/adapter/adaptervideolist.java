package com.cryptoserver.composer.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;

import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cryptoserver.composer.R;
import com.cryptoserver.composer.interfaces.adapteritemclick;
import com.cryptoserver.composer.models.video;
import com.cryptoserver.composer.utils.common;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by devesh on 6/8/18.
 */

public class adaptervideolist extends RecyclerView.Adapter<adaptervideolist.myViewHolder> {

    Context context;
    ArrayList<video> arrayvideolist = new ArrayList<video>();
    adapteritemclick adapter;
    private int row_index = -1;


    public class myViewHolder extends RecyclerView.ViewHolder {
        public TextView tvvideoname,tvvideocreatedate,tvvideoduration,tvvideodescription;
        EditText edtvideoname;
        public ImageView imgshareicon,imgdeleteicon,img_videothumbnail,img_full_screen,img_play_pause;
        public Button btnedit;

        public myViewHolder(View view) {
            super(view);
            tvvideoname = (TextView) view.findViewById(R.id.tv_videoname);
            edtvideoname = (EditText) view.findViewById(R.id.edt_videoname);
            tvvideocreatedate = (TextView) view.findViewById(R.id.tv_videocreatedate);
            tvvideoduration = (TextView) view.findViewById(R.id.tv_videoduration);
            tvvideodescription = (TextView) view.findViewById(R.id.tv_videodescription);
            imgshareicon = (ImageView) view.findViewById(R.id.img_shareicon);
            imgdeleteicon = (ImageView) view.findViewById(R.id.img_deleteicon);
            img_videothumbnail = (ImageView) view.findViewById(R.id.img_videothumbnail);
            btnedit = (Button) view.findViewById(R.id.btn_edit);
        }
    }

    public adaptervideolist(Context context, ArrayList<video> arrayvideolist, adapteritemclick adapter){
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
    public void onBindViewHolder(@NonNull final myViewHolder holder, final int position) {

        if(arrayvideolist.get(position).getName().contains("."))
        {
            holder.edtvideoname.setText(arrayvideolist.get(position).getName().substring(0, arrayvideolist.get(position).getName().lastIndexOf(".")));
        }
        else
        {
            holder.edtvideoname.setText(arrayvideolist.get(position).getName().substring(0, arrayvideolist.get(position).getName().trim().length()));
        }

        holder.tvvideocreatedate.setText(arrayvideolist.get(position).getCreatedate());
        holder.tvvideoduration.setText("Duration : " +arrayvideolist.get(position).getDuration());

        holder.edtvideoname.setEnabled(false);
        holder.edtvideoname.setClickable(false);
        holder.edtvideoname.setFocusable(false);

        final String url = arrayvideolist.get(position).getPath();

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
/*
                        Picasso.get()
                                .load(url)
                                .into(new Target() {
                                    @Override
                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {


                                        ((Activity) context).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                holder.img_videothumbnail.setImageBitmap(bitmap);
                                                     }
                                            };
                                    }
                                    @Override
                                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                    }

                                    @Override
                                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                                    }


                                });*/

                        RequestOptions myOptions = new RequestOptions()
                                .fitCenter()
                                .override(100, 100);

                       final Bitmap bitmap = Glide.
                                with(context).
                                asBitmap().
                                load(url).
                                apply(myOptions).
                                submit().
                                get();// Width and height


                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                holder.img_videothumbnail.setImageBitmap(bitmap);
                            }
                        });

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            });thread.start();

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


        holder.img_videothumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                adapter.onItemClicked(arrayvideolist.get(position),4);

                /*Intent in=new Intent(context, fullscreenvideoactivity.class);
                in.putExtra("videopath",arrayvideolist.get(position).getPath());
                context.startActivity(in);*/
            }
        });

        holder.edtvideoname.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if  ((actionId == EditorInfo.IME_ACTION_DONE)) {
                  /*  Log.i(TAG,"Here you can write the code");*/
                  if(arrayvideolist.size()>0){
                      String renamevalue = holder.edtvideoname.getText().toString();
                      String path = arrayvideolist.get(position).getPath();

                      File sourceFile = new File(path);
                      File filedirectory = sourceFile.getParentFile();
                      String filename = sourceFile.getName();

                      if(renamevalue.toString().trim().length() > 0)
                      {
                          if(!filename.equalsIgnoreCase(renamevalue)){
                              File from = new File(filedirectory,filename);
                              File to = new File(filedirectory,renamevalue + ".mp4");
                              from.renameTo(to);

                              adapter.onItemClicked(arrayvideolist.get(position),3);
                          }
                      }
                      else
                      {
                          holder.edtvideoname.setText(common.removeextension(filename));
                      }

                      return true;
                  }

                }
                return false;
            }
        });

        holder.edtvideoname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    holder.edtvideoname.setKeyListener(null);
                    v.setFocusable(false);
                    InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(holder.edtvideoname.getWindowToken(), 0);
                    if(arrayvideolist.size()>0){
                        String renamevalue = holder.edtvideoname.getText().toString();
                        String path = arrayvideolist.get(position).getPath();

                        File sourceFile = new File(path);
                        File filedirectory = sourceFile.getParentFile();
                        String filename = sourceFile.getName();

                        if(renamevalue.toString().trim().length() > 0)
                        {
                            if(!filename.equalsIgnoreCase(renamevalue)){
                                File from = new File(filedirectory,filename);
                                File to = new File(filedirectory,renamevalue + ".mp4");
                                from.renameTo(to);

                                adapter.onItemClicked(arrayvideolist.get(position),3);
                            }
                        }
                        else
                        {
                            holder.edtvideoname.setText(common.removeextension(filename));
                        }
                    }
                    Log.e("Focaus change ", "Focus change");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayvideolist.size();
    }

}
