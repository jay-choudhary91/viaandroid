package com.deeptruth.app.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.fragments.audiocomposerfragment;
import com.deeptruth.app.android.fragments.basefragment;
import com.deeptruth.app.android.fragments.fragmentsignin;
import com.deeptruth.app.android.fragments.imagecomposerfragment;
import com.deeptruth.app.android.fragments.registrationbasefragment;
import com.deeptruth.app.android.fragments.videocomposerfragment;
import com.deeptruth.app.android.interfaces.apiresponselistener;
import com.deeptruth.app.android.interfaces.homepressedlistener;
import com.deeptruth.app.android.netutils.xapipost;
import com.deeptruth.app.android.utils.homewatcher;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

/**
 * Created by ${matraex} on 5/4/19.
 */

public abstract class registrationbaseactivity extends AppCompatActivity implements registrationbasefragment.registrationfragmentnavigationhelper
{
    homewatcher mHomeWatcher;
    private registrationbasefragment mcurrentfragment;
    private Stack<Fragment> mfragments = new Stack<Fragment>();



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applicationviavideocomposer.setActivity(registrationbaseactivity.this);
       /* getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR,WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR);
*/
       // LayoutInflater inflater = getLayoutInflater();
       // View contentview = inflater.inflate(getlayoutid(), null);
       // setContentView(contentview);

        LayoutInflater inflater = getLayoutInflater();
        View contentview = inflater.inflate(getlayoutid(), null);
        setContentView(contentview);

       // initviews(savedInstanceState);

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
    protected void onDestroy() {
        super.onDestroy();
        if(mHomeWatcher != null)
            mHomeWatcher.stopWatch();
    }

    @Override
    public void onUserLeaveHint() {
            super.onUserLeaveHint();
            hidekeyboard();
    }

    @Override
    public void hidekeyboard() {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null && (ev.getAction() == MotionEvent.ACTION_UP  &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit."))) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
                hidekeyboard();
        }
        return super.dispatchTouchEvent(ev);
    }
    @Override
    public void addFragment(registrationbasefragment f, boolean clearBackStack, boolean addToBackstack) {
        addFragment(f, R.id.fragment_registration_container, clearBackStack, addToBackstack);

    }

    public void addFragment(registrationbasefragment f, int layoutId, boolean clearBackStack, boolean addToBackstack) {
        if (clearBackStack) {
            clearfragmentbackstack();
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // transaction.setCustomAnimations(R.anim.card_flip_right_in, R.anim.card_flip_right_out, R.anim.card_flip_left_in, R.anim.card_flip_left_out);
        transaction.add(layoutId, f);
        if (addToBackstack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();

        mcurrentfragment = f;
        mfragments.push(f);

        onfragmentbackstackchanged();
    }

    @Override
    public void replaceFragment(registrationbasefragment f, boolean clearBackStack, boolean addToBackstack) {
        replaceFragment(f, R.id.fragment_registration_container, clearBackStack, addToBackstack);
    }

    public void replaceFragment(registrationbasefragment f, int layoutId, boolean clearBackStack, boolean addToBackstack) {
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

    public int minnumberoffragments = 1;

    public int getMinNumberOfFragments() {
        return minnumberoffragments;
    }

    public registrationbasefragment getcurrentfragment() {
        return mcurrentfragment;
    }



    @Override
    public void onBack() {

        /*if (getSupportFragmentManager().getBackStackEntryCount() <= getMinNumberOfFragments()) {
            finish();
            return;
        }*/

        /*if (getcurrentfragment() instanceof fragmentsignin) {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
            overridePendingTransition(R.anim.from_right_in,R.anim.from_left_out);
            return;
        }else{
            backtolastfragment();
        }*/
    }

    public void backtolastfragment() {
        getSupportFragmentManager().popBackStack();
        if (mfragments.size() > 0) {
            mfragments.pop();
            mcurrentfragment = (registrationbasefragment) (mfragments.isEmpty() ? null : ((mfragments.peek()
                    instanceof registrationbasefragment) ? mfragments.peek() : null));
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
            // mcurrentfragment = (registrationbasefragment) homefragment;
            mfragments.clear();
            mfragments.push(homefragment);
        }
    }

    public void onfragmentbackstackchanged() {
        if (mcurrentfragment != null) {
            // mcurrentfragment.updateheader();
        }
    }

    @Override
    public void xapipost_send(Context mContext, HashMap<String, String> mPairList, apiresponselistener mListener) {
        xapipost api = new xapipost(mContext,mListener);
        Set keys = mPairList.keySet();
        Iterator itr = keys.iterator();
        while (itr.hasNext()) {
            String key = (String)itr.next();
            String argvalue = (String)mPairList.get(key);
            api.add(key,argvalue);
        }
        api.execute();
    }

    public abstract int getlayoutid();

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
}
