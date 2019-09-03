package com.deeptruth.app.android.utils;

import android.app.Dialog;
import android.content.Context;

import com.kaopiz.kprogresshud.KProgressHUD;

/**
 * Created by Android team on 4/7/17.
 */

public class progressdialog
{
    public static Dialog dialog;
    public static KProgressHUD mprogress;

    public static void showwaitingdialog(Context context) {

        try
        {
            if (mprogress != null && mprogress.isShowing()) {
                mprogress.dismiss();
            }

            mprogress = KProgressHUD.create(context)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setCancellable(false)
                    .setAnimationSpeed(1)
                    .setDimAmount(0.5f)
                    .show();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //
    public static void showwaitingdialog(Context context, String message) {
        /*dialog = new SpotsDialog(context, message, R.style.Custom_dialog);
        dialog.setCancelable(false);
        dialog.show();*/

        if (mprogress != null && mprogress.isShowing()) {
            mprogress.dismiss();
        }

        mprogress = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(1)
                .setDimAmount(0.5f)
                .show();
    }

    public static void dismisswaitdialog() {
        try
        {
            if (mprogress != null && mprogress.isShowing()) {
                mprogress.dismiss();
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
