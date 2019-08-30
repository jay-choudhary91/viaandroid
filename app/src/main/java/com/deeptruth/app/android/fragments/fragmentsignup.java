package com.deeptruth.app.android.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.activity.baseactivity;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.interfaces.apiresponselistener;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.pinviewnumeric;
import com.deeptruth.app.android.utils.progressdialog;
import com.deeptruth.app.android.utils.taskresult;
import com.deeptruth.app.android.utils.xdata;
import com.deeptruth.app.android.views.customfonttextview;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class fragmentsignup extends DialogFragment implements View.OnClickListener {

    @BindView(R.id.btn_signup)
    customfonttextview btn_signup;
    @BindView(R.id.btn_signin)
    customfonttextview btn_signin;
    @BindView(R.id.img_backbutton)
    ImageView img_backbutton;
    @BindView(R.id.img_dialog_background)
    ImageView img_dialog_background;

    View contaionerview = null;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(contaionerview ==null){
            contaionerview = inflater.inflate(R.layout.dialog_signup, container, false);
            ButterKnife.bind(this, contaionerview);

            Bitmap bitmap = BitmapFactory.decodeResource(applicationviavideocomposer.getactivity().getResources(),
                    R.drawable.bluegradient);
            img_dialog_background.setImageBitmap(common.getRoundedCornerBitmap(bitmap,170));

            btn_signup.setOnClickListener(this);
            btn_signin.setOnClickListener(this);
            btn_signin.setText(" "+applicationviavideocomposer.getactivity().getResources().getString(R.string.sign_in));
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
            case R.id.btn_signup:
                baseactivity.getinstance().showdialogcreateaccountfragment();
                getDialog().dismiss();
                break;

            case R.id.btn_signin:
                baseactivity.getinstance().showdialogsigninfragment();
                getDialog().dismiss();
                break;

            case R.id.img_backbutton:
                getDialog().dismiss();
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
    }
}
