package com.cryptoserver.composer.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cryptoserver.composer.R;
import com.cryptoserver.composer.adapter.adaptervideolist;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by devesh on 6/8/18.
 */

public class fragmentvideolist extends basefragment {

    @BindView(R.id.rv_videolist)
    RecyclerView recyrviewvideolist;

    View rootview = null;

    ArrayList<String> arrayvideolist = new ArrayList<String>();

    @Override
    public int getlayoutid() {
        return R.layout.fragment_videolist;
    }


    @Override
    public void initviews(View parent, Bundle savedInstanceState) {
        super.initviews(parent, savedInstanceState);
        ButterKnife.bind(this,parent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(rootview == null) {
            rootview = super.onCreateView(inflater, container, savedInstanceState);
            ButterKnife.bind(this,rootview);

            if(arrayvideolist != null)
                arrayvideolist.clear();

            File mydir = getActivity().getDir("videos", Context.MODE_PRIVATE);
            File lister = mydir.getAbsoluteFile();
            Log.e("app videos",""+lister.list().length);

            for (String list : lister.list())
            {
                Log.e("app videos",""+lister.list().length);
            }

            //arrayvideolist.add("video 1");

            adaptervideolist adptervideolist1 = new adaptervideolist(getActivity(),arrayvideolist);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            recyrviewvideolist.setLayoutManager(mLayoutManager);
            recyrviewvideolist.setAdapter(adptervideolist1);
        }

      //  RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.rv_videolist);


        /*DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyrviewvideolist.getContext(),
                mLayoutManager.getOrientation());
        recyrviewvideolist.addItemDecoration(dividerItemDecoration);*/

        return rootview;


    }


}
