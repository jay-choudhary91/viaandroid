package com.cryptoserver.composer.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.cryptoserver.composer.R;



/**
 * Created by Devesh on 5/30/2017.
 */

public class appdialog
{
    static android.app.AlertDialog dialog;
    static Dialog developerdialog;

    public static AlertDialog showDialog(Context ctx, String tag, String msg,
                                         String btn1, String btn2,
                                         DialogInterface.OnClickListener listener1,
                                         DialogInterface.OnClickListener listener2) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx, R.style.AppCompatAlertDialogStyle);
        if (tag.equalsIgnoreCase("err")) {
            builder.setTitle("Error!");
            builder.setIcon(null);
        } else if (tag.equalsIgnoreCase("info")) {
            builder.setTitle("Info!");
            builder.setIcon(null);
        } else {
            builder.setTitle(tag);
            builder.setIcon(null);
        }
        builder.setMessage(msg).setCancelable(false);
        if (btn1 != null)
            builder.setPositiveButton(btn1, listener1);
        else
            builder.setPositiveButton("OK", listener1);

        if (btn2 != null && listener2 != null)
            builder.setNegativeButton(btn2, listener2);

        builder.setCancelable(false);
        AlertDialog alert = builder.create();
        if(Build.VERSION.SDK_INT >= 19) {
            alert.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
        }
        else
        {
            alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }

        // alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
      //  alert.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST|WindowManager.LayoutParams.TYPE_APPLICATION_PANEL);
        return alert;
    }

    public static AlertDialog showDialog(Context ctx, String tag, String msg,
                                         String btn1, String btn2, DialogInterface.OnClickListener listener) {
        return showDialog(ctx, tag, msg, btn1, btn2, listener,
                new DialogInterface.OnClickListener() {
                    // @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
    }

    public static AlertDialog showConfirmationDialog(Context ctx, String tag, String msg,
                                                     String btn1, String btn2, DialogInterface.OnClickListener listener1, DialogInterface.OnClickListener listener2) {
        return showDialog(ctx, tag, msg, btn1, btn2, listener1,listener2);
    }

    public static AlertDialog showDialog(Context ctx, String tag, String msg,
                                         String btn1, DialogInterface.OnClickListener listener) {
        return showDialog(ctx, tag, msg, btn1, null, listener, null);
    }

    public static AlertDialog showDialog(Context ctx, String tag, String msg,
                                         DialogInterface.OnClickListener listener) {
        return showDialog(ctx, tag, msg, null, null, listener, null);
    }

    public static AlertDialog showDialog(Context ctx, String tag, String msg) {
        return showDialog(ctx, tag, msg, new DialogInterface.OnClickListener() {
            // @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

    }


    public static void showeggfeaturedialog(Context activity){

        developerdialog =new Dialog(activity);
        developerdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        developerdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        developerdialog.setCanceledOnTouchOutside(false);
        developerdialog.setCancelable(false);
        developerdialog.setContentView(R.layout.egg_dialog);

        final EditText edtInputData1 = (EditText) developerdialog.findViewById(R.id.edt_inputdata_1);
        TextView txtTitle = (TextView) developerdialog.findViewById(R.id.txt_title);

        TextView TxtPositiveButton = (TextView) developerdialog.findViewById(R.id.tv_positive);
        TxtPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edtInputData1.getText().toString().trim().length() > 0)
                {
                    if(edtInputData1.getText().toString().trim().equalsIgnoreCase("8813"))
                    {
                        xdata.getinstance().saveSetting(xdata.developermode,"1");
                        if(developerdialog != null && developerdialog.isShowing())
                            developerdialog.dismiss();
                    }
                }
            }
        });

        developerdialog.show();

    }

    public static boolean isdialogshowing()
    {
        if(developerdialog != null)
        {
            if(developerdialog.isShowing())
                return true;
        }
        return false;
    }
}
