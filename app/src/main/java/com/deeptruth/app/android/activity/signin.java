package com.deeptruth.app.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.deeptruth.app.android.BuildConfig;
import com.deeptruth.app.android.R;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.xdata;

import butterknife.BindView;
import butterknife.ButterKnife;

public class signin extends registrationbaseactivity implements View.OnClickListener{


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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginactivity);
        ButterKnife.bind(this);

        txt_login.setOnClickListener(this);
        txt_createaccount.setOnClickListener(this);
        txt_forgotpassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
       switch (view.getId()){
           case R.id.login :
               if (validation())
               {
                   login();
               }
               break;
           case R.id.createaccount:
               Intent intent=new Intent(signin.this,createaccount.class);
               startActivity(intent);
               break;
           case R.id.forgot_password:
               intent = new Intent(signin.this, forgotpassword.class);
               startActivity(intent);
               break;

       }
    }
    public void login(){
                    if(xdata.getinstance().getSetting(config.enableintroscreen).isEmpty() || xdata.getinstance().getSetting(config.enableintroscreen).equalsIgnoreCase("yes"))
                    {
                        if(xdata.getinstance().getSetting(config.enableintroscreen).isEmpty())
                            xdata.getinstance().saveSetting(config.enableintroscreen,"no");

                        Intent intent=new Intent(signin.this,introscreenactivity.class);
                        startActivity(intent);
                        finish();

                    }else{
                        Intent intent=new Intent(signin.this,homeactivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.activityfadein, R.anim.activityfadeout);
                        finish();
                    }
    }

    public boolean validation(){

        if ((! common.isvalidusername(edt_username.getText().toString().trim()))) {
            Toast.makeText(this, "Please enter valid username!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (edt_password.getText().toString().trim().toString().length() == 0) {
            Toast.makeText(this, "Please enter password!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
