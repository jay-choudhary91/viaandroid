package com.deeptruth.app.android.activity;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by ${matraex} on 5/4/19.
 */

public class registrationbaseactivity extends AppCompatActivity
{
    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        finish();
    }
}
