package com.deeptruth.app.android.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.deeptruth.app.android.fragments.inappavailproductslist;
import com.deeptruth.app.android.fragments.inapppurchasedproductslist;


public class inapppageradapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public inapppageradapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                inapppurchasedproductslist tab1 = new inapppurchasedproductslist();
                return tab1;
            case 1:
                inappavailproductslist tab2 = new inappavailproductslist();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}