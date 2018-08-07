package com.cryptoserver.composer.utils;


import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.cryptoserver.composer.R;

/**
 * Created by root on 23/4/18.
 */

public class common
{

     static AlertDialog alertdialog = null;

    public static void changefocusstyle(View view, int fullbordercolor, int fullbackcolor, float borderradius)
    {
        view.setBackgroundResource(R.drawable.style_rounded_view);
        GradientDrawable drawable = (GradientDrawable)view.getBackground();
        drawable.setStroke(2, fullbordercolor);
        drawable.setCornerRadius(borderradius);
        drawable.setColor(fullbackcolor);
    }

    public static void hidekeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
