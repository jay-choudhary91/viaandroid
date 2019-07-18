package com.deeptruth.app.android.adapter;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.deeptruth.app.android.R;

import java.util.ArrayList;

public class adaptersenddialog extends RecyclerView.Adapter<adaptersenddialog.ViewHolder> {


    public ArrayList<Integer> images=new ArrayList<Integer>();
    public ArrayList<String> name=new ArrayList<String>();
    private Context mContext;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.img_send_dialog);
            textView = (TextView) itemView.findViewById(R.id.txt_send_dialog);
        }
    }

    public adaptersenddialog(Context mContext, ArrayList<Integer> datrimage , ArrayList<String> datename ) {
        this.mContext = mContext;
        this.images = datrimage;
        this.name = datename;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.send_recycler_dialog, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.textView.setText(name.get(position));
        holder.imageView.setImageResource(images.get(position));
        //Glide.with(mContext).load(images.get(position)).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }
}

