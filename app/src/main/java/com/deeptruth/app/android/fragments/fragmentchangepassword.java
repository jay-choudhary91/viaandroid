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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.activity.baseactivity;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.fragments.registrationbasefragment;
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

public class fragmentchangepassword extends DialogFragment implements View.OnClickListener{


    @BindView(R.id.edt_password)
    EditText edt_password;
    @BindView(R.id.edt_confirmpassword)
    EditText edt_confirmpassword;
    @BindView(R.id.tv_submit)
    TextView tv_submit;
    @BindView(R.id.img_dialog_background)
    ImageView img_dialog_background;

    View contaionerview = null;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(contaionerview ==null){
            contaionerview = inflater.inflate(R.layout.activity_changepassword, container, false);

            ButterKnife.bind(this, contaionerview);

            Bitmap bitmap = BitmapFactory.decodeResource(applicationviavideocomposer.getactivity().getResources(),
                    R.drawable.bluegradient);
            img_dialog_background.setImageBitmap(common.getRoundedCornerBitmap(bitmap,170));

            tv_submit.setOnClickListener(this);
        }
        return contaionerview;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.tv_submit:
                validatechangepassword();
                break;
        }
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

    public void validatechangepassword()
    {
        HashMap<String,String> requestparams=new HashMap<>();
        requestparams.put("type","client");
        requestparams.put("action","resetpassword");
        requestparams.put("email",xdata.getinstance().getSetting(config.usernameemailaddress));
        requestparams.put("password",""+edt_password.getText().toString().trim());
        requestparams.put("confirmpassword",""+edt_confirmpassword.getText().toString().trim());
        requestparams.put("reset_authtoken",""+xdata.getinstance().getSetting(config.reset_authtoken).toString().trim());
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
                            if(object.getString("success").equalsIgnoreCase("1") || object.getString("success").equalsIgnoreCase("true"))
                            {
                                if(object.has(config.clientid)){
                                    xdata.getinstance().saveSetting(config.clientid,object.getString(config.clientid));

                                    baseactivity.getinstance().showdialogsigninfragment();
                                    getDialog().dismiss();
                                }

                            }
                            else
                            {
                                if(object.has("error")){
                                    new AlertDialog.Builder(applicationviavideocomposer.getactivity())
                                            .setMessage(object.getString("error"))
                                            .setPositiveButton(android.R.string.ok, null)
                                            .show();
                                }
                                    //Toast.makeText(getActivity(), object.getString("error"), Toast.LENGTH_SHORT).show();
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

    public void getscreenwidthheight(int widthpercentage, int heightpercentage) {

        int width = common.getScreenWidth(applicationviavideocomposer.getactivity());
        int height = common.getScreenHeight(applicationviavideocomposer.getactivity());

        int percentageheight = (height / 100) * heightpercentage;
        int percentagewidth = (width / 100) * widthpercentage;

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        getDialog().getWindow().setLayout(percentagewidth, percentageheight);
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        double bottommargin = (height / 100) * 3;
        params.y = 10 + Integer.parseInt(xdata.getinstance().getSetting(config.TOPBAR_HEIGHT));
        getDialog().getWindow().setAttributes(params);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.dialog_slide_animation;
    }
}
