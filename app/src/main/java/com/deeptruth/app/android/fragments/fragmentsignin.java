package com.deeptruth.app.android.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.activity.baseactivity;
import com.deeptruth.app.android.activity.introscreenactivity;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.interfaces.adapteritemclick;
import com.deeptruth.app.android.interfaces.apiresponselistener;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.progressdialog;
import com.deeptruth.app.android.utils.taskresult;
import com.deeptruth.app.android.utils.xdata;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class fragmentsignin extends DialogFragment implements View.OnClickListener{
    
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
    RelativeLayout rootview;
    @BindView(R.id.layout_image)
    LinearLayout layout_image;
    @BindView(R.id.layout_logindetails)
    LinearLayout layout_logindetails;
    @BindView(R.id.img_backbutton)
    ImageView img_backbutton;
    @BindView(R.id.img_dialog_background)
    ImageView img_dialog_background;
    int rootviewheight,imageviewheight,userloginheight;
    View contaionerview = null;
    adapteritemclick adapteritemupdate;
    /*@Override
    public int getlayoutid() {
        return R.layout.activity_loginactivity;
    }*/


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(contaionerview ==null){
            contaionerview = inflater.inflate(R.layout.activity_loginactivity, container, false);

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

            Bitmap bitmap = BitmapFactory.decodeResource(applicationviavideocomposer.getactivity().getResources(),
                    R.drawable.bluegradient);
            img_dialog_background.setImageBitmap(common.getRoundedCornerBitmap(bitmap,170));


            txt_login.setOnClickListener(this);
            txt_createaccount.setOnClickListener(this);
            txt_forgotpassword.setOnClickListener(this);
            rootview.setOnClickListener(this);
            img_backbutton.setOnClickListener(this);
            layout_logindetails.setOnClickListener(this);
            layout_image.setOnClickListener(this);
        }
        return contaionerview;
    }

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(applicationviavideocomposer.getactivity());

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login :
                isvalidated();
                break;
            case R.id.createaccount:
                baseactivity.getinstance().showdialogcreateaccountfragment(config.loginpage);
                getDialog().dismiss();
                break;
            case R.id.forgot_password:
                baseactivity.getinstance().showdialogforgotpasswordfragment();
                getDialog().dismiss();
                break;
            case R.id.img_backbutton:
                getDialog().dismiss();
                baseactivity.getinstance().showdialogsignupfragment();
                break;
            case R.id.layout_logindetails:
                Log.e("ontouch","ontouch");
                break;
            case R.id.layout_image:
                Log.e("ontouchimage","ontouch");
                break;
        }
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
        progressdialog.showwaitingdialog(applicationviavideocomposer.getactivity());
        baseactivity.getinstance().xapipost_send(applicationviavideocomposer.getactivity(),requestparams, new apiresponselistener() {
            @Override
            public void onResponse(taskresult response) {
                progressdialog.dismisswaitdialog();
                if(response.isSuccess())
                {
                    try {
                        JSONObject object=new JSONObject(response.getData().toString());

                        if(object.has(config.clientid))
                            xdata.getinstance().saveSetting(config.clientid,object.getString(config.clientid));

                        if(object.has("success"))
                        {
                            if(object.getString("success").equalsIgnoreCase("true") || object.getString("success").equalsIgnoreCase("1"))
                            {
                                if(object.has(config.authtoken))
                                    xdata.getinstance().saveSetting(config.authtoken,object.getString(config.authtoken));

                                if(object.has(config.clientemail))
                                    xdata.getinstance().saveSetting(config.clientemail,object.getString(config.clientemail));

                                xdata.getinstance().saveSetting(config.isuserlogin,"1");

                                if(object.has(config.clientchannel))
                                {
                                    xdata.getinstance().saveSetting(config.clientchannel,object.getString(config.clientchannel));

                                    if(object.getString(config.clientchannel).equalsIgnoreCase("null") ||
                                            object.getString(config.clientchannel).trim().isEmpty())
                                    {
                                        String not_verified="";
                                        if(object.has("not_verified"))
                                            not_verified=object.getString("not_verified");

                                        baseactivity.getinstance().showdialogsetchannelfragment(not_verified);
                                    }
                                }

                                if(adapteritemupdate != null)
                                    adapteritemupdate.onItemClicked(null);

                                getDialog().dismiss();
                            }
                            else
                            {
                                if(object.has("error")){
                                    new AlertDialog.Builder(applicationviavideocomposer.getactivity())
                                            .setMessage(object.getString("error"))
                                            .setPositiveButton(android.R.string.ok, null)
                                            .show();
                                }
                                //Toast.makeText(applicationviavideocomposer.getactivity(), object.getString("error"), Toast.LENGTH_SHORT).show();
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


                            //Toast.makeText(applicationviavideocomposer.getactivity(), error, Toast.LENGTH_SHORT).show();
                        }

                        if(object.has("not_verified"))
                        {
                            if(object.has(config.clientchannel))
                            {
                                if(object.getString(config.clientchannel).equalsIgnoreCase("null") ||
                                        object.getString(config.clientchannel).trim().isEmpty())
                                {
                                    baseactivity.getinstance().showdialogsetchannelfragment(object.getString("not_verified"));
                                    return;
                                }
                            }
                            if(object.getString("not_verified").equalsIgnoreCase("1"))
                            {
                                if(object.has(config.clientid))
                                    xdata.getinstance().saveSetting(config.clientid,object.getString(config.clientid));

                                baseactivity.getinstance().showdialogverifyuserfragment(config.loginpage);
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
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    public void getscreenwidthheight(int widthpercentage,int heightpercentage) {

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

    public void setdata(adapteritemclick adapteritemupdate) {
        this.adapteritemupdate = adapteritemupdate;
    }
}
