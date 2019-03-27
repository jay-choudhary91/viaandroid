package com.deeptruth.app.android.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.interfaces.adapteritemclick;

import java.io.File;


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
        developerdialog.setCancelable(true);
        developerdialog.setContentView(R.layout.egg_dialog);

        final EditText edtInputData1 = (EditText) developerdialog.findViewById(R.id.edt_inputdata_1);
        TextView txtTitle = (TextView) developerdialog.findViewById(R.id.txt_title);

        TextView TxtPositiveButton = (TextView) developerdialog.findViewById(R.id.tv_positive);
        TxtPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edtInputData1.getText().toString().trim().length() > 0)
                {
                    if(edtInputData1.getText().toString().trim().equalsIgnoreCase("0000"))
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

    public static void showrootdirectoryfolders(final Context activity, final adapteritemclick mitemclick){

        final Dialog dialog =new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.root_dir_folder_dialog);

        final EditText edtInputData1 = (EditText) dialog.findViewById(R.id.edt_inputdata_1);

        TextView TxtPositiveButton = (TextView) dialog.findViewById(R.id.tv_positive);
        TxtPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edtInputData1.getText().toString().trim().length() > 0)
                {
                    if(new File(config.dirmedia +File.separator+edtInputData1.getText().toString().trim()).exists())
                    {
                        Toast.makeText(activity,"Folder already exist!",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        if(mitemclick != null)
                            mitemclick.onItemClicked(edtInputData1.getText().toString());

                        if(dialog != null && dialog.isShowing())
                            dialog.dismiss();
                    }

                }
            }
        });

        dialog.show();

    }

    public static void showcreatedirectorydialog(final Context activity, final adapteritemclick mitemclick){

        final Dialog dialog =new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.create_dir_dialog);

        final EditText edtInputData1 = (EditText) dialog.findViewById(R.id.edt_inputdata_1);
        TextView TxtPositiveButton = (TextView) dialog.findViewById(R.id.tv_positive);
        TxtPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edtInputData1.getText().toString().trim().length() > 0)
                {
                    if(new File(config.dirmedia +File.separator+edtInputData1.getText().toString().trim()).exists())
                    {
                        Toast.makeText(activity,"Folder already exist!",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        if(mitemclick != null)
                            mitemclick.onItemClicked(edtInputData1.getText().toString());

                        if(dialog != null && dialog.isShowing())
                            dialog.dismiss();
                    }

                }else{
                    dialog.dismiss();
                }
            }
        });
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                edtInputData1.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN , 0, 0, 0));
                edtInputData1.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP , 0, 0, 0));

            }
        }, 200);


        dialog.show();

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
