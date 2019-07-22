package com.deeptruth.app.android.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.deeptruth.app.android.R;
import com.deeptruth.app.android.interfaces.adapteritemclick;
import com.deeptruth.app.android.models.sharemedia;
import com.deeptruth.app.android.applicationviavideocomposer;

import java.util.ArrayList;

public class adaptersenddialog extends RecyclerView.Adapter<adaptersenddialog.ViewHolder> {


    ArrayList<sharemedia> sharemedia=new ArrayList<>();
    private Context mContext;
    adapteritemclick mitemclick;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;
        CardView cardview_item;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.img_send_dialog);
            textView = (TextView) itemView.findViewById(R.id.txt_send_dialog);
            cardview_item = (CardView) itemView.findViewById(R.id.cardview_item);
        }
    }

    public adaptersenddialog(Context mContext, ArrayList<sharemedia> sharemedia, adapteritemclick mitemclick) {
        this.mContext = mContext;
        this.sharemedia = sharemedia;
        this.mitemclick = mitemclick;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.send_recycler_dialog, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.textView.setText(sharemedia.get(position).getMedianame());
        holder.imageView.setImageResource(sharemedia.get(position).getMediaicon());
        //Glide.with(mContext).load(images.get(position)).into(holder.imageView);

        holder.cardview_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mitemclick != null)
                    mitemclick.onItemClicked(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sharemedia.size();
    }
}

