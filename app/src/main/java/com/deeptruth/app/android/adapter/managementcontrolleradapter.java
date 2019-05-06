package com.deeptruth.app.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.interfaces.appmanagementitemclick;
import com.deeptruth.app.android.models.managementcontroller;

import java.util.ArrayList;
import java.util.List;

public class managementcontrolleradapter extends RecyclerView.Adapter<managementcontrolleradapter.MyViewHolder> {


    List<managementcontroller> mControllersList = new ArrayList<>();
    Context context;
    boolean isAddSetting;
    appmanagementitemclick mItemClick;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName,tvAddSetting;
        public LinearLayout root_view;


        public MyViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tv_Name);
            tvAddSetting = (TextView) view.findViewById(R.id.tv_addSetting);
            root_view = (LinearLayout) view.findViewById(R.id.root_view);


        }
    }

    public managementcontrolleradapter(Context context, List<managementcontroller> mControllersList, appmanagementitemclick mItemClick){
        this.context = context;
        this.mControllersList = mControllersList;
        this.mItemClick = mItemClick;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_management_controller, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.tvName.setText(mControllersList.get(position).getTxtName().toString());

        if(mControllersList.get(position).isAddString()){
           // holder.tvAddSetting.setVisibility(View.VISIBLE);

            //holder.tvAddSetting.setText(context.getResources().getText(R.string.add_setting));
        }

        holder.root_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemClick.onItemClicked(mControllersList.get(position));
            }
        });

        holder.tvAddSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemClick.onAddSettingClicked(mControllersList.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return mControllersList.size();
    }

}
