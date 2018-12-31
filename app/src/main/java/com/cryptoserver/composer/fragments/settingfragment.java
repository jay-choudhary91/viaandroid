package com.cryptoserver.composer.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cryptoserver.composer.R;
import com.cryptoserver.composer.utils.common;

import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class settingfragment extends basefragment {

    View rootview;
    public settingfragment() {
        // Required empty public constructor
    }

    TextView txt_upgrade,txt_privacy,txt_help;
    LinearLayout layout_upgrade,layout_privacy,layout_help;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (rootview == null) {
            rootview = super.onCreateView(inflater, container, savedInstanceState);
            ButterKnife.bind(this, rootview);

          layout_help=rootview.findViewById(R.id.help_layout);
          layout_privacy=rootview.findViewById(R.id.privacy_layout);
          layout_upgrade=rootview.findViewById(R.id.upgrade_layout);
          txt_help=rootview.findViewById(R.id.txt_help);
          txt_privacy=rootview.findViewById(R.id.txt_privacy);
          txt_upgrade=rootview.findViewById(R.id.txt_upgrade);

          txt_upgrade.setText(getResources().getString(R.string.upgrade));
          txt_privacy.setText(getResources().getString(R.string.privacy));
          txt_help.setText(getResources().getString(R.string.faq));

            common.changeFocusStyle(layout_help,getResources().getColor(R.color.actionbar_solid_normal_transparent),40);
            common.changeFocusStyle(layout_privacy,getResources().getColor(R.color.actionbar_solid_normal_transparent),40);
            common.changeFocusStyle(layout_upgrade,getResources().getColor(R.color.actionbar_solid_normal_transparent),40);
            gethelper().updateheader("Settings");
        }
        return rootview;
    }

    @Override
    public int getlayoutid() {
        return R.layout.fragment_settingfragment;
    }

}
