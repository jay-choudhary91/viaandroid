package com.deeptruth.app.android.fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.Purchase;
import com.deeptruth.app.android.R;
import com.deeptruth.app.android.activity.baseactivity;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.inapputils.IabHelper;
import com.deeptruth.app.android.inapputils.IabResult;
import com.deeptruth.app.android.interfaces.adapteritemclick;
import com.deeptruth.app.android.interfaces.apiresponselistener;
import com.deeptruth.app.android.utils.appdialog;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.pinviewtext;
import com.deeptruth.app.android.utils.progressdialog;
import com.deeptruth.app.android.utils.taskresult;
import com.deeptruth.app.android.utils.xdata;
import com.deeptruth.app.android.videotrimmer.hglvideotrimmer;
import com.deeptruth.app.android.videotrimmer.interfaces.onhglvideolistener;
import com.deeptruth.app.android.videotrimmer.interfaces.ontrimvideolistener;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;


public class fragmentrimvideo extends DialogFragment implements View.OnClickListener, ontrimvideolistener, onhglvideolistener {
    @BindView(R.id.trimerview)
    hglvideotrimmer mvideotrimmer;
    @BindView(R.id.rootlayout)
    CardView rootlayout;
    @BindView(R.id.lyout_publish)
    LinearLayout lyoutpublish;
    @BindView(R.id.lyout_send)
    LinearLayout lyoutsend;
    @BindView(R.id.lyout_export)
    LinearLayout lyoutexport;
    @BindView(R.id.lyout_help)
    LinearLayout lyouthelp;
    @BindView(R.id.img_cancel)
    ImageView img_cancel;

    private progressdialog mprogressdialog;
    View rootview = null;
    String videopath="",videotoken="";
    int videoduration;
    int navigationbarheight = 0;

    IabHelper mHelper;
    // Debug tag, for logging
    final String TAG = "TestInApp";
    String SelectedSku = "";
    Dialog dialogtrimevideo=null,dialogupgradecode=null;

    /*@Override
    public int getlayoutid() {
        return R.layout.fragment_trimvideo;
    }

    @Override
    public void initviews(View parent, Bundle savedInstanceState) {
        super.initviews(parent, savedInstanceState);

        gethelper().updateheader("");
        ButterKnife.bind(this, parent);
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(rootview == null) {
            rootview = inflater.inflate(R.layout.fragment_trimvideo, container, false);

            ButterKnife.bind(this, rootview);
            navigationbarheight =  common.getnavigationbarheight();
            setlayoutmargin();

            lyoutpublish.setOnClickListener(this);
            lyoutsend.setOnClickListener(this);
            lyoutexport.setOnClickListener(this);
            lyouthelp.setOnClickListener(this);
            img_cancel.setOnClickListener(this);

            if (mvideotrimmer != null) {
                mvideotrimmer.setMaxDuration(videoduration);
                mvideotrimmer.setOnTrimVideoListener(this);
                mvideotrimmer.setOnHgLVideoListener(this);
                mvideotrimmer.setVideoURI(Uri.parse(videopath));
                mvideotrimmer.setVideoInformationVisibility(true);
            }

        }

        rootview.post(new Runnable() {
            @Override
            public void run() {
                if(common.getapppaidlevel() <= 0)
                {
                    //showtrimfeaturealert();
                    showinapppurchasepopup(applicationviavideocomposer.
                            getactivity(), "", new adapteritemclick(){

                        @Override
                        public void onItemClicked(Object object) {

                        }

                        @Override
                        public void onItemClicked(Object object, int type) {

                        }
                    });
                }
            }
        });
        return rootview;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.lyout_publish:
                String publish = getActivity().getResources().getString(R.string.publish_details1)+"\n"+"\n"+"\n"+getActivity().getResources().getString(R.string.publish_details2);
                if(xdata.getinstance().getSetting(config.enablenotification).isEmpty() ||
                        xdata.getinstance().getSetting(config.enablenotification).equalsIgnoreCase("0")) {

                    /*if (xdata.getinstance().getSetting(config.enablenotification).isEmpty())
                        xdata.getinstance().saveSetting(config.enablenotification, "0");*/

