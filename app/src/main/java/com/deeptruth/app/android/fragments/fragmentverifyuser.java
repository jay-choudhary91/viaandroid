package com.deeptruth.app.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.interfaces.apiresponselistener;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.progressdialog;
import com.deeptruth.app.android.utils.taskresult;
import com.deeptruth.app.android.utils.xdata;
import com.deeptruth.app.android.views.customfonttextview;
import com.goodiebag.pinview.Pinview;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class fragmentverifyuser extends registrationbasefragment implements View.OnClickListener {

    @BindView(R.id.pinview)
    Pinview pinview;
    @BindView(R.id.tv_complete)
    customfonttextview tvcomplete;
    @BindView(R.id.tv_cancel)
    customfonttextview tvcancel;
    @BindView(R.id.rootview)
    ScrollView rootview;
    @BindView(R.id.layout_bottom)
    LinearLayout layout_bottom;
    @BindView(R.id.layout_top)
    LinearLayout layout_top;
    int topviewheight,bottomviewheight,rootviewheight;
    View contaionerview = null;
    String forgotpassword = "";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(contaionerview ==null){
            contaionerview = super.onCreateView(inflater, container, savedInstanceState);
            ButterKnife.bind(this, contaionerview);

            rootview.post(new Runnable() {
                @Override
                public void run() {
                    rootviewheight = rootview.getHeight();
                    topviewheight = ((rootviewheight *55)/100);
                    layout_top.getLayoutParams().height = topviewheight;
                    layout_top.setVisibility(View.VISIBLE);
                    layout_top.requestLayout();

                    bottomviewheight = (rootviewheight -topviewheight);
                    layout_bottom.getLayoutParams().height = bottomviewheight;
                    layout_bottom.setVisibility(View.VISIBLE);
                    layout_bottom.requestLayout();
                }
            });


            tvcomplete.setOnClickListener(this);
            tvcancel.setOnClickListener(this);
            pinview.setPinViewEventListener(new Pinview.PinViewEventListener() {
                @Override
                public void onDataEntered(Pinview pinview, boolean fromUser) {
                   getHelper().hidekeyboard();
                }
            });
        }
        return contaionerview;
    }

    @Override
    public int getlayoutid() {
        return R.layout.activity_verifyuser;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.tv_complete:
                validatecallapi();
                break;

            case R.id.tv_cancel:
                gotologin();
                break;

        }
    }

    public void validatecallapi()
    {
        String value=pinview.getValue();
        String clientid= xdata.getinstance().getSetting(config.clientid);
       // String activityname = getgetIntent().getExtras().getString("activityname");

        HashMap<String,String> requestparams=new HashMap<>();
        requestparams.put("type","client");
        requestparams.put("action","verify");
        if(!forgotpassword.isEmpty() && forgotpassword.equalsIgnoreCase(config.forgotpassword))
                requestparams.put("action","verifyforgotten");

        requestparams.put("clientid",clientid);
        requestparams.put("code",value);
        progressdialog.showwaitingdialog(getActivity());
        getHelper().xapipost_send(getActivity(),requestparams, new apiresponselistener() {
            @Override
            public void onResponse(taskresult response) {
                progressdialog.dismisswaitdialog();
                if(response.isSuccess())
                {
                    try {
                        JSONObject object=new JSONObject(response.getData().toString());
                        if(object.has("success"))
                        {
                            if(object.getString("success").equalsIgnoreCase("true") || object.getString("success").equalsIgnoreCase("1"))
                            {
                                if(object.has(config.clientid))
                                    xdata.getinstance().saveSetting(config.clientid,object.getString(config.clientid));

                                xdata.getinstance().saveSetting(config.reset_authtoken,"");

                                if(object.has(config.reset_authtoken))
                                    xdata.getinstance().saveSetting(config.reset_authtoken,object.getString(config.reset_authtoken));

                                navigateuser();
                            }
                            else
                            {
                                if(object.has("error"))
                                    Toast.makeText(getActivity(), object.getString("error"), Toast.LENGTH_SHORT).show();
                            }
                        }
                        if(object.has("errors"))
                        {
                            JSONArray errorarray=object.getJSONArray("errors");
                            String error="";
                            for(int i=0;i<errorarray.length();i++)
                            {
                                if(error.trim().isEmpty())
                                {
                                    error=error+errorarray.get(i).toString();
                                }
                                else
                                {
                                    error=error+"\n"+errorarray.get(i).toString();
                                }
                            }
                            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    Toast.makeText(getActivity(), getResources().getString(R.string.json_parsing_failed), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void navigateuser()
    {
        if(xdata.getinstance().getSetting(config.reset_authtoken).toString().trim().length() > 0)
        {

            fragmentchangepassword fragchangepassword = new fragmentchangepassword();
            getHelper().addFragment(fragchangepassword,false,true);
        }
        else
        {
            gotologin();
        }
    }

    public void setdata(String forgotpassword){
        this.forgotpassword = forgotpassword;

    }

    public void gotologin(){
        fragmentsignin fragverifyuser = new fragmentsignin();
        getHelper().addFragment(fragverifyuser,true,true);
    }
}
