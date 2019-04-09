package com.deeptruth.app.android.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.view.WindowManager;

import com.deeptruth.app.android.interfaces.homepressedlistener;
import com.deeptruth.app.android.utils.homewatcher;

/**
 * Created by ${matraex} on 5/4/19.
 */

public class registrationbaseactivity extends AppCompatActivity
{
    homewatcher mHomeWatcher;
    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        hidekeyboard();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR,WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR);

        mHomeWatcher = new homewatcher(this);
        mHomeWatcher.setOnHomePressedListener(new homepressedlistener() {
            @Override
            public void onHomePressed() {
                // do something here...
                finish();
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

    public void hidekeyboard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }
}
