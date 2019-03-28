package com.deeptruth.app.android.fragments;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

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
    @BindView(R.id.ll_rootlayout)
    LinearLayout llrootlayout;
    int navigationbarheight = 0;

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

            if(xdata.getinstance().getSetting("enableintroscreen").isEmpty() || xdata.getinstance().getSetting("enableintroscreen").equalsIgnoreCase("yes")){
                togglebutton.setEnable(true);
                togglebutton.setVisibility(View.VISIBLE);

            }else{
                togglebutton.setEnable(false);
                togglebutton.setVisibility(View.VISIBLE);
            }

          navigationbarheight =  common.getnavigationbarheight();
          setlayoutmargin();

            togglebutton.setToggleListener(new OnToggleListener() {
                @Override
                public void onToggle(boolean isChecked) {
                    if(isChecked){

                        xdata.getinstance().saveSetting("enableintroscreen","yes");


                    }else{

                        xdata.getinstance().saveSetting("enableintroscreen","no");

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
