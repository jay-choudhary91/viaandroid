package com.deeptruth.app.android.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.interfaces.itemchanged;
import com.deeptruth.app.android.models.managementcontroller;

import java.util.HashMap;



/**
 * Created by android-dev-17c on 7/20/2017.
 */

public class popupview {

   static Dialog dialog = null;

    static CountDownTimer mCounterTimer=null;
    public void showPopupView(final Context context, String title, String description, final managementcontroller mController, final itemchanged mItemChanged){

        final Dialog dialog=new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.xapi_popupview);

        final EditText edtInputData1 = (EditText)dialog.findViewById(R.id.edt_inputdata_1);
        final EditText edtInputData2 = (EditText)dialog.findViewById(R.id.edt_inputdata_2);

        TextView txtTitle = (TextView)dialog.findViewById(R.id.txt_title);
        TextView txtDescription = (TextView)dialog.findViewById(R.id.txt_description);

        TextView TxtPositiveButton = (TextView)dialog.findViewById(R.id.tv_positive);
        final TextView TxtNegativeButton = (TextView)dialog.findViewById(R.id.tv_negative);

        txtTitle.setText(title);
        txtDescription.setText(description);

        edtInputData1.setHintTextColor(context.getResources().getColor(R.color.grey_x));
        edtInputData2.setHintTextColor(context.getResources().getColor(R.color.grey_x));

        switch (mController.getTxtName())
        {
            case config.LIST_XAPI:
                {
                    //HashMap<String, String> map= xdata.getinstance().getSettingArray();
                    edtInputData2.setText(xdata.getinstance().getSetting(xdata.xapi_url));
                }
                break;

            case config.LIST_IMAGES:
                {
                    HashMap<String, String> map= xdata.getinstance().getSettingArray();
                   // edtInputData2.setText(map.get(config.XAPI_IMAGE_BASE_URL));
                }
                break;

            case config.LIST_CONFIGACTION:

                break;

            case config.LIST_SETTINGS:
                edtInputData1.setVisibility(View.VISIBLE);
                TxtPositiveButton.setText("Save");
                edtInputData1.setHint("key");
                edtInputData2.setHint("value");

                edtInputData1.setText(mController.getKeyName());
                edtInputData2.setText(mController.getKeyValue());
                break;

            default:
                break;
        }

        TxtPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(edtInputData2.getText().toString().trim().length() > 0)
                {

                    switch (mController.getTxtName()) {
                        case config.LIST_XAPI:
                            if(edtInputData2.getText().toString().trim().equalsIgnoreCase(xdata.getinstance().getSetting(xdata.xapi_url))){
                                xdata.getinstance().saveSettingArray(config.XAPI_URL,edtInputData2.getText().toString().trim());
                                showPopup("Alert","URL saved successfully.");
                                dialog.dismiss();
                            }
                            else
                            {
                                //Toasts.showToast(context,"Invalid URL!",1);
                            }
                            break;

                        /*case config.LIST_IMAGES:
                            if(edtInputData2.getText().toString().trim().equalsIgnoreCase(config.XAPI_BASE_IMAGE_URL_SECURED_3)
                                    || edtInputData2.getText().toString().trim().equalsIgnoreCase(config.XAPI_BASE_IMAGE_URL_3))
                            {
                                xdata.getinstance().saveSettingArray(config.XAPI_IMAGE_BASE_URL,edtInputData2.getText().toString().trim());
                                dialog.dismiss();
                            }
                            else
                            {
                                Toasts.showToast(context,"Invalid URL!",1);
                            }
                            break;

                        case config.LIST_CONFIGACTION:
                            xdata.getinstance().saveSetting(config.XAPI_CONFIGACTION,edtInputData2.getText().toString().trim());
                            String config = edtInputData2.getText().toString();
                            ConfigUtil.configaction(config);

                            dialog.dismiss();
                            break;
*/
                       /* case config.LIST_SETTINGS:
                            if(edtInputData1.getText().toString().trim().length()>0)
                            {
                                xdata.getinstance().saveSettingArray(edtInputData1.getText().toString().trim(),edtInputData2.getText().toString().trim());
                                mItemChanged.onItemChanged(null);
                                dialog.dismiss();
                            }
                            break;*/

                        default:
                            break;
                    }
                }
            }
        });

        TxtNegativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void showPopup(String Title,String Message)
    {
        //AppDialog.showDialog(ApplicationCoreBuild.getActivity(),Title,Message).show();
    }

    public static void showHidePopupView(final Context context){

        if(dialog != null && dialog.isShowing())
            dialog.dismiss();

        dialog=new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);

        dialog.setContentView(R.layout.xapi_popupview);

        final EditText editTextConfig = (EditText)dialog.findViewById(R.id.edt_inputdata_2);

        TextView txtTitle = (TextView)dialog.findViewById(R.id.txt_title);
        TextView txtDescription = (TextView)dialog.findViewById(R.id.txt_description);

        TextView TxtPositiveButton = (TextView)dialog.findViewById(R.id.tv_positive);
        final TextView TxtNegativeButton = (TextView)dialog.findViewById(R.id.tv_negative);


        TxtPositiveButton.setText("Run");
        TxtPositiveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                /*String configAction = editTextConfig.getText().toString().trim();
                configutil.configaction(configAction);*/

            }
        });

        dialog.show();

    }

    /*public static void popupServerDownRetry(final Context context, final itemchanged mItemChanged){

        if(dialog != null && dialog.isShowing())
            dialog.dismiss();

        dialog=new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        dialog.setContentView(R.layout.xapi_popup_retry);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        TextView txtTitle = (TextView)dialog.findViewById(R.id.txt_title);
        final TextView txtDescription = (TextView)dialog.findViewById(R.id.txt_description);

        TextView TxtPositiveButton = (TextView)dialog.findViewById(R.id.tv_positive);

        Common.changeFocusStyle(TxtPositiveButton,Color.parseColor("#00FFFFFF")
                ,Color.parseColor("#0088fe"),10);

        txtTitle.setText(context.getResources().getString(R.string.couldnt_load_apps));

        if(mCounterTimer != null)
            mCounterTimer.cancel();

        mCounterTimer=new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                Log.e("Timer running", " Tick");
                txtDescription.setText("Will  attempt to connect in : \n"+"" + millisUntilFinished / 1000+" seconds");
            }

            public void onFinish() {
                Log.e("Timer finish", " Api hit");
                mItemChanged.onItemChanged(context.getResources().getString(R.string.try_again));
                if(mCounterTimer != null)
                    mCounterTimer.cancel();

                if(dialog != null && dialog.isShowing())
                    dialog.dismiss();
            }

        }.start();


        txtDescription.setText(context.getResources().getString(R.string.will_attempt_to_connect)+"\n"+" seconds");

        TxtPositiveButton.setText(context.getResources().getString(R.string.try_again));
        TxtPositiveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mItemChanged.onItemChanged(context.getResources().getString(R.string.try_again));
                if(mCounterTimer != null)
                    mCounterTimer.cancel();

                if(dialog != null && dialog.isShowing())
                    dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public static void dialogHide(){

        if(dialog != null && dialog.isShowing())
           dialog.dismiss();
    }*/
}