package com.deeptruth.app.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class fragmentcreateaccount extends registrationbasefragment implements View.OnClickListener  {


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


    View contaionerview = null;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(contaionerview ==null){
            contaionerview = super.onCreateView(inflater, container, savedInstanceState);
            ButterKnife.bind(this, contaionerview);
            getHelper().onUserLeaveHint();

            txt_submit.setOnClickListener(this);
            edt_phonenumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if ((actionId & EditorInfo.IME_MASK_ACTION) != 0) {
                        getHelper().hidekeyboard();
                    }
                    return false;
                }
            });

        }
        return contaionerview;
    }

    @Override
    public int getlayoutid() {
        return R.layout.activity_createaccountactivity;
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

                                    fragmentverifyuser fragverifyuser = new fragmentverifyuser();
                                    fragverifyuser.setdata(config.createaccount);
                                    getHelper().addFragment(fragverifyuser,false,true);


                                   /* Intent intent=new Intent(getActivity(),fragmentverifyuser.class);
                                    intent.putExtra("activityname",config.createaccount);
                                    startActivity(intent);*/
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

                                   /* Intent intent=new Intent(getActivity(),fragmentverifyuser.class);
                                    intent.putExtra("activityname",config.fragmentcreateaccount);
                                    startActivity(intent);*/
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
