package com.deeptruth.app.android.activity;

import android.app.Activity;
import android.app.Dialog;
import android.arch.lifecycle.Lifecycle;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
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
import android.support.v7.widget.AppCompatCheckBox;
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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.android.billingclient.api.Purchase;
import com.android.vending.billing.IInAppBillingService;
import com.daimajia.androidanimations.library.YoYo;
import com.deeptruth.app.android.BuildConfig;
import com.deeptruth.app.android.R;
import com.deeptruth.app.android.adapter.adapterbuttontrim;
import com.deeptruth.app.android.adapter.adaptersenddialog;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.database.databasemanager;
import com.deeptruth.app.android.fragments.audiocomposerfragment;
import com.deeptruth.app.android.fragments.audioreaderfragment;
import com.deeptruth.app.android.fragments.basefragment;
import com.deeptruth.app.android.fragments.composeoptionspagerfragment;
import com.deeptruth.app.android.fragments.fragmentchangepassword;
import com.deeptruth.app.android.fragments.fragmentconfirmchannel;
import com.deeptruth.app.android.fragments.fragmentcreateaccount;
import com.deeptruth.app.android.fragments.fragmentforgotpassword;
import com.deeptruth.app.android.fragments.fragmentsharemedia;
import com.deeptruth.app.android.fragments.fragmentmedialist;
import com.deeptruth.app.android.fragments.fragmentsignin;
import com.deeptruth.app.android.fragments.fragmentsignup;
import com.deeptruth.app.android.fragments.fragmentverifyuser;
import com.deeptruth.app.android.fragments.imagecomposerfragment;
import com.deeptruth.app.android.fragments.imagereaderfragment;
import com.deeptruth.app.android.fragments.videocomposerfragment;
import com.deeptruth.app.android.fragments.videoreaderfragment;
import com.deeptruth.app.android.interfaces.adapteritemclick;
import com.deeptruth.app.android.interfaces.apiresponselistener;
import com.deeptruth.app.android.interfaces.homepressedlistener;
import com.deeptruth.app.android.interfaces.progressupdate;
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
import com.deeptruth.app.android.views.customseekbar;
import com.dropbox.core.android.Auth;
import com.dropbox.core.v2.files.FileMetadata;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.Permission;
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
import java.io.IOException;
import java.net.HttpURLConnection;
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
import com.box.androidsdk.content.BoxApiFile;
import com.box.androidsdk.content.BoxApiFolder;
import com.box.androidsdk.content.BoxConfig;
import com.box.androidsdk.content.BoxException;
import com.box.androidsdk.content.auth.BoxAuthentication;
import com.box.androidsdk.content.models.BoxEntity;
import com.box.androidsdk.content.models.BoxError;
import com.box.androidsdk.content.models.BoxFile;
import com.box.androidsdk.content.models.BoxSession;
import com.box.androidsdk.content.requests.BoxRequestsFile;

