package com.cryptoserver.composer.activity;

import android.app.Activity;
import android.os.Bundle;

import com.cryptoserver.composer.R;
import com.cryptoserver.composer.fragments.Camera2VideoFragment;

/**
 * Created by devesh on 29/8/18.
 */

public class CameraActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, Camera2VideoFragment.newInstance())
                    .commit();
        }
    }
}
