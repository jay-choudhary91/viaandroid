package com.cryptoserver.composer.fragments;


import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.cryptoserver.composer.R;
import com.cryptoserver.composer.utils.common;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class settingfragment extends basefragment implements View.OnClickListener{

    View rootview;
    public settingfragment() {
        // Required empty public constructor
    }

    TextView txt_upgrade,txt_privacy,txt_help;
    @BindView(R.id.img_arrow_back)
    ImageView img_arrow_back;
    LinearLayout layout_upgrade,layout_privacy,layout_help;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (rootview == null) {
            rootview = super.onCreateView(inflater, container, savedInstanceState);
            ButterKnife.bind(this, rootview);


          txt_help=rootview.findViewById(R.id.txt_help);
          txt_privacy=rootview.findViewById(R.id.txt_privacy);
          txt_upgrade=rootview.findViewById(R.id.txt_upgrade);

          txt_upgrade.setText(getResources().getString(R.string.upgrade));
          txt_privacy.setText(getResources().getString(R.string.privacy));
          txt_help.setText(getResources().getString(R.string.faq));
            img_arrow_back.setOnClickListener(this);


         //   gethelper().updateheader("Settings");
        }
        return rootview;
    }

    @Override
    public int getlayoutid() {
        return R.layout.fragment_settingfragment;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
          case R.id.img_arrow_back:
            gethelper().onBack();
            break;
        }

    }
    @Override
    public void onHeaderBtnClick(int btnid) {
        super.onHeaderBtnClick(btnid);
        switch (btnid){

            case R.id.img_backarrow:
                gethelper().onBack();
                break;
        }
    }

}
