package com.deeptruth.app.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.deeptruth.app.android.BuildConfig;
import com.deeptruth.app.android.R;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.xdata;

import butterknife.BindView;
import butterknife.ButterKnife;

public class signin extends Activity implements View.OnClickListener{


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

    String username , password;
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
               login();
               break;
           case R.id.createaccount:
               Intent intent=new Intent(signin.this,createaccount.class);
               startActivity(intent);
               finish();
               break;
           case R.id.forgot_password:
               intent = new Intent(signin.this, forgotpassword.class);
               startActivity(intent);
               finish();
               break;

       }
    }
    public void login(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_reader))
                {
                    Intent in=new Intent(signin.this,homeactivity.class);
                    startActivity(in);
                    finish();
                }
                else
                {

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
            }
        },1000);
    }

    public void validation(){

    }
}
