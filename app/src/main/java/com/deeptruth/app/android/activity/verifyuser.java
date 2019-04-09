package com.deeptruth.app.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.interfaces.apiresponselistener;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifyuser);
        ButterKnife.bind(verifyuser.this);

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
                gotologin();
                break;

            case R.id.tv_cancel:
                gotologin();
                break;

        }
    }

    public void gotologin(){

        String value=pinview.getValue();
        String clientid= xdata.getinstance().getSetting("clientid");

        HashMap<String,String> requestparams=new HashMap<>();
        requestparams.put("type","client");
        requestparams.put("action","verify");
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
                                if(object.has("clientid"))
                                    xdata.getinstance().saveSetting("clientid",object.getString("clientid"));

                                //Toast.makeText(createaccount.this, "Auth success", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(verifyuser.this, signin.class);
                                hidekeyboard();
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                finish();
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
                    Toast.makeText(verifyuser.this, "Failed to parse json!", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
