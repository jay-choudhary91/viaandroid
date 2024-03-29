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
import android.widget.TextView;
import android.widget.Toast;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.progressdialog;
import com.deeptruth.app.android.utils.xdata;
import com.suke.widget.SwitchButton;

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
    SwitchButton togglebutton;
    @BindView(R.id.production_toogle)
    SwitchButton production_toogle;
    @BindView(R.id.dev_toogle)
    SwitchButton dev_toogle;
    @BindView(R.id.resetnotification_toogle)
    SwitchButton resetnotification_toogle;
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
    @BindView(R.id.txt_logout)
    TextView txt_logout;
    @BindView(R.id.txt_username)
    TextView txt_username;

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
            txt_logout.setOnClickListener(this);

            txt_username.setText("");

            if(gethelper().isuserlogin())
            {
                if(!xdata.getinstance().getSetting(config.clientemail).isEmpty())
                {
                    txt_logout.setText(getResources().getString(R.string.logout));
                    txt_logout.setVisibility(View.VISIBLE);
                    txt_username.setText(xdata.getinstance().getSetting(config.clientemail));
                }
            }

            title.setText(common.getapplicationname(applicationviavideocomposer.getactivity()) + "\n" +
                    applicationviavideocomposer.getactivity().getResources().getString(R.string.appversion) + common.getapplicationversion(applicationviavideocomposer.getactivity()));

            if(xdata.getinstance().getSetting(config.enableintroscreen).isEmpty() ||
                    xdata.getinstance().getSetting(config.enableintroscreen).equalsIgnoreCase("1"))
            {
                togglebutton.setChecked(true);

            }else{
                togglebutton.setChecked(false);
            }

            if((xdata.getinstance().getSetting(config.enableintroscreen).isEmpty() ||
                    xdata.getinstance().getSetting(config.enableintroscreen).equalsIgnoreCase("1")) ||
                    (xdata.getinstance().getSetting(config.enableplubishnotification).isEmpty() ||
                       xdata.getinstance().getSetting(config.enableplubishnotification).equalsIgnoreCase("0") ) ||
                    (xdata.getinstance().getSetting(config.enablesendnotification).isEmpty() ||
                            xdata.getinstance().getSetting(config.enablesendnotification).equalsIgnoreCase("0")) ||
                    (xdata.getinstance().getSetting(config.enableexportnotification).isEmpty() ||
                            xdata.getinstance().getSetting(config.enableexportnotification).equalsIgnoreCase("0")))

            {
                resetnotification_toogle.setChecked(true);

            }else{
                resetnotification_toogle.setChecked(false);
            }

            navigationbarheight =  common.getnavigationbarheight();
            setlayoutmargin();


            if(xdata.getinstance().getSetting(config.enabledevelopment).isEmpty() ||
                    xdata.getinstance().getSetting(config.enabledevelopment).equalsIgnoreCase("1"))
            {
                dev_toogle.setChecked(true);
                production_toogle.setChecked(false);
                common.switchtodevelopmentconnection(true);
            }
            else if(xdata.getinstance().getSetting(config.enableproduction).isEmpty() ||
                xdata.getinstance().getSetting(config.enableproduction).equalsIgnoreCase("1"))
            {
                production_toogle.setChecked(true);
                dev_toogle.setChecked(false);
                common.switchtodevelopmentconnection(false);
            }
            else
            {
                dev_toogle.setChecked(false);
                production_toogle.setChecked(true);
                common.switchtodevelopmentconnection(true);
            }

            togglebutton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                    if(isChecked)
                        xdata.getinstance().saveSetting(config.enableintroscreen,"1");
                    else
                        xdata.getinstance().saveSetting(config.enableintroscreen,"0");
                }
            });

            resetnotification_toogle.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                    if(isChecked){
                        xdata.getinstance().saveSetting(config.enableintroscreen,"1");
                        xdata.getinstance().saveSetting(config.enableexportnotification,"0");
                        xdata.getinstance().saveSetting(config.enablesendnotification,"0");
                        xdata.getinstance().saveSetting(config.enableplubishnotification,"0");
                    }
                    else{
                        xdata.getinstance().saveSetting(config.enableintroscreen,"0");
                        xdata.getinstance().saveSetting(config.enableexportnotification,"1");
                        xdata.getinstance().saveSetting(config.enablesendnotification,"1");
                        xdata.getinstance().saveSetting(config.enableplubishnotification,"1");
                     //   xdata.getinstance().saveSetting(config.enablenotification,"0");
                    }

                }
            });

            production_toogle.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                    if(isChecked){
                        xdata.getinstance().saveSetting(config.enableproduction,"1");
                        xdata.getinstance().saveSetting(config.enabledevelopment,"0");
                        common.switchtodevelopmentconnection(false);
                    }else{
                        xdata.getinstance().saveSetting(config.enableproduction,"0");
                        xdata.getinstance().saveSetting(config.enabledevelopment,"1");
                        common.switchtodevelopmentconnection(true);
                    }
                    dev_toogle.setChecked(!isChecked);
                }
            });

            dev_toogle.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                    if(isChecked){
                        xdata.getinstance().saveSetting(config.enableproduction,"0");
                        xdata.getinstance().saveSetting(config.enabledevelopment,"1");
                        common.switchtodevelopmentconnection(true);
                    }else{
                        xdata.getinstance().saveSetting(config.enableproduction,"1");
                        xdata.getinstance().saveSetting(config.enabledevelopment,"0");
                        common.switchtodevelopmentconnection(false);
                    }
                    production_toogle.setChecked(!isChecked);
                }
            });

            img_arrow_back.setOnClickListener(this);
            /*webview.getSettings().setJavaScriptEnabled(true);
            webview.getSettings().setSupportZoom(true);       //Zoom Control on web (You don't need this
            webview.getSettings().setBuiltInZoomControls(true); //Enable Multitouch if supported by ROM
            webview.loadUrl(config.settingpageurl);
            webview.setWebViewClient(new mywebview());*/
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

            case R.id.txt_logout:
                xdata.getinstance().saveSetting(config.isuserlogin,"");
                Toast.makeText(applicationviavideocomposer.getactivity(),"Logout Successful",Toast.LENGTH_SHORT).show();
                txt_username.setText("");
                txt_logout.setText("");
                txt_username.setVisibility(View.INVISIBLE);
                txt_logout.setVisibility(View.INVISIBLE);
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
