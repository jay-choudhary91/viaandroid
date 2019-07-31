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
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
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
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.deeptruth.app.android.BuildConfig;
import com.deeptruth.app.android.R;
import com.deeptruth.app.android.adapter.adapterbuttontrim;
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
import com.deeptruth.app.android.models.trimbuttontext;
import com.deeptruth.app.android.netutils.connectivityreceiver;
import com.deeptruth.app.android.netutils.xapi;
import com.deeptruth.app.android.netutils.xapipost;
import com.deeptruth.app.android.netutils.xapipostjson;
import com.deeptruth.app.android.netutils.xapipostfile;
import com.deeptruth.app.android.inapputils.IabBroadcastReceiver;
import com.deeptruth.app.android.inapputils.IabHelper;
import com.deeptruth.app.android.inapputils.IabResult;
import com.deeptruth.app.android.onedrive.onedrivedefaultcallback;
import com.deeptruth.app.android.utils.DropboxClientFactory;
import com.deeptruth.app.android.utils.googledriveutils.GoogleDriveFileHolder;
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
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.onedrive.sdk.concurrency.ICallback;
import com.onedrive.sdk.concurrency.IProgressCallback;
import com.onedrive.sdk.core.ClientException;
import com.onedrive.sdk.core.OneDriveErrorCodes;
import com.onedrive.sdk.extensions.IOneDriveClient;
import com.onedrive.sdk.extensions.Item;
import com.onedrive.sdk.options.Option;
import com.onedrive.sdk.options.QueryOption;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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
    Dialog dialoginapppurchase =null,dialogupgradecode=null,dialogfileuploadoptions=null;
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


    /* Azure AD v2 Configs */
    final static String[] onedrivescopes = {"https://graph.microsoft.com/User.Read"};
    final static String msgraphurl = "https://graph.microsoft.com/v1.0/me";

    /* UI & Debugging Variables */
    private static final String TAG = baseactivity.class.getSimpleName();

    homewatcher mHomeWatcher;
    private Executor mexecutor = Executors.newSingleThreadExecutor();
    private File readytouploadfile=null;


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

    private void setreadytouploadfile(File file)
    {
        readytouploadfile=file;
    }

    private File getreadytouploadfile()
    {
        return readytouploadfile;
    }

    public static baseactivity getinstance() {
        return instance;
    }

    public void isapprunning(boolean b) {
        isapprunning = b;
    }

    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
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
            /*if(resultCode == RESULT_OK) {
                callsharapiafterlogin();
            }*/
        }
        else if (requestCode == config.requestcode_googlesignin)
        {
            if (resultCode == Activity.RESULT_OK && data != null) {
                Toast.makeText(applicationviavideocomposer.getactivity(),applicationviavideocomposer.getactivity()
                        .getResources().getString(R.string.fetching_user_info),Toast.LENGTH_SHORT).show();
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

                if(!isuserlogin()){
                    redirecttologin();
                    return;
                }

                if(type.equalsIgnoreCase(config.type_video) && ismediatrimmed)
                {
                    common.shouldshowupgradepopup(config.mediatrimcount);
                    if(common.ismediatrimcountexceed(config.mediatrimcount))
                    {
                        checkinapppurchasestatus(config.gravitycenter);
                        return;
                    }
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

                if(type.equalsIgnoreCase(config.type_video) && ismediatrimmed)
                {
                    common.shouldshowupgradepopup(config.mediatrimcount);
                    if(common.ismediatrimcountexceed(config.mediatrimcount))
                    {
                        checkinapppurchasestatus(config.gravitycenter);
                        return;
                    }
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
                        checkinapppurchasestatus(config.gravitycenter);
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

    public void checkinapppurchasestatus(String gravity)
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
        },gravity);
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

    public void share_alert_dialog(final Context context,final String title, String content,adapteritemclick mitemclick){

        dialog =new Dialog(applicationviavideocomposer.getactivity(),R.style.transparent_dialog_borderless);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.share_alert_popup);

        TextView txttitle = (TextView)dialog.findViewById(R.id.txt_title);
        TextView txt_content = (TextView)dialog.findViewById(R.id.txt_content);
        txttitle.setText(title);
        String str = content;

        txttitle.setTypeface(applicationviavideocomposer.bahnschriftregular, Typeface.BOLD);
        final CheckBox notifycheckbox = (CheckBox) dialog.findViewById(R.id.notifycheckbox);
        notifycheckbox.setText(applicationviavideocomposer.getactivity().getResources().getString(R.string.do_not_notify_again));
        notifycheckbox.setTextColor(Color.BLACK);
        notifycheckbox.setTypeface(applicationviavideocomposer.comfortaaregular, Typeface.NORMAL);

        if(title.equalsIgnoreCase(context.getResources().getString(R.string.txt_publish))){

            ArrayList<sharepopuptextspanning> textsharepopup = new ArrayList<>();

            textsharepopup.add(new sharepopuptextspanning(1.05f,0,28,str));
            textsharepopup.add(new sharepopuptextspanning(1.11f,29,56,str));
            textsharepopup.add(new sharepopuptextspanning(0.99f,57,86,str));
            textsharepopup.add(new sharepopuptextspanning(1.11f,87,114,str));

            textsharepopup.add(new sharepopuptextspanning(1.02f,117,144,str));//144
            textsharepopup.add(new sharepopuptextspanning(1.08f,144,172,str));//172
            textsharepopup.add(new sharepopuptextspanning(1.11f,175,str.length(),str));


            common.setspanning(textsharepopup,txt_content);

        }
         if(title.equalsIgnoreCase(context.getResources().getString(R.string.txt_send))){
             ArrayList<sharepopuptextspanning> textsharepopup = new ArrayList<>();

             textsharepopup.add(new sharepopuptextspanning(1.12f,0,25,str));
             textsharepopup.add(new sharepopuptextspanning(1.16f,25,53,str));
             textsharepopup.add(new sharepopuptextspanning(1.14f,53,82,str));
             textsharepopup.add(new sharepopuptextspanning(1.19f,82,109,str));
             textsharepopup.add(new sharepopuptextspanning(1.14f,109,136,str));
             textsharepopup.add(new sharepopuptextspanning(1.19f,136,167,str));
             textsharepopup.add(new sharepopuptextspanning(1.11f,167,195,str));

             textsharepopup.add(new sharepopuptextspanning(1.10f,199,227,str));//28,29,30
             textsharepopup.add(new sharepopuptextspanning(1.12f,228,258,str));//172
             textsharepopup.add(new sharepopuptextspanning(1.10f,259,290,str));
             textsharepopup.add(new sharepopuptextspanning(1.04f,291,str.length(),str));
             common.setspanning(textsharepopup,txt_content);
        }

        if(title.equalsIgnoreCase(context.getResources().getString(R.string.txt_export))){
            char[] linecontent = str.toCharArray();
            ArrayList<sharepopuptextspanning> textsharepopup = new ArrayList<>();

            textsharepopup.add(new sharepopuptextspanning(1.11f,0,25,str));
            textsharepopup.add(new sharepopuptextspanning(1.14f,25,53,str));
            textsharepopup.add(new sharepopuptextspanning(1.14f,53,82,str));
            textsharepopup.add(new sharepopuptextspanning(1.19f,82,109,str));
            textsharepopup.add(new sharepopuptextspanning(1.14f,109,136,str));
            textsharepopup.add(new sharepopuptextspanning(1.19f,136,167,str));
            textsharepopup.add(new sharepopuptextspanning(1.11f,167,195,str));

            textsharepopup.add(new sharepopuptextspanning(1.25f,199,220,str));//144
            textsharepopup.add(new sharepopuptextspanning(1.21f,222,250,str));//172
            textsharepopup.add(new sharepopuptextspanning(1.05f,252,str.length(),str));

            common.setspanning(textsharepopup,txt_content);
        }

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
        setscreenwidthheight(dialog,85,getResources().getString(R.string.popup_publish));
        dialog.show();
    }

    public void showinapppurchasepopup(final Context activity, String title,String message, final adapteritemclick mitemclick,String  gravity)
    {

        if(dialoginapppurchase != null && dialoginapppurchase.isShowing())
            dialoginapppurchase.dismiss();


        ArrayList<trimbuttontext> trimbuttontext=new ArrayList<>();
        trimbuttontext.add(new trimbuttontext(config.txtyear));
        trimbuttontext.add(new trimbuttontext(config.txtmonth));
        trimbuttontext.add(new trimbuttontext(config.txtupgrade));

        dialoginapppurchase =new Dialog(activity,R.style.transparent_dialog_borderless);
        dialoginapppurchase.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialoginapppurchase.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialoginapppurchase.setCanceledOnTouchOutside(false);
        dialoginapppurchase.setCancelable(true);
        dialoginapppurchase.setContentView(R.layout.inapp_purchase_popup);

        TextView txttitle = (TextView)dialoginapppurchase.findViewById(R.id.txt_title);
        TextView txt_content = (TextView) dialoginapppurchase.findViewById(R.id.inapppurchasetext);


        RecyclerView recyclerView = (RecyclerView) dialoginapppurchase.findViewById(R.id.ryclr_trim_buttontext);
        adapterbuttontrim adaptersend = new adapterbuttontrim(activity, trimbuttontext, new adapteritemclick() {
            @Override
            public void onItemClicked(Object object) {
                if(object != null)
                {
                    int position=(int)object;
                    if(trimbuttontext.get(position).getButtontext().equalsIgnoreCase(config.txtyear))
                    {
                        baseactivity.getinstance().inapppurchase("ABC");
                    }
                    else if(trimbuttontext.get(position).getButtontext().equalsIgnoreCase(config.txtmonth))
                    {
                        baseactivity.getinstance().inapppurchase("ABC");
                    }
                    else if(trimbuttontext.get(position).getButtontext().equalsIgnoreCase(config.txtupgrade))
                    {
                        showupgradecodepopup(applicationviavideocomposer.getactivity());
                    }
                }
            }

            @Override
            public void onItemClicked(Object object, int type) {

            }
        });
        TextView tvnothanks = (TextView) dialoginapppurchase.findViewById(R.id.btn_nothanks);
        TextView txt_title = (TextView) dialoginapppurchase.findViewById(R.id.txt_title);

        if(! title.trim().isEmpty())
            txt_title.setText(title);


        if(txt_title.getText().toString().contains("UPGRADE")){

            String str = getResources().getString(R.string.upgrade_line1) +"\n"+ getResources().getString(R.string.upgrade_line2) +"\n"+
                    getResources().getString(R.string.upgrade_line3) +"\n"+ getResources().getString(R.string.upgrade_line4) +"\n"+
                    getResources().getString(R.string.upgrade_line5) +"\n"+ getResources().getString(R.string.upgrade_line6)+ "\n\n" +
                    getResources().getString(R.string.upgrade_line7) +"\n"+ getResources().getString(R.string.upgrade_line8) +"\n"+
                    getResources().getString(R.string.upgrade_line9) +"\n"+ getResources().getString(R.string.upgrade_line10);

            ArrayList<sharepopuptextspanning> textsharepopup = new ArrayList<>();
            textsharepopup.add(new sharepopuptextspanning(1.03f,1,23,str));
            textsharepopup.add(new sharepopuptextspanning(1.01f,25,48,str));
            textsharepopup.add(new sharepopuptextspanning(0.99f,50,74,str));
            textsharepopup.add(new sharepopuptextspanning(1.03f,75,96,str));
            textsharepopup.add(new sharepopuptextspanning(1.05f,98,117,str));
            textsharepopup.add(new sharepopuptextspanning(1.02f,119,141,str));
            textsharepopup.add(new sharepopuptextspanning(1.01f,143,165,str));
            textsharepopup.add(new sharepopuptextspanning(1.02f,167,189,str));
            textsharepopup.add(new sharepopuptextspanning(0.96f,191,214,str));
            textsharepopup.add(new sharepopuptextspanning(1.03f,216,str.length(),str));
            common.setspanning(textsharepopup,txt_content);

        }else if(txt_title.getText().toString().contains("30-SECOND")){


            String str = getResources().getString(R.string.limit_reached_line1)+"\n"+ getResources().getString(R.string.limit_reached_line2) +"\n"+
                    getResources().getString(R.string.limit_reached_line3) +"\n"+ getResources().getString(R.string.limit_reached_line4) +"\n\n"+
                    getResources().getString(R.string.limit_reached_line5) +"\n"+ getResources().getString(R.string.limit_reached_line6) +"\n"+
                    getResources().getString(R.string.limit_reached_line7) +"\n"+ getResources().getString(R.string.limit_reached_line8);

            ArrayList<sharepopuptextspanning> textsharepopup = new ArrayList<>();
            textsharepopup.add(new sharepopuptextspanning(1.15f,1,24,str));
            textsharepopup.add(new sharepopuptextspanning(1.05f,26,47,str));
            textsharepopup.add(new sharepopuptextspanning(1.08f,49,70,str));
            textsharepopup.add(new sharepopuptextspanning(1.10f,72,89,str));
            textsharepopup.add(new sharepopuptextspanning(1.21f,91,111,str));
            textsharepopup.add(new sharepopuptextspanning(1.14f,113,133,str));
            textsharepopup.add(new sharepopuptextspanning(1.14f,135,155,str));
            textsharepopup.add(new sharepopuptextspanning(1.11f,157,str.length(),str));
            common.setspanning(textsharepopup,txt_content);

        }else if(txt_title.getText().toString().contains("TRIM")){

            String str = getResources().getString(R.string.trim_line1)+"\n"+ getResources().getString(R.string.trim_line2) +"\n"+
                    getResources().getString(R.string.trim_line3) +"\n"+ getResources().getString(R.string.trim_line4) +"\n"+
                    getResources().getString(R.string.trim_line5) +"\n"+ getResources().getString(R.string.trim_line6);

            ArrayList<sharepopuptextspanning> textsharepopup = new ArrayList<>();
            textsharepopup.add(new sharepopuptextspanning(1.02f,1,28,str));
            textsharepopup.add(new sharepopuptextspanning(1.039f,30,55,str));
            textsharepopup.add(new sharepopuptextspanning(0.99f,57,84,str));
            textsharepopup.add(new sharepopuptextspanning(1.00f,86,113,str));
            textsharepopup.add(new sharepopuptextspanning(1.01f,115,139,str));
            textsharepopup.add(new sharepopuptextspanning(1.07f,141,str.length(),str));
            common.setspanning(textsharepopup,txt_content);
        }

        tvnothanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dialoginapppurchase != null && dialoginapppurchase.isShowing())
                    dialoginapppurchase.dismiss();
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(activity,1);
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

        if(gravity.equalsIgnoreCase(config.gravitytop)){
            setscreenwidthheight(dialoginapppurchase,80,getResources().getString(R.string.popup_upgrade));
        }else{
            setscreenwidthheight(dialoginapppurchase,80,getResources().getString(R.string.popup_trim));
        }

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

        setscreenwidthheight(dialogupgradecode,85,getResources().getString(R.string.popup_upgradecode));
        dialogupgradecode.show();
    }

    public void senditemsdialog(Context context,String mediafilepath,String mediatoken,final String type,boolean ismediatrimmed){

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

        if(dialogfileuploadoptions != null && dialogfileuploadoptions.isShowing())
            dialogfileuploadoptions.dismiss();

        dialogfileuploadoptions = new Dialog(context, R.style.transparent_dialog_borderless);
        dialogfileuploadoptions.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogfileuploadoptions.setContentView(R.layout.send_alert_dialog);
        dialogfileuploadoptions.setCanceledOnTouchOutside(true);
        dialogfileuploadoptions.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        RecyclerView recyclerView = (RecyclerView) dialogfileuploadoptions.findViewById(R.id.ryclr_send_items);
        ImageView img_backbutton = (ImageView) dialogfileuploadoptions.findViewById(R.id.img_backbutton);
        adaptersenddialog adaptersend = new adaptersenddialog(context, sharemedia, new adapteritemclick() {
            @Override
            public void onItemClicked(Object object) {
                if(object != null)
                {
                    setreadytouploadfile(new File(mediafilepath));
                    if(type.equalsIgnoreCase(config.type_video) && ismediatrimmed)
                    {
                        if(common.ismediatrimcountexceed(config.mediatrimcount))
                        {
                            checkinapppurchasestatus(config.gravitycenter);
                            return;
                        }
                        common.shouldshowupgradepopup(config.mediatrimcount);
                    }

                    int position=(int)object;
                    if(sharemedia.get(position).getMedianame().equalsIgnoreCase(config.item_googledrive))
                    {
                        requestgooglesignin();
                    }
                    else if(sharemedia.get(position).getMedianame().equalsIgnoreCase(config.item_videoLock_share))
                    {
                        if(dialogfileuploadoptions != null && dialogfileuploadoptions.isShowing())
                            dialogfileuploadoptions.dismiss();

                        videolocksharedialog(applicationviavideocomposer.getactivity());
                    }
                    else if(sharemedia.get(position).getMedianame().equalsIgnoreCase(config.item_microsoft_onedrive))
                    {
                        loginwithonedrive(mediafilepath);
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
                if(dialogfileuploadoptions != null && dialogfileuploadoptions.isShowing())
                    dialogfileuploadoptions.dismiss();

                showtrimdialogfragment(mediafilepath,mediatoken);
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

        setscreenwidthheight(dialogfileuploadoptions,95,context.getResources().getString(R.string.popup_send));
        dialogfileuploadoptions.show();
    }

    public void dropboxuploadfile(String filepath) {

        progressdialog.showwaitingdialog(applicationviavideocomposer.getactivity());
        new uploadfileatdropbox(applicationviavideocomposer.getactivity(), DropboxClientFactory.getClient(),
                new uploadfileatdropbox.Callback() {
            @Override
            public void onUploadComplete(FileMetadata result) {
                progressdialog.dismisswaitdialog();
                Toast.makeText(applicationviavideocomposer.getactivity(), "File uploaded successfully!", Toast.LENGTH_SHORT).show();

                if(dialogfileuploadoptions != null && dialogfileuploadoptions.isShowing())
                    dialogfileuploadoptions.dismiss();
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
                .addOnSuccessListener(googleAccount ->
                {
                    try
                    {

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

                        //DriveServiceHelper mDriveServiceHelper = new DriveServiceHelper(googleDriveService);
                        uploadfileatgoogledrive(googleDriveService,getreadytouploadfile(),
                                common.getmimetype(getreadytouploadfile().getAbsolutePath()),null);

                        progressdialog.dismisswaitdialog();
                        Toast.makeText(applicationviavideocomposer.getactivity(),applicationviavideocomposer.getactivity()
                                .getResources().getString(R.string.uploading_has_started),Toast.LENGTH_SHORT).show();

                        if(dialogfileuploadoptions != null && dialogfileuploadoptions.isShowing())
                            dialogfileuploadoptions.dismiss();

                    }catch (Exception e)
                    {
                        progressdialog.dismisswaitdialog();
                        Toast.makeText(applicationviavideocomposer.getactivity(),applicationviavideocomposer.getactivity()
                                .getResources().getString(R.string.unable_signin),Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                })
                .addOnFailureListener(exception ->
                {
                    progressdialog.dismisswaitdialog();
                    Toast.makeText(applicationviavideocomposer.getactivity(),applicationviavideocomposer.getactivity()
                            .getResources().getString(R.string.unable_signin),Toast.LENGTH_SHORT).show();
                });
    }


    public Task<GoogleDriveFileHolder> uploadfileatgoogledrive(Drive googleDriveService,
                                                  final java.io.File localFile,final String mimeType, @Nullable String folderId) {
        return Tasks.call(mexecutor, new Callable<GoogleDriveFileHolder>()
        {
            @Override
            public GoogleDriveFileHolder call() throws Exception
            {
                // Retrieve the metadata as a File object.

                List<String> root;
                if (folderId == null) {
                    root = Collections.singletonList("root");
                } else {

                    root = Collections.singletonList(folderId);
                }

                com.google.api.services.drive.model.File metadata = new com.google.api.services.drive.model.File()
                        .setParents(root)
                        .setMimeType(mimeType)
                        .setName(localFile.getName());

                FileContent fileContent = new FileContent(mimeType, localFile);

                com.google.api.services.drive.model.File fileMeta = googleDriveService.files().create(metadata, fileContent).execute();
                GoogleDriveFileHolder googleDriveFileHolder = new GoogleDriveFileHolder();
                googleDriveFileHolder.setId(fileMeta.getId());
                googleDriveFileHolder.setName(fileMeta.getName());
                return googleDriveFileHolder;
            }
        });
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
        TextView txt_content = (TextView) dialog.findViewById(R.id.txt_content);

        TextView txt_line_one = (TextView) dialog.findViewById(R.id.txt_lineone);
        TextView txt_line_two = (TextView) dialog.findViewById(R.id.txt_linetwo);
        TextView txt_line_three = (TextView) dialog.findViewById(R.id.txt_linethree);
        TextView txt_line_four = (TextView) dialog.findViewById(R.id.txt_linefour);
        TextView txt_line_five = (TextView) dialog.findViewById(R.id.txt_linefive);
        TextView txt_line_six = (TextView) dialog.findViewById(R.id.txt_linesix);
        TextView txt_line_seven = (TextView) dialog.findViewById(R.id.txt_lineseven);
        TextView txt_line_eight = (TextView) dialog.findViewById(R.id.txt_lineeight);
        TextView txt_line_nine = (TextView) dialog.findViewById(R.id.txt_linenine);

        txt_line_one.setVisibility(View.VISIBLE);
        txt_line_two.setVisibility(View.VISIBLE);
        txt_line_three.setVisibility(View.VISIBLE);
        txt_line_four.setVisibility(View.VISIBLE);
        txt_line_five.setVisibility(View.VISIBLE);
        txt_line_six.setVisibility(View.VISIBLE);
        txt_line_seven.setVisibility(View.VISIBLE);
        txt_line_eight.setVisibility(View.VISIBLE);
        txt_line_nine.setVisibility(View.VISIBLE);

        txt_line_one.setText(R.string.vl_share_lineone);
        txt_line_two.setText(R.string.vl_share_linetwo);
        txt_line_three.setText("");
        txt_line_four.setText(R.string.vl_share_linefour);
        txt_line_five.setText(R.string.vl_share_linefive);
        txt_line_six.setText(R.string.vl_share_linesix);
        txt_line_seven.setText(R.string.vl_share_lineseven);
        txt_line_eight.setText(R.string.vl_share_lineeight);
        txt_line_nine.setText(R.string.vl_share_linenine);

        /*txt_line_one.setTextSize(14.8f);
        txt_line_two.setTextSize(13.9f);
        txt_line_four.setTextSize(14.1f);
        txt_line_five.setTextSize(14);
        txt_line_six.setTextSize(14.99f);
        txt_line_seven.setTextSize(14.1f);
        txt_line_eight.setTextSize(13f);
        txt_line_nine.setTextSize(14.1f);*/

        txt_line_one.setTextSize(15);
        txt_line_two.setTextSize(14.9f);
        txt_line_four.setTextSize(14.3f);
        txt_line_five.setTextSize(14.2f);
        txt_line_six.setTextSize(15);
        txt_line_seven.setTextSize(14.3f);
        txt_line_eight.setTextSize(13.2f);
        txt_line_nine.setTextSize(14.3f);


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

        setscreenwidthheight(dialog,95,context.getResources().getString(R.string.popup_videolock));
        dialog.show();
    }


    public void firstmediacapturedialog(Context context){

        final Dialog dialog =new Dialog(context,R.style.transparent_dialog_borderless);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.mediafirstrecording_popup);

        TextView txt_title = (TextView) dialog.findViewById(R.id.txt_title);
        TextView btn_next = (TextView) dialog.findViewById(R.id.btn_next);
        YoYo.YoYoString rope;


        TextView txt_line_one = (TextView) dialog.findViewById(R.id.txt_lineone);
        TextView txt_line_two = (TextView) dialog.findViewById(R.id.txt_linetwo);
        TextView txt_line_three = (TextView) dialog.findViewById(R.id.txt_linethree);
        TextView txt_line_four = (TextView) dialog.findViewById(R.id.txt_linefour);
        TextView txt_line_five = (TextView) dialog.findViewById(R.id.txt_linefive);
        TextView txt_line_six = (TextView) dialog.findViewById(R.id.txt_linesix);
        TextView txt_line_seven = (TextView) dialog.findViewById(R.id.txt_lineseven);
        TextView txt_line_eight = (TextView) dialog.findViewById(R.id.txt_lineeight);

        txt_line_one.setVisibility(View.VISIBLE);
        txt_line_two.setVisibility(View.VISIBLE);
        txt_line_three.setVisibility(View.VISIBLE);
        txt_line_four.setVisibility(View.VISIBLE);
        txt_line_five.setVisibility(View.VISIBLE);
        txt_line_six.setVisibility(View.VISIBLE);
        txt_line_seven.setVisibility(View.VISIBLE);
        txt_line_eight.setVisibility(View.VISIBLE);

        txt_line_one.setText(R.string.cong_lineone);
        txt_line_two.setText(R.string.cong_linetwo);
        txt_line_three.setText("");
        txt_line_four.setText(R.string.cong_linefour);
        txt_line_five.setText(R.string.cong_linefive);
        txt_line_six.setText(R.string.cong_linesix);
        txt_line_seven.setText(R.string.cong_lineseven);
        txt_line_eight.setText(R.string.cong_lineeight);

        txt_line_one.setTextSize(14.8f);
        txt_line_two.setTextSize(15.3f);
        txt_line_four.setTextSize(14.5f);
        txt_line_five.setTextSize(15.5f);
        txt_line_six.setTextSize(15);
        txt_line_seven.setTextSize(14.6f);
        txt_line_eight.setTextSize(14.5f);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(btn_next.getText().toString().equalsIgnoreCase("NEXT")){
                    setscreenwidthheight(dialog,85,context.getResources().getString(R.string.popup_congrats));
                    txt_title.setText(context.getResources().getString(R.string.txt_privacy));

                    txt_line_one.setText(R.string.priv_lineone);
                    txt_line_two.setText(R.string.priv_linetwo);
                    txt_line_three.setText(R.string.priv_linethree);
                    txt_line_four.setText(R.string.priv_linefour);
                    txt_line_five.setText(R.string.priv_linefive);
                    txt_line_six.setText(R.string.priv_linesix);
                    txt_line_seven.setText(R.string.priv_lineseven);
                    txt_line_eight.setVisibility(View.GONE);

                    txt_line_one.setTextSize(14.8f);
                    txt_line_two.setTextSize(15.6f);
                    txt_line_three.setTextSize(15);
                    txt_line_four.setTextSize(14.3f);
                    txt_line_five.setTextSize(15.3f);
                    txt_line_six.setTextSize(15.4f);
                    txt_line_seven.setTextSize(15.4f);

                    btn_next.setText(context.getResources().getString(R.string.ok));
                }else{
                    dialog.dismiss();
                }
            }
        });

        xdata.getinstance().saveSetting(config.firstmediacreated,"1").isEmpty();
        setscreenwidthheight(dialog,85,context.getResources().getString(R.string.popup_congrats));
        dialog.show();
    }

    public void welcomedialog(final Context context){

        final Dialog dialog =new Dialog(context,R.style.transparent_dialog_borderless);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.welcome_popup);

        TextView txt_line_one = (TextView) dialog.findViewById(R.id.txt_lineone);
        TextView txt_line_two = (TextView) dialog.findViewById(R.id.txt_linetwo);
        TextView txt_line_three = (TextView) dialog.findViewById(R.id.txt_linethree);
        TextView txt_line_four = (TextView) dialog.findViewById(R.id.txt_linefour);
        TextView txt_line_five = (TextView) dialog.findViewById(R.id.txt_linefive);
        TextView txt_line_six = (TextView) dialog.findViewById(R.id.txt_linesix);
        TextView txt_line_seven = (TextView) dialog.findViewById(R.id.txt_lineseven);

        txt_line_one.setVisibility(View.VISIBLE);
        txt_line_two.setVisibility(View.VISIBLE);
        txt_line_three.setVisibility(View.VISIBLE);
        txt_line_four.setVisibility(View.VISIBLE);
        txt_line_five.setVisibility(View.VISIBLE);
        txt_line_six.setVisibility(View.VISIBLE);
        txt_line_seven.setVisibility(View.VISIBLE);

        txt_line_one.setText(R.string.wel_lineone);
        txt_line_two.setText(R.string.wel_linetwo);
        txt_line_three.setText(R.string.wel_linethree);
        txt_line_four.setText(R.string.wel_linefour);
        txt_line_five.setText("");
        txt_line_six.setText(R.string.wel_linesix);
        txt_line_seven.setText(R.string.wel_lineseven);

        txt_line_one.setTextSize(14.8f);
        txt_line_two.setTextSize(15.4f);
        txt_line_three.setTextSize(15.1f);
        txt_line_four.setTextSize(15.4f);
        txt_line_six.setTextSize(15);
        txt_line_seven.setTextSize(15.8f);


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

        setscreenwidthheight(dialog,80,getResources().getString(R.string.popup_welcome));
        dialog.show();
    }

    // MS Onedrive callbacks and methods

    public void loginwithonedrive(String filepath)
    {
        final applicationviavideocomposer app = (applicationviavideocomposer)baseactivity.this.getApplication();
        final ICallback<Void> serviceCreated = new onedrivedefaultcallback<Void>(baseactivity.this) {
            @Override
            public void success(final Void result) {
                progressdialog.showwaitingdialog(applicationviavideocomposer.getactivity());
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        uploadfileatmsonedrive(filepath);
                    }
                },500);
            }
        };
        try {
            app.createOneDriveClient(baseactivity.this, serviceCreated);
            Toast.makeText(baseactivity.this,applicationviavideocomposer.getactivity().getResources()
                            .getString(R.string.create_onedrive_client),
                    Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //app.createOneDriveClient(baseactivity.this, serviceCreated);
        }
    }


    public void uploadfileatmsonedrive(String filepath)
    {
        final applicationviavideocomposer application = (applicationviavideocomposer)baseactivity.this.getApplication();
        IOneDriveClient oneDriveClient = application.getOneDriveClient();
        final AsyncTask<Void, Void, Void> uploadFile = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... params) {
                try
                {
                    File file = new File(filepath);
                    byte[] fileInMemory = new byte[(int) file.length()];
                    FileInputStream fis = new FileInputStream(file);
                    fis.read(fileInMemory); //read file into bytes[]
                    fis.close();
                    // Fix up the file name (needed for camera roll photos, etc)
                    final String filename = new File(filepath).getName();
                    final Option option = new QueryOption("@name.conflictBehavior", "fail");
                    oneDriveClient
                            .getDrive()
                            .getItems("root")
                            .getChildren()
                            .byId(filename)
                            .getContent()
                            .buildRequest(Collections.singletonList(option))
                            .put(fileInMemory,
                                    new IProgressCallback<Item>() {
                                        @Override
                                        public void success(final Item item) {
                                            progressdialog.dismisswaitdialog();
                                            Toast.makeText(baseactivity.this,applicationviavideocomposer.getactivity().getResources()
                                                    .getString(R.string.file_uploaded),
                                                    Toast.LENGTH_SHORT).show();

                                            if(dialogfileuploadoptions != null && dialogfileuploadoptions.isShowing())
                                                dialogfileuploadoptions.dismiss();
                                        }

                                        @Override
                                        public void failure(final ClientException error) {
                                            progressdialog.dismisswaitdialog();
                                            if (error.isError(OneDriveErrorCodes.NameAlreadyExists)) {
                                                Toast.makeText(baseactivity.this,"FileName already exist!",
                                                        Toast.LENGTH_LONG).show();

                                            } else {
                                                Toast.makeText(baseactivity.this,applicationviavideocomposer.getactivity().getResources()
                                                                .getString(R.string.upload_failed),
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        }

                                        @Override
                                        public void progress(final long current, final long max) {
                                            /*dialog.setProgress((int) current);
                                            dialog.setMax((int) max);*/
                                        }
                                    });
                } catch (final Exception e) {
                    Log.e(getClass().getSimpleName(), e.getMessage());
                    Log.e(getClass().getSimpleName(), e.toString());
                }
                return null;
            }
        };
        uploadFile.execute();
    }


    public void setscreenwidthheight(Dialog dialog,int widthpercentage,String type) {

        int width = common.getScreenWidth(applicationviavideocomposer.getactivity());
        int height = common.getScreenHeight(applicationviavideocomposer.getactivity());

        int percentagewidth = (width / 100) * widthpercentage;

        if(type.equalsIgnoreCase(getResources().getString(R.string.popup_upgrade)) ){

            dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            double topmargin = (height / 100) * 6;
            params.y = (int)topmargin;
            dialog.getWindow().setAttributes(params);
            dialog.getWindow().setLayout(percentagewidth, WindowManager.LayoutParams.WRAP_CONTENT);

        }
        else if(type.equalsIgnoreCase(getResources().getString(R.string.popup_welcome)) ||
                type.equalsIgnoreCase(getResources().getString(R.string.popup_congrats)) ){

            dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            double bottommargin = (height / 100) * 25;
            params.y = (int)bottommargin;
            dialog.getWindow().setAttributes(params);
            dialog.getWindow().setLayout(percentagewidth,WindowManager.LayoutParams.WRAP_CONTENT);
        }
        else if(type.equalsIgnoreCase(getResources().getString(R.string.popup_publish))||
                type.equalsIgnoreCase(getResources().getString(R.string.popup_trim))){

            dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            double bottommargin = (height / 100) * 19;
            params.y = (int)bottommargin;
            dialog.getWindow().setAttributes(params);
            dialog.getWindow().setLayout(percentagewidth,WindowManager.LayoutParams.WRAP_CONTENT);
        }
        else if(type.equalsIgnoreCase(getResources().getString(R.string.popup_send))||
                type.equalsIgnoreCase(getResources().getString(R.string.popup_videolock))){

            dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            int percentageheight = (height / 100) * 75;
            double bottommargin = (height / 100) * 3;
            dialog.getWindow().setLayout(percentagewidth,percentageheight);
            params.y = (int)bottommargin;
            dialog.getWindow().setAttributes(params);
        }


        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
    }
}



