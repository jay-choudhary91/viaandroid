package com.deeptruth.app.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.models.customedittext;
import com.deeptruth.app.android.views.customfontedittext;
import com.deeptruth.app.android.views.customfonttextview;

import butterknife.BindView;
import butterknife.ButterKnife;

public class forgotpassword extends Activity implements View.OnClickListener {

    @BindView(R.id.tv_forgotpassword)
    customfonttextview tvforgotpassword;
    @BindView(R.id.edt_username)
    customfontedittext edtusername;
    @BindView(R.id.tv_submit)
    customfonttextview tvsubmit;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        ButterKnife.bind(forgotpassword.this);

        tvsubmit.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.tv_submit:
               Intent  intent = new Intent(forgotpassword.this,verifiedemail.class);
               intent.putExtra("code","12345" );
               startActivity(intent);
        }
    }
}
