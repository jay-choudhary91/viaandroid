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
import com.deeptruth.app.android.models.customedittext;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.xdata;
import com.deeptruth.app.android.views.customfontedittext;
import com.deeptruth.app.android.views.customfonttextview;
import com.google.android.gms.common.internal.service.Common;

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
        layout_forgotpass.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.tv_next:
                if(common.checkemailvalidation(forgotpassword.this,edtusername))
                {
                    xdata.getinstance().saveSetting(config.usernameemailaddress,edtusername.getText().toString().trim());
                    Intent  intent = new Intent(forgotpassword.this,changepassword.class);
                    startActivity(intent);
                }
                break;

            case R.id.layout_forgotpass:
                hidekeyboard();
                break;
        }
    }
}
