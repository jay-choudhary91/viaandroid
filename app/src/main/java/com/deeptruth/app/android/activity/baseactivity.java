package com.deeptruth.app.android.activity;

import android.app.Activity;
import android.app.Dialog;
import android.arch.lifecycle.Lifecycle;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.android.billingclient.api.Purchase;
import com.android.vending.billing.IInAppBillingService;
import com.deeptruth.app.android.R;
import com.deeptruth.app.android.adapter.adaptersenddialog;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.fragments.audiocomposerfragment;
import com.deeptruth.app.android.fragments.audioreaderfragment;
import com.deeptruth.app.android.fragments.basefragment;
import com.deeptruth.app.android.fragments.composeoptionspagerfragment;
import com.deeptruth.app.android.fragments.fragmentmedialist;
import com.deeptruth.app.android.fragments.fragmentrimvideo;
import com.deeptruth.app.android.fragments.imagecomposerfragment;
import com.deeptruth.app.android.fragments.imagereaderfragment;
import com.deeptruth.app.android.fragments.videocomposerfragment;
import com.deeptruth.app.android.fragments.videoreaderfragment;
import com.deeptruth.app.android.interfaces.adapteritemclick;
import com.deeptruth.app.android.interfaces.apiresponselistener;
import com.deeptruth.app.android.interfaces.homepressedlistener;
import com.deeptruth.app.android.netutils.connectivityreceiver;
import com.deeptruth.app.android.netutils.xapi;
import com.deeptruth.app.android.netutils.xapipost;
import com.deeptruth.app.android.netutils.xapipostjson;
import com.deeptruth.app.android.netutils.xapipostfile;
import com.deeptruth.app.android.inapputils.IabBroadcastReceiver;
import com.deeptruth.app.android.inapputils.IabHelper;
import com.deeptruth.app.android.inapputils.IabResult;
import com.deeptruth.app.android.utils.pinviewtext;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.homewatcher;
import com.deeptruth.app.android.utils.progressdialog;
import com.deeptruth.app.android.utils.taskresult;
import com.deeptruth.app.android.utils.xdata;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

