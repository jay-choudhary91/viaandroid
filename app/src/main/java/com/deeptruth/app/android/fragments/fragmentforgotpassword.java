package com.deeptruth.app.android.fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.Toast;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.activity.baseactivity;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.fragments.registrationbasefragment;
import com.deeptruth.app.android.interfaces.apiresponselistener;
import com.deeptruth.app.android.models.customedittext;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.progressdialog;
import com.deeptruth.app.android.utils.taskresult;
import com.deeptruth.app.android.utils.xdata;
import com.deeptruth.app.android.views.customfontedittext;
import com.deeptruth.app.android.views.customfonttextview;
import com.google.android.gms.common.internal.service.Common;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class fragmentforgotpassword extends DialogFragment implements View.OnClickListener {

    @BindView(R.id.edt_username)
    customfontedittext edtusername;
    @BindView(R.id.tv_next)
    customfonttextview tv_next;
    View contaionerview = null;
    @BindView(R.id.img_dialog_background)
    ImageView img_dialog_background;
    @BindView(R.id.img_backbutton)
    ImageView img_backbutton;
    @BindView(R.id.lay_logo)
    LinearLayout lay_logo;

    String type = "";
    String lastfragment = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(contaionerview ==null){
            contaionerview = inflater.inflate(R.layout.activity_forgotpassword, container, false);

            ButterKnife.bind(this, contaionerview);
            lay_logo.setVisibility(View.GONE);

          /*  Bitmap bitmap = BitmapFactory.decodeResource(applicationviavideocomposer.getactivity().getResources(),
                    R.drawable.bluegradient);
            img_dialog_background.setImageBitmap(common.getRoundedCornerBitmap(bitmap,170));
*/
            tv_next.setOnClickListener(this);
            img_backbutton.setOnClickListener(this);

        }
        return contaionerview;
    }

    /*@Override
    public int getlayoutid() {
        return R.layout.activity_forgotpassword;
    }*/

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.tv_next:
                if(edtusername.getText().toString().trim().length() > 0)
                {
                    xdata.getinstance().saveSetting(config.usernameemailaddress,edtusername.getText().toString().trim());
                    validatecallapi();
                }
                break;

            case R.id.img_backbutton:
                if(type.equalsIgnoreCase(config.loginpage) && type.equalsIgnoreCase(config.loginpage) ){
                    baseactivity.getinstance().showdialogsigninfragment();
                    getDialog().dismiss();
                }else{
                    baseactivity.getinstance().showdialogsignupfragment();
                    getDialog().dismiss();
                }


                break;
        }
    }

    public void validatecallapi()
    {
        HashMap<String,String> requestparams=new HashMap<>();
        requestparams.put("type","client");
        requestparams.put("action","forgotpassword");
        requestparams.put("email",edtusername.getText().toString().trim());
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

                                baseactivity.getinstance().showdialogverifyuserfragment(config.forgotpassword,config.forgotpassword,lastfragment);
                                getDialog().dismiss();
                            }
                            else
                            {
                                if(object.has("error")){
                                    new AlertDialog.Builder(applicationviavideocomposer.getactivity())
                                            .setMessage( object.getString("error"))
                                            .setPositiveButton(android.R.string.ok, null)
                                            .show();
                                }



                                   // Toast.makeText(applicationviavideocomposer.getactivity(), object.getString("error"), Toast.LENGTH_SHORT).show();
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

    public void setdata(String type,String lastfragment){
        this.type = type;
        this.lastfragment = lastfragment;
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
