package com.deeptruth.app.android.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.interfaces.adapteritemclick;
import com.deeptruth.app.android.models.trimbuttontext;

import java.util.ArrayList;

public class adapterbuttontrim extends RecyclerView.Adapter<adapterbuttontrim.ViewHolder> {


    ArrayList<trimbuttontext> trimbuttontexts=new ArrayList<>();
    private Context mContext;
    adapteritemclick mitemclick;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.tv_purchase1);
        }
    }

    public adapterbuttontrim(Context mContext, ArrayList<trimbuttontext> trimbuttontexts, adapteritemclick mitemclick) {
        this.mContext = mContext;
        this.trimbuttontexts = trimbuttontexts;
        this.mitemclick = mitemclick;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trim_recycler_button, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.textView.setText(trimbuttontexts.get(position).getButtontext());
        //Glide.with(mContext).load(images.get(position)).into(holder.imageView);

        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mitemclick != null)
                    mitemclick.onItemClicked(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return trimbuttontexts.size();
    }
}

