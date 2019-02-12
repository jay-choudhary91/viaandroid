package com.deeptruth.app.android.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.utils.xdata;

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

    @BindView(R.id.layout_mediatype)
    RelativeLayout layout_mediatype;
    @BindView(R.id.setting_webview)
    WebView webview;
    String url = "http://console.dev.crypto-servers.com/inapp-settings.php";
    LinearLayout layout_upgrade,layout_privacy,layout_help;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (rootview == null) {
            rootview = super.onCreateView(inflater, container, savedInstanceState);
            ButterKnife.bind(this, rootview);
            setheadermargine();


          txt_help=rootview.findViewById(R.id.txt_help);
          txt_privacy=rootview.findViewById(R.id.txt_privacy);
          txt_upgrade=rootview.findViewById(R.id.txt_upgrade);

          txt_upgrade.setText(getResources().getString(R.string.upgrade));
          txt_privacy.setText(getResources().getString(R.string.privacy));
          txt_help.setText(getResources().getString(R.string.faq));
            img_arrow_back.setOnClickListener(this);

            webview.loadUrl(url);

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

    public void setheadermargine(){
        LinearLayout.LayoutParams params  = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,Integer.parseInt(xdata.getinstance().getSetting("statusbarheight")),0,0);
        layout_mediatype.setLayoutParams(params);
    }

}
