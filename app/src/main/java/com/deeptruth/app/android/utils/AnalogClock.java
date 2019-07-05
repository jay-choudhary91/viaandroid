package com.deeptruth.app.android.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.interfaces.itemupdatelistener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static android.text.format.DateUtils.SECOND_IN_MILLIS;

public class AnalogClock extends View {

    boolean isdatacomposing=true;
    boolean isdrawercontroller=true;
    String serverdate="";
    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mTimeZone == null && Intent.ACTION_TIMEZONE_CHANGED.equals(intent.getAction())) {
                final String tz = intent.getStringExtra("time-zone");
                mTime = Calendar.getInstance(TimeZone.getTimeZone(tz));
            }
            ontimechanged();
        }
    };

    private final Runnable mClockTick = new Runnable() {
        @Override
        public void run() {
            ontimechanged();

            if (mEnableSeconds) {
                final long now = System.currentTimeMillis();
                final long delay = SECOND_IN_MILLIS - now % SECOND_IN_MILLIS;
                postDelayed(this, delay);
            }
        }
    };

    private Drawable mDial;
    private Drawable mHourHand;
    private Drawable mMinuteHand;
    private Drawable mSecondHand;

    private Calendar mTime;
    private String mDescFormat;
    private TimeZone mTimeZone;
    private boolean mEnableSeconds = true;
    private itemupdatelistener itemupdator;
    public AnalogClock(Context context) {
        this(context, null);
    }

    public AnalogClock(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnalogClock(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final Resources r = context.getResources();
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AnalogClock);
        mTime = Calendar.getInstance();
        mDescFormat = ((SimpleDateFormat) DateFormat.getTimeFormat(context)).toLocalizedPattern();
        mEnableSeconds = a.getBoolean(R.styleable.AnalogClock_showSecondHand, true);
        mDial = a.getDrawable(R.styleable.AnalogClock_dial);
        if (mDial == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mDial = context.getDrawable(R.drawable.clock_face_white);
            } else {
                mDial = r.getDrawable(R.drawable.clock_face_white);
            }
        }
        mHourHand = a.getDrawable(R.styleable.AnalogClock_hour);
        if (mHourHand == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mHourHand = context.getDrawable(R.drawable.hour);
            } else {
                mHourHand = r.getDrawable(R.drawable.hour);
            }
        }
        mMinuteHand = a.getDrawable(R.styleable.AnalogClock_minute);
        if (mMinuteHand == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mMinuteHand = context.getDrawable(R.drawable.minute);
            } else {
                mMinuteHand = r.getDrawable(R.drawable.minute);
            }
        }
        mSecondHand = a.getDrawable(R.styleable.AnalogClock_second);
        if (mSecondHand == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mSecondHand = context.getDrawable(R.drawable.second);
            } else {
                mSecondHand = r.getDrawable(R.drawable.second);
            }
        }
        initdrawable(context, mDial);
        initdrawable(context, mHourHand);
        initdrawable(context, mMinuteHand);
        initdrawable(context, mSecondHand);
        mMinuteHand.setTint(Color.WHITE);
        mHourHand.setTint(Color.WHITE);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        getContext().registerReceiver(mIntentReceiver, filter);
        mTime = Calendar.getInstance(mTimeZone != null ? mTimeZone : TimeZone.getDefault());
        ontimechanged();
        if (mEnableSeconds) {
            mClockTick.run();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        getContext().unregisterReceiver(mIntentReceiver);
        removeCallbacks(mClockTick);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int minWidth = Math.max(mDial.getIntrinsicWidth(), getSuggestedMinimumWidth());
        final int minHeight = Math.max(mDial.getIntrinsicHeight(), getSuggestedMinimumHeight());
        setMeasuredDimension(getDefaultSize(minWidth, widthMeasureSpec),
                getDefaultSize(minHeight, heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int w = getWidth();
        final int h = getHeight();
        final int saveCount = canvas.save();
        canvas.translate(w / 2, h / 2);
        final float scale = Math.min((float) w / mDial.getIntrinsicWidth(),
                (float) h / mDial.getIntrinsicHeight());
        if (scale < 1f) {
            canvas.scale(scale, scale, 0f, 0f);
        }
        mDial.draw(canvas);

        if(isdrawercontroller)
        {
            mDial.setTint(Color.WHITE);
            mHourHand.setTint(Color.WHITE);
            mMinuteHand.setTint(Color.WHITE);
        }
        else
        {
            mDial.setTint(Color.BLACK);
            mHourHand.setTint(Color.BLACK);
            mMinuteHand.setTint(Color.BLACK);
        }

        if(isdatacomposing)
        {
            final float hourAngle = mTime.get(Calendar.HOUR) * 30f;
            canvas.rotate(hourAngle, 0f, 0f);
            mHourHand.draw(canvas);

            final float minuteAngle = mTime.get(Calendar.MINUTE) * 6f;
            canvas.rotate(minuteAngle - hourAngle, 0f, 0f);
            mMinuteHand.draw(canvas);

            if (mEnableSeconds) {
                final float secondAngle = mTime.get(Calendar.SECOND) * 6f;
                canvas.rotate(secondAngle - minuteAngle, 0f, 0f);
                mSecondHand.draw(canvas);
            }

            if(itemupdator != null)
                itemupdator.onitemupdate(mTime,common.gettimezoneshortname());
        }
        else
        {
            if(! serverdate.trim().isEmpty() && (! serverdate.equalsIgnoreCase("NA")) && (! serverdate.equalsIgnoreCase("null")))
            {
                String[] arrayitem=serverdate.split(" ");
                if(arrayitem.length > 0)
                {
                    String dateitem=arrayitem[0];
                    try {

                        //1. Create a Date from String
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                        Date date = sdf.parse(dateitem);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);

                        int hourofday=calendar.get(Calendar.HOUR_OF_DAY);
                        if(hourofday > 12)
                            hourofday=hourofday-12;

                        final float hourAngle = hourofday * 30f;
                        canvas.rotate(hourAngle, 0f, 0f);
                        mHourHand.draw(canvas);

                        final float minuteAngle = calendar.get(Calendar.MINUTE) * 6f;
                        canvas.rotate(minuteAngle - hourAngle, 0f, 0f);
                        mMinuteHand.draw(canvas);

                        if (mEnableSeconds) {
                            final float secondAngle = calendar.get(Calendar.SECOND) * 6f;
                            canvas.rotate(secondAngle - minuteAngle, 0f, 0f);
                            mSecondHand.draw(canvas);
                        }
                        if(itemupdator != null)
                        {
                            if(arrayitem.length > 1)
                                itemupdator.onitemupdate(calendar,arrayitem[1]);
                            else
                                itemupdator.onitemupdate(calendar,"");
                        }

                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }
            }
        }
        canvas.restoreToCount(saveCount);
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return mDial == who
                || mHourHand == who
                || mMinuteHand == who
                || mSecondHand == who
                || super.verifyDrawable(who);
    }

    private void initdrawable(Context context, Drawable drawable) {
        final int midX = drawable.getIntrinsicWidth() / 2;
        final int midY = drawable.getIntrinsicHeight() / 2;
        drawable.setBounds(-midX, -midY, midX, midY);
    }

    private void ontimechanged() {
        mTime.setTimeInMillis(System.currentTimeMillis());
        setContentDescription(DateFormat.format(mDescFormat, mTime));
        invalidate();
    }

    public void settimezone(String id, itemupdatelistener itemupdate) {
        itemupdator=itemupdate;
        mTimeZone = TimeZone.getTimeZone(id);
        mTime.setTimeZone(mTimeZone);
        ontimechanged();
    }

    public void setpostrecorddata(boolean isdatacomposing, String serverdate)
    {
        this.isdatacomposing=isdatacomposing;
        this.serverdate=serverdate;
    }

    public void setfromdrawercontroller(boolean isdrawercontroller)
    {
        this.isdrawercontroller=isdrawercontroller;
        if(mDial != null && mHourHand != null && mMinuteHand != null)
        {
            if(isdrawercontroller)
            {
                mDial.setTint(Color.WHITE);
                mHourHand.setTint(Color.WHITE);
                mMinuteHand.setTint(Color.WHITE);
            }
            else
            {
                mDial.setTint(Color.BLACK);
                mHourHand.setTint(Color.BLACK);
                mMinuteHand.setTint(Color.BLACK);
            }
        }
    }
}
