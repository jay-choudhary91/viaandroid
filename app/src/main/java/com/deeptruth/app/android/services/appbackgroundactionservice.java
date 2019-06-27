package com.deeptruth.app.android.services;

import android.app.Dialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.view.WindowManager;
import android.widget.Toast;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.activity.homeactivity;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.xdata;

/**
 * Created by ${matraex} on 26/6/19.
 */

public class appbackgroundactionservice extends Service
{

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        try
        {
            if(xdata.getinstance().getSetting(config.sidecar_syncstatus).trim().equalsIgnoreCase("1"))
            {
                DialogInterface.OnClickListener listener
                        = new   DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do some stuff eg: context.onCreate(super)
                        dialog.dismiss();
                    }
                };

                /** create builder for dialog */
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getApplicationContext(),
                        R.style.myDialog))
                        .setCancelable(false)
                        .setTitle("Alert")
                        .setPositiveButton("OK", listener);
                final String[] items= {"Keep running sync & notify me when completed","Keep running sync & close after completed",
                        "Do not allow sync to run in background"};
                builder.setSingleChoiceItems(items, 1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        /*Toast.makeText(getApplicationContext(),items[item],Toast.LENGTH_SHORT).show();
                        dialog.dismiss();*/
                    }
                });
                /** create dialog & set builder on it */
                Dialog dialog = builder.create();
                /** this required special permission but u can use aplication context */
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
                else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

                /** show dialog */
                dialog.show();
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}