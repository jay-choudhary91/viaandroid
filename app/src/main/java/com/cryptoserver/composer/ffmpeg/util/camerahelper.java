/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cryptoserver.composer.ffmpeg.util;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;

import com.cryptoserver.composer.BuildConfig;
import com.cryptoserver.composer.utils.config;

import java.io.File;
import java.util.List;

/**
 * Camera related utilities.
 */
public class camerahelper {
    private static final String tag = camerahelper.class.getSimpleName();

    public static final int media_type_image = 1;
    public static final int media_type_video = 2;

    /**
     * Iterate over supported camera preview sizes to see which one best fits the
     * dimensions of the given view while maintaining the aspect ratio. If none can,
     * be lenient with the aspect ratio.
     *
     * @param sizes Supported camera preview sizes.
     * @param w     The width of the view.
     * @param h     The height of the view.
     * @return Best match camera preview size to fit in the view.
     */
    public static Camera.Size getoptimalsize(List<Camera.Size> sizes, int w, int h) {
        // Use a very small tolerance because we want an exact match.
        final double ASPECT_TOLERANCE = 0.1;
        double targetratio = (double) w / h;
        if (sizes == null)
            return null;

        Camera.Size optimalsize = null;

        // Start with max value and refine as we iterate over available preview sizes. This is the
        // minimum difference between view and camera height.
        double mindiff = Double.MAX_VALUE;

        // Target view height
        int targetheight = h;

        // Try to find a preview size that matches aspect ratio and the target view size.
        // Iterate over all available sizes and pick the largest size that can fit in the view and
        // still maintain the aspect ratio.
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetratio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetheight) < mindiff) {
                optimalsize = size;
                mindiff = Math.abs(size.height - targetheight);
            }
        }

        // Cannot find preview size that matches the aspect ratio, ignore the requirement
        if (optimalsize == null) {
            mindiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetheight) < mindiff) {
                    optimalsize = size;
                    mindiff = Math.abs(size.height - targetheight);
                }
            }
        }
        return optimalsize;
    }

    /**
     * Creates a media file in the {@code Environment.DIRECTORY_PICTURES} directory. The directory
     * is persistent and available to other applications like gallery.
     *
     * @param type Media type. Can be video or image.
     * @return A file object pointing to the newly created file.
     */
    public static File getoutputmediafile(String fileName, int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        if (!Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return null;
        }

        File mediastoragedir=new File(config.videodir);

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediastoragedir.exists()) {
            if (!mediastoragedir.mkdirs()) {
                Log.d(tag, "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        File mediaFile;
        if (type == media_type_image) {
            mediaFile = new File(mediastoragedir.getPath() + File.separator +
                    fileName + ".jpg");
        } else if (type == media_type_video) {
            mediaFile = new File(mediastoragedir.getPath() + File.separator +
                    fileName + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    public static int getcameradisplayorientation(Activity activity, int cameraid) {
        Camera.CameraInfo info =
                new Camera.CameraInfo();
        Camera.getCameraInfo(cameraid, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

    public static int getorientationhint(Activity activity, int cameraid) {
        Camera.CameraInfo info =
                new Camera.CameraInfo();
        Camera.getCameraInfo(cameraid, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result = 0;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation - degrees + 360) % 360;
        } else {  // back-facing camera
            result = (info.orientation + degrees) % 360;
        }
        return result;
    }
}
