package com.deeptruth.app.android.activity;

import android.app.Activity;
import android.app.Dialog;
import android.arch.lifecycle.Lifecycle;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.android.billingclient.api.Purchase;
import com.android.vending.billing.IInAppBillingService;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.deeptruth.app.android.BuildConfig;
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
import com.deeptruth.app.android.models.sharemedia;
import com.deeptruth.app.android.models.sharepopuptextspanning;
import com.deeptruth.app.android.netutils.connectivityreceiver;
import com.deeptruth.app.android.netutils.xapi;
import com.deeptruth.app.android.netutils.xapipost;
import com.deeptruth.app.android.netutils.xapipostjson;
import com.deeptruth.app.android.netutils.xapipostfile;
import com.deeptruth.app.android.inapputils.IabBroadcastReceiver;
import com.deeptruth.app.android.inapputils.IabHelper;
import com.deeptruth.app.android.inapputils.IabResult;
import com.deeptruth.app.android.utils.DropboxClientFactory;
import com.deeptruth.app.android.utils.googledriveutils.DriveServiceHelper;
import com.deeptruth.app.android.utils.pinviewtext;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.homewatcher;
import com.deeptruth.app.android.utils.progressdialog;
import com.deeptruth.app.android.utils.taskresult;
import com.deeptruth.app.android.utils.uploadfileatdropbox;
import com.deeptruth.app.android.utils.xdata;
import com.dropbox.core.android.Auth;
import com.dropbox.core.v2.files.FileMetadata;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.exception.MsalClientException;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.identity.client.exception.MsalServiceException;
import com.microsoft.identity.client.exception.MsalUiRequiredException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
    Dialog dialoginapppurchase =null,dialogupgradecode=null;
    // In the class declaration section:
    //private DropboxAPI<AndroidAuthSession> mdropboxapi;
    // The helper object
    IabHelper mHelper;

    // Debug tag, for logging
    String SelectedSku = "";
    //String SKU_ID="com.matraex.xapi.hackcheck.inapp.credits";
    //String ItemApple = "android.test.purchased";

    // Provides purchase notification while this app is running
    IabBroadcastReceiver mBroadcastReceiver;

    String Base64Key="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkeYT6cIVSzimdAdqOBYjDawrRzLzKLXhcsPquXMb+ICvc9aPCGE5txPbYCNkuK72lTGYfGYUBgJqCItyEE0LDLi49YZQKPbcwf8Zs13L0X9Lnrw0lDdWhoN815+Z5YZ4ZfZ0KXxJU3gvpfg8vroYf3twEH87XTp13+lNHuUecj7djJy8N+oV0x0XAb3JlfZ1JoDaQuQ6Sry+0Ab8AIPHwWBLtnAPjE+m1RTD5PXFHQCSf7jiBvSJb6JkzvM0EDiwBE1YuW9BM7iPcqY4jgW0qyDfUzdwG+tV1durLw0qr7/VN7P7TCYt2mYBrRBc+vE0Xini5Fw47o3+e3C0O3LiVwIDAQAB";

    IInAppBillingService mService;
    ServiceConnection mServiceConn;
    Bundle skuDetails;

    /* Azure AD Variables */
    private PublicClientApplication onedriveapplication;
    private IAuthenticationResult onedriveresult;

    /* Azure AD v2 Configs */
    final static String[] onedrivescopes = {"https://graph.microsoft.com/User.Read"};
    final static String msgraphurl = "https://graph.microsoft.com/v1.0/me";

    /* UI & Debugging Variables */
    private static final String TAG = baseactivity.class.getSimpleName();

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

        if(! xdata.getinstance().getSetting(config.dropboxauthtoken).trim().isEmpty())
        {
            String authtoken=xdata.getinstance().getSetting(config.dropboxauthtoken);
            DropboxClientFactory.init(authtoken);
        }

        /* Configure your sample app and save state for this activity */
        /*onedriveapplication = new PublicClientApplication(
                applicationviavideocomposer.getactivity(),
                R.raw.auth_config);*/


        /* Attempt to get a user and acquireTokenSilent
         * If this fails we do an interactive request
         */
        /*onedriveapplication.getAccounts(new PublicClientApplication.AccountsLoadedCallback() {
            @Override
            public void onAccountsLoaded(final List<IAccount> accounts) {

                if (!accounts.isEmpty()) {
                    *//* This sample doesn't support multi-account scenarios, use the first account *//*
                    onedriveapplication.acquireTokenSilentAsync(onedrivescopes, accounts.get(0), getAuthSilentCallback());
                } else {
                    *//* No accounts or >1 account *//*
                }
            }
        });*/


        //mdropboxapi = new DropboxAPI<AndroidAuthSession>(builddropboxsession());

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

    /*private AndroidAuthSession builddropboxsession() {
        // And later in some initialization function:
        AppKeyPair appKeys = new AppKeyPair(applicationviavideocomposer.getactivity().getResources().getString(R.string.dropbox_appkey),
                applicationviavideocomposer.getactivity().getResources().getString(R.string.dropbox_appsecret));
        AndroidAuthSession session = new AndroidAuthSession(appKeys);

        return session;
    }*/


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
    protected void onResume() {
        super.onResume();
        try
        {
            if(xdata.getinstance().getSetting(config.dropboxauthtoken).trim().isEmpty())
            {
                if(Auth.getOAuth2Token() != null && (! Auth.getOAuth2Token().equalsIgnoreCase("null")) && (!
                        Auth.getOAuth2Token().trim().isEmpty()))
                {
                    String authtoken= Auth.getOAuth2Token();
                    xdata.getinstance().saveSetting(config.dropboxauthtoken,authtoken);
                    DropboxClientFactory.init(xdata.getinstance().getSetting(config.dropboxauthtoken));
                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
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
                handleSignInResult(data);
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

    @Override
    public void showsharepopupsub(final String path, final String type, final String mediatoken,boolean ismediatrimmed) {
            if (subdialogshare != null && subdialogshare.isShowing())
                subdialogshare.dismiss();

            mediapath = path;
            mediatype = type;
            mediavideotoken = mediatoken;

            subdialogshare =new Dialog(applicationviavideocomposer.getactivity(),R.style.transparent_dialog_borderless);
            subdialogshare.requestWindowFeature(Window.FEATURE_NO_TITLE);
            subdialogshare.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            subdialogshare.setCanceledOnTouchOutside(false);
            subdialogshare.setCancelable(true);

            subdialogshare.setContentView(R.layout.share_popup);
            //subdialogshare.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            int[] widthHeight = common.getScreenWidthHeight(applicationviavideocomposer.getactivity());
            int width = widthHeight[0];
            double height = widthHeight[1] / 1.6;
            subdialogshare.getWindow().setLayout(width - 20, (int) height);

            final LinearLayout linear_share_btn1 = (LinearLayout) subdialogshare.findViewById(R.id.linear_share_btn1);
            final LinearLayout linear_share_btn2 = (LinearLayout) subdialogshare.findViewById(R.id.linear_share_btn2);
            final LinearLayout linear_share_btn3 = (LinearLayout) subdialogshare.findViewById(R.id.linear_share_btn3);
            TextView txt_title1 = (TextView) subdialogshare.findViewById(R.id.txt_title1);
            TextView txt_title2 = (TextView) subdialogshare.findViewById(R.id.txt_title2);
            ImageView img_cancel = subdialogshare.findViewById(R.id.img_cancelicon);

            linear_share_btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediamethod = config.type_private;

                if(type.equalsIgnoreCase(config.type_video) && ismediatrimmed)
                {
                    common.shouldshowupgradepopup(config.mediatrimcount);
                    if(common.ismediatrimcountexceed(config.mediatrimcount))
                    {
                        checkinapppurchasestatus();
                        return;
                    }
                }

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

                if(type.equalsIgnoreCase(config.type_video) && ismediatrimmed)
                {
                    common.shouldshowupgradepopup(config.mediatrimcount);
                    if(common.ismediatrimcountexceed(config.mediatrimcount))
                    {
                        checkinapppurchasestatus();
                        return;
                    }
                }

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

                if(type.equalsIgnoreCase(config.type_video) && ismediatrimmed)
                {
                    common.shouldshowupgradepopup(config.mediatrimcount);
                    if(common.ismediatrimcountexceed(config.mediatrimcount))
                    {
                        checkinapppurchasestatus();
                        return;
                    }
                }

                if(!isuserlogin()){
                    redirecttologin();
                    return;
                }
                callmediashareapi(type, mediatoken, path, mediamethod);
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

    public void checkinapppurchasestatus()
    {
        String message = applicationviavideocomposer.getactivity().getResources().getString(R.string.if_you_would_like_the_option);

        baseactivity.getinstance().showinapppurchasepopup(applicationviavideocomposer.
                getactivity(), "", message, new adapteritemclick(){
            @Override
            public void onItemClicked(Object object) {

            }
            @Override
            public void onItemClicked(Object object, int type) {

            }
        });
    }

    public void showtrimdialogfragment(String filepath,String mediatoken)
    {
        try
        {
            Uri uri= FileProvider.getUriForFile(applicationviavideocomposer.getactivity(),
                    BuildConfig.APPLICATION_ID + ".provider", new File(filepath));

            final MediaPlayer mp = MediaPlayer.create(applicationviavideocomposer.getactivity(), uri);
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
                        fragtrimvideo.setdata(filepath, duration,mediatoken);
                        fragtrimvideo.show(ft, "dialog");

                               /* int duration = mediaPlayer.getDuration();
                                fragmentrimvideo fragtrimvideo = new fragmentrimvideo();
                                fragtrimvideo.setdata(mediapath, duration,mediatoken);
                                addFragment(fragtrimvideo, false, true);*/
                    }
                });
            }
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

    public void share_alert_dialog(final Context context, final String title, String content,adapteritemclick mitemclick ){

        dialog =new Dialog(applicationviavideocomposer.getactivity(),R.style.transparent_dialog_borderless);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.share_alert_popup);

        TextView txttitle = (TextView)dialog.findViewById(R.id.txt_title);
        TextView txtcontent = (TextView)dialog.findViewById(R.id.txt_content);

        txttitle.setTypeface(applicationviavideocomposer.bahnschriftregular, Typeface.BOLD);
        final CheckBox notifycheckbox = (CheckBox) dialog.findViewById(R.id.notifycheckbox);
        notifycheckbox.setText(applicationviavideocomposer.getactivity().getResources().getString(R.string.do_not_notify_again));
        notifycheckbox.setTextColor(Color.BLACK);
        notifycheckbox.setTypeface(applicationviavideocomposer.comfortaaregular, Typeface.NORMAL);

        if(title.equalsIgnoreCase(context.getResources().getString(R.string.txt_publish))){

            Log.e("textpublish","publish");
            ArrayList<sharepopuptextspanning> textsharepopup = new ArrayList<>();
            textsharepopup.add(new sharepopuptextspanning(1.28f,0,30,content));
            textsharepopup.add(new sharepopuptextspanning(1.30f,31,59,content));
            textsharepopup.add(new sharepopuptextspanning(1.2f,60,91,content));
            textsharepopup.add(new sharepopuptextspanning(1.27f,92,122,content));

            textsharepopup.add(new sharepopuptextspanning(1.22f,124,153,content));
            textsharepopup.add(new sharepopuptextspanning(1.29f,154,183,content));
            textsharepopup.add(new sharepopuptextspanning(1.32f,183,content.length(),content));

            common.setspanning(textsharepopup,txtcontent);
        }
        else if(title.equalsIgnoreCase(context.getResources().getString(R.string.txt_send))){

            Log.e("textpublish","publish");
            ArrayList<sharepopuptextspanning> textsharepopup = new ArrayList<>();
            textsharepopup.add(new sharepopuptextspanning(0.99f,0,22,content));
            textsharepopup.add(new sharepopuptextspanning(0.99f,23,24,content));
           /* textsharepopup.add(new sharepopuptextspanning(0.99f,55,82,content));
            textsharepopup.add(new sharepopuptextspanning(1.26f,83,110,content));
            textsharepopup.add(new sharepopuptextspanning(1.22f,111,136,content));
            textsharepopup.add(new sharepopuptextspanning(1.29f,137,165,content));
            textsharepopup.add(new sharepopuptextspanning(1.31f,166,193,content));
*/
            /*textsharepopup.add(new sharepopuptextspanning(1.28f,0,30,content));
            textsharepopup.add(new sharepopuptextspanning(1.30f,31,59,content));
            textsharepopup.add(new sharepopuptextspanning(1.2f,60,91,content));
            textsharepopup.add(new sharepopuptextspanning(1.26f,92,122,content));*/
            common.setspanning(textsharepopup,txtcontent);
        }

        if(title.equalsIgnoreCase(context.getResources().getString(R.string.txt_export))){

            Log.e("textpublish","publish");
            ArrayList<sharepopuptextspanning> textsharepopup = new ArrayList<>();
            textsharepopup.add(new sharepopuptextspanning(1.28f,0,30,content));
            textsharepopup.add(new sharepopuptextspanning(1.30f,31,59,content));
            textsharepopup.add(new sharepopuptextspanning(1.2f,60,91,content));
            textsharepopup.add(new sharepopuptextspanning(1.26f,92,122,content));
            textsharepopup.add(new sharepopuptextspanning(1.22f,124,153,content));
            textsharepopup.add(new sharepopuptextspanning(1.29f,154,183,content));
            textsharepopup.add(new sharepopuptextspanning(1.31f,183,content.length(),content));

            textsharepopup.add(new sharepopuptextspanning(1.28f,0,30,content));
            textsharepopup.add(new sharepopuptextspanning(1.30f,31,59,content));
            textsharepopup.add(new sharepopuptextspanning(1.2f,60,91,content));
            common.setspanning(textsharepopup,txtcontent);
        }
        txttitle.setText(title);
       // txtcontent.setText(content);

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

                if(mitemclick != null)
                    mitemclick.onItemClicked(null);

                //baseactivity.getinstance().senditemsdialog(context);

                dialog.dismiss();

            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void showinapppurchasepopup(final Context activity, String title,String message, final adapteritemclick mitemclick)
    {

        if(dialoginapppurchase != null && dialoginapppurchase.isShowing())
            dialoginapppurchase.dismiss();

        dialoginapppurchase =new Dialog(activity,R.style.transparent_dialog_borderless);
        dialoginapppurchase.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialoginapppurchase.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialoginapppurchase.setCanceledOnTouchOutside(false);
        dialoginapppurchase.setCancelable(true);
        dialoginapppurchase.setContentView(R.layout.inapp_purchase_popup);

        TextView txt_content = (TextView) dialoginapppurchase.findViewById(R.id.txt_content);
        TextView tv_purchase1 = (TextView) dialoginapppurchase.findViewById(R.id.tv_purchase1);
        TextView tv_purchase2 = (TextView) dialoginapppurchase.findViewById(R.id.tv_purchase2);
        TextView tv_upgradecode = (TextView) dialoginapppurchase.findViewById(R.id.tv_upgradecode);
        TextView tvnothanks = (TextView) dialoginapppurchase.findViewById(R.id.btn_nothanks);
        TextView txt_title = (TextView) dialoginapppurchase.findViewById(R.id.txt_title);

        if(! title.trim().isEmpty())
            txt_title.setText(title);

        txt_content.setText(message);

        tv_upgradecode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showupgradecodepopup(applicationviavideocomposer.getactivity());
            }
        });

        tv_purchase1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                baseactivity.getinstance().inapppurchase("ABC");
            }
        });

        tv_purchase2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                baseactivity.getinstance().inapppurchase("ABC");
            }
        });

        tvnothanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dialoginapppurchase != null && dialoginapppurchase.isShowing())
                    dialoginapppurchase.dismiss();
            }
        });
        dialoginapppurchase.show();
    }

    public void showupgradecodepopup(final Context activity)
    {
        if(dialogupgradecode != null && dialogupgradecode.isShowing())
            dialogupgradecode.dismiss();

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

        pinview.setTextColor(Color.WHITE);

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

                                        if(object.getString("success").equalsIgnoreCase("1"))
                                            xdata.getinstance().saveSetting(xdata.app_paid_level,"1");

                                        if(dialogupgradecode != null && dialogupgradecode.isShowing())
                                            dialogupgradecode.dismiss();

                                        if(dialoginapppurchase != null && dialoginapppurchase.isShowing())
                                            dialoginapppurchase.dismiss();
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

    public void senditemsdialog(Context context,String mediafilepath){

        if(!isuserlogin()){
            redirecttologin();
            return;
        }

        ArrayList<sharemedia> sharemedia=new ArrayList<>();
        sharemedia.add(new sharemedia(R.drawable.box,config.item_box));
        sharemedia.add(new sharemedia(R.drawable.dropbox,config.item_dropbox));
        sharemedia.add(new sharemedia(R.drawable.googledrive,config.item_googledrive));
        sharemedia.add(new sharemedia(R.drawable.onedrive,config.item_microsoft_onedrive));
        sharemedia.add(new sharemedia(R.drawable.videolock,config.item_videoLock_share));


        Dialog send_item_dialog = new Dialog(context, android.R.style.Theme_Dialog);
        send_item_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        send_item_dialog.setContentView(R.layout.send_alert_dialog);
        send_item_dialog.setCanceledOnTouchOutside(true);
        send_item_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        RecyclerView recyclerView = (RecyclerView) send_item_dialog.findViewById(R.id.ryclr_send_items);
        ImageView img_backbutton = (ImageView) send_item_dialog.findViewById(R.id.img_backbutton);
        adaptersenddialog adaptersend = new adaptersenddialog(context, sharemedia, new adapteritemclick() {
            @Override
            public void onItemClicked(Object object) {
                if(object != null)
                {
                    common.shouldshowupgradepopup(config.mediatrimcount);
                    int position=(int)object;
                    if(sharemedia.get(position).getMedianame().equalsIgnoreCase(config.item_googledrive))
                    {
                        requestgooglesignin();
                    }
                    else if(sharemedia.get(position).getMedianame().equalsIgnoreCase(config.item_videoLock_share))
                    {
                        videolocksharedialog(applicationviavideocomposer.getactivity());
                    }
                    else if(sharemedia.get(position).getMedianame().equalsIgnoreCase(config.item_microsoft_onedrive))
                    {
                        //getmsonedrivetoken();
                    }
                    else if(sharemedia.get(position).getMedianame().equalsIgnoreCase(config.item_dropbox))
                    {
                        if(xdata.getinstance().getSetting(config.dropboxauthtoken).trim().isEmpty())
                        {
                            Auth.startOAuth2Authentication(baseactivity.this, applicationviavideocomposer.getactivity()
                                    .getResources().getString(R.string.dropbox_appkey));
                        }
                        else
                        {
                            dropboxuploadfile(mediafilepath);
                        }
                    }
                }
            }

            @Override
            public void onItemClicked(Object object, int type) {

            }
        });

        img_backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(send_item_dialog != null && send_item_dialog.isShowing())
                    send_item_dialog.dismiss();
            }
        });

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

    public void dropboxuploadfile(String filepath) {

        progressdialog.showwaitingdialog(applicationviavideocomposer.getactivity());
        new uploadfileatdropbox(applicationviavideocomposer.getactivity(), DropboxClientFactory.getClient(),
                new uploadfileatdropbox.Callback() {
            @Override
            public void onUploadComplete(FileMetadata result) {
                progressdialog.dismisswaitdialog();
                Toast.makeText(applicationviavideocomposer.getactivity(), "File uploaded successfully!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Exception e) {
                progressdialog.dismisswaitdialog();
                Toast.makeText(applicationviavideocomposer.getactivity(), "Failed to upload file!", Toast.LENGTH_SHORT).show();
            }
        }).execute(filepath);
    }

    /**
     * Handles the {@code result} of a completed sign-in activity initiated from {@link
     * #requestgooglesignin()}.
     */
    private void handleSignInResult(Intent result) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
                .addOnSuccessListener(googleAccount -> {
                    Log.d(TAG, "Signed in as " + googleAccount.getEmail());

                    // Use the authenticated account to sign in to the Drive service.
                    GoogleAccountCredential credential =
                            GoogleAccountCredential.usingOAuth2(
                                    applicationviavideocomposer.getactivity(), Collections.singleton(DriveScopes.DRIVE_FILE));
                    credential.setSelectedAccount(googleAccount.getAccount());
                    Drive googleDriveService =
                            new Drive.Builder(
                                    AndroidHttp.newCompatibleTransport(),
                                    new GsonFactory(),
                                    credential)
                                    .setApplicationName(applicationviavideocomposer.getactivity().getResources().getString(R.string.app_name))
                                    .build();

                    DriveServiceHelper mDriveServiceHelper = new DriveServiceHelper(googleDriveService);
                })
                .addOnFailureListener(exception -> Log.e(TAG, "Unable to sign in.", exception));
    }


    private void requestgooglesignin() {
        Log.d(TAG, "Requesting sign-in");

        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                        .build();
        GoogleSignInClient client = GoogleSignIn.getClient(applicationviavideocomposer.getactivity(), signInOptions);
        Intent signInIntent = client.getSignInIntent();
        // The result of the sign-in Intent is handled in onActivityResult.
        applicationviavideocomposer.getactivity().startActivityForResult(signInIntent, config.requestcode_googlesignin);
    }


    public void videolocksharedialog(final Context context){
        final Dialog dialog =new Dialog(context,R.style.transparent_dialog_borderless);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.videolockshare_popup);

        ImageView imageview = (ImageView) dialog.findViewById(R.id.back);
        TextView txt_upload = (TextView) dialog.findViewById(R.id.btn_upload);

        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        txt_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }


    public void firstmediacapturedialog(Context context){

        final Dialog dialog =new Dialog(context,R.style.transparent_dialog_borderless);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.mediafirstrecording_popup);

        TextView txt_content = (TextView) dialog.findViewById(R.id.txt_content);
        TextView txt_title = (TextView) dialog.findViewById(R.id.txt_title);
        TextView btn_next = (TextView) dialog.findViewById(R.id.btn_next);
        YoYo.YoYoString rope;
        String str = getResources().getString(R.string.txt_congrats_content_demo);

       // txt_content.setText(context.getResources().getString(R.string.txt_congrats_content));
        ArrayList<sharepopuptextspanning> textsharepopup = new ArrayList<>();
        textsharepopup.add(new sharepopuptextspanning(1.0f,0,26,str));
        textsharepopup.add(new sharepopuptextspanning(1.0f,27,55,str));

        textsharepopup.add(new sharepopuptextspanning(0.96f,56,85,str));
        textsharepopup.add(new sharepopuptextspanning(1.03f,86,113,str));
        textsharepopup.add(new sharepopuptextspanning(0.98f,114,140,str));
        textsharepopup.add(new sharepopuptextspanning(0.96f,141,167,str));
        textsharepopup.add(new sharepopuptextspanning(0.96f,168,str.length(),str));

        common.setspanning(textsharepopup,txt_content);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(btn_next.getText().toString().equalsIgnoreCase("NEXT")){
                    txt_title.setText(context.getResources().getString(R.string.txt_privacy));
                    txt_content.setText(context.getResources().getString(R.string.txt_privacy_content));
                    YoYo.with(Techniques.SlideInRight)
                            .duration(100)
                            .repeat(0)
                            .playOn(txt_title);
                    YoYo.with(Techniques.SlideInRight)
                            .duration(100)
                            .repeat(0)
                            .playOn(txt_content);

                    btn_next.setText(context.getResources().getString(R.string.ok));
                }else{
                    dialog.dismiss();
                }
            }
        });

        xdata.getinstance().saveSetting(config.firstmediacreated,"1").isEmpty();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void welcomedialog(final Context context){
        final Dialog dialog =new Dialog(context,R.style.transparent_dialog_borderless);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.welcome_popup);

        TextView btn_yes = (TextView) dialog.findViewById(R.id.btn_yes);
        TextView btn_maybe = (TextView) dialog.findViewById(R.id.btn_maybe);
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btn_maybe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }


    // MS Onedrive callbacks and methods


    //
    // Core Identity methods used by MSAL
    // ==================================
    // getmsonedrivetoken() - attempts to get tokens for graph, if it succeeds calls graph & updates UI
    // msonedrivesignout() - Signs account out of the app & updates UI
    // callGraphAPI() - called on successful token acquisition which makes an HTTP request to graph
    //

    /* Use MSAL to acquireToken for the end-user
     * Callback will call Graph api w/ access token & update UI
     */
    private void getmsonedrivetoken() {
        onedriveapplication.acquireToken(applicationviavideocomposer.getactivity(), onedrivescopes, getAuthInteractiveCallback());
    }

    /* Clears an account's tokens from the cache.
     * Logically similar to "sign out" but only signs out of this app.
     * User will get interactive SSO if trying to sign back-in.
     */
    private void msonedrivesignout() {
        /* Attempt to get a user and acquireTokenSilent
         * If this fails we do an interactive request
         */
        onedriveapplication.getAccounts(new PublicClientApplication.AccountsLoadedCallback() {
            @Override
            public void onAccountsLoaded(final List<IAccount> accounts) {

                if (accounts.isEmpty()) {
                    /* No accounts to remove */

                } else {
                    for (final IAccount account : accounts) {
                        onedriveapplication.removeAccount(
                                account,
                                new PublicClientApplication.AccountsRemovedCallback() {
                                    @Override
                                    public void onAccountsRemoved(Boolean isSuccess) {
                                        if (isSuccess) {
                                    /* successfully removed account */
                                        } else {
                                    /* failed to remove account */
                                        }
                                    }
                                });
                    }
                }

               // updateSignedOutUI();
            }
        });
    }

    /* Use Volley to make an HTTP request to the /me endpoint from MS Graph using an access token */
    private void callGraphAPI() {
        Log.d(TAG, "Starting volley request to graph");

        /* Make sure we have a token to send to graph */
        if (onedriveresult.getAccessToken() == null) {return;}

        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject parameters = new JSONObject();

        try {
            parameters.put("key", "value");
        } catch (Exception e) {
            Log.d(TAG, "Failed to put parameters: " + e.toString());
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, msgraphurl,
                parameters,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                /* Successfully called graph, process data and send to UI */
                Log.d(TAG, "Response: " + response.toString());

               // updateGraphUI(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + onedriveresult.getAccessToken());
                return headers;
            }
        };

        Log.d(TAG, "Adding HTTP GET to Queue, Request: " + request.toString());

        request.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }


    /* Callback used in for silent acquireToken calls.
     * Looks if tokens are in the cache (refreshes if necessary and if we don't forceRefresh)
     * else errors that we need to do an interactive request.
     */
    private AuthenticationCallback getAuthSilentCallback() {
        return new AuthenticationCallback() {

            @Override
            public void onSuccess(IAuthenticationResult authenticationResult) {
                /* Successfully got a token, call graph now */
                Log.d(TAG, "Successfully authenticated");

                /* Store the onedriveresult */
                onedriveresult = authenticationResult;

                /* call graph */
                callGraphAPI();

                /* update the UI to post call graph state */
              //  updateSuccessUI();
            }

            @Override
            public void onError(MsalException exception) {
                /* Failed to acquireToken */

                //Log.e("MsalException ",exception.getMessage());
                /*if (exception instanceof MsalClientException) {
                    *//* Exception inside MSAL, more info inside MsalError.java *//*
                } else if (exception instanceof MsalServiceException) {
                    *//* Exception when communicating with the STS, likely config issue *//*
                } else if (exception instanceof MsalUiRequiredException) {
                    *//* Tokens expired or no session, retry with interactive *//*
                }*/
            }

            @Override
            public void onCancel() {
                /* User cancelled the authentication */
                Log.d(TAG, "User cancelled login.");
            }
        };
    }

    /* Callback used for interactive request.  If succeeds we use the access
     * token to call the Microsoft Graph. Does not check cache
     */
    private AuthenticationCallback getAuthInteractiveCallback() {
        return new AuthenticationCallback() {

            @Override
            public void onSuccess(IAuthenticationResult authenticationResult) {
                /* Successfully got a token, call graph now */
                Log.d(TAG, "Successfully authenticated");
                Log.d(TAG, "ID Token: " + authenticationResult.getIdToken());

                /* Store the auth result */
                onedriveresult = authenticationResult;

                /* call graph */
                callGraphAPI();

                /* update the UI to post call graph state */
               // updateSuccessUI();
            }

            @Override
            public void onError(MsalException exception) {
                /* Failed to acquireToken */

             //   Log.e("MsalException ",exception.getMessage());
                /*if (exception instanceof MsalClientException) {
                    *//* Exception inside MSAL, more info inside MsalError.java *//*
                } else if (exception instanceof MsalServiceException) {
                    *//* Exception when communicating with the STS, likely config issue *//*
                }*/
            }

            @Override
            public void onCancel() {
                /* User canceled the authentication */
                Log.d(TAG, "User cancelled login.");
            }
        };
    }
}



