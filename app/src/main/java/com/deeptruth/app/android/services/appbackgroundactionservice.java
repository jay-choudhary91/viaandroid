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
                final String[] selected_sync_setting = {config.selectedsyncsetting_0};
                DialogInterface.OnClickListener listener
                        = new   DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do some stuff eg: context.onCreate(super)
                        xdata.getinstance().saveSetting(config.selectedsyncsetting,selected_sync_setting[0]);
                        dialog.dismiss();
                    }
                };

                int selectedindex=0;
                if(xdata.getinstance().getSetting(config.selectedsyncsetting).equalsIgnoreCase(config.selectedsyncsetting_0))
                    selectedindex=0;
                else if(xdata.getinstance().getSetting(config.selectedsyncsetting).equalsIgnoreCase(config.selectedsyncsetting_1))
                    selectedindex=1;
                else if(xdata.getinstance().getSetting(config.selectedsyncsetting).equalsIgnoreCase(config.selectedsyncsetting_2))
                    selectedindex=2;

                /** create builder for dialog */
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getApplicationContext(),
                        R.style.myDialog))
                        .setCancelable(false)
                        .setTitle(getApplicationContext().getResources().getString(R.string.app_name))
                        .setPositiveButton("OK", listener);
                final String[] items = {config.selectedsyncsetting_0,config.selectedsyncsetting_1,config.selectedsyncsetting_2};
                builder.setSingleChoiceItems(items, selectedindex, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        selected_sync_setting[0] =items[item];
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