package com.deeptruth.app.android.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.activity.baseactivity;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.interfaces.apiresponselistener;
import com.deeptruth.app.android.utils.common;
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

public class fragmentcreateaccount extends DialogFragment implements View.OnClickListener  {


    @BindView(R.id.edt_channel_name)
    EditText edt_channel_name;
    @BindView(R.id.email)
    EditText edt_email;
    @BindView(R.id.edt_password)
    EditText edt_password;
    @BindView(R.id.edt_confirmpassword)
    EditText edt_confirmpassword;
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
    String type = "";


    View contaionerview = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(contaionerview ==null)
        {
            contaionerview = inflater.inflate(R.layout.dialog_createaccount, container, false);
            ButterKnife.bind(this, contaionerview);

            txt_submit.setOnClickListener(this);
            img_backbutton.setOnClickListener(this);
            imgvideolock_background.setOnClickListener(this);
            rootview.setOnClickListener(this);

            Bitmap bitmap = BitmapFactory.decodeResource(applicationviavideocomposer.getactivity().getResources(),
                    R.drawable.bluegradient);
            img_dialog_background.setImageBitmap(common.getRoundedCornerBitmap(bitmap,170));
        }
        return contaionerview;
    }

    /*@Override
    public int getlayoutid() {
        return R.layout.activity_createaccountactivity;
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_submit:
                checkValidations();

                break;
            case R.id.img_backbutton:
                //checkValidations();
                getDialog().dismiss();

                if(!type.isEmpty() && type.equalsIgnoreCase(config.loginpage)){
                    baseactivity.getinstance().showdialogsigninfragment();
              }else {
                    baseactivity.getinstance().showdialogsignupfragment();
                }
                baseactivity.getinstance().showdialogsignupfragment();

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
                                    baseactivity.getinstance().showdialogconfirmchannelfragment();


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

        getscreenwidthheight(97,80);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.dialog_slide_animation;
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

}