                    appdialog.share_alert_dialog(getActivity(),getActivity().getResources().getString(R.string.txt_publish),publish);

                }
                break;

            case R.id.lyout_send:
                String send = getActivity().getResources().getString(R.string.send_details1)+"\n"+"\n"+"\n"+getActivity().getResources().getString(R.string.send_details2);
                appdialog.share_alert_dialog(getActivity(),getActivity().getResources().getString(R.string.txt_send),send);

                break;

            case R.id.lyout_export:
                String export = getActivity().getResources().getString(R.string.export_details1)+"\n"+"\n"+"\n"+getActivity().getResources().getString(R.string.export_details2);
                appdialog.share_alert_dialog(getActivity(),getActivity().getResources().getString(R.string.txt_export),export);

                break;

            case R.id.img_cancel:
                getDialog().dismiss();
                break;

        }
    }


    @Override
    public void ontrimstarted() {
    }

    @Override
    public void getresult(Uri uri) {

    }

    @Override
    public void getresult(final String filePath) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                progressdialog.dismisswaitdialog();
                if(filePath != null){
                    //progressdialog.showwaitingdialog(getActivity());
                    String selectedvideopath = filePath;
                    getDialog().dismiss();
                   baseactivity.getinstance().showsharepopupsub(selectedvideopath,"video",videotoken);
                    //common.sharevideo(getActivity(),selectedvideopath);
                }
            }
        });
    }


    @Override
    public void onPause() {
        super.onPause();
        progressdialog.dismisswaitdialog();
    }

    @Override
    public void cancelaction() {
        //mvideotrimmer.destroy();
    }

    @Override
    public void onerror(final String message) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onclik() {

        getDialog().dismiss();

        //gethelper().onBack();
       // gethelper().showsharepopupsub(videopath,"video",videotoken);
    }

    @Override
    public void onvideoprepared() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Toast.makeText(getactivity(), "onvideoprepared", Toast.LENGTH_SHORT).show();
            }
        });
    }

   /* @Override
    public void onHeaderBtnClick(int btnid) {
        super.onHeaderBtnClick(btnid);
        switch (btnid)
        {
            case R.id.img_share_icon:

                if(mvideotrimmer != null)
                {
                    //progressdialog.showwaitingdialog(getActivity());
                    mvideotrimmer.onSaveClicked();
                }

                break;

            case R.id.img_menu:
                gethelper().onBack();
                break;
        }
    }
*/

    @Override
    public void onResume() {
        super.onResume();

        getscreenwidthheight(100,85);
    }

    public void showdialog(){
        if(common.getapppaidlevel() <= 0)
        {
            //showtrimfeaturealert();
            showinapppurchasepopup(applicationviavideocomposer.
                    getactivity(), "", new adapteritemclick(){

                @Override
                public void onItemClicked(Object object) {
                    inapppurchase(object.toString());
                }

                @Override
                public void onItemClicked(Object object, int type) {

                }
            });
            return;
        }
    }

    public void setdata(String videoPath, int duration, String videotoken)
    {
        this.videopath =videoPath;
        this.videoduration =  duration;
        this.videotoken =  videotoken;

    }

    public void setlayoutmargin(){
        CardView.LayoutParams params = new CardView.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,0,0,navigationbarheight);
        rootlayout.setLayoutParams(params);
        rootlayout.requestLayout();
    }

    public void getscreenwidthheight(int widthpercentage,int heightpercentage) {


        int width = common.getScreenWidth(getActivity());
        int height = common.getScreenHeight(getActivity());

        int percentageheight = (height / 100) * heightpercentage;
        int percentagewidth = (width / 100) * widthpercentage;

        getDialog().getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        getDialog().getWindow().setLayout(percentagewidth, percentageheight);
    }


    public void showinapppurchasepopup(final Context activity, String message, final adapteritemclick mitemclick)
    {
        dialogtrimevideo =new Dialog(activity);
        dialogtrimevideo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogtrimevideo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogtrimevideo.setCanceledOnTouchOutside(false);
        dialogtrimevideo.setCancelable(true);
        dialogtrimevideo.setContentView(R.layout.inapp_purchase_popup);

        TextView txt_content = (TextView) dialogtrimevideo.findViewById(R.id.txt_content);
        TextView tv_purchase1 = (TextView) dialogtrimevideo.findViewById(R.id.tv_purchase1);
        TextView tv_purchase2 = (TextView) dialogtrimevideo.findViewById(R.id.tv_purchase2);
        TextView tv_upgradecode = (TextView) dialogtrimevideo.findViewById(R.id.tv_upgradecode);
        TextView tvcancel = (TextView) dialogtrimevideo.findViewById(R.id.btn_nothanks);
        TextView txt_title = (TextView) dialogtrimevideo.findViewById(R.id.txt_title);

        String content = applicationviavideocomposer.getactivity().getResources().getString(R.string.txt_trim)+"\n"+"\n"+ applicationviavideocomposer.getactivity().getResources().getString(R.string.txt_upgrade);
        txt_content.setTypeface(applicationviavideocomposer.comfortaaregular, Typeface.BOLD);
        tv_purchase1.setTypeface(applicationviavideocomposer.comfortaaregular, Typeface.BOLD);
        tv_purchase2.setTypeface(applicationviavideocomposer.comfortaaregular, Typeface.BOLD);
        tv_upgradecode.setTypeface(applicationviavideocomposer.comfortaaregular, Typeface.BOLD);
        tvcancel.setTypeface(applicationviavideocomposer.comfortaaregular, Typeface.BOLD);
        txt_title.setTypeface(applicationviavideocomposer.bahnschriftregular, Typeface.BOLD);
        txt_content.setText(content);

        tv_upgradecode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showupgradecodepopup(applicationviavideocomposer.getactivity());
            }
        });

        tv_purchase1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dialogtrimevideo != null && dialogtrimevideo.isShowing())
                    dialogtrimevideo.dismiss();


                if(mitemclick != null)
                    mitemclick.onItemClicked("ABC");
            }
        });

        tv_purchase2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dialogtrimevideo != null && dialogtrimevideo.isShowing())
                    dialogtrimevideo.dismiss();


                if(mitemclick != null)
                    mitemclick.onItemClicked("ABC");
            }
        });

        tvcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dialogtrimevideo != null && dialogtrimevideo.isShowing())
                    dialogtrimevideo.dismiss();
                    getDialog().dismiss();
                    baseactivity.getinstance().showsharepopupsub(videopath,"video",videotoken);
            }
        });

        dialogtrimevideo.show();
    }

    public void showupgradecodepopup(final Context activity)
    {
        dialogupgradecode =new Dialog(activity);
        dialogupgradecode.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogupgradecode.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogupgradecode.setCanceledOnTouchOutside(false);
        dialogupgradecode.setCancelable(true);
        dialogupgradecode.setContentView(R.layout.upgradecode_popup);

        TextView txt_content = (TextView) dialogupgradecode.findViewById(R.id.txt_content);
        TextView tv_upgrade = (TextView) dialogupgradecode.findViewById(R.id.tv_upgrade);
        TextView tvcancel = (TextView) dialogupgradecode.findViewById(R.id.btn_nothanks);
        TextView txt_title = (TextView) dialogupgradecode.findViewById(R.id.txt_title);
        final pinviewtext pinview = (pinviewtext) dialogupgradecode.findViewById(R.id.pinview);

        String content = applicationviavideocomposer.getactivity().getResources().getString(R.string.txt_upgradecode);
        txt_content.setTypeface(applicationviavideocomposer.comfortaaregular, Typeface.BOLD);
        tvcancel.setTypeface(applicationviavideocomposer.comfortaaregular, Typeface.BOLD);
        tv_upgrade.setTypeface(applicationviavideocomposer.comfortaaregular, Typeface.BOLD);
        txt_title.setTypeface(applicationviavideocomposer.bahnschriftregular, Typeface.BOLD);
        txt_content.setText(content);


        tv_upgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pinview.getValue().trim().length() >= 6)
                {

                    HashMap<String,String> requestparams=new HashMap<>();
                    requestparams.put("code",pinview.getValue().trim());
                    requestparams.put("action","appunlockcode_use");
                    progressdialog.showwaitingdialog(applicationviavideocomposer.
                            getactivity());
                    baseactivity.getinstance().xapipost_send(applicationviavideocomposer.
                            getactivity(),requestparams, new apiresponselistener() {
                        @Override
                        public void onResponse(taskresult response) {
                            progressdialog.dismisswaitdialog();
                            if(response.isSuccess())
                            {
                                try {
                                    JSONObject object=new JSONObject(response.getData().toString());
                                    if(object.has("success"))
                                    {
                                        if(object.has("message"))
                                            Toast.makeText(applicationviavideocomposer.getactivity(), object.getString("message"), Toast.LENGTH_SHORT).show();

                                            dialogupgradecode.dismiss();
                                            dialogtrimevideo.dismiss();
                                    }

                                }catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }
                else
                {
                    Toast.makeText(applicationviavideocomposer.getactivity(), "Please enter 6 digit code!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dialogupgradecode != null && dialogupgradecode.isShowing())
                    dialogupgradecode.dismiss();
            }
        });

        dialogupgradecode.show();
    }

    public void inapppurchase(final String productId)
    {
        SelectedSku=productId;
        if (mHelper != null)
            mHelper.flagEndAsync();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    /*mHelper.launchPurchaseFlow(baseactivity.this, productId, 10001,
                            mPurchaseFinishedListener, "mypurchasetoken");*/
                    mHelper.launchPurchaseFlow(applicationviavideocomposer.getactivity(), "android.test.purchased", 10001,
                            mPurchaseFinishedListener, "purchasetoken");

                } catch (IabHelper.IabAsyncInProgressException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        @Override
        public void onIabPurchaseFinished(IabResult result, com.deeptruth.app.android.inapputils.Purchase info) {

        }

        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            /*if (result.isFailure()) {
                return;
            }*/

            switch (result.getResponse())
            {
                /*case 0:
                    ConfigUtil.configaction("alert|"+result.getMessage());
                    break;
                case 1:
                    ConfigUtil.configaction("alert|"+result.getMessage());
                    break;
                case 2:
                    ConfigUtil.configaction("alert|"+result.getMessage());
                    break;
                case 3:
                    configutil.configaction("alert|"+result.getMessage());
                    break;
                case 4:
                {
                    String message=xData.getInstance().getSetting("inapppurchase_invalidproduct_message");
                    message=message.replaceAll("PRODUCT",SelectedSku);
                    ConfigUtil.configaction("alert|"+message);
                }
                break;
                case 5:
                    ConfigUtil.configaction("alert|"+result.getMessage());
                    break;
                case 6:
                    ConfigUtil.configaction("alert|"+result.getMessage());
                    break;
                case 7:
                    ConfigUtil.configaction("alert|"+result.getMessage());
                    break;
                case 8:
                    ConfigUtil.configaction("alert|"+result.getMessage());
                    break;*/
            }


            // Response code 7 => item already purchased
        }
    };
}
