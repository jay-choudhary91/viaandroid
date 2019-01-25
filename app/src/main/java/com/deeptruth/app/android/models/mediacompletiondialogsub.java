package com.deeptruth.app.android.models;

import android.annotation.SuppressLint;
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


public class mediacompletiondialogsub extends DialogFragment {

    View rootView;
    adapteritemclick madapterclickpopup;
    String saveto_camera,share_partialmedia,cancel_viewlist,how_would_you,blank;


    @SuppressLint("ValidFragment")
    public mediacompletiondialogsub() {

    }
    @SuppressLint("ValidFragment")
    public mediacompletiondialogsub(adapteritemclick popupclicksub, String saveto_camera, String share_partialvideo, String cancel_viewlist, String how_wpuld_you, String blank) {
        this.madapterclickpopup=popupclicksub;
        this.saveto_camera=saveto_camera;
        this.share_partialmedia=share_partialvideo;
        this.cancel_viewlist=cancel_viewlist;
        this.how_would_you=how_wpuld_you;
        this.blank=blank;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      rootView = inflater.inflate(R.layout.popup_sharescreen, container,
                false);


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

        TextView txt_share_btn1 = (TextView)rootView.findViewById(R.id.txt_share_btn1);
        TextView txt_share_btn2 = (TextView)rootView.findViewById(R.id.txt_share_btn2);
        TextView txt_share_btn3 = (TextView)rootView.findViewById(R.id.txt_share_btn3);
        TextView txt_title1 = (TextView)rootView.findViewById(R.id.txt_title1);
        TextView txt_title2 = (TextView)rootView.findViewById(R.id.txt_title2);
        ImageView img_cancel= rootView.findViewById(R.id.img_cancelicon);


        if(share_partialmedia.endsWith("Photo") || share_partialmedia.endsWith("Audio")){
            txt_share_btn2.setVisibility(View.GONE);
        }
        txt_share_btn1.setText(saveto_camera);
        txt_share_btn2.setText(share_partialmedia);
        txt_share_btn3.setText(cancel_viewlist);

        txt_title1.setText(how_would_you);
        txt_title2.setText(blank);

        common.changeFocusStyle(txt_share_btn1,getResources().getColor(R.color.share_a),5);
        common.changeFocusStyle(txt_share_btn2,getResources().getColor(R.color.share_b),5);
        common.changeFocusStyle(txt_share_btn3,getResources().getColor(R.color.share_c),5);

        txt_share_btn1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(getDialog() != null && getDialog().isShowing())
                    getDialog().dismiss();
                madapterclickpopup.onItemClicked(4);

            }
        });

        txt_share_btn2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(getDialog() != null && getDialog().isShowing())
                    getDialog().dismiss();
                madapterclickpopup.onItemClicked(5);
            }
        });

        txt_share_btn3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(getDialog() != null && getDialog().isShowing())
                    getDialog().dismiss();
                madapterclickpopup.onItemClicked(6);
            }
        });

        img_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getDialog() != null && getDialog().isShowing())
                    getDialog().dismiss();
            }
        });

        return rootView;
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
        int[] widthHeight=common.getScreenWidthHeight(applicationviavideocomposer.getactivity());
        int width=widthHeight[0];
        double height=widthHeight[1]/1.6;
        getDialog().getWindow().setLayout(width-20, (int)height);
    }
}
