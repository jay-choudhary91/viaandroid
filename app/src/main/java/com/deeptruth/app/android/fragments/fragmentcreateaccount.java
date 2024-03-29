package com.deeptruth.app.android.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.activity.baseactivity;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.interfaces.apiresponselistener;
import com.deeptruth.app.android.utils.nochangingbackgroundtextinputLlayout;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.progressdialog;
import com.deeptruth.app.android.utils.taskresult;
import com.deeptruth.app.android.utils.xdata;
import com.deeptruth.app.android.views.customfontedittext;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class fragmentcreateaccount extends DialogFragment implements View.OnClickListener  {


    @BindView(R.id.edt_channel_name)
    customfontedittext edt_channel_name;
    @BindView(R.id.email)
    customfontedittext edt_email;
    @BindView(R.id.edt_password)
    customfontedittext edt_password;
    @BindView(R.id.edt_confirmpassword)
    customfontedittext edt_confirmpassword;
    @BindView(R.id.txt_submit)
    TextView txt_submit;
    @BindView(R.id.img_backbutton)
    ImageView img_backbutton;
    @BindView(R.id.imgvideolock_background)
    LinearLayout imgvideolock_background;
    @BindView(R.id.rootview)
    LinearLayout rootview;
    @BindView(R.id.img_dialog_background)
    ImageView img_dialog_background;
    @BindView(R.id.lay_logo)
    LinearLayout lay_logo;

    @BindView(R.id.input_layout_email)
    nochangingbackgroundtextinputLlayout input_layout_email;
    @BindView(R.id.input_layout_channel_name)
    nochangingbackgroundtextinputLlayout input_layout_channel_name;
    @BindView(R.id.input_layout_password)
    nochangingbackgroundtextinputLlayout input_layout_password;
    @BindView(R.id.input_layout_confirmpassword)
    nochangingbackgroundtextinputLlayout input_layout_confirmpassword;
    String type = "";


    View contaionerview = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(contaionerview ==null)
        {
            contaionerview = inflater.inflate(R.layout.dialog_createaccount, container, false);
            ButterKnife.bind(this, contaionerview);

            lay_logo.setVisibility(View.GONE);

            txt_submit.setOnClickListener(this);
            img_backbutton.setOnClickListener(this);
            imgvideolock_background.setOnClickListener(this);
            rootview.setOnClickListener(this);
           /* Bitmap bitmap = BitmapFactory.decodeResource(applicationviavideocomposer.getactivity().getResources(),
                    R.drawable.bluegradient);
            img_dialog_background.setImageBitmap(common.getRoundedCornerBitmap(bitmap,140));*/

        }
        return contaionerview;
    }

    /*@Override
    public int getlayoutid() {
        return R.layout.activity_createaccountactivity;
    }*/

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme()) {
            @Override
            public boolean dispatchTouchEvent(@NonNull MotionEvent motionEvent) {
                if (getCurrentFocus() != null) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
                return super.dispatchTouchEvent(motionEvent);
            }

        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_submit:

                checkvalidation();
                break;
            case R.id.img_backbutton:
                //checkValidations();
             if(!type.isEmpty() && type.equalsIgnoreCase(config.loginpage)){
                    baseactivity.getinstance().showdialogsigninfragment();
              }else {
                    baseactivity.getinstance().showdialogsignupfragment();
                }

                getDialog().dismiss();
                break;
            case R.id.imgvideolock_background:
                common.hidekeyboard(applicationviavideocomposer.getactivity());
                break;

            case R.id.rootview:
                common.hidekeyboard(applicationviavideocomposer.getactivity());
                break;
        }
    }

        public  boolean checkValidations() {

            HashMap<String,String> requestparams=new HashMap<>();
            requestparams.put("type","client");
            requestparams.put("action","create");
            requestparams.put("name",edt_channel_name.getText().toString().trim());
            requestparams.put("email",edt_email.getText().toString().trim());
            requestparams.put("password",edt_password.getText().toString().trim());
            requestparams.put("confirmpassword",edt_confirmpassword.getText().toString().trim());
            progressdialog.showwaitingdialog(getActivity());
            baseactivity.getinstance().xapipost_send(getActivity(),requestparams, new apiresponselistener() {

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

                                }
                                else
                                {
                                    if(object.has("error"))
                                        Toast.makeText(applicationviavideocomposer.getactivity(), object.getString("error"), Toast.LENGTH_SHORT).show();
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

                                new AlertDialog.Builder(applicationviavideocomposer.getactivity())
                                        .setMessage(error)
                                        .setPositiveButton(android.R.string.ok, null)
                                        .show();

                               // Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                            }

                            if(object.has("not_verified"))
                            {
                                if(object.getString("not_verified").equalsIgnoreCase("1"))
                                {
                                    if(object.has(config.clientid))
                                        xdata.getinstance().saveSetting(config.clientid,object.getString(config.clientid));

                                    getDialog().dismiss();
                                    baseactivity.getinstance().showdialogverifyuserfragment("",config.createaccount,config.createaccount);
                                }
                            }

                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        Toast.makeText(applicationviavideocomposer.getactivity(), getResources().getString(R.string.json_parsing_failed), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            return true;
        }


    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getscreenwidthheight(97,80);

        getDialog().setCanceledOnTouchOutside(false);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.dialog_slide_animation;
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    public void setdata(String type){
        this.type = type;

    }


    public void getscreenwidthheight(int widthpercentage, int heightpercentage) {

        int width = common.getScreenWidth(applicationviavideocomposer.getactivity());
        int height = common.getScreenHeight(applicationviavideocomposer.getactivity());

        int percentageheight = (height / 100) * heightpercentage;
        int percentagewidth = (width / 100) * widthpercentage;

        getDialog().getWindow().setBackgroundDrawableResource(R.color.transparent);
        getDialog().getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        getDialog().getWindow().setLayout(percentagewidth, percentageheight);
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        double bottommargin = (height / 100) * 3;
        params.y = 10 + Integer.parseInt(xdata.getinstance().getSetting(config.TOPBAR_HEIGHT));
        getDialog().getWindow().setAttributes(params);
    }


    /*public String validphonenumber(){
            String phonenumber= edt_phonenumber.getText().toString().trim().toString();
            phonenumber=phonenumber.replace("(" , "");
            phonenumber=phonenumber.replace(")" , "");
            phonenumber=phonenumber.replace(" ","");
            phonenumber=phonenumber.replace("#","");
            phonenumber=phonenumber.replace("-","");
            phonenumber=phonenumber.replace("-","");
            return phonenumber;
    }*/

    private void checkvalidation() {
        if (!common.checkemailvalidation(applicationviavideocomposer.getactivity(),edt_email,input_layout_email))
            return;

        if (!common.checkchennelnamevalidation(applicationviavideocomposer.getactivity(),edt_channel_name,input_layout_channel_name))
            return;

        if(!common.checkpasswordloginvalidation(applicationviavideocomposer.getactivity(),edt_password,input_layout_password))
            return;

        if (!common.checkpasswordvalidation(applicationviavideocomposer.getactivity(),edt_password,edt_confirmpassword,input_layout_confirmpassword))
            return;

        checkValidations();
    }
}

