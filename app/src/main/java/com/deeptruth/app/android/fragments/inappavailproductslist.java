package com.deeptruth.app.android.fragments;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.deeptruth.app.android.R;

import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;


public class inappavailproductslist extends basefragment {

    @BindView(R.id.rvContacts)
    RecyclerView recyviewItem;
    @BindView(R.id.txt_no_record)
    TextView txt_no_record;

    @Override
    public void initviews(View parent, Bundle savedInstanceState) {
        super.initviews(parent, savedInstanceState);

        txt_no_record.setText(getActivity().getResources().getString(R.string.no_record));

        ButterKnife.bind(this, parent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public int getlayoutid() {
        return R.layout.fragment_purchase_list;
    }
}
