package com.cryptoserver.composer.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.TelephonyManager;

import com.cryptoserver.composer.applicationviavideocomposer;
import com.cryptoserver.composer.utils.config;
import com.cryptoserver.composer.utils.xdata;

import java.util.Date;

/**
 * Created by root on 21/5/18.
 */

public class callservice extends Service {

    private static final String ACTION="android.intent.action.PHONE_STATE";
    private static final String ACTION_OUT_CALL="android.intent.action.NEW_OUTGOING_CALL";
    private BroadcastReceiver yourReceiver;

    private int lastState = TelephonyManager.CALL_STATE_IDLE;
    private Date callStartTime;
    private boolean isIncoming;
    private String savedNumber;
    private long start_time, end_time;
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        final IntentFilter theFilter = new IntentFilter();
        final IntentFilter theFilter2 = new IntentFilter();
        theFilter.addAction(ACTION);
        theFilter2.addAction(ACTION_OUT_CALL);
        this.yourReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                String stateStr = "";
                String number = "";
                int state = 0;

                if (intent.getAction().equals(ACTION))
                {
                    stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
                    number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);

                }
                else if (intent.getAction().equals(ACTION_OUT_CALL))
                {
                    stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
                    number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                    if(stateStr == null)
                        stateStr="";
                }

                if(stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                    state = TelephonyManager.CALL_STATE_IDLE;
                }
                else if(stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                    state = TelephonyManager.CALL_STATE_OFFHOOK;
                }
                else if(stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                    state = TelephonyManager.CALL_STATE_RINGING;
                }


                onCallStateChanged(context,intent, state, number);
            }
        };
        // Registers the receiver so that your service will listen for
        // broadcasts
        this.registerReceiver(this.yourReceiver, theFilter);
        this.registerReceiver(this.yourReceiver, theFilter2);
        return START_STICKY;
    }

    public void onCallStateChanged(Context context, Intent intent, int state, String number) {

        String status="",duration="";
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;

                if (intent.getAction().equals(ACTION_OUT_CALL))
                    callStartTime = new Date();

                status="Yes";
                duration="";
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                callStartTime = new Date();
                status="Yes";
                duration="";

                break;
            case TelephonyManager.CALL_STATE_IDLE:
                //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                end_time = System.currentTimeMillis();
                status="No";
                number="";

                callStartTime=null;

                xdata.getinstance().saveSetting("CALL_STATUS","");
                xdata.getinstance().saveSetting("CALL_DURATION","");
                xdata.getinstance().saveSetting("CALL_REMOTE_NUMBER","");

                break;
        }

        try {

            start_time=0;

            if(callStartTime != null)
                start_time=callStartTime.getTime();

            Intent Broadcastintent = new Intent(config.broadcast_call);
            Broadcastintent.putExtra("CALL_STATUS",status);
            Broadcastintent.putExtra("CALL_DURATION",""+duration);
            Broadcastintent.putExtra("CALL_REMOTE_NUMBER",""+number);
            Broadcastintent.putExtra("CALL_START_TIME",""+start_time);
            applicationviavideocomposer.getactivity().sendBroadcast(Broadcastintent);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Do not forget to unregister the receiver!!!
        if(yourReceiver != null)
            this.unregisterReceiver(this.yourReceiver);
    }
}