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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.LongSparseArray;
import android.view.View;
import com.cryptoserver.composer.R;
import com.cryptoserver.composer.videoTrimmer.utils.backgroundexecutor;
import com.cryptoserver.composer.videoTrimmer.utils.uithreadexecutor;


public class timelineview extends View {

    private Uri mvideouri;
    private int mheightview;
    int x = 0;
    private LongSparseArray<Bitmap> mbitmaplist = null;

    public timelineview(@NonNull Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public timelineview(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mheightview = getContext().getResources().getDimensionPixelOffset(R.dimen.frames_video_height);
    }


    @Override
    protected void onMeasure(int widthmeasurespec, int heightmeasurespec) {
        final int minw = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        int w = resolveSizeAndState(minw, widthmeasurespec, 1);

        final int minh = getPaddingBottom() + getPaddingTop() + mheightview;
        int h = resolveSizeAndState(minh, heightmeasurespec, 1);

        setMeasuredDimension(w, h);
    }

    @Override
    protected void onSizeChanged(final int w, int h, final int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        if (w != oldW) {
            x = w;
            getbitmap(w);
        }
    }

    private void getbitmap(final int viewwidth) {
        backgroundexecutor.execute(new backgroundexecutor.task("", 0L, "") {
                                       @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                                       @Override
                                       public void execute() {

                                           if(mvideouri !=null){
                                               try {
                                                   LongSparseArray<Bitmap> thumbnailList = new LongSparseArray<>();

                                                   MediaMetadataRetriever mediametadataretriever = new MediaMetadataRetriever();
                                                   mediametadataretriever.setDataSource(getContext(), mvideouri);

                                                   // Retrieve media data
                                                   long videoLengthInMs = Integer.parseInt(mediametadataretriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)) * 1000;

                                                   // Set thumbnail properties (Thumbs are squares)
                                                   final int thumbWidth = mheightview;
                                                   final int thumbHeight = mheightview;

                                                   int numThumbs = (int) Math.ceil(((float) viewwidth) / thumbWidth);

                                                   final long interval = videoLengthInMs / numThumbs;

                                                   for (int i = 0; i < numThumbs; ++i) {
                                                       Bitmap bitmap = mediametadataretriever.getFrameAtTime(i * interval, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                                                       // TODO: bitmap might be null here, hence throwing NullPointerException. You were right
                                                       try {
                                                           bitmap = Bitmap.createScaledBitmap(bitmap, thumbWidth, thumbHeight, false);
                                                       } catch (Exception e) {
                                                           e.printStackTrace();
                                                       }
                                                       thumbnailList.put(i, bitmap);
                                                   }

                                                   mediametadataretriever.release();
                                                   returnbitmaps(thumbnailList);
                                               } catch (final Throwable e) {
                                                   Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                                               }
                                           }

                                       }
                                   }
        );
    }

    private void returnbitmaps(final LongSparseArray<Bitmap> thumbnailList) {
        uithreadexecutor.runtask("", new Runnable() {
                    @Override
                    public void run() {
                        mbitmaplist = thumbnailList;
                        invalidate();
                    }
                }
                , 0L);
    }


    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        if (mbitmaplist != null) {
            canvas.save();
            int x = 0;

            for (int i = 0; i < mbitmaplist.size(); i++) {
                Bitmap bitmap = mbitmaplist.get(i);

                if (bitmap != null) {
                    canvas.drawBitmap(bitmap, x, 5, null);
                    x = x + bitmap.getWidth();
                }
            }
        }
    }

    public void setvideo(@NonNull Uri data) {
        mvideouri = data;
    }
}
