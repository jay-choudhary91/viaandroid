package com.deeptruth.app.android.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.deeptruth.app.android.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class confirmpassword extends registrationbaseactivity implements View.OnClickListener{


    @BindView(R.id.edt_password)
    EditText edt_password;
    @BindView(R.id.edt_confirmpassword)
    EditText edt_confirmpassword;
    @BindView(R.id.tv_submit)
    EditText tv_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmpassword);
        ButterKnife.bind(confirmpassword.this);
    }

    @Override
    public void onClick(View v) {
        
    }
}
