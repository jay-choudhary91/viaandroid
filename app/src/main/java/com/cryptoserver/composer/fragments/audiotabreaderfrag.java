package com.cryptoserver.composer.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cryptoserver.composer.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class audiotabreaderfrag extends basefragment {


    public audiotabreaderfrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_audiotabreaderfrag, container, false);
    }

    @Override
    public int getlayoutid() {
        return R.layout.fragment_audiotabreaderfrag;
    }

}
