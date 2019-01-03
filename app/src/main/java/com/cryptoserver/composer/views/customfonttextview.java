package com.cryptoserver.composer.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.cryptoserver.composer.R;


public class customfonttextview extends android.support.v7.widget.AppCompatTextView {

    public static final String SANS_BOLD = "fonts/OpenSans-Bold.ttf";
    public static final String SANS_BOLD_ITALIC = "fonts/OpenSans-BoldItalic.ttf";
    public static final String SANS_EXTRA_BOLD= "fonts/OpenSans-ExtraBold.ttf";
    public static final String SANS_EXTRA_BOLD_ITALIC= "fonts/OpenSans-ExtraBoldItalic.ttf";
    public static final String SANS_ITALIC = "fonts/OpenSans-Italic.ttf";
    public static final String SANS_LIGHT = "fonts/OpenSans-Light.ttf";
    public static final String SANS_LIGHT_ITALIC = "fonts/OpenSans-LightItalic.ttf";
    public static final String SANS_REGULAR = "fonts/OpenSans-Regular.ttf";
    public static final String SANS_SEMI_BOLD = "fonts/OpenSans-Semibold.ttf";
    public static final String SANS_SEMI_BOLD_ITALIC = "fonts/OpenSans-SemiboldItalic.ttf";
    public static final String SANS_BOLD_COMFORTAA = "fonts/Comfortaa-Bold.ttf";
    public static final String SANS_LIGHT_COMFORTAA = "fonts/Comfortaa-Light.ttf";
    public static final String SANS_REGULAR_COMFORTAA = "fonts/Comfortaa-Regular.ttf";


    public customfonttextview(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.customfonttextview, 0, 0);
        int value =ta.getInteger(R.styleable.customfonttextview_fontType,0);
        switch (value){
            case 0:
                this.setTypeface(Typeface.createFromAsset(context.getAssets(),SANS_BOLD));

                break;
            case 1:
                this.setTypeface(Typeface.createFromAsset(context.getAssets(),SANS_BOLD_ITALIC));

                break;
            case 2:
                this.setTypeface(Typeface.createFromAsset(context.getAssets(),SANS_EXTRA_BOLD));

                break;
            case 3:
                this.setTypeface(Typeface.createFromAsset(context.getAssets(),SANS_EXTRA_BOLD_ITALIC));

                break;
            case 4:
                this.setTypeface(Typeface.createFromAsset(context.getAssets(),SANS_ITALIC));

                break;
            case 5:
                this.setTypeface(Typeface.createFromAsset(context.getAssets(),SANS_LIGHT));

                break;
            case 6:
                this.setTypeface(Typeface.createFromAsset(context.getAssets(),SANS_LIGHT_ITALIC));

                break;
            case 7:
                this.setTypeface(Typeface.createFromAsset(context.getAssets(),SANS_REGULAR));

                break;
            case 8:
                this.setTypeface(Typeface.createFromAsset(context.getAssets(),SANS_SEMI_BOLD));

                break;
            case 9:
                this.setTypeface(Typeface.createFromAsset(context.getAssets(),SANS_SEMI_BOLD_ITALIC));

                break;
            case 10:
                this.setTypeface(Typeface.createFromAsset(context.getAssets(),SANS_BOLD_COMFORTAA));

                break;
            case 11:
                this.setTypeface(Typeface.createFromAsset(context.getAssets(),SANS_LIGHT_COMFORTAA));

                break;
            case 12:
                this.setTypeface(Typeface.createFromAsset(context.getAssets(),SANS_REGULAR_COMFORTAA));

                break;

        }
        ta.recycle();
    }

}
