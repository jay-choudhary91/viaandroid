package com.deeptruth.app.android.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.activity.baseactivity;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.xdata;
import com.deeptruth.app.android.views.customfontedittext;
import com.deeptruth.app.android.views.customfonttextview;

import butterknife.BindView;
import butterknife.ButterKnife;

public class fragmentconfirmchannel extends DialogFragment implements View.OnClickListener {

    @BindView(R.id.edt_username)
    customfontedittext edt_username;
    @BindView(R.id.txt_createaccount)
    customfonttextview txt_createaccount;
    @BindView(R.id.img_backbutton)
    ImageView img_backbutton;
    View contaionerview = null;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(contaionerview ==null){
            contaionerview = inflater.inflate(R.layout.dialog_confirmchannel, container, false);
            ButterKnife.bind(this, contaionerview);

            txt_createaccount.setOnClickListener(this);
            img_backbutton.setOnClickListener(this);
        }
        return contaionerview;
    }

    /*@Override
    public int getlayoutid() {
        return R.layout.activity_verifyuser;
    }*/

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.txt_createaccount:
                getDialog().dismiss();
                baseactivity.getinstance().showdialogverifyuserfragment();

                break;

            case R.id.img_backbutton:
                getDialog().dismiss();
                baseactivity.getinstance().showdialogcreateaccountfragment();

                break;
        }
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
}
