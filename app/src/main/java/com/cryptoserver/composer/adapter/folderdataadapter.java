package com.cryptoserver.composer.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cryptoserver.composer.R;
import com.cryptoserver.composer.interfaces.adapteritemclick;
import com.cryptoserver.composer.models.folder;
import com.cryptoserver.composer.models.graphicalmodel;
import com.cryptoserver.composer.utils.common;

import java.util.ArrayList;

public class folderdataadapter extends RecyclerView.Adapter<folderdataadapter.ViewHolder> {

    ArrayList<folder> dataarrayList;
    private Context mContext;
    adapteritemclick mItemClick;

    public folderdataadapter(Context mContext,ArrayList<folder> dataarrayList,adapteritemclick adapterclick ) {
        this.mContext = mContext;
        this.dataarrayList = dataarrayList;
        this.mItemClick = adapterclick;
    }

    @NonNull
    @Override
    public folderdataadapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_my_folder, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull folderdataadapter.ViewHolder holder, final int position) {
        final folder myfolder = dataarrayList.get(position);
        holder.tv_foldername.setText(myfolder.getFoldername());
        holder.tv_mediacount.setText(""+myfolder.getFilecount());

        if(! myfolder.isIsplus())
        {
            holder.img_mediathumbnail.setImageResource(R.mipmap.app_icon);
            holder.img_mediathumbnail.setVisibility(View.VISIBLE);
            holder.img_plus_icon.setVisibility(View.GONE);
        }
        else
        {
            holder.img_mediathumbnail.setImageResource(R.drawable.plusimage);
            holder.tv_foldername.setText("");
            holder.tv_mediacount.setText("");
            holder.img_mediathumbnail.setVisibility(View.GONE);
            holder.img_plus_icon.setVisibility(View.VISIBLE);
        }

        holder.img_plus_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myfolder.isIsplus())
                {
                    if(mItemClick != null)
                        mItemClick.onItemClicked(myfolder,1);   // Add item
                }
            }
        });

        holder.img_mediathumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(! myfolder.isIsplus() && (! myfolder.isIsallfolder()))
                {
                    if(mItemClick != null)
                        mItemClick.onItemClicked(myfolder,3);  // Simple click
                }
            }
        });

        holder.img_mediathumbnail.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(! myfolder.isIsplus() && (! myfolder.isIsallfolder()))
                {
                    dataarrayList.remove(position);
                    if(mItemClick != null)
                        mItemClick.onItemClicked(myfolder,4);   // Long press delete action
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataarrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_foldername,tv_mediacount;
        ImageView img_mediathumbnail,img_plus_icon;
        LinearLayout layout_root;
        public ViewHolder(View itemView) {
            super(itemView);
            img_mediathumbnail = (ImageView) itemView.findViewById(R.id.img_mediathumbnail);
            img_plus_icon = (ImageView) itemView.findViewById(R.id.img_plus_icon);
            tv_foldername = (TextView) itemView.findViewById(R.id.tv_foldername);
            tv_mediacount = (TextView) itemView.findViewById(R.id.tv_mediacount);
            layout_root = (LinearLayout) itemView.findViewById(R.id.layout_root);
        }
    }
}
