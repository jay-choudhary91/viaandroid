package com.deeptruth.app.android.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.deeptruth.app.android.R;


public class
customfonttextview extends android.support.v7.widget.AppCompatTextView {

    public static final String SANS_BOLD = "fonts/Comfortaa-Bold.ttf";
    // SANS_REGULAR : "fonts/OpenSans-Regular.ttf"
    // SANS_SEMI_BOLD : "fonts/OpenSans-Semibold.ttf"
    // SANS_BOLD : "fonts/OpenSans-Bold.ttf"
    public static final String SANS_REGULAR = "fonts/Comfortaa-Regular.ttf";
    public static final String SANS_SEMI_BOLD = "fonts/Comfortaa-Bold.ttf";
    public static final String SANS_BOLD_COMFORTAA = "fonts/Comfortaa-Bold.ttf";
    public static final String SANS_LIGHT_COMFORTAA = "fonts/Comfortaa-Light.ttf";
    public static final String SANS_REGULAR_COMFORTAA = "fonts/Comfortaa-Regular.ttf";
    public static final String bahnschrift_regular = "fonts/Comfortaa-Regular.ttf";
    public static final String bahnschrift_light = "fonts/BAHNSCHRIFT-11.TTF";
    public static final String arial_narrow_bold = "fonts/Arial-Narrow-Bold.ttf";


    public customfonttextview(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.customfonttextview, 0, 0);
        int value =ta.getInteger(R.styleable.customfonttextview_fontType,0);
        switch (value){
            case 0:
                this.setTypeface(Typeface.createFromAsset(context.getAssets(),SANS_BOLD));

                break;
            case 1:
                this.setTypeface(Typeface.createFromAsset(context.getAssets(),SANS_REGULAR));

                break;
            case 2:
                this.setTypeface(Typeface.createFromAsset(context.getAssets(),SANS_SEMI_BOLD));

                break;
            case 3:
                this.setTypeface(Typeface.createFromAsset(context.getAssets(),SANS_BOLD_COMFORTAA));

                break;
            case 4:
                this.setTypeface(Typeface.createFromAsset(context.getAssets(),SANS_LIGHT_COMFORTAA));

                break;
            case 5:
                this.setTypeface(Typeface.createFromAsset(context.getAssets(),SANS_REGULAR_COMFORTAA));

                break;
            case 6:
                this.setTypeface(Typeface.createFromAsset(context.getAssets(),bahnschrift_regular));

                break;
            case 7:
                this.setTypeface(Typeface.createFromAsset(context.getAssets(),bahnschrift_light));

                break;
            case 8:
                this.setTypeface(Typeface.createFromAsset(context.getAssets(),arial_narrow_bold));

                break;

        }
        ta.recycle();
    }

}
