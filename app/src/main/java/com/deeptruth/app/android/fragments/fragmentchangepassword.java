package com.deeptruth.app.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.fragments.registrationbasefragment;
import com.deeptruth.app.android.interfaces.apiresponselistener;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.progressdialog;
import com.deeptruth.app.android.utils.taskresult;
import com.deeptruth.app.android.utils.xdata;
import com.goodiebag.pinview.Pinview;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class fragmentchangepassword extends registrationbasefragment implements View.OnClickListener{


    @BindView(R.id.edt_password)
    EditText edt_password;
    @BindView(R.id.edt_confirmpassword)
    EditText edt_confirmpassword;
    @BindView(R.id.tv_submit)
    TextView tv_submit;

    View contaionerview = null;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(contaionerview ==null){
            contaionerview = super.onCreateView(inflater, container, savedInstanceState);
            ButterKnife.bind(this, contaionerview);

            tv_submit.setOnClickListener(this);
        }
        return contaionerview;
    }

    @Override
    public int getlayoutid() {
        return R.layout.activity_changepassword;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.tv_submit:
                validatechangepassword();
                break;
        }
    }

    public void validatechangepassword()
    {
        HashMap<String,String> requestparams=new HashMap<>();
        requestparams.put("type","client");
        requestparams.put("action","resetpassword");
        requestparams.put("email",xdata.getinstance().getSetting(config.usernameemailaddress));
        requestparams.put("password",""+edt_password.getText().toString().trim());
        requestparams.put("confirmpassword",""+edt_confirmpassword.getText().toString().trim());
        requestparams.put("reset_authtoken",""+xdata.getinstance().getSetting(config.reset_authtoken).toString().trim());
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
                            if(object.getString("success").equalsIgnoreCase("true"))
                            {
                                if(object.has(config.clientid))
                                    xdata.getinstance().saveSetting(config.clientid,object.getString(config.clientid));

                               /* Intent i = new Intent(fragmentchangepassword.this, fragmentsigninactivity.class);
                                 getHelper().hidekeyboard();
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                finish();*/
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
