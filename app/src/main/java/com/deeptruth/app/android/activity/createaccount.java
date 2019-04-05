package com.deeptruth.app.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.utils.common;
import com.github.reinaldoarrosi.maskededittext.MaskedEditText;
import com.google.android.gms.common.internal.service.Common;

import butterknife.BindView;
import butterknife.ButterKnife;

public class createaccount extends registrationbaseactivity implements View.OnClickListener  {


    @BindView(R.id.edt_username)
    EditText edt_username;
    @BindView(R.id.email)
    EditText edt_email;
    @BindView(R.id.edt_password)
    EditText edt_password;
    @BindView(R.id.edt_confirmpassword)
    EditText edt_confirmpassword;
    @BindView(R.id.edt_phonenumber)
    MaskedEditText edt_phonenumber;
    @BindView(R.id.txt_submit)
    TextView txt_submit;

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_createaccountactivity);
        ButterKnife.bind(this);

        txt_submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txt_submit:
                if(checkValidations() == true){
                    Intent intent=new Intent(createaccount.this,verifiedemail.class);
                    startActivity(intent);
                }


                break;
        }
    }

        public  boolean checkValidations() {

            if (edt_username.getText().toString().trim().toString().length() == 0) {
                Toast.makeText(this, "Please enter user name!", Toast.LENGTH_SHORT).show();
                return false;
            }
            if(! common.isvalidusername(edt_username.getText().toString().trim().toString())){
                Toast.makeText(this, "Please enter user name!", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (! common.isValidEmail(edt_email.getText().toString().trim())) {
                Toast.makeText(this, "Please enter valid email!", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (edt_password.getText().toString().trim().toString().length() == 0) {
                Toast.makeText(this, "Please enter password!", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (edt_confirmpassword.getText().toString().trim().toString().length() == 0) {
                Toast.makeText(this, "Please enter confirm password!", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (!(edt_password.getText().toString().trim().toString()).equals(edt_confirmpassword.getText().toString().trim().toString())) {
                Toast.makeText(this, " confirm password is not correct!", Toast.LENGTH_SHORT).show();
                return false;
            }

            return true;
        }
}
