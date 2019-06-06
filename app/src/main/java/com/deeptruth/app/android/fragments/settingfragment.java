package com.deeptruth.app.android.fragments;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akash.RevealSwitch;
import com.akash.revealswitch.OnToggleListener;
import com.deeptruth.app.android.R;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.progressdialog;
import com.deeptruth.app.android.utils.xdata;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class settingfragment extends basefragment implements View.OnClickListener{

    View rootview;
    @BindView(R.id.img_arrow_back)
    ImageView img_arrow_back;
    @BindView(R.id.layout_mediatype)
    RelativeLayout layout_mediatype;
    @BindView(R.id.setting_webview)
    WebView webview;
    @BindView(R.id.togglebutton)
    RevealSwitch togglebutton;
    @BindView(R.id.production_dev_toogle)
    RevealSwitch production_dev_toogle;
    @BindView(R.id.ll_rootlayout)
    LinearLayout llrootlayout;
    int navigationbarheight = 0;
    @BindView(R.id.apptitle)
    TextView title;
    @BindView(R.id.txt_help)
    TextView txt_help;
    @BindView(R.id.txt_privacy)
    TextView  txt_privacy;
    @BindView(R.id.txt_upgrade)
    TextView txt_upgrade;

    public settingfragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        if (rootview == null)
        {
          rootview = super.onCreateView(inflater, container, savedInstanceState);
          ButterKnife.bind(this, rootview);
          gethelper().drawerenabledisable(false);

            txt_upgrade.setText(getResources().getString(R.string.upgrade));
            txt_privacy.setText(getResources().getString(R.string.privacy));
            txt_help.setText(getResources().getString(R.string.faq));


            title.setText(common.getapplicationname(getActivity()) + "\n" +getResources().getString(R.string.appversion) +common.getapplicationversion(getActivity()));

            if(xdata.getinstance().getSetting(config.enableintroscreen).isEmpty() || xdata.getinstance().getSetting(config.enableintroscreen).equalsIgnoreCase("yes")){
                togglebutton.setEnable(true);
                togglebutton.setVisibility(View.VISIBLE);

            }else{
                togglebutton.setEnable(false);
                togglebutton.setVisibility(View.VISIBLE);
            }

          navigationbarheight =  common.getnavigationbarheight();
          setlayoutmargin();

            if(xdata.getinstance().getSetting(config.enableproductionanddev).isEmpty() || xdata.getinstance().getSetting(config.enableproductionanddev).equalsIgnoreCase("yes")){
                production_dev_toogle.setEnable(true);
                production_dev_toogle.setVisibility(View.VISIBLE);

            }else{
                production_dev_toogle.setEnable(false);
                production_dev_toogle.setVisibility(View.VISIBLE);
            }

            togglebutton.setToggleListener(new OnToggleListener() {
                @Override
                public void onToggle(boolean isChecked) {
                    if(isChecked){
                        xdata.getinstance().saveSetting(config.enableintroscreen,"yes");
                    }else{
                        xdata.getinstance().saveSetting(config.enableintroscreen,"no");
                    }
                 }
            });

            production_dev_toogle.setToggleListener(new OnToggleListener() {
                @Override
                public void onToggle(boolean isChecked) {
                    if(isChecked){
                        xdata.getinstance().saveSetting(config.enableproductionanddev,"yes");
                    }else{
                        xdata.getinstance().saveSetting(config.enableproductionanddev,"no");
                    }
                }
            });

          img_arrow_back.setOnClickListener(this);
          webview.getSettings().setJavaScriptEnabled(true);
          webview.getSettings().setSupportZoom(true);       //Zoom Control on web (You don't need this
          webview.getSettings().setBuiltInZoomControls(true); //Enable Multitouch if supported by ROM
          webview.loadUrl(config.settingpageurl);
          webview.setWebViewClient(new mywebview());
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
        switch (btnid)
        {
            case R.id.img_backarrow:
                gethelper().onBack();
                break;
        }
    }

    public class mywebview extends WebViewClient
    {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            progressdialog.showwaitingdialog(applicationviavideocomposer.getactivity());
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            view.loadUrl(url);
            return true;

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
            progressdialog.dismisswaitdialog();
        }

    }

    public void setlayoutmargin(){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,0,0,navigationbarheight);
        llrootlayout.setLayoutParams(params);
        llrootlayout.requestLayout();
    }
}
