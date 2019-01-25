package com.deeptruth.app.android.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.deeptruth.app.android.R;
import com.deeptruth.app.android.interfaces.adapteritemclick;
import com.deeptruth.app.android.models.folder;

import java.io.File;
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

        holder.layout_image_container.setBackgroundColor(mContext.getResources().getColor(R.color.grey_x));

        if(! myfolder.isIsplus())
        {
            holder.img_mediathumbnail.setImageResource(R.drawable.icon_folder);
            holder.img_mediathumbnail.setVisibility(View.VISIBLE);
            holder.img_plus_icon.setVisibility(View.GONE);

            if(! myfolder.getThumbnailurl().trim().isEmpty())
            {
                if(new File(myfolder.getThumbnailurl()).exists())
                {
                    Uri uri = Uri.fromFile(new File(myfolder.getThumbnailurl().trim()));
                    Glide.with(mContext).
                            load(uri).
                            thumbnail(0.1f).
                            into(holder.img_mediathumbnail);

                    holder.layout_image_container.setBackgroundColor(mContext.getResources().getColor(R.color.dark_blue_solid));
                }

            }
            else
            {
                Glide.with(mContext).
                        load(R.drawable.icon_folder).
                        thumbnail(0.1f).
                        into(holder.img_mediathumbnail);
            }

        }
        else
        {
            Glide.with(mContext).
                    load(R.drawable.plusimage).
                    thumbnail(0.1f).
                    into(holder.img_plus_icon);

            //holder.img_mediathumbnail.setImageResource(R.drawable.plusimage);
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
        LinearLayout layout_root,layout_image_container;
        public ViewHolder(View itemView) {
            super(itemView);
            img_mediathumbnail = (ImageView) itemView.findViewById(R.id.img_mediathumbnail);
            img_plus_icon = (ImageView) itemView.findViewById(R.id.img_plus_icon);
            tv_foldername = (TextView) itemView.findViewById(R.id.tv_foldername);
            tv_mediacount = (TextView) itemView.findViewById(R.id.tv_mediacount);
            layout_root = (LinearLayout) itemView.findViewById(R.id.layout_root);
            layout_image_container = (LinearLayout) itemView.findViewById(R.id.layout_image_container);
        }
    }
}
