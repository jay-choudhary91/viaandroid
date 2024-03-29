package com.deeptruth.app.android.models;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.interfaces.adapteritemclick;
import com.deeptruth.app.android.utils.common;

public class mediacompletiondialogmain extends DialogFragment {

    TextView txt_share_btn1;
    TextView txt_share_btn2;
    TextView txt_share_btn3;
    TextView txt_title1;
    ImageView img_cancel;
    TextView txt_title2;
    adapteritemclick madapterclickpopup;
    View rootView;
    String share,newmedia,playmedia,media_has_been_encrypted,congratulations_media;


    @SuppressLint("ValidFragment")
    public mediacompletiondialogmain() {

    }
    @SuppressLint("ValidFragment")
    public mediacompletiondialogmain(adapteritemclick popupclickmain, String share, String newmedia, String playmedia, String media_has_been_encrypted, String congratulations_media) {
        this.madapterclickpopup=popupclickmain;
        this.share=share;
        this.newmedia=newmedia;
        this.playmedia=playmedia;
        this.media_has_been_encrypted=media_has_been_encrypted;
        this.congratulations_media=congratulations_media;
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

        txt_share_btn1.setText(share);
        txt_share_btn2.setText(newmedia);
        txt_share_btn3.setText(playmedia);

        txt_title1.setText(media_has_been_encrypted);
        txt_title2.setText(congratulations_media);

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
