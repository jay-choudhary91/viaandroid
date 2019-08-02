package com.deeptruth.app.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.activity.introscreenactivity;
import com.deeptruth.app.android.interfaces.apiresponselistener;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.progressdialog;
import com.deeptruth.app.android.utils.taskresult;
import com.deeptruth.app.android.utils.xdata;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class fragmentsignin extends registrationbasefragment implements View.OnClickListener{


    @BindView(R.id.edt_username)
    EditText edt_username;
    @BindView(R.id.edt_password)
    EditText edt_password;
    @BindView(R.id.login)
    TextView txt_login;
    @BindView(R.id.createaccount)
    TextView txt_createaccount;
    @BindView(R.id.forgot_password)
    TextView txt_forgotpassword;
    @BindView(R.id.rootview)
    ScrollView rootview;
    @BindView(R.id.layout_image)
    LinearLayout layout_image;
    @BindView(R.id.layout_logindetails)
    LinearLayout layout_logindetails;
    int rootviewheight,imageviewheight,userloginheight;
    View contaionerview = null;

    @Override
    public int getlayoutid() {
        return R.layout.activity_loginactivity;
    }


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
                    imageviewheight = ((rootviewheight *50)/100);
                    layout_image.getLayoutParams().height = imageviewheight;
                    layout_image.setVisibility(View.VISIBLE);
                    layout_image.requestLayout();

                    userloginheight = (rootviewheight -imageviewheight);
                    layout_logindetails.getLayoutParams().height = userloginheight;
                    layout_logindetails.setVisibility(View.VISIBLE);
                    layout_logindetails.requestLayout();

                }
            });

            txt_login.setOnClickListener(this);
            txt_createaccount.setOnClickListener(this);
            txt_forgotpassword.setOnClickListener(this);
            rootview.setOnClickListener(this);
        }
        return contaionerview;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(getActivity());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login :
                isvalidated();
                break;
            case R.id.createaccount:

                fragmentcreateaccount fragcreateaccount = new fragmentcreateaccount();
                getHelper().addFragment(fragcreateaccount,false,true);

               /* Intent intent=new Intent(fragmentsignin.this,fragmentcreateaccount.class);
                startActivity(intent);*/
                break;
            case R.id.forgot_password:

                fragmentforgotpassword fragforgotpassword = new fragmentforgotpassword();
                getHelper().addFragment(fragforgotpassword,false,true);

                /*intent = new Intent(fragmentsignin.this, fragmentforgotpassword.class);
                startActivity(intent);*/
                break;
        }
    }
    public void login()
    {
        /*if(xdata.getinstance().getSetting(config.enableintroscreen).isEmpty() || xdata.getinstance().getSetting(config.enableintroscreen).equalsIgnoreCase("yes"))
        {
            if(xdata.getinstance().getSetting(config.enableintroscreen).isEmpty())
                xdata.getinstance().saveSetting(config.enableintroscreen,"no");

            Intent intent=new Intent(getActivity(),introscreenactivity.class);
            startActivity(intent);
            getHelper().onBack();

        }
        else
        {
            getHelper().onBack();
        }*/
        getHelper().onBack();
    }

    public boolean isvalidated(){

        /*if ((! common.isvalidusername(edt_username.getText().toString().trim()))) {
            Toast.makeText(this, "Please enter valid username!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (edt_password.getText().toString().trim().toString().length() == 0) {
            Toast.makeText(this, "Please enter password!", Toast.LENGTH_SHORT).show();
            return false;
        }*/

        HashMap<String,String> requestparams=new HashMap<>();
        requestparams.put("type","client");
        requestparams.put("action","authorize");
        requestparams.put("email",edt_username.getText().toString().trim());
        requestparams.put("password",edt_password.getText().toString().trim());
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

                                if(object.has(config.authtoken))
                                    xdata.getinstance().saveSetting(config.authtoken,object.getString(config.authtoken));

                                if(object.has(config.clientemail))
                                    xdata.getinstance().saveSetting(config.clientemail,object.getString(config.clientemail));

                                login();
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

                        if(object.has("not_verified"))
                        {
                            if(object.getString("not_verified").equalsIgnoreCase("1"))
                            {
                                if(object.has(config.clientid))
                                    xdata.getinstance().saveSetting(config.clientid,object.getString(config.clientid));

                                Intent intent=new Intent(getActivity(),fragmentverifyuser.class);
                                intent.putExtra("activityname",config.loginpage);
                                startActivity(intent);
                            }
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
        return true;
    }
}
