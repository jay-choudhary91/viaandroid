package com.cryptoserver.composer.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cryptoserver.composer.R;
import com.cryptoserver.composer.activity.FullScreenVideoActivity;
import com.cryptoserver.composer.fragments.fullscreenvideofragment;
import com.cryptoserver.composer.interfaces.adapteritemclick;
import com.cryptoserver.composer.models.video;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import java.io.File;
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


        holder.edtvideoname.setText(arrayvideolist.get(position).getName().substring(0, arrayvideolist.get(position).getName().lastIndexOf(".")));
        holder.tvvideocreatedate.setText(arrayvideolist.get(position).getCreatedate());
        holder.tvvideoduration.setText("Duration : " +arrayvideolist.get(position).getDuration());

        holder.edtvideoname.setEnabled(false);
        holder.edtvideoname.setClickable(false);
        holder.edtvideoname.setFocusable(false);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                       final Bitmap bitmap = Glide.
                                with(context).
                                load(arrayvideolist.get(position).getPath()).
                                asBitmap().
                                into(100, 100). // Width and height
                                get();


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

        }
        else
        {
            arrayvideolist.get(position).setSelected(false);
            holder.edtvideoname.setEnabled(false);
            holder.edtvideoname.setClickable(false);
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

                /*Intent in=new Intent(context, FullScreenVideoActivity.class);
                in.putExtra("videopath",arrayvideolist.get(position).getPath());
                context.startActivity(in);*/
            }
        });

        holder.edtvideoname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    v.setFocusable(false);
                    InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(holder.edtvideoname.getWindowToken(), 0);

                    String renamevalue = holder.edtvideoname.getText().toString();
                    String path = arrayvideolist.get(position).getPath();

                        File sourceFile = new File(path);
                        File filedirectory = sourceFile.getParentFile();
                        String filename = sourceFile.getName();

                    if(!filename.equalsIgnoreCase(renamevalue)){
                        File from = new File(filedirectory,filename);
                        File to = new File(filedirectory,renamevalue + ".mp4");
                        from.renameTo(to);

                        adapter.onItemClicked(arrayvideolist.get(position),3);
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
