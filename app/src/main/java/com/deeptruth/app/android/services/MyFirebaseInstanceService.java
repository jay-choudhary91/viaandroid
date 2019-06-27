package com.deeptruth.app.android.services;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Created by ${matraex} on 27/6/19.
 */

public class MyFirebaseInstanceService extends FirebaseInstanceIdService {

    private static final String TAG="MyFirebaseInstanceServi";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken =           FirebaseInstanceId.getInstance().getToken();
        FirebaseMessaging.getInstance().subscribeToTopic("all");
        Log.d(TAG, "Refreshed token: " + refreshedToken);

/* If you want to send messages to this application instance or manage this apps subscriptions on the server side, send the Instance ID token to your app server.*/

        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String refreshedToken) {
        Log.d("TOKEN ", refreshedToken.toString());
    }
}
