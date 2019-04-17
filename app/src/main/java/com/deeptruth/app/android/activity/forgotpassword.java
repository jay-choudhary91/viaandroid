package com.deeptruth.app.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.interfaces.apiresponselistener;
import com.deeptruth.app.android.models.customedittext;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.progressdialog;
import com.deeptruth.app.android.utils.taskresult;
import com.deeptruth.app.android.utils.xdata;
import com.deeptruth.app.android.views.customfontedittext;
import com.deeptruth.app.android.views.customfonttextview;
import com.google.android.gms.common.internal.service.Common;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class forgotpassword extends registrationbaseactivity implements View.OnClickListener {

    @BindView(R.id.tv_forgotpassword)
    customfonttextview tvforgotpassword;
    @BindView(R.id.edt_username)
    customfontedittext edtusername;
    @BindView(R.id.tv_next)
    customfonttextview tv_next;
    @BindView(R.id.layout_forgotpass)
    RelativeLayout layout_forgotpass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        ButterKnife.bind(forgotpassword.this);
        tv_next.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.tv_next:
                if(edtusername.getText().toString().trim().length() > 0)
                {
                    xdata.getinstance().saveSetting(config.usernameemailaddress,edtusername.getText().toString().trim());
                    validatecallapi();
                }
                break;
        }
    }

    public void validatecallapi()
    {
        HashMap<String,String> requestparams=new HashMap<>();
        requestparams.put("type","client");
        requestparams.put("action","forgotpassword");
        requestparams.put("email",edtusername.getText().toString().trim());
        progressdialog.showwaitingdialog(forgotpassword.this);
        xapipost_send(forgotpassword.this,requestparams, new apiresponselistener() {
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

                                Intent intent=new Intent(forgotpassword.this,verifyuser.class);
                                startActivity(intent);
                            }
                            else
                            {
                                if(object.has("error"))
                                    Toast.makeText(forgotpassword.this, object.getString("error"), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(forgotpassword.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    Toast.makeText(forgotpassword.this, getResources().getString(R.string.json_parsing_failed), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
