package com.deeptruth.app.android.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Size;
import android.view.Surface;

/**
 * Created by devesh on 31/8/18.
 */

public class camerautil {

    @TargetApi(21)
    public static Size chooseVideoSize(Size[] choices) {
        for (Size size : choices) {
            if (size.getWidth() == size.getHeight() * 4 / 3 && size.getWidth() <= 720) {
                return size;
            }
        }
        return choices[choices.length - 1];
    }

   /* public static boolean shouldShowRequestPermissionRationale(Fragment context, String[] permissions) {
        for (String permission : permissions) {
            if (FragmentCompat.shouldShowRequestPermissionRationale(context, permission)) {
                return true;
            }
        }
        return false;
    }
*/
    public static boolean hasPermissionsGranted(Activity context, String[]permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static int getOrientation(int rotation, boolean upsideDown) {
        if (upsideDown) {
            switch (rotation) {
                case Surface.ROTATION_0: return 270;
                case Surface.ROTATION_90: return 180;
                case Surface.ROTATION_180: return 90;
                case Surface.ROTATION_270: return 0;
            }
        } else {
            switch (rotation) {
                case Surface.ROTATION_0: return 90;
                case Surface.ROTATION_90: return 0;
                case Surface.ROTATION_180: return 270;
                case Surface.ROTATION_270: return 180;
            }
        }


        return 0;
    }
}
