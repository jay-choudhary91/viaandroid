package com.deeptruth.app.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.interfaces.apiresponselistener;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.progressdialog;
import com.deeptruth.app.android.utils.taskresult;
import com.deeptruth.app.android.utils.xdata;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import br.com.sapereaude.maskedEditText.MaskedEditText;
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
    @BindView(R.id.layout_userdeatils)
    RelativeLayout layout_userdeatils;

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_createaccountactivity);
        ButterKnife.bind(this);
        txt_submit.setOnClickListener(this);
        edt_phonenumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId & EditorInfo.IME_MASK_ACTION) != 0) {
                    hidekeyboard();
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_submit:
                checkValidations();
                break;
        }
    }

        public  boolean checkValidations() {

            HashMap<String,String> requestparams=new HashMap<>();
            requestparams.put("type","client");
            requestparams.put("action","create");
            requestparams.put("name",edt_username.getText().toString().trim());
            requestparams.put("email",edt_email.getText().toString().trim());
            requestparams.put("password",edt_password.getText().toString().trim());
            requestparams.put("confirmpassword",edt_confirmpassword.getText().toString().trim());
            progressdialog.showwaitingdialog(createaccount.this);
            xapipost_send(createaccount.this,requestparams, new apiresponselistener() {
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

                                    Intent intent=new Intent(createaccount.this,verifyuser.class);
                                    startActivity(intent);
                                }
                                else
                                {
                                    if(object.has("error"))
                                        Toast.makeText(createaccount.this, object.getString("error"), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(createaccount.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        Toast.makeText(createaccount.this, getResources().getString(R.string.json_parsing_failed), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            return true;
        }

        public String validphonenumber(){
            String phonenumber= edt_phonenumber.getText().toString().trim().toString();
            phonenumber=phonenumber.replace("(" , "");
            phonenumber=phonenumber.replace(")" , "");
            phonenumber=phonenumber.replace(" ","");
            phonenumber=phonenumber.replace("#","");
            phonenumber=phonenumber.replace("-","");
            phonenumber=phonenumber.replace("-","");
            return phonenumber;
    }
}
