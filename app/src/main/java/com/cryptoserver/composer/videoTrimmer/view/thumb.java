/*
 * MIT License
 *
 * Copyright (c) 2016 Knowledge, education for life.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.cryptoserver.composer.videoTrimmer.view;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;


import com.cryptoserver.composer.R;

import java.util.List;
import java.util.Vector;


public class thumb {

    public static final int left = 0;
    public static final int right = 1;

    private int mindex;
    private float mval;
    private float mpos;
    private Bitmap mbitmap;
    private int mwidthbitmap;
    private int mheightbitmap;

    private float mlasttouchx;

    private thumb() {
        mval = 0;
        mpos = 0;
    }

    public int getindex() {
        return mindex;
    }

    private void setindex(int index) {
        mindex = index;
    }

    public float getval() {
        return mval;
    }

    public void setval(float val) {
        mval = val;
    }

    public float getpos() {
        return mpos;
    }

    public void setpos(float pos) {
        mpos = pos;
    }

    public Bitmap getbitmap() {
        return mbitmap;
    }

    private void setbitmap(@NonNull Bitmap bitmap) {
        mbitmap = bitmap;
        mwidthbitmap = bitmap.getWidth();
        mheightbitmap = bitmap.getHeight();
    }

    @NonNull
    public static List<thumb> initthumbs(Resources resources) {

        List<thumb> thumbs = new Vector<>();

        for (int i = 0; i < 2; i++) {
            thumb th = new thumb();
            th.setindex(i);
            if (i == 0) {
                int resimageleft = R.drawable.apptheme_text_select_handle_left;
                th.setbitmap(BitmapFactory.decodeResource(resources, resimageleft));
            } else {
                int resimageright = R.drawable.apptheme_text_select_handle_right;
                th.setbitmap(BitmapFactory.decodeResource(resources, resimageright));
            }

            thumbs.add(th);
        }

        return thumbs;
    }

    public static int getwidthbitmap(@NonNull List<thumb> thumbs) {
        return thumbs.get(0).getwidthbitmap();
    }

    public static int getheightbitmap(@NonNull List<thumb> thumbs) {
        return thumbs.get(0).getheightbitmap();
    }

    public float getlasttouchx() {
        return mlasttouchx;
    }

    public void setlasttouchx(float lasttouchx) {
        mlasttouchx = lasttouchx;
    }

    public int getwidthbitmap() {
        return mwidthbitmap;
    }

    private int getheightbitmap() {
        return mheightbitmap;
    }
}
