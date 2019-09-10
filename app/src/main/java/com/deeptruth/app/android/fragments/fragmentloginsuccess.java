package com.deeptruth.app.android.fragments;

import android.app.Dialog;
import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.activity.baseactivity;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.interfaces.adapteritemclick;
import com.deeptruth.app.android.interfaces.apiresponselistener;
import com.deeptruth.app.android.models.sharepopuptextspanning;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.progressdialog;
import com.deeptruth.app.android.utils.taskresult;
import com.deeptruth.app.android.utils.xdata;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class fragmentloginsuccess extends DialogFragment implements View.OnClickListener{

    @BindView(R.id.txt_content)
    TextView txt_content;
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
            contaionerview = inflater.inflate(R.layout.dialog_loginsuccess, container, false);

            ButterKnife.bind(this, contaionerview);
            String str = applicationviavideocomposer.getactivity().getResources().getString(R.string.you_have_successfully_registered);
            char[] linecontent = str.toCharArray();
            ArrayList<sharepopuptextspanning> textsharepopup = new ArrayList<>();

            textsharepopup.add(new sharepopuptextspanning(1.15f,0,32,str));
            textsharepopup.add(new sharepopuptextspanning(1.15f,34,65,str));

            textsharepopup.add(new sharepopuptextspanning(1.15f,66,98,str));
            textsharepopup.add(new sharepopuptextspanning(1.21f,99,131,str));

            textsharepopup.add(new sharepopuptextspanning(1.18f,133,167,str));//144
            textsharepopup.add(new sharepopuptextspanning(1.17f,167,196,str));
            textsharepopup.add(new sharepopuptextspanning(1.16f,196,227,str));

            textsharepopup.add(new sharepopuptextspanning(1.14f,227,str.length(),str));


            common.setspanning(textsharepopup,txt_content);

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

    /*public void setdata(adapteritemclick adapteritemupdate) {
        this.adapteritemupdate = adapteritemupdate;
    }*/
}
