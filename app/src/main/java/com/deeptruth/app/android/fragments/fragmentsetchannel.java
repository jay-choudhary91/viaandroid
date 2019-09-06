package com.deeptruth.app.android.fragments;

import android.app.Dialog;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.deeptruth.app.android.views.customfontedittext;
import com.deeptruth.app.android.views.customfonttextview;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class fragmentsetchannel extends DialogFragment implements View.OnClickListener {

    @BindView(R.id.edt_username)
    customfontedittext edt_channelname;
    @BindView(R.id.txt_createaccount)
    customfonttextview txt_createaccount;
    @BindView(R.id.img_backbutton)
    ImageView img_backbutton;
    @BindView(R.id.imgvideolock_background)
    LinearLayout imgvideolock_background;
    @BindView(R.id.rootview)
    RelativeLayout rootview;
    @BindView(R.id.img_dialog_background)
    ImageView img_dialog_background;
    View contaionerview = null;
    String userverified="";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(contaionerview ==null){
            contaionerview = inflater.inflate(R.layout.dialog_setchannel, container, false);
            ButterKnife.bind(this, contaionerview);

            Bitmap bitmap = BitmapFactory.decodeResource(applicationviavideocomposer.getactivity().getResources(),
                    R.drawable.bluegradient);
            img_dialog_background.setImageBitmap(common.getRoundedCornerBitmap(bitmap,170));

            txt_createaccount.setOnClickListener(this);
            img_backbutton.setOnClickListener(this);
            imgvideolock_background.setOnClickListener(this);
            rootview.setOnClickListener(this);
        }
        return contaionerview;
    }

    /*@Override
    public int getlayoutid() {
        return R.layout.activity_verifyuser;
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

        switch (v.getId()){
            case R.id.txt_createaccount:
                if(edt_channelname.getText().toString().trim().length() > 0)
                    baseactivity.getinstance().showdialogconfirmchannelfragment(edt_channelname.getText().toString().trim(),userverified);
                else
                    Toast.makeText(applicationviavideocomposer.getactivity(),"Please enter channel name!",Toast.LENGTH_SHORT).show();
                break;

            case R.id.img_backbutton:
                baseactivity.getinstance().showdialogcreateaccountfragment("");
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
    }


    public void getscreenwidthheight(int widthpercentage,int heightpercentage) {

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

    public void setdata(String userverified) {
        this.userverified = userverified;
    }
}