package com.cryptoserver.composer.models;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.cryptoserver.composer.R;
import com.cryptoserver.composer.applicationviavideocomposer;
import com.cryptoserver.composer.fragments.composervideoplayerfragment;
import com.cryptoserver.composer.interfaces.adapteritemclick;
import com.cryptoserver.composer.utils.common;
import com.cryptoserver.composer.utils.xdata;

public class customsharepopupmain extends DialogFragment {

    TextView txt_share_btn1;
    TextView txt_share_btn2;
    TextView txt_share_btn3;
    TextView txt_title1;
    ImageView img_cancel;
    TextView txt_title2;
    adapteritemclick madapterclickpopup;
    View rootView;

    @SuppressLint("ValidFragment")
    public  customsharepopupmain(adapteritemclick popupclick) {
        this.madapterclickpopup=popupclick;
    }

    @SuppressLint("ValidFragment")
    public customsharepopupmain() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.popup_sharescreen, container,
                false);


        txt_share_btn1 = rootView.findViewById(R.id.txt_share_btn1);
        txt_share_btn2 = rootView.findViewById(R.id.txt_share_btn2);
        txt_share_btn3 = rootView.findViewById(R.id.txt_share_btn3);
        txt_title1 = rootView.findViewById(R.id.txt_title1);
        txt_title2 = rootView.findViewById(R.id.txt_title2);
        img_cancel=rootView.findViewById(R.id.img_cancelicon);

        if(getDialog() != null && getDialog().isShowing())
            getDialog().dismiss();
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(true);
        getDialog().setCanceledOnTouchOutside(true);

        getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

            }
        });

        txt_share_btn1.setText(getResources().getString(R.string.share));
        txt_share_btn2.setText(getResources().getString(R.string.new_video));
        txt_share_btn3.setText(getResources().getString(R.string.watch));

        txt_title1.setText(getResources().getString(R.string.video_has_been_encrypted));
        txt_title2.setText(getResources().getString(R.string.congratulations_video));

        common.changeFocusStyle(txt_share_btn1,getResources().getColor(R.color.share_a),20);
        common.changeFocusStyle(txt_share_btn2,getResources().getColor(R.color.share_b),20);
        common.changeFocusStyle(txt_share_btn3,getResources().getColor(R.color.share_c),20);

        txt_share_btn1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(getDialog() != null && getDialog().isShowing())
                    getDialog().dismiss();
                madapterclickpopup.onItemClicked(1);
            }
        });

        txt_share_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(getDialog() != null &&getDialog().isShowing())
                    getDialog().dismiss();
                madapterclickpopup.onItemClicked(2);
            }
        });

        txt_share_btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getDialog() != null && getDialog().isShowing())
                    getDialog().dismiss();
                madapterclickpopup.onItemClicked(3);
            }
        });
        img_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getDialog() != null && getDialog().isShowing())
                    getDialog().dismiss();

            }
        });
        // Do something else
        return rootView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);


        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();
        int[] widthHeight=common.getScreenWidthHeight(applicationviavideocomposer.getactivity());
        int width=widthHeight[0];
        double height=widthHeight[1]/1.6;
        getDialog().getWindow().setLayout(width-20, (int)height);
    }

    @Override
    public void onStart() {
        super.onStart();
        int[] widthHeight= common.getScreenWidthHeight(applicationviavideocomposer.getactivity());
        int width=widthHeight[0];
        double height=widthHeight[1]/1.6;
        getDialog().getWindow().setLayout(width-20, (int)height);
    }

}
