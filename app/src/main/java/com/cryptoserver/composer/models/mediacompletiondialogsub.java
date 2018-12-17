package com.cryptoserver.composer.models;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
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
import com.cryptoserver.composer.fragments.videoplayfragment;
import com.cryptoserver.composer.interfaces.adapteritemclick;
import com.cryptoserver.composer.utils.common;


public class mediacompletiondialogsub extends DialogFragment {

    View rootView;
    adapteritemclick madapterclickpopup;


    @SuppressLint("ValidFragment")
    public mediacompletiondialogsub(adapteritemclick popupclick) {
        this.madapterclickpopup=popupclick;
    }

    @SuppressLint("ValidFragment")
    public mediacompletiondialogsub() {

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

        txt_share_btn1.setText(getResources().getString(R.string.shave_to_camera));
        txt_share_btn2.setText(getResources().getString(R.string.share_partial_video));
        txt_share_btn3.setText(getResources().getString(R.string.cancel_viewlist));

        txt_title1.setText(getResources().getString(R.string.how_would_you));
        txt_title2.setText("");

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
