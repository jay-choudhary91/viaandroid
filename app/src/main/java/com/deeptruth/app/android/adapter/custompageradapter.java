package com.deeptruth.app.android.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.models.frame;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 30/7/18.
 */

public class custompageradapter extends PagerAdapter {

    List<frame> mItemList = new ArrayList<>();
    Context mContext;
    int screenWidth;
    LayoutInflater mLayoutInflater;

    public custompageradapter(Context context, List<frame> mItemList, int screenWidth) {
        mContext = context;
        this.mItemList = mItemList;
        this.screenWidth = screenWidth;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mItemList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.row_frame, container, false);
        ImageView img_item = (ImageView) itemView.findViewById(R.id.img_item);
        if(mItemList.get(position).getBitmap() != null)
            img_item.setImageBitmap(mItemList.get(position).getBitmap());

        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

    @Override
    public float getPageWidth(int position) {
        return (0.2f);
    }
}