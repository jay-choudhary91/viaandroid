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
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by devesh on 6/8/18.
 */

public class adaptermediagrid extends RecyclerView.Adapter<adaptermediagrid.myViewHolder> {

    Context context;
    ArrayList<video> arrayvideolist = new ArrayList<video>();
    adapteritemclick adapter;
    public class myViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_mediaduration;
        public ImageView img_mediathumbnail;
        public RelativeLayout rl_row_media;
        public Button btnedit;

        public myViewHolder(View view) {
            super(view);
            tv_mediaduration = (TextView) view.findViewById(R.id.tv_mediaduration);
            img_mediathumbnail = (ImageView) view.findViewById(R.id.img_mediathumbnail);
            rl_row_media = (RelativeLayout) view.findViewById(R.id.rl_row_media);
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
            holder.rl_row_media.setVisibility(View.VISIBLE);
            if (arrayvideolist.get(position).getmimetype().contains("image/"))
            {
                holder.tv_mediaduration.setText("");
            }
            else
            {
                holder.tv_mediaduration.setText(arrayvideolist.get(position).getDuration());
            }

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
                Glide.with(context).
                        load(R.drawable.audiothum).
                        thumbnail(0.1f).
                        into(holder.img_mediathumbnail);
            }
            holder.img_mediathumbnail.getLayoutParams().height = arrayvideolist.get(position).getGriditemheight();
        }
        else
        {
            holder.rl_row_media.setVisibility(View.GONE);
            holder.img_mediathumbnail.getLayoutParams().height = 0;
        }
    }

    @Override
    public int getItemCount() {
        return arrayvideolist.size();
    }

}
