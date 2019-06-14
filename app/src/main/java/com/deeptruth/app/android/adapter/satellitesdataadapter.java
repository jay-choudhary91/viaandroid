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
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.interfaces.adapteritemclick;
import com.deeptruth.app.android.models.folder;
import com.deeptruth.app.android.models.satellites;

import java.io.File;
import java.util.ArrayList;

public class satellitesdataadapter extends RecyclerView.Adapter<satellitesdataadapter.ViewHolder> {

    ArrayList<satellites> dataarrayList;
    private Context mContext;
    boolean isfrommetaordrawer =false ;

    public satellitesdataadapter(Context mContext, ArrayList<satellites> dataarrayList , boolean isfrommetaordrawer ) {
        this.mContext = mContext;
        this.dataarrayList = dataarrayList;
        this.isfrommetaordrawer = isfrommetaordrawer;
    }

    @NonNull
    @Override
    public satellitesdataadapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_satellite_data, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull satellitesdataadapter.ViewHolder holder, final int position) {

        if(isfrommetaordrawer){
            holder.txt_satellite_number.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
            holder.txt_satellite_altitude.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        }else{
            holder.txt_satellite_number.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
            holder.txt_satellite_altitude.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        }
        holder.txt_satellite_number.setText("#"+dataarrayList.get(position).getNumber()+": ");
        holder.txt_satellite_altitude.setText(dataarrayList.get(position).getAltitude()+" km");
    }

    @Override
    public int getItemCount() {
        return dataarrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_satellite_number,txt_satellite_altitude;
        public ViewHolder(View itemView) {
            super(itemView);
            txt_satellite_number = (TextView) itemView.findViewById(R.id.txt_satellite_number);
            txt_satellite_altitude = (TextView) itemView.findViewById(R.id.txt_satellite_altitude);
        }
    }
}
