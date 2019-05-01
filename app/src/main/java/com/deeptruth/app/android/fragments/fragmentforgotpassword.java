package com.deeptruth.app.android.fragments;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.fragments.registrationbasefragment;
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

public class fragmentforgotpassword extends registrationbasefragment implements View.OnClickListener {

    @BindView(R.id.tv_forgotpassword)
    customfonttextview tvforgotpassword;
    @BindView(R.id.edt_username)
    customfontedittext edtusername;
    @BindView(R.id.tv_next)
    customfonttextview tv_next;
    @BindView(R.id.layout_forgotpass)
    RelativeLayout layout_forgotpass;
    View contaionerview = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(contaionerview ==null){
            contaionerview = super.onCreateView(inflater, container, savedInstanceState);
            ButterKnife.bind(this, contaionerview);

            tv_next.setOnClickListener(this);
        }
        return contaionerview;
    }

    @Override
    public int getlayoutid() {
        return R.layout.activity_forgotpassword;
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

                                fragmentverifyuser fragverifyuser = new fragmentverifyuser();
                                fragverifyuser.setdata(config.forgotpassword);
                                getHelper().addFragment(fragverifyuser,false,true);
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
}
