package com.deeptruth.app.android.activity;

import android.app.Dialog;
import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;


import com.deeptruth.app.android.R;
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
import com.deeptruth.app.android.interfaces.apiresponselistener;
import com.deeptruth.app.android.interfaces.homepressedlistener;
import com.deeptruth.app.android.netutils.connectivityreceiver;
import com.deeptruth.app.android.netutils.xapi;
import com.deeptruth.app.android.netutils.xapipost;
import com.deeptruth.app.android.netutils.xapipostjson;
import com.deeptruth.app.android.netutils.xapipostfile;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.homewatcher;
import com.deeptruth.app.android.utils.progressdialog;
import com.deeptruth.app.android.utils.taskresult;
import com.deeptruth.app.android.utils.xdata;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
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
                //finish();
            }

            @Override
            public void onHomeLongPressed() {
            }
        });
        mHomeWatcher.startWatch();

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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHomeWatcher != null)
            mHomeWatcher.stopWatch();
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
            finish();
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
            finish();
        } else if (getcurrentfragment() instanceof composeoptionspagerfragment) {
            if (mfragments.size() == 1) {
                mfragments.pop();
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
                finish();
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
        api.execute();
    }


    @Override
    public void xapi_uploadfile(Context mContext, String serverurl, String filepath, apiresponselistener mListener) {
        xapipostfile xapiupload = new xapipostfile(mContext,serverurl,filepath,mListener);
        xapiupload.execute();
    }

    @Override
    public void showsharepopupsub(final String path, final String type, final String videotoken) {
        if (subdialogshare != null && subdialogshare.isShowing())
            subdialogshare.dismiss();

            subdialogshare = new Dialog(applicationviavideocomposer.getactivity());
            subdialogshare.requestWindowFeature(Window.FEATURE_NO_TITLE);
            subdialogshare.setCanceledOnTouchOutside(true);

            subdialogshare.setContentView(R.layout.share_popup);
            //subdialogshare.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            int[] widthHeight = common.getScreenWidthHeight(applicationviavideocomposer.getactivity());
            int width = widthHeight[0];
            double height = widthHeight[1] / 1.6;
            subdialogshare.getWindow().setLayout(width - 20, (int) height);

            final TextView txt_share_btn1 = (TextView) subdialogshare.findViewById(R.id.txt_share_btn1);
            final TextView txt_share_btn2 = (TextView) subdialogshare.findViewById(R.id.txt_share_btn2);
            final TextView txt_share_btn3 = (TextView) subdialogshare.findViewById(R.id.txt_share_btn3);
            final TextView txt_share_btn4 = (TextView) subdialogshare.findViewById(R.id.txt_share_btn4);
            TextView txt_title1 = (TextView) subdialogshare.findViewById(R.id.txt_title1);
            TextView txt_title2 = (TextView) subdialogshare.findViewById(R.id.txt_title2);
            ImageView img_cancel = subdialogshare.findViewById(R.id.img_cancelicon);

            txt_share_btn4.setVisibility(View.GONE);
            if(type.equalsIgnoreCase(config.item_video))
                txt_share_btn4.setVisibility(View.VISIBLE);

            txt_share_btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callshareapi(type, videotoken, path, txt_share_btn1.getText().toString());
                }
            });

            txt_share_btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callshareapi(type, videotoken, path, txt_share_btn2.getText().toString());
                }
            });

            txt_share_btn3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callshareapi(type, videotoken, path, txt_share_btn3.getText().toString());
                }
            });

            txt_share_btn4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //callshareapi(type, videotoken, path, txt_share_btn4.getText().toString());

                    if(!isuserlogin())
                    {
                        redirecttologin();
                        return;
                    }

                    if (subdialogshare != null && subdialogshare.isShowing())
                          subdialogshare.dismiss();

                    Uri selectedimageuri = Uri.fromFile(new File(path));

                    final MediaPlayer mp = MediaPlayer.create(applicationviavideocomposer.getactivity(), selectedimageuri);
                    if (mp != null) {
                        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mediaPlayer) {
                                int duration = mp.getDuration();
                                fragmentrimvideo fragtrimvideo = new fragmentrimvideo();
                                fragtrimvideo.setdata(path, duration,videotoken);
                                addFragment(fragtrimvideo, false, true);
                            }
                        });
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

    public void callshareapi(String type, String videotoken, final String path, String method) {

        HashMap<String, String> requestparams = new HashMap<>();
        requestparams.put("type", type);
        requestparams.put("action", "share");
        requestparams.put("videotoken", videotoken);
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
                    String storageurl="";
                    try {
                        object = new JSONObject(response.getData().toString());
                        if(object.has("success"))
                        {
                            if(object.has("storageurl"))
                                storageurl=object.getString("storageurl");

                            if(! storageurl.trim().isEmpty())
                            {
                                xapi_uploadfile(getinstance(),storageurl,path, new apiresponselistener() {
                                    @Override
                                    public void onResponse(taskresult response) {
                                        if(response.isSuccess())
                                        {
                                            //callvideostoreapi(videotoken,storedkey);

                                        }
                                    }
                                });
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void callvideostoreapi(String videotoken,String storedkey){

        HashMap<String, String> requestparams = new HashMap<>();
        requestparams.put("type", "video");
        requestparams.put("action", "stored");
        requestparams.put("videotoken", videotoken);
        requestparams.put("authtoken", xdata.getinstance().getSetting(config.authtoken));
        requestparams.put("sharemethod", "storedkey");

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
                        if(object.has("success"))
                        {
                            //upLoad2Server();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}



