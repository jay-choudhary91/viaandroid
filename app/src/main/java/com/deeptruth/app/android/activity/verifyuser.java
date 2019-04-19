package com.deeptruth.app.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class verifyuser extends registrationbaseactivity implements View.OnClickListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifyuser);
        ButterKnife.bind(verifyuser.this);
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
                hidekeyboard();
            }
        });
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
        String activityname = getIntent().getExtras().getString("activityname");

        HashMap<String,String> requestparams=new HashMap<>();
        requestparams.put("type","client");
        if(activityname.equals(config.createaccount)){
            requestparams.put("action","verify");
        }else{
            requestparams.put("action","verifyforgotten");
        }
        requestparams.put("clientid",clientid);
        requestparams.put("code",value);
        progressdialog.showwaitingdialog(verifyuser.this);
        xapipost_send(verifyuser.this,requestparams, new apiresponselistener() {
            @Override
            public void onResponse(taskresult response) {
                progressdialog.dismisswaitdialog();
                if(response.isSuccess())
                {
                    try {
                        JSONObject object=new JSONObject(response.getData().toString());
                        if(object.has("success"))
                        {
                            if(object.getString("success").equalsIgnoreCase("true"))
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
                                    Toast.makeText(verifyuser.this, object.getString("error"), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(verifyuser.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    Toast.makeText(verifyuser.this, getResources().getString(R.string.json_parsing_failed), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void navigateuser()
    {
        if(xdata.getinstance().getSetting(config.reset_authtoken).toString().trim().length() > 0)
        {
            Intent i = new Intent(verifyuser.this, changepassword.class);
            hidekeyboard();
            startActivity(i);
        }
        else
        {
            gotologin();
        }
    }

    public void gotologin(){
        Intent i = new Intent(verifyuser.this, signinactivity.class);
        hidekeyboard();
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
}