public abstract class baseactivity extends AppCompatActivity implements basefragment.fragmentnavigationhelper,
        connectivityreceiver.ConnectivityReceiverListener {
    public static baseactivity instance;
    public boolean isapprunning = false;
    private basefragment mcurrentfragment;
    private SharedPreferences prefs;
    static Dialog subdialogshare = null;
    private Stack<Fragment> mfragments = new Stack<Fragment>();
    private static final int permission_location_request_code = 91;
    String serverresponsemessage = "";
    int serverresponsecode = 0;
    private BroadcastReceiver broadcastshareapiafterlogin;
    String mediapath = "";
    String mediatype = "";
    String mediavideotoken = "",mediamethod = "";
    Dialog dialog;

    // The helper object
    IabHelper mHelper;

    // Debug tag, for logging
    final String TAG = "TestInApp";
    String SelectedSku = "";
    //String SKU_ID="com.matraex.xapi.hackcheck.inapp.credits";
    //String ItemApple = "android.test.purchased";

    // Provides purchase notification while this app is running
    IabBroadcastReceiver mBroadcastReceiver;

    String Base64Key="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkeYT6cIVSzimdAdqOBYjDawrRzLzKLXhcsPquXMb+ICvc9aPCGE5txPbYCNkuK72lTGYfGYUBgJqCItyEE0LDLi49YZQKPbcwf8Zs13L0X9Lnrw0lDdWhoN815+Z5YZ4ZfZ0KXxJU3gvpfg8vroYf3twEH87XTp13+lNHuUecj7djJy8N+oV0x0XAb3JlfZ1JoDaQuQ6Sry+0Ab8AIPHwWBLtnAPjE+m1RTD5PXFHQCSf7jiBvSJb6JkzvM0EDiwBE1YuW9BM7iPcqY4jgW0qyDfUzdwG+tV1durLw0qr7/VN7P7TCYt2mYBrRBc+vE0Xini5Fw47o3+e3C0O3LiVwIDAQAB";

    IInAppBillingService mService;
    ServiceConnection mServiceConn;
    Bundle skuDetails;

    public boolean isisapprunning() {
        return isapprunning;
    }

    homewatcher mHomeWatcher;

    public static baseactivity getinstance() {
        return instance;
    }

    public void isapprunning(boolean b) {
        isapprunning = b;
    }

    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        applicationviavideocomposer.setActivity(baseactivity.this);
        prefs = getSharedPreferences(config.prefs_name, Context.MODE_PRIVATE);

        isapprunning = true;
        instance = this;

        LayoutInflater inflater = getLayoutInflater();
        View contentview = inflater.inflate(getlayoutid(), null);
        setContentView(contentview);

        initviews(savedInstanceState);

        mHomeWatcher = new homewatcher(this);
        mHomeWatcher.setOnHomePressedListener(new homepressedlistener() {
            @Override
            public void onHomePressed() {
                // do something here...
                //finishactivity();
            }

            @Override
            public void onHomeLongPressed() {
            }
        });
        mHomeWatcher.startWatch();

        try
        {
            mHelper = new IabHelper(baseactivity.this, Base64Key.trim());

            mHelper.startSetup(new
                                       IabHelper.OnIabSetupFinishedListener() {
                                           public void onIabSetupFinished(IabResult result)
                                           {
                                               if (!result.isSuccess()) {
                                                   Log.d(TAG, "In-app Billing setup failed: " +
                                                           result);
                                               } else {
                                                   Log.d(TAG, "In-app Billing is set up OK");
                                               }
                                           }
                                       });

            mServiceConn = new ServiceConnection() {
                @Override
                public void onServiceDisconnected(ComponentName name) {
                    mService = null;
                }

                @Override
                public void onServiceConnected(ComponentName name,IBinder service) {
                    mService = IInAppBillingService.Stub.asInterface(service);
                }
            };
            Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
            serviceIntent.setPackage("com.android.vending");
            bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
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
                    mHelper.launchPurchaseFlow(baseactivity.this, "android.test.purchased", 10001,
                            mPurchaseFinishedListener, "purchasetoken");

                } catch (IabHelper.IabAsyncInProgressException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onnetworkconnectionchanged(boolean isconnected) {
        if (isconnected) {

        }
    }

    public basefragment getcurrentfragment() {
        return mcurrentfragment;
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (getcurrentfragment() instanceof videoreaderfragment) {
            try {
                ((videoreaderfragment) getcurrentfragment()).onRestart();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (getcurrentfragment() instanceof audioreaderfragment) {
            try {
                ((audioreaderfragment) getcurrentfragment()).onRestart();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101) {
            progressdialog.dismisswaitdialog();
        }
        else if (requestCode == config.requestcode_login)
        {
            if(resultCode == RESULT_OK) {
                callsharapiafterlogin();
            }
        }
        else if (requestCode == config.requestcode_googlesignin)
        {
            if (resultCode == Activity.RESULT_OK && data != null) {
                Fragment fragment = getSupportFragmentManager().findFragmentByTag("dialog");
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHomeWatcher != null)
            mHomeWatcher.stopWatch();

        try {
            if(mServiceConn != null)
                unbindService(mServiceConn);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void initviews(Bundle savedInstanceState) {

    }

    public abstract int getlayoutid();

    @Override
    public void addFragment(basefragment f, boolean clearBackStack, boolean addToBackstack) {
        addFragment(f, R.id.fragment_container, clearBackStack, addToBackstack);
    }

    public void addFragment(basefragment f, int layoutId, boolean clearBackStack, boolean addToBackstack) {
        if (clearBackStack) {
            clearfragmentbackstack();
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.add(R.id.fragment_container, f);
        if (addToBackstack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();

        mcurrentfragment = f;
        mfragments.push(f);

        onfragmentbackstackchanged();
    }

    @Override
    public void replaceFragment(basefragment f, boolean clearBackStack, boolean addToBackstack) {
        replaceFragment(f, R.id.fragment_container, clearBackStack, addToBackstack);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.activityfadein, R.anim.activityfadeout);
    }

    public void replaceFragment(basefragment f, int layoutId, boolean clearBackStack, boolean addToBackstack) {
        if (clearBackStack) {
            clearfragmentbackstack();
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // transaction.setCustomAnimations(R.anim.card_flip_right_in, R.anim.card_flip_right_out, R.anim.card_flip_left_in, R.anim.card_flip_left_out);
        transaction.replace(layoutId, f);
        if (addToBackstack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();

        mcurrentfragment = f;
        mfragments.push(f);

        onfragmentbackstackchanged();
    }

    @Override
    public void replacetabfragment(basefragment f, boolean clearBackStack, boolean addToBackstack) {
        replacetabfragment(f, R.id.tab_container, clearBackStack, addToBackstack);
    }

    public void replacetabfragment(basefragment f, int layoutId, boolean clearBackStack, boolean addToBackstack) {
        if (clearBackStack) {
            clearfragmentbackstack();
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.fadein, R.anim.fadeout);
        transaction.replace(layoutId, f);
        if (addToBackstack) {
            transaction.addToBackStack(null);
        }

        // Placed after crash on 2018-11-16
        if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
            // Do fragment's transaction commit
            transaction.commit();
        }

        mcurrentfragment = f;
        mfragments.push(f);

        onfragmentbackstackchanged();
    }

    public int minnumberoffragments = 1;

    public int getMinNumberOfFragments() {
        return minnumberoffragments;
    }

    @Override
    public void switchtomedialist() {
        if (getcurrentfragment() instanceof composeoptionspagerfragment) {
            if (mfragments.size() == 1) {
                mfragments.pop();
                clearfragmentstack();
                launchmedialistfragment();
            } else {
                backtolastfragment();
                switchtomedialist();
            }
        } else if (getcurrentfragment() instanceof fragmentmedialist) {

        } else {
            backtolastfragment();
            switchtomedialist();
        }
    }

    @Override
    public void onBack() {

        int a = getSupportFragmentManager().getBackStackEntryCount();
        int b = getMinNumberOfFragments();

        int size = mfragments.size();
        if (getSupportFragmentManager().getBackStackEntryCount() <= getMinNumberOfFragments()) {
            finishactivity();
            return;
        }

        if (getcurrentfragment() instanceof videocomposerfragment || getcurrentfragment() instanceof audiocomposerfragment
                || getcurrentfragment() instanceof imagecomposerfragment) {
            backtolastfragment();
            onBack();
        } else if (getcurrentfragment() instanceof audioreaderfragment
                || getcurrentfragment() instanceof videoreaderfragment || getcurrentfragment() instanceof imagereaderfragment) {
            backtolastfragment();

        } else if (getcurrentfragment() instanceof fragmentmedialist) {
            finishactivity();
        } else if (getcurrentfragment() instanceof composeoptionspagerfragment) {
            if (mfragments.size() == 1) {
                clearfragmentstack();
                launchmedialistfragment();
            } else {
                backtolastfragment();
            }
        } else {
            backtolastfragment();
        }
    }

    public void launchmedialistfragment() {
        fragmentmedialist frag = new fragmentmedialist();
        replaceFragment(frag, false, true);
    }

    public void backtolastfragment() {
        getSupportFragmentManager().popBackStack();
        if (mfragments.size() > 0) {
            mfragments.pop();
            mcurrentfragment = (basefragment) (mfragments.isEmpty() ? null : ((mfragments.peek()
                    instanceof basefragment) ? mfragments.peek() : null));
            onfragmentbackstackchanged();
        }

    }

    public void clearfragmentstack()
    {
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount() - getMinNumberOfFragments(); i++) {
            fm.popBackStack();
        }

        if (!mfragments.isEmpty()) {
            mfragments.clear();
        }
    }

    public void clearfragmentbackstack() {
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount() - getMinNumberOfFragments(); i++) {
            fm.popBackStack();
        }

        if (!mfragments.isEmpty()) {
            Fragment homefragment = mfragments.get(0);
            mcurrentfragment = (basefragment) homefragment;
            mfragments.clear();
            mfragments.push(homefragment);
        }
    }

    public void onfragmentbackstackchanged() {
        if (mcurrentfragment != null) {
            mcurrentfragment.updateheader();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == permission_location_request_code) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay! Do the
                // contacts-related task you need to do.

            }
            return;
        }
//        if (getcurrentfragment() != null) {
//            getcurrentfragment().onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent event) {
        if (keycode == KeyEvent.KEYCODE_BACK) {

            if (getSupportFragmentManager().getBackStackEntryCount() <= getMinNumberOfFragments()) {
                finishactivity();
                return true;
            } else {
                onBack();
                return true;
            }
        }
        return super.onKeyDown(keycode, event);
    }


    public void hidekeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public void showkeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public SharedPreferences getPrefs() {
        return prefs;
    }

    @Override
    protected void onPause() {
        super.onPause();
        getinstance().isapprunning(false);
    }


    @Override
    public void xapi_send(Context mContext, HashMap<String, String> mPairList, apiresponselistener mListener) {
        xapi api = new xapi(mContext, mListener);
        Set keys = mPairList.keySet();
        Iterator itr = keys.iterator();
        while (itr.hasNext()) {
            String key = (String) itr.next();
            String argvalue = (String) mPairList.get(key);
            api.add(key, argvalue);
        }
        api.execute();
    }

    @Override
    public void xapipost_send(Context mContext, HashMap<String, String> mPairList, apiresponselistener mListener) {
        xapipost api = new xapipost(mContext, mListener);
        Set keys = mPairList.keySet();
        Iterator itr = keys.iterator();
        while (itr.hasNext()) {
            String key = (String) itr.next();
            String argvalue = (String) mPairList.get(key);
            api.add(key, argvalue);
        }
        api.execute();
    }

    @Override
    public void xapipost_sendjson(Context mContext, String Action, HashMap<String, Object> mPairList, apiresponselistener mListener) {
        xapipostjson api = new xapipostjson(mContext, Action, mListener);
        Set keys = mPairList.keySet();
        Iterator itr = keys.iterator();
        while (itr.hasNext()) {
            String key = (String) itr.next();
            Object argvalue = mPairList.get(key);
            api.add(key, argvalue);
        }
        if(Build.VERSION.SDK_INT >= 11)
            api.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            api.execute();
    }


    @Override
    public void xapi_uploadfile(Context mContext, String serverurl, String filepath, apiresponselistener mListener) {
        xapipostfile xapiupload = new xapipostfile(mContext,serverurl,filepath,mListener);
        xapiupload.execute();
    }

    public void showsharepopupsub(final String path, final String type, final String mediatoken) {
            if (subdialogshare != null && subdialogshare.isShowing())
                subdialogshare.dismiss();

            mediapath = path;
            mediatype = type;
            mediavideotoken = mediatoken;

            subdialogshare = new Dialog(applicationviavideocomposer.getactivity());
            subdialogshare.requestWindowFeature(Window.FEATURE_NO_TITLE);
            subdialogshare.setCanceledOnTouchOutside(true);

            subdialogshare.setContentView(R.layout.share_popup);
            //subdialogshare.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            int[] widthHeight = common.getScreenWidthHeight(applicationviavideocomposer.getactivity());
            int width = widthHeight[0];
            double height = widthHeight[1] / 1.6;
            subdialogshare.getWindow().setLayout(width - 20, (int) height);

            final LinearLayout linear_share_btn1 = (LinearLayout) subdialogshare.findViewById(R.id.linear_share_btn1);
            final LinearLayout linear_share_btn2 = (LinearLayout) subdialogshare.findViewById(R.id.linear_share_btn2);
            final LinearLayout linear_share_btn3 = (LinearLayout) subdialogshare.findViewById(R.id.linear_share_btn3);
            final LinearLayout linear_share_btn4 = (LinearLayout) subdialogshare.findViewById(R.id.linear_share_btn4);
            TextView txt_title1 = (TextView) subdialogshare.findViewById(R.id.txt_title1);
            TextView txt_title2 = (TextView) subdialogshare.findViewById(R.id.txt_title2);
            ImageView img_cancel = subdialogshare.findViewById(R.id.img_cancelicon);
            ImageView img_lock_trimmer = subdialogshare.findViewById(R.id.img_lock_trimmer);

            if(common.getapppaidlevel() <= 0)
                img_lock_trimmer.setVisibility(View.VISIBLE);

            linear_share_btn4.setVisibility(View.GONE);
            if(type.equalsIgnoreCase(config.item_video))
            linear_share_btn4.setVisibility(View.VISIBLE);

            linear_share_btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediamethod = config.type_private;
                if(!isuserlogin()){
                    redirecttologin();
                    return;
                }
                callmediashareapi(type, mediatoken, path, mediamethod);
            }
            });

            linear_share_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediamethod = config.type_public;
                if(!isuserlogin()){
                    redirecttologin();
                    return;
                }
                callmediashareapi(type , mediatoken, path, mediamethod);
            }
            });

            linear_share_btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediamethod = config.type_linkinvite;
                if(!isuserlogin()){
                    redirecttologin();
                    return;
                }
                callmediashareapi(type, mediatoken, path, mediamethod);
            }
            });

            linear_share_btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    /*if(common.getapppaidlevel() <= 0)
                    {
                        //showtrimfeaturealert();
                        showinapppurchasepopup(applicationviavideocomposer.
                                getactivity(), applicationviavideocomposer.getactivity().getResources().
                                getString(R.string.sharing_a_trimmed_video), new adapteritemclick() {
                            @Override
                            public void onItemClicked(Object object) {
                                inapppurchase(object.toString());
                            }

                            @Override
                            public void onItemClicked(Object object, int type) {

                            }
                        });
                        return;
                    }*/

                if (subdialogshare != null && subdialogshare.isShowing())
                    subdialogshare.dismiss();

                /*if(getcurrentfragment() instanceof fragmentrimvideo)
                    return;*/

                if(type.equalsIgnoreCase(config.item_video))
                {
                    Uri selectedimageuri = Uri.fromFile(new File(path));
                    final MediaPlayer mp = MediaPlayer.create(applicationviavideocomposer.getactivity(), selectedimageuri);
                    if (mp != null) {
                        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mediaPlayer) {
                                int duration = mediaPlayer.getDuration();
                                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
                                if (prev != null) {
                                    ft.remove(prev);
                                }
                                ft.addToBackStack(null);
                                fragmentrimvideo fragtrimvideo = new fragmentrimvideo();
                                fragtrimvideo.setdata(mediapath, duration,mediatoken);
                                fragtrimvideo.show(ft, "dialog");

                               /* int duration = mediaPlayer.getDuration();
                                fragmentrimvideo fragtrimvideo = new fragmentrimvideo();
                                fragtrimvideo.setdata(mediapath, duration,mediatoken);
                                addFragment(fragtrimvideo, false, true);*/
                            }
                        });
                    }
                }
            }
            });

            img_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (subdialogshare != null && subdialogshare.isShowing())
                subdialogshare.dismiss();
            }
            });
            subdialogshare.show();
    }

    
    public void showinapppurchasepopup(final Context activity, String message, final adapteritemclick mitemclick)
    {
        final Dialog dialog =new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_inapppurchase_options);

        TextView txt_purchase1 = (TextView) dialog.findViewById(R.id.txt_purchase1);
        TextView txt_purchase2 = (TextView) dialog.findViewById(R.id.txt_purchase2);
        TextView TxtPositiveButton = (TextView) dialog.findViewById(R.id.tv_positive);
        TextView txt_message = (TextView) dialog.findViewById(R.id.txt_message);
        ImageView img_cancelicon = (ImageView) dialog.findViewById(R.id.img_cancelicon);
        final pinviewtext pinview = (pinviewtext) dialog.findViewById(R.id.pinview);

        txt_message.setText(message);
        TxtPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(pinview.getValue().trim().length() >= 6)
                {

                    HashMap<String,String> requestparams=new HashMap<>();
                    requestparams.put("code",pinview.getValue().trim());
                    requestparams.put("action","appunlockcode_use");
                    progressdialog.showwaitingdialog(baseactivity.this);
                    xapipost_send(baseactivity.this,requestparams, new apiresponselistener() {
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
                                            Toast.makeText(baseactivity.this, object.getString("message"), Toast.LENGTH_SHORT).show();

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
                    Toast.makeText(baseactivity.this, "Please enter 6 digit code!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        txt_purchase1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dialog != null && dialog.isShowing())
                    dialog.dismiss();

                if(mitemclick != null)
                    mitemclick.onItemClicked("ABC");
            }
        });

        txt_purchase2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dialog != null && dialog.isShowing())
                    dialog.dismiss();

                if(mitemclick != null)
                    mitemclick.onItemClicked("ABC");
            }
        });

        img_cancelicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dialog != null && dialog.isShowing())
                    dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void showtrimfeaturealert() {
        try
        {
            new AlertDialog.Builder(baseactivity.this, R.style.customdialogtheme)
                    .setTitle("Alert")
                    .setMessage("Sharing a trimmed version is an advanced feature.")
                    .setPositiveButton("Upgrade", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (dialog != null)
                                dialog.dismiss();
                        }
                    })
                    .show();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public void callmediashareapi(final String mediatype, final String mediatoken, final String path, String method) {

        HashMap<String, String> requestparams = new HashMap<>();
        requestparams.put("type", mediatype);
        requestparams.put("action", "share");
        if(mediatype.equalsIgnoreCase(config.item_image))
            requestparams.put("imagetoken", mediatoken);
        else if(mediatype.equalsIgnoreCase(config.item_audio))
            requestparams.put("audiotoken", mediatoken);
        else if(mediatype.equalsIgnoreCase(config.item_video))
            requestparams.put("videotoken", mediatoken);

        requestparams.put("authtoken", xdata.getinstance().getSetting(config.authtoken));
        requestparams.put("sharemethod", method);
        requestparams.put("fileextension",common.getfileextension(path));

        progressdialog.showwaitingdialog(getinstance());
        xapipost_send(getinstance(), requestparams, new apiresponselistener() {

            @Override
            public void onResponse(taskresult response) {

                progressdialog.dismisswaitdialog();
                if(response.isSuccess())
                {
                    JSONObject object= null;
                    String storageurl="",shareurl="";
                    try {
                        object = new JSONObject(response.getData().toString());

                        if(object.has("storageuploadurl"))
                            storageurl=object.getString("storageuploadurl");

                        if(object.has("sharedurl"))
                            shareurl=object.getString("sharedurl");

                        if(object.has("shareurl"))
                            shareurl=object.getString("shareurl");

                        if(! storageurl.trim().isEmpty())
                        {
                            progressdialog.showwaitingdialog(getinstance());
                            xapi_uploadfile(getinstance(),storageurl,path, new apiresponselistener() {
                                @Override
                                public void onResponse(taskresult response) {
                                    if(response.isSuccess())
                                    {
                                        callmediastoreapi(mediatoken,mediatype,path);
                                    }
                                    else
                                    {
                                        progressdialog.dismisswaitdialog();
                                    }
                                }
                            });
                        }
                        else if(! shareurl.trim().isEmpty())
                        {
                            common.sharemessagewithapps(shareurl);
                        }

                        if(object.has("message"))
                            Toast.makeText(baseactivity.this,object.getString("message"),Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void callmediastoreapi(String mediatoken, String mediatype, String mediapath){

        HashMap<String, String> requestparams = new HashMap<>();
        requestparams.put("action", "stored");
        requestparams.put("type", mediatype);
        requestparams.put("authtoken", xdata.getinstance().getSetting(config.authtoken));
        requestparams.put("fileextension",common.getfileextension(mediapath));
        if(mediatype.equalsIgnoreCase(config.item_image))
            requestparams.put("imagetoken", mediatoken);
        else if(mediatype.equalsIgnoreCase(config.item_audio))
            requestparams.put("audiotoken", mediatoken);
        else if(mediatype.equalsIgnoreCase(config.item_video))
            requestparams.put("videotoken", mediatoken);

        progressdialog.showwaitingdialog(getinstance());
        xapipost_send(getinstance(), requestparams, new apiresponselistener() {

            @Override
            public void onResponse(taskresult response) {

                progressdialog.dismisswaitdialog();
                if(response.isSuccess())
                {
                    JSONObject object= null;
                    try {
                        object = new JSONObject(response.getData().toString());

                        if(object.has("message"))
                            Toast.makeText(baseactivity.this,object.getString("message"),Toast.LENGTH_SHORT).show();

                        String shareurl="";
                        if(object.has("sharedurl"))
                            shareurl=object.getString("sharedurl");

                        if(object.has("shareurl"))
                            shareurl=object.getString("shareurl");

                        if (subdialogshare != null && subdialogshare.isShowing())
                            subdialogshare.dismiss();

                        if(! shareurl.trim().isEmpty())
                            common.sharemessagewithapps(shareurl);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void callsharapiafterlogin()
    {
        callmediashareapi(mediatype , mediavideotoken, mediapath, mediamethod);
    }

    public void callloginscreen(){
        redirecttologin();
        return;
    }

    public  void share_alert_dialog(final Context context, final String title, String content){
        final Dialog dialog =new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.share_alert_popup);

        TextView txttitle = (TextView)dialog.findViewById(R.id.txt_title);
        TextView txtcontent = (TextView)dialog.findViewById(R.id.txt_content);
        txtcontent.setTypeface(applicationviavideocomposer.comfortaaregular, Typeface.BOLD);

        txttitle.setTypeface(applicationviavideocomposer.bahnschriftregular, Typeface.BOLD);
        final CheckBox notifycheckbox = (CheckBox) dialog.findViewById(R.id.notifycheckbox);
        notifycheckbox.setText("Do not notify again");
        notifycheckbox.setTypeface(applicationviavideocomposer.comfortaaregular, Typeface.BOLD);

        txttitle.setText(title);
        txtcontent.setText(content);

        TextView ok = (TextView) dialog.findViewById(R.id.btn_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(title.equalsIgnoreCase(context.getResources().getString(R.string.txt_publish))){
                    if(notifycheckbox.isChecked())
                        xdata.getinstance().saveSetting(config.enableplubishnotification,"1");
                    else
                        xdata.getinstance().saveSetting(config.enableplubishnotification,"0");
                }
                if(title.equalsIgnoreCase(context.getResources().getString(R.string.txt_send))){
                    if(notifycheckbox.isChecked())
                        xdata.getinstance().saveSetting(config.enablesendnotification,"1");
                    else
                        xdata.getinstance().saveSetting(config.enablesendnotification,"0");
                }
                if(title.equalsIgnoreCase(context.getResources().getString(R.string.txt_export))){
                    if(notifycheckbox.isChecked())
                        xdata.getinstance().saveSetting(config.enableexportnotification,"1");
                    else
                        xdata.getinstance().saveSetting(config.enableexportnotification,"0");
                }
                ArrayList<Integer> array_image = new ArrayList<Integer>();
                array_image.add(R.drawable.dropbox);
                array_image.add(R.drawable.dropbox);
                array_image.add(R.drawable.googledrive);
                array_image.add(R.drawable.onedrive);
                array_image.add(R.drawable.videolock);
                ArrayList<String> array_name = new ArrayList<String>();
                array_name.add("Box");
                array_name.add("Dropbox");
                array_name.add("Google Drive");
                array_name.add("Microsoft OneDrive");
                array_name.add("VideoLock Share");

                baseactivity.getinstance().senditemsdialog(context,array_image,array_name);
                dialog.dismiss();

            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public  void senditemsdialog(Context context, ArrayList<Integer> arrayImage, ArrayList<String> arrayName){
        Dialog send_item_dialog = new Dialog(context, android.R.style.Theme_Dialog);
        send_item_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        send_item_dialog.setContentView(R.layout.send_alert_dialog);
        send_item_dialog.setCanceledOnTouchOutside(true);
        send_item_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        RecyclerView recyclerView = (RecyclerView) send_item_dialog.findViewById(R.id.ryclr_send_items);
        adaptersenddialog adaptersend = new adaptersenddialog(context, arrayImage, arrayName);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context,2);
        ((GridLayoutManager)mLayoutManager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 4) {
                    return 2;
                }else return 1;
            }
        });
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adaptersend);
        send_item_dialog.show();
    }
}



