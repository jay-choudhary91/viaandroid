package com.deeptruth.app.android.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.adapter.adaptersenddialog;
import com.deeptruth.app.android.activity.baseactivity;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.interfaces.adapteritemclick;

import java.io.File;
import java.util.ArrayList;


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
                        xdata.getinstance().saveSetting(xdata.developer_mode,"1");

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

    /*public static void share_alert_dialog(final Context context, final String title, String content){
        final Dialog dialog =new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.share_alert_popup);

        TextView txttitle = (TextView)dialog.findViewById(R.id.txt_title);
        TextView txtcontent = (TextView)dialog.findViewById(R.id.txt_content);
        txtcontent.setTypeface(applicationviavideocomposer.comfortaaregular, Typeface.BOLD);

        txttitle.setTypeface(applicationviavideocomposer.bahnschriftregular, Typeface.BOLD);
        final CheckBox notifycheckbox = (CheckBox) dialog.findViewById(R.id.notifycheckbox);
        notifycheckbox.setText("Do not notify again");
        notifycheckbox.setTypeface(applicationviavideocomposer.comfortaaregular, Typeface.BOLD);

        txttitle.setText(title);
        txtcontent.setText(content);

        TextView ok = (TextView) dialog.findViewById(R.id.btn_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(title.equalsIgnoreCase(context.getResources().getString(R.string.txt_publish))){
                    if(notifycheckbox.isChecked())
                        xdata.getinstance().saveSetting(config.enableplubishnotification,"1");
                    else
                        xdata.getinstance().saveSetting(config.enableplubishnotification,"0");
                }
                if(title.equalsIgnoreCase(context.getResources().getString(R.string.txt_send))){
                    if(notifycheckbox.isChecked())
                        xdata.getinstance().saveSetting(config.enablesendnotification,"1");
                    else
                        xdata.getinstance().saveSetting(config.enablesendnotification,"0");
                }
                if(title.equalsIgnoreCase(context.getResources().getString(R.string.txt_export))){
                    if(notifycheckbox.isChecked())
                        xdata.getinstance().saveSetting(config.enableexportnotification,"1");
                    else
                        xdata.getinstance().saveSetting(config.enableexportnotification,"0");
                }
                ArrayList<Integer> array_image = new ArrayList<Integer>();
                array_image.add(R.drawable.dropbox);
                array_image.add(R.drawable.dropbox);
                array_image.add(R.drawable.googledrive);
                array_image.add(R.drawable.onedrive);
                array_image.add(R.drawable.videolock);
                ArrayList<String> array_name = new ArrayList<String>();
                array_name.add("Box");
                array_name.add("Dropbox");
                array_name.add("Google Drive");
                array_name.add("Microsoft OneDrive");
                array_name.add("VideoLock Share");

                senditemsdialog(context,array_image,array_name);


                dialog.dismiss();

            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public static void senditemsdialog(Context context, ArrayList<Integer> arrayImage, ArrayList<String> arrayName){
        Dialog send_item_dialog = new Dialog(context, android.R.style.Theme_Dialog);
        send_item_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        send_item_dialog.setContentView(R.layout.send_alert_dialog);
        send_item_dialog.setCanceledOnTouchOutside(true);
        send_item_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        RecyclerView recyclerView = (RecyclerView) send_item_dialog.findViewById(R.id.ryclr_send_items);
        adaptersenddialog adaptersend = new adaptersenddialog(context, arrayImage, arrayName);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context,2);
        ((GridLayoutManager)mLayoutManager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 4) {
                    return 2;
                }else return 1;
            }
        });
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adaptersend);        send_item_dialog.show();
    }*/
}