public abstract class baseactivity extends AppCompatActivity implements basefragment.fragmentnavigationhelper,
        connectivityreceiver.ConnectivityReceiverListener,BoxAuthentication.AuthListener {
    public static baseactivity instance;
    public boolean isapprunning = false;
    private basefragment mcurrentfragment;
    private SharedPreferences prefs;
    Dialog subdialogshare = null, standarduploadingdialog =null,videolockuploadingdialog =null,aboutsharedialog=null,shareoptionsdialog=null;
    private Stack<Fragment> mfragments = new Stack<Fragment>();
    private static final int permission_location_request_code = 91;
    String serverresponsemessage = "";
    int serverresponsecode = 0;
    private BroadcastReceiver broadcastshareapiafterlogin;
    String mediapath = "";
    String mediatype = "";
    String mediavideotoken = "",mediamethod = "";
    //Dialog dialog;
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

    private BoxSession mSession = null;
    private BoxSession mOldSession = null;
    private BoxApiFolder mFolderApi;
    private BoxApiFile mFileApi;
    private xapipostfile xapiupload;

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

    /**
     * Set required config parameters. Use values from your application settings in the box developer console.
     */
    private void configureboxclient() {
        BoxConfig.CLIENT_ID = applicationviavideocomposer.getactivity().getResources().getString(R.string.box_clientid);
        BoxConfig.CLIENT_SECRET = applicationviavideocomposer.getactivity().getResources().getString(R.string.box_secret);

        // needs to match redirect uri in developer settings if set.
        //   BoxConfig.REDIRECT_URL = "<YOUR_REDIRECT_URI>";
    }

    /**
     * Create a BoxSession and authenticate.
     */
    private void initboxsession() {
        mSession = new BoxSession(this);
        mSession.setSessionAuthListener(this);
        mSession.authenticate(this);
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

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(getreadytouploadfile() != null)
                                dropboxuploadfile(getreadytouploadfile().getAbsolutePath());
                        }
                    },1000);
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
        transaction.commitAllowingStateLoss();

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

        transaction.commitAllowingStateLoss();

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
            transaction.commitAllowingStateLoss();
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
        isapprunning(false);
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
        /*if(Build.VERSION.SDK_INT >= 11)
            api.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else*/
            api.execute();
    }


    @Override
    public void xapi_uploadfile(Context mContext, String serverurl, String filepath, apiresponselistener mListener,
                                progressupdate progressupdate) {
        xapiupload = new xapipostfile(mContext,serverurl,filepath,mListener,progressupdate);
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
                    //redirecttologin();
                    showdialogsignupfragment();

                    return;
                }

                /*if(type.equalsIgnoreCase(config.type_video) && ismediatrimmed)
                {
                    common.shouldshowupgradepopup(config.mediatrimcount);
                    if(common.ismediatrimcountexceed(config.mediatrimcount))
                    {
                        checkinapppurchasestatus(config.gravitycenter);
                        return;
                    }
                }*/

                callmediashareapi(type, mediatoken, path, mediamethod);
            }
            });

            linear_share_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediamethod = config.type_public;

                if(!isuserlogin()){
                    //redirecttologin();
                    showdialogsignupfragment();
                    return;
                }

                /*if(type.equalsIgnoreCase(config.type_video) && ismediatrimmed)
                {
                    common.shouldshowupgradepopup(config.mediatrimcount);
                    if(common.ismediatrimcountexceed(config.mediatrimcount))
                    {
                        checkinapppurchasestatus(config.gravitycenter);
                        return;
                    }
                }*/

                callmediashareapi(type , mediatoken, path, mediamethod);
            }
            });

            linear_share_btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediamethod = config.type_linkinvite;

                /*if(type.equalsIgnoreCase(config.type_video) && ismediatrimmed)
                {
                    common.shouldshowupgradepopup(config.mediatrimcount);
                    if(common.ismediatrimcountexceed(config.mediatrimcount))
                    {
                        checkinapppurchasestatus(config.gravitycenter);
                        return;
                    }
                }*/

                if(!isuserlogin()){
                   // redirecttologin();
                    showdialogsignupfragment();
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


            setscreenwidthheight(subdialogshare,80,60,getResources().getString(R.string.popup_share));

            subdialogshare.show();
    }

    public void checkinapppurchasestatus(String gravity)
    {
        String message = applicationviavideocomposer.getactivity().getResources().getString(R.string.if_you_would_like_the_option);

        showinapppurchasepopup(applicationviavideocomposer.
                getactivity(), "", message, new adapteritemclick(){
            @Override
            public void onItemClicked(Object object) {

            }
            @Override
            public void onItemClicked(Object object, int type) {

            }
        },gravity);
    }

    public void showaboutsharedialog(String filepath,String mediatoken,String mediatype,String mediathumbnailurl)
    {
        if(aboutsharedialog != null && aboutsharedialog.isShowing())
            aboutsharedialog.dismiss();

        aboutsharedialog =new Dialog(applicationviavideocomposer.getactivity(),R.style.transparent_dialog_borderless);
        aboutsharedialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        aboutsharedialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        aboutsharedialog.setCanceledOnTouchOutside(false);
        aboutsharedialog.setCancelable(true);
        aboutsharedialog.setContentView(R.layout.share_alert_popup);

        TextView txttitle = (TextView)aboutsharedialog.findViewById(R.id.txt_title);
        TextView txt_content = (TextView)aboutsharedialog.findViewById(R.id.txt_content);
        txttitle.setText("SHARE");

        txttitle.setTypeface(applicationviavideocomposer.boldfonttype, Typeface.BOLD);
        final CheckBox notifycheckbox = (CheckBox) aboutsharedialog.findViewById(R.id.notifycheckbox);
        notifycheckbox.setText(applicationviavideocomposer.getactivity().getResources().getString(R.string.do_not_notify_again));
        notifycheckbox.setTextColor(Color.BLACK);
        notifycheckbox.setTypeface(applicationviavideocomposer.comfortaaregular, Typeface.NORMAL);

        String strnew = getResources().getString(R.string.sharetext_line1)+"\n"+
                getResources().getString(R.string.sharetext_line2)+"\n"+
                getResources().getString(R.string.sharetext_line3)+"\n"+
                getResources().getString(R.string.sharetext_line4)+"\n"+
                getResources().getString(R.string.sharetext_line5)+"\n\n"+
                getResources().getString(R.string.sharetext_line7)+"\n"+
                getResources().getString(R.string.sharetext_line8)+"\n"+
                getResources().getString(R.string.sharetext_line9)+"\n"+
                getResources().getString(R.string.sharetext_line10)+"\n\n"+
                getResources().getString(R.string.sharetext_line12)+"\n\n"+
                getResources().getString(R.string.sharetext_line14)+"\n"+
                getResources().getString(R.string.sharetext_line15)+"\n"+
                getResources().getString(R.string.sharetext_line16)+"\n"+
                getResources().getString(R.string.sharetext_line17);

        ArrayList<sharepopuptextspanning> textsharepopup = new ArrayList<>();

        textsharepopup.add(new sharepopuptextspanning(1.01f,0,33,strnew));
        textsharepopup.add(new sharepopuptextspanning(0.99f,35,68,strnew));
        textsharepopup.add(new sharepopuptextspanning(1.03f,70,102,strnew));
        textsharepopup.add(new sharepopuptextspanning(1.02f,104,136,strnew));
        textsharepopup.add(new sharepopuptextspanning(1.04f,138,169,strnew));

        textsharepopup.add(new sharepopuptextspanning(1.02f,171,204,strnew));
        textsharepopup.add(new sharepopuptextspanning(0.99f,206,239,strnew));
        textsharepopup.add(new sharepopuptextspanning(1.01f,241,273,strnew));
        textsharepopup.add(new sharepopuptextspanning(1.04f,275,304,strnew));


        textsharepopup.add(new sharepopuptextspanning(1.05f,307,315,strnew));


        textsharepopup.add(new sharepopuptextspanning(1.01f,318,348,strnew));
        textsharepopup.add(new sharepopuptextspanning(1.08f,350,377,strnew));
        textsharepopup.add(new sharepopuptextspanning(1.08f,379,407,strnew));
        textsharepopup.add(new sharepopuptextspanning(1.05f,409,strnew.length(),strnew));


        common.setspanning(textsharepopup,txt_content);

        TextView ok = (TextView) aboutsharedialog.findViewById(R.id.btn_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aboutsharedialog.dismiss();

                if(notifycheckbox.isChecked())
                    xdata.getinstance().saveSetting(config.mediasharedialog,"1");
                else
                    xdata.getinstance().saveSetting(config.mediasharedialog,"0");

                showsharedialogfragment(filepath, mediatoken, mediatype, mediathumbnailurl);
            }
        });

        aboutsharedialog.setCanceledOnTouchOutside(false);
        setscreenwidthheight(aboutsharedialog,96,80,getResources().getString(R.string.popup_publish));
        aboutsharedialog.show();
    }

    public void showsharedialogfragment(String filepath, String mediatoken, String mediatype, String mediathumbnailurl)
    {
        try
        {
            if(mediatype.equalsIgnoreCase(config.type_image))
            {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment prev = getSupportFragmentManager().findFragmentByTag("mediatrimdialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);
                fragmentsharemedia fragment = new fragmentsharemedia();
                fragment.setdata(filepath, 0,mediatoken,mediatype,mediathumbnailurl);
                fragment.show(ft, "mediatrimdialog");
            }
            else
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
                            Fragment prev = getSupportFragmentManager().findFragmentByTag("mediatrimdialog");
                            if (prev != null) {
                                ft.remove(prev);
                            }
                            ft.addToBackStack(null);
                            fragmentsharemedia fragment = new fragmentsharemedia();
                            fragment.setdata(filepath, duration,mediatoken,mediatype,mediathumbnailurl);
                            fragment.show(ft, "mediatrimdialog");
                        }
                    });
                }
            }

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public void preparesharedialogfragment(String filepath, String mediatoken, String mediatype, String mediathumbnailurl)
    {
        if(xdata.getinstance().getSetting(config.mediasharedialog).trim().isEmpty() ||
                xdata.getinstance().getSetting(config.mediasharedialog).equalsIgnoreCase("0"))
            showaboutsharedialog(filepath,mediatoken,mediatype,mediathumbnailurl);
        else
            showsharedialogfragment(filepath, mediatoken, mediatype, mediathumbnailurl);
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

        progressdialog.showwaitingdialog(applicationviavideocomposer.getactivity());
        xapipost_send(applicationviavideocomposer.getactivity(), requestparams, new apiresponselistener() {

            @Override
            public void onResponse(taskresult response) {
                final int[] callbackstatus = {0};
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
                            xdata.getinstance().saveSetting(config.datauploading_process_dialog,"0");
                            xapi_uploadfile(applicationviavideocomposer.getactivity(), storageurl, path, new apiresponselistener() {
                                @Override
                                public void onResponse(taskresult response) {
                                    progressdialog.dismisswaitdialog();
                                    if (response.isSuccess())
                                    {
                                        callmediastoreapi(mediatoken, mediatype, path);
                                        Log.e("xapipostfile ","step6");
                                    }
                                    else
                                    {
                                        if(videolockuploadingdialog != null && videolockuploadingdialog.isShowing())
                                            videolockuploadingdialog.dismiss();
                                    }
                                }
                            }, new progressupdate() {
                                @Override
                                public void onupdateprogress(String percentage) {
                                    progressdialog.dismisswaitdialog();
                                    if(xdata.getinstance().getSetting(config.datauploading_process_dialog).equalsIgnoreCase("0")
                                            && callbackstatus[0] == 0)
                                    {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        showvideolockuploadingprocessdialog(applicationviavideocomposer.getactivity(),
                                                            Long.parseLong(percentage),100,"",false,
                                                            new adapteritemclick() {
                                                                @Override
                                                                public void onItemClicked(Object object) {
                                                                    callbackstatus[0] =1;
                                                                }

                                                                @Override
                                                                public void onItemClicked(Object object, int type) {
                                                                }
                                                            });
                                                    }
                                                });
                                            }
                                        }).start();
                                    }
                                }
                            });
                        }
                        else if(! shareurl.trim().isEmpty())
                        {
                            updatemediapublishedstatus(mediatoken);
                            common.sharemessagewithapps(shareurl);
                            medialistitemaddbroadcast();
                            progressdialog.dismisswaitdialog();
                        }

                        if(object.has("message"))
                            Toast.makeText(baseactivity.this,object.getString("message"),Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    progressdialog.dismisswaitdialog();
                    Toast.makeText(baseactivity.this,response.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void callmediastoreapi(String mediatoken, String mediatype, String mediapath)
    {

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

        progressdialog.showwaitingdialog(applicationviavideocomposer.getactivity());
        xapipost_send(applicationviavideocomposer.getactivity(), requestparams, new apiresponselistener() {

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
                        {
                            updatemediapublishedstatus(mediatoken);
                            //common.sharemessagewithapps(shareurl);
                            medialistitemaddbroadcast();

                            if(xdata.getinstance().getSetting(config.datauploaded_success_dialog).equalsIgnoreCase("1"))
                            {
                                common.shownotification(applicationviavideocomposer.getactivity(),applicationviavideocomposer.getactivity()
                                    .getResources().getString(R.string.your_file_has_been_copied));
                                showvideolockuploadingprocessdialog(applicationviavideocomposer.getactivity(),
                                        100,100,shareurl,true,
                                        new adapteritemclick() {
                                            @Override
                                            public void onItemClicked(Object object) {

                                            }

                                            @Override
                                            public void onItemClicked(Object object, int type) {

                                            }
                                        });
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    if(videolockuploadingdialog != null && videolockuploadingdialog.isShowing())
                        videolockuploadingdialog.dismiss();
                }
            }
        });
    }

    public void medialistitemaddbroadcast()
    {
        Intent intent = new Intent(config.broadcast_medialistnewitem);
        applicationviavideocomposer.getactivity().sendBroadcast(intent);
    }

    public void updatemediapublishedstatus(String mediatoken)
    {
        try
        {
            databasemanager mdbhelper = new databasemanager(baseactivity.this);
            mdbhelper.createDatabase();
            try
            {
                mdbhelper.open();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                mdbhelper.updatepublishmediastatus(mediatoken);
                mdbhelper.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public void callsharapiafterlogin()
    {
        callmediashareapi(mediatype , mediavideotoken, mediapath, mediamethod);
    }

    public void callloginscreen(){
        //redirecttologin();
        return;
    }

    public void share_alert_dialog(final Context context,final String title, String content,adapteritemclick mitemclick){

        if(shareoptionsdialog != null && shareoptionsdialog.isShowing())
            shareoptionsdialog.dismiss();

        shareoptionsdialog =new Dialog(applicationviavideocomposer.getactivity(),R.style.transparent_dialog_borderless);
        shareoptionsdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        shareoptionsdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        shareoptionsdialog.setCanceledOnTouchOutside(false);
        shareoptionsdialog.setCancelable(true);
        shareoptionsdialog.setContentView(R.layout.share_alert_popup);

        TextView txttitle = (TextView)shareoptionsdialog.findViewById(R.id.txt_title);
        TextView txt_content = (TextView)shareoptionsdialog.findViewById(R.id.txt_content);
        txttitle.setText(title);
        String str = content;

        txttitle.setTypeface(applicationviavideocomposer.boldfonttype, Typeface.BOLD);
        final CheckBox notifycheckbox = (CheckBox) shareoptionsdialog.findViewById(R.id.notifycheckbox);
        notifycheckbox.setText(applicationviavideocomposer.getactivity().getResources().getString(R.string.do_not_notify_again));
        notifycheckbox.setTextColor(Color.BLACK);
        notifycheckbox.setTypeface(applicationviavideocomposer.comfortaaregular, Typeface.NORMAL);

        if(title.equalsIgnoreCase(context.getResources().getString(R.string.txt_publish))){
            char[] linecontent = str.toCharArray();
            ArrayList<sharepopuptextspanning> textsharepopup = new ArrayList<>();

            textsharepopup.add(new sharepopuptextspanning(1.08f,0,29,str));
            textsharepopup.add(new sharepopuptextspanning(1.00f,29,59,str));
            textsharepopup.add(new sharepopuptextspanning(1.00f,59,92,str));
            textsharepopup.add(new sharepopuptextspanning(1.05f,92,123,str));

            textsharepopup.add(new sharepopuptextspanning(1.00f,123,157,str));//144
            textsharepopup.add(new sharepopuptextspanning(1.05f,157,187,str));
            textsharepopup.add(new sharepopuptextspanning(1.04f,187,217,str));
            textsharepopup.add(new sharepopuptextspanning(1.10f,217,247,str));
            textsharepopup.add(new sharepopuptextspanning(1.05f,247,277,str));

            textsharepopup.add(new sharepopuptextspanning(1.00f,278,310,str));
            textsharepopup.add(new sharepopuptextspanning(1.03f,313,343,str));
            textsharepopup.add(new sharepopuptextspanning(1.01f,346,373,str));
            textsharepopup.add(new sharepopuptextspanning(1.03f,377,404,str));

            textsharepopup.add(new sharepopuptextspanning(1.07f,408,437,str));
            textsharepopup.add(new sharepopuptextspanning(1.03f,440,str.length(),str));


            common.setspanning(textsharepopup,txt_content);

        }
         if(title.equalsIgnoreCase(context.getResources().getString(R.string.txt_send))){
             ArrayList<sharepopuptextspanning> textsharepopup = new ArrayList<>();

             textsharepopup.add(new sharepopuptextspanning(1.09f,0,28,str));
             textsharepopup.add(new sharepopuptextspanning(1.07f,29,58,str));
             textsharepopup.add(new sharepopuptextspanning(1.11f,58,89,str));

             textsharepopup.add(new sharepopuptextspanning(1.01f,93,122,str));
             textsharepopup.add(new sharepopuptextspanning(1.05f,122,153,str));
             textsharepopup.add(new sharepopuptextspanning(1.03f,153,186,str));
             textsharepopup.add(new sharepopuptextspanning(1.06f,186,219,str));
             textsharepopup.add(new sharepopuptextspanning(1.02f,219,253,str));

             textsharepopup.add(new sharepopuptextspanning(1.04f,254,282,str));//172
             textsharepopup.add(new sharepopuptextspanning(1.02f,282,315,str));
             textsharepopup.add(new sharepopuptextspanning(1.04f,315,348,str));//172
             textsharepopup.add(new sharepopuptextspanning(1.05f,348,381,str));

             textsharepopup.add(new sharepopuptextspanning(1.06f,381,413,str));//172
             textsharepopup.add(new sharepopuptextspanning(1.03f,413,str.length(),str));
             common.setspanning(textsharepopup,txt_content);
        }

        if(title.equalsIgnoreCase(context.getResources().getString(R.string.txt_save))){
            char[] linecontent = str.toCharArray();
            ArrayList<sharepopuptextspanning> textsharepopup = new ArrayList<>();

            String strnew = getResources().getString(R.string.savetext_line1)+"\n"+
                    getResources().getString(R.string.savetext_line2)+"\n"+
                    getResources().getString(R.string.savetext_line3)+"\n\n"+
                    getResources().getString(R.string.savetext_line5)+"\n"+
                    getResources().getString(R.string.savetext_line6)+"\n"+
                    getResources().getString(R.string.savetext_line7)+"\n"+
                    getResources().getString(R.string.savetext_line8)+"\n"+
                    getResources().getString(R.string.savetext_line9)+"\n"+
                    getResources().getString(R.string.savetext_line10)+"\n"+
                    getResources().getString(R.string.savetext_line11)+"\n\n"+
                    getResources().getString(R.string.savetext_line13)+"\n"+
                    getResources().getString(R.string.savetext_line14)+"\n"+
                    getResources().getString(R.string.savetext_line15)+"\n"+
                    getResources().getString(R.string.savetext_line16)+"\n"+
                    getResources().getString(R.string.savetext_line17);

            textsharepopup.add(new sharepopuptextspanning(1.09f,0,29,strnew));
            textsharepopup.add(new sharepopuptextspanning(1.04f,31,61,strnew));
            textsharepopup.add(new sharepopuptextspanning(1.04f,63,93,strnew));

            textsharepopup.add(new sharepopuptextspanning(1.11f,96,124,strnew));
            textsharepopup.add(new sharepopuptextspanning(1.07f,126,154,strnew));
            textsharepopup.add(new sharepopuptextspanning(1.07f,156,184,strnew));
            textsharepopup.add(new sharepopuptextspanning(1.06f,186,213,strnew));
            textsharepopup.add(new sharepopuptextspanning(1.06f,215,242,strnew));
            textsharepopup.add(new sharepopuptextspanning(1.07f,244,273,strnew));
            textsharepopup.add(new sharepopuptextspanning(1.02f,275,309,strnew));

            textsharepopup.add(new sharepopuptextspanning(1.04f,312,342,strnew));//144
            textsharepopup.add(new sharepopuptextspanning(1.04f,344,373,strnew));
            textsharepopup.add(new sharepopuptextspanning(1.11f,375,400,strnew));
            textsharepopup.add(new sharepopuptextspanning(1.04f,402,433,strnew));//172
            textsharepopup.add(new sharepopuptextspanning(1.04f,435,strnew.length(),strnew));

            common.setspanning(textsharepopup,txt_content);
        }

        TextView ok = (TextView) shareoptionsdialog.findViewById(R.id.btn_ok);
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
                if(title.equalsIgnoreCase(context.getResources().getString(R.string.txt_save))){
                    if(notifycheckbox.isChecked())
                        xdata.getinstance().saveSetting(config.enableexportnotification,"1");
                    else
                        xdata.getinstance().saveSetting(config.enableexportnotification,"0");
                }

                if(mitemclick != null)
                    mitemclick.onItemClicked(null);

                //baseactivity.getinstance().senditemsdialog(context);

                shareoptionsdialog.dismiss();

            }
        });

        shareoptionsdialog.setCanceledOnTouchOutside(false);
        setscreenwidthheight(shareoptionsdialog,96,80,getResources().getString(R.string.popup_publish));
        shareoptionsdialog.show();
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
                        inapppurchase("ABC");
                    }
                    else if(trimbuttontext.get(position).getButtontext().equalsIgnoreCase(config.txtmonth))
                    {
                        inapppurchase("ABC");
                    }
                    else if(trimbuttontext.get(position).getButtontext().equalsIgnoreCase(config.txtupgrade))
                    {
                        if(dialoginapppurchase != null && dialoginapppurchase.isShowing())
                            dialoginapppurchase.dismiss();

                        showupgradecodepopup(applicationviavideocomposer.getactivity(), title, message, mitemclick, gravity);
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
            textsharepopup.add(new sharepopuptextspanning(1.03f,0,23,str));
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
            textsharepopup.add(new sharepopuptextspanning(1.18f,0,24,str));
            textsharepopup.add(new sharepopuptextspanning(1.09f,26,47,str));
            textsharepopup.add(new sharepopuptextspanning(1.10f,47,70,str));
            textsharepopup.add(new sharepopuptextspanning(1.12f,72,89,str));

            textsharepopup.add(new sharepopuptextspanning(1.23f,91,112,str));
            textsharepopup.add(new sharepopuptextspanning(1.16f,113,133,str));
            textsharepopup.add(new sharepopuptextspanning(1.16f,133,156,str));
            textsharepopup.add(new sharepopuptextspanning(1.14f,157,str.length(),str));
            common.setspanning(textsharepopup,txt_content);

        }else if(txt_title.getText().toString().contains("TRIM")){

            String str = getResources().getString(R.string.trim_line1)+"\n"+ getResources().getString(R.string.trim_line2) +"\n"+
                    getResources().getString(R.string.trim_line3) +"\n"+ getResources().getString(R.string.trim_line4) +"\n"+
                    getResources().getString(R.string.trim_line5) +"\n"+ getResources().getString(R.string.trim_line6);

            ArrayList<sharepopuptextspanning> textsharepopup = new ArrayList<>();
            textsharepopup.add(new sharepopuptextspanning(1.02f,0,28,str));
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
            setscreenwidthheight(dialoginapppurchase,80,0,getResources().getString(R.string.popup_upgrade));
        }else{
            setscreenwidthheight(dialoginapppurchase,80,0,getResources().getString(R.string.popup_trim));
        }

        dialoginapppurchase.show();
    }

    public void showupgradecodepopup(final Context activity,String title,String message, final adapteritemclick mitemclick,String  gravity)
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
        txt_content.setTypeface(applicationviavideocomposer.comfortaaregular, Typeface.NORMAL);
        tvcancel.setTypeface(applicationviavideocomposer.comfortaaregular, Typeface.BOLD);
        tv_upgrade.setTypeface(applicationviavideocomposer.comfortaaregular, Typeface.BOLD);
        txt_title.setTypeface(applicationviavideocomposer.boldfonttype, Typeface.BOLD);
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
                    xapipost_send(applicationviavideocomposer.
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

                if(dialoginapppurchase != null)
                    showinapppurchasepopup(applicationviavideocomposer.getactivity(), title, message,  mitemclick, gravity);

            }
        });

        setscreenwidthheight(dialogupgradecode,85,60,getResources().getString(R.string.popup_upgradecode));
        dialogupgradecode.show();
    }

    public void senditemsdialog(Context context,String mediafilepath,String mediatoken,final String type,
                                boolean ismediatrimmed,String mediathumbnailurl,String trimmedmediapath,String popupcontenttype,boolean ismedialist){

        /*if(popupcontenttype.equalsIgnoreCase(getResources().getString(R.string.txt_publish)))
        {
            if(!isuserlogin()){
                redirecttologin();
                return;
            }
        }*/

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
        TextView txt_title = (TextView) dialogfileuploadoptions.findViewById(R.id.txt_title);

        txt_title.setText(popupcontenttype);

        adaptersenddialog adaptersend = new adaptersenddialog(context, sharemedia, new adapteritemclick() {
            @Override
            public void onItemClicked(Object object) {
                if(object != null)
                {
                    setreadytouploadfile(new File(trimmedmediapath));
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
                    if(sharemedia.get(position).getMedianame().equalsIgnoreCase(config.item_box))
                    {
                        if(mFileApi == null)
                        {
                            BoxConfig.IS_LOG_ENABLED = true;
                            configureboxclient();
                            initboxsession();
                        }
                        else
                        {
                            uploadfileonbox(getreadytouploadfile());
                        }
                    }
                    else if(sharemedia.get(position).getMedianame().equalsIgnoreCase(config.item_googledrive))
                    {
                        requestgooglesignin();
                    }
                    else if(sharemedia.get(position).getMedianame().equalsIgnoreCase(config.item_videoLock_share))
                    {
                        if(dialogfileuploadoptions != null && dialogfileuploadoptions.isShowing())
                            dialogfileuploadoptions.dismiss();

                        videolocksharedialog(applicationviavideocomposer.getactivity(),mediafilepath, mediatoken,  type,
                                ismediatrimmed,mediathumbnailurl,trimmedmediapath,popupcontenttype,ismedialist);
                    }
                    else if(sharemedia.get(position).getMedianame().equalsIgnoreCase(config.item_microsoft_onedrive))
                    {
                        loginwithonedrive(trimmedmediapath);
                    }
                    else if(sharemedia.get(position).getMedianame().equalsIgnoreCase(config.item_dropbox))
                    {
                        if(xdata.getinstance().getSetting(config.dropboxauthtoken).trim().isEmpty())
                        {
                            if(BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_composer))
                            {
                                Auth.startOAuth2Authentication(baseactivity.this, applicationviavideocomposer.getactivity()
                                        .getResources().getString(R.string.dropbox_appkey));
                            }
                            else if(BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_reader))
                            {
                                Auth.startOAuth2Authentication(baseactivity.this, applicationviavideocomposer.getactivity()
                                        .getResources().getString(R.string.dropbox_appkey_reader));
                            }
                        }
                        else
                        {
                            dropboxuploadfile(trimmedmediapath);
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

                if(!ismedialist)
                      showsharedialogfragment(mediafilepath, mediatoken, type, mediathumbnailurl);

                if(dialogfileuploadoptions != null && dialogfileuploadoptions.isShowing())
                    dialogfileuploadoptions.dismiss();

                //preparesharedialogfragment(mediafilepath,mediatoken,type);
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

        setscreenwidthheight(dialogfileuploadoptions,95,80,context.getResources().getString(R.string.popup_send));
        dialogfileuploadoptions.show();
    }

    public void dropboxuploadfile(String filepath) {

        //progressdialog.showwaitingdialog(applicationviavideocomposer.getactivity());
        final int[] callbackstatus = {0};
        xdata.getinstance().saveSetting(config.datauploading_process_dialog,"0");
        new uploadfileatdropbox(applicationviavideocomposer.getactivity(), DropboxClientFactory.getClient(),
                new uploadfileatdropbox.Callback()
        {
            @Override
            public void onUploadComplete(FileMetadata result) {
                progressdialog.dismisswaitdialog();
                //Toast.makeText(applicationviavideocomposer.getactivity(), "File uploaded successfully!", Toast.LENGTH_SHORT).show();

                if(xdata.getinstance().getSetting(config.datauploaded_success_dialog).equalsIgnoreCase("1"))
                {
                    common.shownotification(applicationviavideocomposer.getactivity(),applicationviavideocomposer.getactivity()
                            .getResources().getString(R.string.file_uploaded_dropbox));
                    showstandarduploadprocesscompletedialog(applicationviavideocomposer.getactivity(),100,100,
                            "",applicationviavideocomposer.getactivity().getResources().getString(
                                    R.string.file_uploaded_dropbox));

                }

                if(dialogfileuploadoptions != null && dialogfileuploadoptions.isShowing())
                    dialogfileuploadoptions.dismiss();
            }

            @Override
            public void onError(Exception e) {
                progressdialog.dismisswaitdialog();
                if(standarduploadingdialog == null && (standarduploadingdialog.isShowing()))
                    standarduploadingdialog.dismiss();

                Toast.makeText(applicationviavideocomposer.getactivity(), "Failed to upload file!", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onProgress(int percentagecompleted) {
                applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(xdata.getinstance().getSetting(config.datauploading_process_dialog).equalsIgnoreCase("0")
                                && callbackstatus[0] == 0)
                        {
                            showstandarduploadingprocessdialog(applicationviavideocomposer.getactivity(),percentagecompleted,100,
                                    "",applicationviavideocomposer.getactivity().getResources().getString(
                                            R.string.file_uploaded_dropbox),false, new adapteritemclick() {
                                        @Override
                                        public void onItemClicked(Object object)
                                        {
                                            callbackstatus[0] =1;
                                        }

                                        @Override
                                        public void onItemClicked(Object object, int type) {

                                        }
                                    });
                        }

                    }
                });
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

                    //progressdialog.showwaitingdialog(applicationviavideocomposer.getactivity());
                    try
                    {
                        xdata.getinstance().saveSetting(config.datauploading_process_dialog,"0");
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
                        Task<GoogleDriveFileHolder> fileHolder=uploadfileatgoogledrive(googleDriveService,getreadytouploadfile(),
                                common.getmimetype(getreadytouploadfile().getAbsolutePath()),null);

                        fileHolder.addOnCompleteListener(applicationviavideocomposer.getactivity(), new OnCompleteListener<GoogleDriveFileHolder>() {
                            @Override
                            public void onComplete(@NonNull Task<GoogleDriveFileHolder> task) {

                            }
                        });

                        fileHolder.addOnCanceledListener(applicationviavideocomposer.getactivity(), new OnCanceledListener() {
                            @Override
                            public void onCanceled() {
                                if(standarduploadingdialog == null && (standarduploadingdialog.isShowing()))
                                    standarduploadingdialog.dismiss();

                                Toast.makeText(applicationviavideocomposer.getactivity(),applicationviavideocomposer.getactivity()
                                        .getResources().getString(R.string.media_upload_failed),Toast.LENGTH_SHORT).show();
                            }
                        });

                        fileHolder.addOnFailureListener(applicationviavideocomposer.getactivity(), new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                if(standarduploadingdialog == null && (standarduploadingdialog.isShowing()))
                                    standarduploadingdialog.dismiss();

                                Toast.makeText(applicationviavideocomposer.getactivity(),applicationviavideocomposer.getactivity()
                                        .getResources().getString(R.string.media_upload_failed),Toast.LENGTH_SHORT).show();
                            }
                        });

                        fileHolder.addOnSuccessListener(applicationviavideocomposer.getactivity(),
                                new OnSuccessListener<GoogleDriveFileHolder>() {
                            @Override
                            public void onSuccess(GoogleDriveFileHolder googleDriveFileHolder) {

                                String mediatype=common.getmediatypebymimetype(common.getmimetype
                                        (getreadytouploadfile().getAbsolutePath()));

                                String sharabalelink="https://drive.google.com/open?id="+googleDriveFileHolder.getId();

                                String sharemessage=applicationviavideocomposer.getactivity().getResources().getString(R.string.a_certified_videoLock)
                                        +" "+mediatype+": "+sharabalelink+" "+applicationviavideocomposer.getactivity().
                                        getResources().getString(R.string.if_you_dont_have_the_videolock);

                                progressdialog.dismisswaitdialog();
                               // Toast.makeText(applicationviavideocomposer.getactivity(),applicationviavideocomposer.getactivity()
                                 //       .getResources().getString(R.string.media_upload_success),Toast.LENGTH_SHORT).show();

                                if(xdata.getinstance().getSetting(config.datauploaded_success_dialog).equalsIgnoreCase("1"))
                                {
                                    common.shownotification(applicationviavideocomposer.getactivity(),applicationviavideocomposer.getactivity()
                                            .getResources().getString(R.string.file_uploaded_googledrive));
                                    showstandarduploadprocesscompletedialog(applicationviavideocomposer.getactivity(),100,100,
                                            sharemessage,applicationviavideocomposer.getactivity().getResources().getString(
                                                    R.string.file_uploaded_googledrive));
                                }

                            }
                        });

                    }catch (Exception e)
                    {
                        progressdialog.dismisswaitdialog();
                        if(standarduploadingdialog == null && (standarduploadingdialog.isShowing()))
                            standarduploadingdialog.dismiss();

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

    public void uploadfileatmsonedrive(String filepath)
    {
        final applicationviavideocomposer application = (applicationviavideocomposer)baseactivity.this.getApplication();
        IOneDriveClient oneDriveClient = application.getOneDriveClient();
        xdata.getinstance().saveSetting(config.datauploading_process_dialog,"0");
        progressdialog.dismisswaitdialog();
        final int[] callbackstatus = {0};
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
                                            //Toast.makeText(baseactivity.this,applicationviavideocomposer.getactivity().getResources()
                                            //       .getString(R.string.file_uploaded),
                                            //      Toast.LENGTH_SHORT).show();

                                            applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if(xdata.getinstance().getSetting(config.datauploaded_success_dialog).
                                                            equalsIgnoreCase("1"))
                                                    {
                                                        common.shownotification(applicationviavideocomposer.getactivity(),applicationviavideocomposer.getactivity()
                                                                .getResources().getString(R.string.file_uploaded_onedrive));
                                                        showstandarduploadprocesscompletedialog(applicationviavideocomposer.getactivity(),
                                                                100,100,"",applicationviavideocomposer.getactivity().getResources().getString(
                                                                        R.string.file_uploaded_onedrive));
                                                    }

                                                }
                                            });

                                            if(dialogfileuploadoptions != null && dialogfileuploadoptions.isShowing())
                                                dialogfileuploadoptions.dismiss();
                                        }

                                        @Override
                                        public void failure(final ClientException error) {
                                            progressdialog.dismisswaitdialog();
                                            if(standarduploadingdialog == null && (standarduploadingdialog.isShowing()))
                                                standarduploadingdialog.dismiss();

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
                                            applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run()
                                                {
                                                    if(xdata.getinstance().getSetting(config.datauploading_process_dialog).
                                                            equalsIgnoreCase("0") && callbackstatus[0] == 0)
                                                    {
                                                        double progresspercentage = (current * 100) / max;
                                                        showstandarduploadingprocessdialog(applicationviavideocomposer.getactivity(),
                                                                (long)progresspercentage,100,"",
                                                                "",false, new adapteritemclick() {
                                                                    @Override
                                                                    public void onItemClicked(Object object) {
                                                                        callbackstatus[0] = 1;
                                                                    }

                                                                    @Override
                                                                    public void onItemClicked(Object object, int type) {

                                                                    }
                                                                });
                                                    }

                                                }
                                            });
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

    public void showstandarduploadingprocessdialog(final Context context, long progress, long maxvalue, String sharemessage,
                                                   String fileuploaded,boolean isuploadcomplete, adapteritemclick mitemclick)
    {

        if(standarduploadingdialog == null || (! standarduploadingdialog.isShowing()))
        {
            standarduploadingdialog =new Dialog(context,R.style.transparent_dialog_borderless);
            standarduploadingdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            standarduploadingdialog.setCanceledOnTouchOutside(false);
            standarduploadingdialog.setCancelable(true);
            standarduploadingdialog.setContentView(R.layout.dialog_standarduploadingprocess);
        }

        TextView txt_progress = (TextView) standarduploadingdialog.findViewById(R.id.txt_progress);
        TextView txt_fileuploaded = (TextView) standarduploadingdialog.findViewById(R.id.txt_fileuploaded);
        TextView txt_ok = (TextView) standarduploadingdialog.findViewById(R.id.txt_ok);
        TextView txt_cancel = (TextView) standarduploadingdialog.findViewById(R.id.txt_cancel);
        TextView txt_please_wait = (TextView) standarduploadingdialog.findViewById(R.id.txt_please_wait);
        customseekbar seekbar_uploading = (customseekbar) standarduploadingdialog.findViewById(R.id.seekbar_uploading);
        AppCompatCheckBox checkbox_notify = (AppCompatCheckBox) standarduploadingdialog.findViewById(R.id.checkbox_notify);
        checkbox_notify.setText(context.getResources().getString(R.string.notify_when_complete));
        checkbox_notify.setTextColor(context.getResources().getColor(R.color.black));
        txt_fileuploaded.setText(fileuploaded);

        seekbar_uploading.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b)
            {
                txt_progress.setText(progress+"% Complete");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        if(progress == 100)
        {
            if(standarduploadingdialog != null && (standarduploadingdialog.isShowing()))
                standarduploadingdialog.dismiss();
        }
        else
        {
            if(isuploadcomplete)
            {
                checkbox_notify.setVisibility(View.GONE);
                txt_fileuploaded.setVisibility(View.VISIBLE);
                txt_cancel.setVisibility(View.GONE);
                txt_please_wait.setVisibility(View.GONE);
            }
            else
            {
                txt_please_wait.setVisibility(View.VISIBLE);
                checkbox_notify.setVisibility(View.VISIBLE);
                txt_fileuploaded.setVisibility(View.GONE);
                txt_cancel.setVisibility(View.VISIBLE);
            }

            seekbar_uploading.setProgress((int)progress);
            seekbar_uploading.setMax((int)maxvalue);
            seekbar_uploading.setPadding(0, 0, 0, 0);

            txt_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if(isuploadcomplete)
                    {
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("googledrivelink",sharemessage );
                        clipboard.setPrimaryClip(clip);
                        common.sharemessagewithapps(sharemessage);
                    }
                    else
                    {
                        if(checkbox_notify.isChecked())
                            xdata.getinstance().saveSetting(config.datauploaded_success_dialog,"1");
                        else
                            xdata.getinstance().saveSetting(config.datauploaded_success_dialog,"0");

                        xdata.getinstance().saveSetting(config.datauploading_process_dialog,"1");

                        if(mitemclick != null)
                            mitemclick.onItemClicked("1");
                    }

                    if(standarduploadingdialog != null && standarduploadingdialog.isShowing())
                        standarduploadingdialog.dismiss();
                }
            });

            if(checkbox_notify.isChecked())
                xdata.getinstance().saveSetting(config.datauploaded_success_dialog,"1");

            txt_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if(standarduploadingdialog != null && standarduploadingdialog.isShowing())
                        standarduploadingdialog.dismiss();
                }
            });
            setscreenwidthheight(standarduploadingdialog,95,80,
                    context.getResources().getString(R.string.popup_videolock));
            standarduploadingdialog.show();
        }
    }

    public void showstandarduploadprocesscompletedialog(final Context context, long progress, long maxvalue, String sharemessage, String fileuploaded)
    {

        Dialog datauploadcompletedialog =new Dialog(context,R.style.transparent_dialog_borderless);
        datauploadcompletedialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        datauploadcompletedialog.setCanceledOnTouchOutside(false);
        datauploadcompletedialog.setCancelable(true);
        datauploadcompletedialog.setContentView(R.layout.dialog_standarduploadingprocess);

        TextView txt_progress = (TextView) datauploadcompletedialog.findViewById(R.id.txt_progress);
        TextView txt_fileuploaded = (TextView) datauploadcompletedialog.findViewById(R.id.txt_fileuploaded);
        TextView txt_ok = (TextView) datauploadcompletedialog.findViewById(R.id.txt_ok);
        TextView txt_cancel = (TextView) datauploadcompletedialog.findViewById(R.id.txt_cancel);
        TextView txt_please_wait = (TextView) datauploadcompletedialog.findViewById(R.id.txt_please_wait);
        customseekbar seekbar_uploading = (customseekbar) datauploadcompletedialog.findViewById(R.id.seekbar_uploading);
        AppCompatCheckBox checkbox_notify = (AppCompatCheckBox) datauploadcompletedialog.findViewById(R.id.checkbox_notify);
        checkbox_notify.setText(context.getResources().getString(R.string.notify_when_complete));
        checkbox_notify.setTextColor(context.getResources().getColor(R.color.black));
        txt_fileuploaded.setText(fileuploaded);

        seekbar_uploading.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b)
            {
                txt_progress.setText(progress+"% Complete");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        checkbox_notify.setVisibility(View.GONE);
        txt_fileuploaded.setVisibility(View.VISIBLE);
        txt_cancel.setVisibility(View.GONE);
        txt_please_wait.setVisibility(View.GONE);

        seekbar_uploading.setProgress((int)progress);
        seekbar_uploading.setMax((int)maxvalue);
        seekbar_uploading.setPadding(0, 0, 0, 0);

        txt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("uploadfilelink",sharemessage );
                clipboard.setPrimaryClip(clip);
                common.sharemessagewithapps(sharemessage);

                if(datauploadcompletedialog != null && datauploadcompletedialog.isShowing())
                    datauploadcompletedialog.dismiss();
            }
        });

        txt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(datauploadcompletedialog != null && datauploadcompletedialog.isShowing())
                    datauploadcompletedialog.dismiss();
            }
        });
        setscreenwidthheight(datauploadcompletedialog,95,80,
                context.getResources().getString(R.string.popup_videolock));
        datauploadcompletedialog.show();
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

                Drive.Files.Create create=googleDriveService.files().create(metadata, fileContent);
                MediaHttpUploader uploader=create.getMediaHttpUploader();
                uploader.setDirectUploadEnabled(false);
                uploader.setChunkSize(MediaHttpUploader.MINIMUM_CHUNK_SIZE); // previously I am using 1000000 thats why it won't work
                uploader.setProgressListener(new FileUploadProgressListener());

                com.google.api.services.drive.model.File fileMeta = create.execute();

                Permission anyoneReadPerm = new Permission();
                anyoneReadPerm.setType("anyone");
                anyoneReadPerm.setRole("reader");
                googleDriveService.permissions().create(fileMeta.getId(), anyoneReadPerm).execute();

                GoogleDriveFileHolder googleDriveFileHolder = new GoogleDriveFileHolder();
                googleDriveFileHolder.setId(fileMeta.getId());
                googleDriveFileHolder.setName(fileMeta.getName());
                return googleDriveFileHolder;
            }
        });
    }

    private class FileUploadProgressListener implements MediaHttpUploaderProgressListener {

        final int[] callbackstatus = {0};

        public FileUploadProgressListener() {
        }

        @Override
        public void progressChanged(MediaHttpUploader mediaHttpUploader) throws IOException {
            if (mediaHttpUploader == null) return;
            switch (mediaHttpUploader.getUploadState()) {
                case INITIATION_STARTED:
                    Log.d("INITIATION_STARTED",  " INITIATION_STARTED");
                    break;
                case INITIATION_COMPLETE:
                    Log.d("INITIATION_COMPLETE",  " INITIATION_COMPLETE");
                    break;
                case MEDIA_IN_PROGRESS:
                    double percent = mediaHttpUploader.getProgress() * 100;
                    applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(xdata.getinstance().getSetting(config.datauploading_process_dialog).
                                    equalsIgnoreCase("0") && callbackstatus[0] == 0)
                            {
                                showstandarduploadingprocessdialog(applicationviavideocomposer.getactivity(), (int) percent, 100,
                                        "", "", false, new adapteritemclick() {
                                            @Override
                                            public void onItemClicked(Object object) {
                                                callbackstatus[0] = 1;
                                            }

                                            @Override
                                            public void onItemClicked(Object object, int type) {

                                            }
                                        });
                            }
                        }
                    });

                    break;
                case MEDIA_COMPLETE:
                    //System.out.println("Upload is complete!");
                    Log.d("MEDIA_COMPLETE",  " MEDIA_COMPLETE");
                    applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(xdata.getinstance().getSetting(config.datauploading_process_dialog).
                                    equalsIgnoreCase("0") && callbackstatus[0] == 0)
                            {
                                showstandarduploadingprocessdialog(applicationviavideocomposer.getactivity(), 100, 100,
                                        "", "", false, new adapteritemclick() {
                                            @Override
                                            public void onItemClicked(Object object) {
                                                callbackstatus[0] = 1;
                                            }

                                            @Override
                                            public void onItemClicked(Object object, int type) {

                                            }
                                        });
                            }
                        }
                    });
            }
        }
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

    public void showvideolockuploadingprocessdialog(final Context context, long progress, long maxvalue, String sharemessage,
                                                   boolean isuploadcomplete, adapteritemclick mitemclick)
    {
        if(videolockuploadingdialog == null || (! videolockuploadingdialog.isShowing()))
        {
            videolockuploadingdialog =new Dialog(context,R.style.transparent_dialog_borderless);
            videolockuploadingdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            videolockuploadingdialog.setCanceledOnTouchOutside(false);
            videolockuploadingdialog.setCancelable(true);
            videolockuploadingdialog.setContentView(R.layout.dialog_videolockuploadingprocess);
        }

        TextView txt_progress = (TextView) videolockuploadingdialog.findViewById(R.id.txt_progress);
        TextView txt_fileuploaded = (TextView) videolockuploadingdialog.findViewById(R.id.txt_fileuploaded);
        TextView txt_ok = (TextView) videolockuploadingdialog.findViewById(R.id.txt_ok);
        TextView txt_cancel = (TextView) videolockuploadingdialog.findViewById(R.id.txt_cancel);
        TextView txt_please_wait = (TextView) videolockuploadingdialog.findViewById(R.id.txt_please_wait);
        customseekbar seekbar_uploading = (customseekbar) videolockuploadingdialog.findViewById(R.id.seekbar_uploading);
        AppCompatCheckBox checkbox_notify = (AppCompatCheckBox) videolockuploadingdialog.findViewById(R.id.checkbox_notify);
        checkbox_notify.setText(context.getResources().getString(R.string.notify_when_complete));
        checkbox_notify.setTextColor(context.getResources().getColor(R.color.white));
        txt_fileuploaded.setText(applicationviavideocomposer.getactivity().getResources().getString(R.string.your_file_has_been_copied));

        seekbar_uploading.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b)
            {
                txt_progress.setText(progress+"% Complete");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        if(isuploadcomplete)
        {
            checkbox_notify.setVisibility(View.GONE);
            txt_fileuploaded.setVisibility(View.VISIBLE);
            txt_cancel.setVisibility(View.GONE);
            txt_please_wait.setVisibility(View.GONE);
        }
        else
        {
            txt_please_wait.setVisibility(View.VISIBLE);
            checkbox_notify.setVisibility(View.VISIBLE);
            txt_fileuploaded.setVisibility(View.GONE);
            txt_cancel.setVisibility(View.VISIBLE);
        }

        seekbar_uploading.setProgress((int)progress);
        seekbar_uploading.setMax((int)maxvalue);
        seekbar_uploading.setPadding(0, 0, 0, 0);

        txt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(isuploadcomplete)
                {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("uploadfilelink",sharemessage );
                    clipboard.setPrimaryClip(clip);
                    common.sharemessagewithapps(sharemessage);
                }
                else
                {
                    if(checkbox_notify.isChecked())
                        xdata.getinstance().saveSetting(config.datauploaded_success_dialog,"1");
                    else
                        xdata.getinstance().saveSetting(config.datauploaded_success_dialog,"0");

                    xdata.getinstance().saveSetting(config.datauploading_process_dialog,"1");

                    if(mitemclick != null)
                        mitemclick.onItemClicked("1");
                }

                if(videolockuploadingdialog != null && videolockuploadingdialog.isShowing())
                    videolockuploadingdialog.dismiss();
            }
        });

        if(checkbox_notify.isChecked())
            xdata.getinstance().saveSetting(config.datauploaded_success_dialog,"1");

        txt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(xapiupload != null)
                    xapiupload.cancel(true);

                if(mitemclick != null)
                    mitemclick.onItemClicked("1");

                if(videolockuploadingdialog != null && videolockuploadingdialog.isShowing())
                    videolockuploadingdialog.dismiss();
            }
        });
        setscreenwidthheight(videolockuploadingdialog,95,80,
                context.getResources().getString(R.string.popup_videolock));
        videolockuploadingdialog.show();
    }


    public void videolocksharedialog(final Context context,String mediafilepath,String mediatoken,final String type,
                                     boolean ismediatrimmed,String mediathumbnailurl,String trimmedmediapath,String popupcontenttype,boolean ismedialist){

        final Dialog dialog =new Dialog(context,R.style.transparent_dialog_borderless);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.videolockshare_popup);

        ImageView imageview = (ImageView) dialog.findViewById(R.id.back);
        TextView txt_upload = (TextView) dialog.findViewById(R.id.btn_upload);
        TextView txt_content = (TextView) dialog.findViewById(R.id.txt_content);

        String str = getResources().getString(R.string.vl_share_lineone) +"\n"+ getResources().getString(R.string.vl_share_linetwo) +"\n\n"+
                getResources().getString(R.string.vl_share_linefour) +"\n"+ getResources().getString(R.string.vl_share_linefive) +"\n"+
                getResources().getString(R.string.vl_share_linesix) +"\n"+ getResources().getString(R.string.vl_share_lineseven)+ "\n" +
                getResources().getString(R.string.vl_share_lineeight) +"\n"+ getResources().getString(R.string.vl_share_linenine);

        ArrayList<sharepopuptextspanning> textsharepopup = new ArrayList<>();
        textsharepopup.add(new sharepopuptextspanning(1.04f,1,33,str));
        textsharepopup.add(new sharepopuptextspanning(0.99f,35,71,str));
        textsharepopup.add(new sharepopuptextspanning(1.01f,73, 108,str));
        textsharepopup.add(new sharepopuptextspanning(0.98f,110,144,str));
        textsharepopup.add(new sharepopuptextspanning(1.06f,146,176,str));
        textsharepopup.add(new sharepopuptextspanning(1.04f,178,213,str));
        textsharepopup.add(new sharepopuptextspanning(0.95f,215,251,str));
        textsharepopup.add(new sharepopuptextspanning(1.02f,253,str.length(),str));
        common.setspanning(textsharepopup,txt_content);

        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
                if(!ismedialist)
                    showsharedialogfragment(mediafilepath, mediatoken, type, mediathumbnailurl);


                if(dialogfileuploadoptions != null && dialogfileuploadoptions.isShowing())
                    dialogfileuploadoptions.dismiss();
            }
        });

        txt_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //dialog.dismiss();
                showsharepopupsub(trimmedmediapath,type,mediatoken,ismediatrimmed);
            }
        });
        setscreenwidthheight(dialog,95,80,context.getResources().getString(R.string.popup_videolock));
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
        txt_line_six.setTextSize(14.05f);
        txt_line_seven.setTextSize(14.6f);
        txt_line_eight.setTextSize(14.5f);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(btn_next.getText().toString().equalsIgnoreCase("NEXT")){
                    setscreenwidthheight(dialog,85,0,context.getResources().getString(R.string.popup_congrats));
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
                    txt_line_four.setTextSize(14.2f);
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
        setscreenwidthheight(dialog,85,0,context.getResources().getString(R.string.popup_congrats));
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

        setscreenwidthheight(dialog,80,0,getResources().getString(R.string.popup_welcome));
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


    public void setscreenwidthheight(Dialog dialog,int widthpercentage,int heightpercentage,String type) {

        int width = common.getScreenWidth(applicationviavideocomposer.getactivity());
        int height = common.getScreenHeight(applicationviavideocomposer.getactivity());

        int percentagewidth = (width / 100) * widthpercentage;
        int percentageheight = (height / 100) * heightpercentage;

        Log.e("%heightpopup=",""+percentageheight);

        if(type.equalsIgnoreCase(getResources().getString(R.string.popup_upgrade)) ){

            dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            double topmargin = (height / 100) * 6;
            params.y = (int)topmargin;
            dialog.getWindow().setAttributes(params);
            dialog.getWindow().setLayout(percentagewidth, WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_zoom_animation;

        }else if(type.equalsIgnoreCase(getResources().getString(R.string.popup_share)) ){

            dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER);
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            dialog.getWindow().setLayout(percentagewidth,percentageheight);
            dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_zoom_animation;

        }
        else if(type.equalsIgnoreCase(getResources().getString(R.string.popup_welcome)) ||
                type.equalsIgnoreCase(getResources().getString(R.string.popup_congrats))||
                type.equalsIgnoreCase(getResources().getString(R.string.popup_trim)) ){

            dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            double bottommargin = (height / 100) * 25;
            params.y = (int)bottommargin;
            dialog.getWindow().setAttributes(params);
            dialog.getWindow().setLayout(percentagewidth,WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_zoom_animation;

            if(type.equalsIgnoreCase(getResources().getString(R.string.popup_trim)))
                dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_slide_animation;

        }
        else if(type.equalsIgnoreCase(getResources().getString(R.string.popup_upgradecode)) ){

            dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            dialog.getWindow().setLayout(percentagewidth,percentageheight);
            double bottommargin = (height / 100) * 25;
            params.y = (int)bottommargin;
            dialog.getWindow().setAttributes(params);
            dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_zoom_animation;

        }
        else if(type.equalsIgnoreCase(getResources().getString(R.string.popup_publish))){

            dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            //  double bottommargin = (height / 100) * 3;
            dialog.getWindow().setLayout(percentagewidth,percentageheight);
            double bottommargin = (height / 100) * 5;
            params.y = 10 + Integer.parseInt(xdata.getinstance().getSetting(config.TOPBAR_HEIGHT));
            dialog.getWindow().setAttributes(params);
            dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_slide_animation;
        }
        else if(type.equalsIgnoreCase(getResources().getString(R.string.popup_send))||
                type.equalsIgnoreCase(getResources().getString(R.string.popup_videolock))){

            dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
          //  double bottommargin = (height / 100) * 3;
            dialog.getWindow().setLayout(percentagewidth,percentageheight);
            params.y = 10 + Integer.parseInt(xdata.getinstance().getSetting(config.TOPBAR_HEIGHT));
            dialog.getWindow().setAttributes(params);
            dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_slide_animation;
        }

        else if(type.equalsIgnoreCase(getResources().getString(R.string.popup_alert))){

            dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER);
            dialog.getWindow().setLayout(percentagewidth,WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_slide_animation;
        }

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    /**
     * Method demonstrates a sample file being uploaded using the file api
     */
    private void uploadfileonbox(File sourcefile) {

        applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressdialog.showwaitingdialog(applicationviavideocomposer.getactivity());
            }
        });

        new Thread() {
            @Override
            public void run() {
                try {
                    //String uploadFileName = sourcefile.getName();
                    //InputStream uploadStream = getResources().getAssets().open(sourcefile.getAbsolutePath());
                    String destinationFolderId = "0";
                    String uploadName = sourcefile.getName();
                   // BoxRequestsFile.UploadFile request = mFileApi.getUploadRequest(uploadStream, uploadName, destinationFolderId);
                    BoxRequestsFile.UploadFile request = mFileApi.getUploadRequest(sourcefile, destinationFolderId);
                    final BoxFile uploadFileInfo = request.send();
                    showToast("Media uploaded successfully!" + uploadFileInfo.getName());
                } catch (BoxException e) {
                    e.printStackTrace();
                    BoxError error = e.getAsBoxError();
                    if (error != null && error.getStatus() == HttpURLConnection.HTTP_CONFLICT) {
                        ArrayList<BoxEntity> conflicts = error.getContextInfo().getConflicts();
                        if (conflicts != null && conflicts.size() == 1 && conflicts.get(0) instanceof BoxFile) {
                            uploadNewVersion(sourcefile,(BoxFile) conflicts.get(0));
                            return;
                        }
                    }
                    showToast("Media upload failed!");
                } catch (Exception e) {
                    e.printStackTrace();

                } finally {
                    //progressdialog.dismisswaitdialog();
                    applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressdialog.dismisswaitdialog();
                        }
                    });
                }
            }
        }.start();

    }

    /**
     * Method demonstrates a new version of a file being uploaded using the file api
     * @param file
     */
    private void uploadNewVersion(File sourcefile,final BoxFile file) {
        new Thread() {
            @Override
            public void run() {
                try {
                    BoxRequestsFile.UploadNewVersion request = mFileApi.getUploadNewVersionRequest(sourcefile, file.getId());
                    final BoxFile uploadFileVersionInfo = request.send();
                    showToast("Uploaded new version of " + uploadFileVersionInfo.getName());
                } catch (BoxException e) {
                    e.printStackTrace();
                    showToast("Media already exist!");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressdialog.dismisswaitdialog();
                        }
                    });
                }
            }
        }.start();
    }

    private void showToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(applicationviavideocomposer.getactivity(), text, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onRefreshed(BoxAuthentication.BoxAuthenticationInfo info) {
        // do nothing when auth info is refreshed
    }

    @Override
    public void onAuthCreated(BoxAuthentication.BoxAuthenticationInfo info) {
        //Init file, and folder apis; and use them to fetch the root folder
        mFolderApi = new BoxApiFolder(mSession);
        mFileApi = new BoxApiFile(mSession);
        uploadfileonbox(getreadytouploadfile());
        //loadRootFolder();
    }

    @Override
    public void onAuthFailure(BoxAuthentication.BoxAuthenticationInfo info, Exception ex) {
        if (ex != null) {

        } else if (info == null && mOldSession != null) {
            mSession = mOldSession;
            mSession.setSessionAuthListener(this);
            mOldSession = null;
            onAuthCreated(mSession.getAuthInfo());
        }
    }

    @Override
    public void onLoggedOut(BoxAuthentication.BoxAuthenticationInfo info, Exception ex) {
        initboxsession();
    }

    public void showdialogsigninfragment(){

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("signindialog");
        if (prev != null) {
            ft.remove(prev);
        }
        //ft.addToBackStack(null);
        fragmentsignin fragment = new fragmentsignin();
        fragment.show(ft, "signindialog");
    }

    public void showdialogsignupfragment(){

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("signupdialog");
        if (prev != null) {
            ft.remove(prev);
        }
        //ft.addToBackStack(null);
        fragmentsignup fragment = new fragmentsignup();
        fragment.show(ft, "signupdialog");
    }

    public void showdialogcreateaccountfragment(){

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("createaccount");
        if (prev != null) {
            ft.remove(prev);
        }
        //ft.addToBackStack(null);
        fragmentcreateaccount fragment = new fragmentcreateaccount();
        fragment.show(ft, "createaccount");
    }

    public void showdialogverifyuserfragment(String type){

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("verifyuser");
        if (prev != null) {
            ft.remove(prev);
        }
        //ft.addToBackStack(null);
        fragmentverifyuser fragment = new fragmentverifyuser();
        fragment.setdata(type);
        fragment.show(ft, "verifyuser");
    }

    public void showdialogforgotpasswordfragment(){

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("forgotpassword");
        if (prev != null) {
            ft.remove(prev);
        }
        //ft.addToBackStack(null);
        fragmentforgotpassword fragment = new fragmentforgotpassword();
        fragment.show(ft, "forgotpassword");
    }

    public void showdialogchangepasswordfragment(){

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("changepassword");
        if (prev != null) {
            ft.remove(prev);
        }
        //ft.addToBackStack(null);
        fragmentchangepassword fragment = new fragmentchangepassword();
        fragment.show(ft, "changepassword");
    }

    public void showdialogconfirmchannelfragment(){

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("confirmchannel");
        if (prev != null) {
            ft.remove(prev);
        }
        //ft.addToBackStack(null);
        fragmentconfirmchannel fragment = new fragmentconfirmchannel();
        fragment.show(ft, "confirmchannel");
    }

    public void alertdialog(final Context context,String msg){

        final Dialog dialog =new Dialog(context,R.style.transparent_dialog_borderless);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.alert_dialog);

        TextView txt_title = (TextView) dialog.findViewById(R.id.txt_title);
        if(!msg.isEmpty())
            txt_title.setText(msg);

        TextView btn_ok = (TextView) dialog.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        setscreenwidthheight(dialog,70,0,getResources().getString(R.string.popup_alert));
        dialog.show();
    }
}



