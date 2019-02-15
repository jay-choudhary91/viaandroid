
package com.deeptruth.app.android.sensor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.deeptruth.app.android.R;

public class AttitudeIndicator extends View {

  private static final boolean LOG_FPS = false;

  private static final int EARTH_COLOR = Color.parseColor("#FDD012");  // Yellow
  private static final int MIN_PLANE_COLOR = Color.parseColor("#0EAE3E"); // Green
  private static final float TOTAL_VISIBLE_PITCH_DEGREES = 45 * 2; // � 45�

  private final PorterDuffXfermode mXfermode;
  private final Paint mBitmapPaint;
  private final Paint mEarthPaint;
  private final Paint mPitchLadderPaint;
  private final Paint mMinPlanePaint;
  private final Paint mBottomPitchLadderPaint;

  // These are created once and reused in subsequent onDraw calls.
  private Bitmap mSrcBitmap;
  private Canvas mSrcCanvas;
  private Bitmap mDstBitmap;
  private int mWidth;
  private int mHeight;

  private float mPitch = 0; // Degrees
  private float mRoll = 0; // Degrees, left roll is positive

  public AttitudeIndicator(Context context) {
    this(context, null);
  }

  public AttitudeIndicator(Context context, AttributeSet attrs) {
    super(context, attrs);

    mXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
    mBitmapPaint = new Paint();
    mBitmapPaint.setFilterBitmap(false);

    mEarthPaint = new Paint();
    mEarthPaint.setAntiAlias(true);
    mEarthPaint.setColor(EARTH_COLOR);

    mPitchLadderPaint = new Paint();
    mPitchLadderPaint.setAntiAlias(true);
    mPitchLadderPaint.setColor(Color.WHITE);
    mPitchLadderPaint.setStrokeWidth(3);

    mBottomPitchLadderPaint = new Paint();
    mBottomPitchLadderPaint.setAntiAlias(true);
    mBottomPitchLadderPaint.setColor(Color.BLUE);
    mBottomPitchLadderPaint.setStrokeWidth(3);
    mBottomPitchLadderPaint.setAlpha(128);

    mMinPlanePaint = new Paint();
    mMinPlanePaint.setAntiAlias(true);
    mMinPlanePaint.setColor(MIN_PLANE_COLOR);
    mMinPlanePaint.setStrokeWidth(5);
    mMinPlanePaint.setStyle(Paint.Style.STROKE);

  }

  public float getPitch() {
    return mPitch;
  }

  public float getRoll() {
    return mRoll;
  }

  public void setAttitude(float pitch, float roll) {
    mPitch = pitch;
    mRoll = roll;
    invalidate();
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    mWidth = w;
    mHeight = h;
  }

  private Bitmap getSrc() {
    if (mSrcBitmap == null) {
      mSrcBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
      mSrcCanvas = new Canvas(mSrcBitmap);
    }
    Canvas canvas = mSrcCanvas;

    float centerX = mWidth / 2;
    float centerY = mHeight / 2;

    // Background
    //canvas.drawColor(Color.RED);
    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

    // Save the state without any rotation/translation so
    // we can revert back to it to draw the fixed components.
    canvas.save();

    // Orient the earth to reflect the pitch and roll angles
    canvas.rotate(mRoll, centerX, centerY);
    //Log.e("Pitch roll dy ",""+(mPitch / TOTAL_VISIBLE_PITCH_DEGREES) * mHeight);
    float translatevalue=(mPitch / TOTAL_VISIBLE_PITCH_DEGREES) * mHeight;
    /*if(translatevalue >= 60)
    {
       canvas.translate(0, 60);
    }
    else if(translatevalue <= -60)
    {
      canvas.translate(0, -60);
    }
    else
    {
      canvas.translate(0, translatevalue);
    }*/
    //canvas.translate(0, translatevalue);
    canvas.translate(0, 15);

    //x=136, y= 150

    {
      Paint myPaint = new Paint();
      myPaint.setColor(Color.GRAY);
      myPaint.setStrokeWidth(5);
      //canvas.drawRect(100, 70, 170, 200, myPaint);
      float left=centerX-40;
      float top=centerY-70;
      float right=centerX+40;
      float bottom=centerY+60;
      canvas.drawRect(left, top, right, bottom, myPaint);
    }

    {
      Paint myPaint = new Paint();
      myPaint.setColor(Color.WHITE);
      myPaint.setStrokeWidth(5);
      //canvas.drawRect(110, 90, 160, 180, myPaint);
      float left=centerX-30;
      float top=centerY-60;
      float right=centerX+30;
      float bottom=centerY+50;
      canvas.drawRect(left, top, right, bottom, myPaint);
    }

    // Return to normal to draw the miniature plane
    canvas.restore();

    // Draw the nose dot
    canvas.drawPoint(centerX, centerY, mMinPlanePaint);
    return mSrcBitmap;
  }

  private Bitmap getDst() {
    if (mDstBitmap == null) {
      mDstBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
      Canvas c = new Canvas(mDstBitmap);
      c.drawColor(Color.TRANSPARENT);

      Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
      p.setColor(Color.RED);
      c.drawOval(new RectF(0, 0, mWidth, mHeight), p);
    }
    return mDstBitmap;
  }

  @Override
  protected void onDraw(Canvas canvas) {
    if (LOG_FPS) {
      countFps();
    }

    Bitmap src = getSrc();
    Bitmap dst = getDst();

    int sc = saveLayer(canvas);
    canvas.drawBitmap(dst, 0, 0, mBitmapPaint);
    mBitmapPaint.setXfermode(mXfermode);
    canvas.drawBitmap(src, 0, 0, mBitmapPaint);
    mBitmapPaint.setXfermode(null);

    canvas.restoreToCount(sc);
  }

  private int saveLayer(Canvas canvas) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      return canvas.saveLayer(0, 0, mWidth, mHeight, null);
    } else {
      //noinspection deprecation
      return canvas.saveLayer(0, 0, mWidth, mHeight, null, Canvas.ALL_SAVE_FLAG);
    }
  }

  private long frameCountStartedAt = 0;
  private long frameCount = 0;

  private void countFps() {
    frameCount++;
    if (frameCountStartedAt == 0) {
      frameCountStartedAt = System.currentTimeMillis();
    }
    long elapsed = System.currentTimeMillis() - frameCountStartedAt;
    if (elapsed >= 1000) {
      LogUtil.i("FPS: " + frameCount);
      frameCount = 0;
      frameCountStartedAt = System.currentTimeMillis();
    }
  }
}
