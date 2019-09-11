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
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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
    @BindView(R.id.tv_signin)
    customfonttextview tv_signin;
    @BindView(R.id.tv_verify)
    customfonttextview tv_verify;
    @BindView(R.id.tv_forgotpassword)
    customfonttextview tv_forgotpassword;
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

           /* Bitmap bitmap = BitmapFactory.decodeResource(applicationviavideocomposer.getactivity().getResources(),
                    R.drawable.bluegradient);
            img_dialog_background.setImageBitmap(common.getRoundedCornerBitmap(bitmap,170));*/

            btn_signup.setOnClickListener(this);
            img_backbutton.setOnClickListener(this);

            setsigninclickabletext(tv_signin);
            setverifyclickabletext(tv_verify);
            setforgotpasswordclickabletext(tv_forgotpassword);
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
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_signup:
                baseactivity.getinstance().showdialogcreateaccountfragment(config.signuppage);
                getDialog().dismiss();
                break;

            /*case R.id.btn_signin:
                baseactivity.getinstance().showdialogsigninfragment();
                getDialog().dismiss();
                break;*/

            case R.id.img_backbutton:
                getDialog().dismiss();
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
    }


    public void setsigninclickabletext(customfonttextview tv_signin){

        SpannableString ss = new SpannableString(applicationviavideocomposer.getactivity().getResources().getString(R.string.already_have_an_account));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {

                baseactivity.getinstance().showdialogsigninfragment();
                getDialog().dismiss();
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.blue));
                ds.setUnderlineText(false);
            }
        };
        ss.setSpan(clickableSpan, 25, 32, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tv_signin.setText(ss);
        tv_signin.setMovementMethod(LinkMovementMethod.getInstance());
        tv_signin.setHighlightColor(Color.TRANSPARENT);

    }


    public void setverifyclickabletext(customfonttextview tv_verify){
        SpannableString ss = new SpannableString(applicationviavideocomposer.getactivity().getResources().getString(R.string.have_a_confirmation_code));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {

                baseactivity.getinstance().showdialogverifyuserfragment("",config.signuppage,config.signuppage);
                getDialog().dismiss();
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.blue));
                ds.setUnderlineText(false);
            }
        };
        ss.setSpan(clickableSpan, 26, 32, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tv_verify   .setText(ss);
        tv_verify.setMovementMethod(LinkMovementMethod.getInstance());
        tv_verify.setHighlightColor(Color.TRANSPARENT);
    }


    public void setforgotpasswordclickabletext(customfonttextview tv_forgotpassword){
        SpannableString ss = new SpannableString(applicationviavideocomposer.getactivity().getResources().getString(R.string.reset));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {

                baseactivity.getinstance().showdialogforgotpasswordfragment(config.signuppage,config.signuppage);
                getDialog().dismiss();
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.blue));
                ds.setUnderlineText(false);
            }
        };
        ss.setSpan(clickableSpan, 17, 22, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tv_forgotpassword.setText(ss);
        tv_forgotpassword.setMovementMethod(LinkMovementMethod.getInstance());
        tv_forgotpassword.setHighlightColor(Color.TRANSPARENT);

    }
}
