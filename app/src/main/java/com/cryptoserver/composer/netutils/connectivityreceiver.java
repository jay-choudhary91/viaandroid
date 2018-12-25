package com.cryptoserver.composer.netutils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.cryptoserver.composer.applicationviavideocomposer;

public class connectivityreceiver extends BroadcastReceiver
{
    public static ConnectivityReceiverListener connectivityreceiverlistener;

    public connectivityreceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent arg1) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activenetwork = cm.getActiveNetworkInfo();
        boolean isconnected = activenetwork != null
                && activenetwork.isConnectedOrConnecting();

        if (connectivityreceiverlistener != null) {
            connectivityreceiverlistener.onnetworkconnectionchanged(isconnected);
        }
    }

    public static boolean isconnected() {
        ConnectivityManager cm = (ConnectivityManager) applicationviavideocomposer.getactivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activenetwork = cm.getActiveNetworkInfo();
        return activenetwork != null
                && activenetwork.isConnectedOrConnecting();
    }


    public interface ConnectivityReceiverListener {
        void onnetworkconnectionchanged(boolean isconnected);
    }
}
