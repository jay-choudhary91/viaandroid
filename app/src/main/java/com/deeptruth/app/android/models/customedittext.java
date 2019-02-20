package com.deeptruth.app.android.models;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by devesh on 20/2/19.
 */

public class customedittext  extends android.support.v7.widget.AppCompatEditText {

    OnKeyboardHidden mOnKeyboardHidden;

    public customedittext(Context context)
    {
        super(context);
        init();
    }

    public customedittext(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public customedittext(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    private void init()
    { }

    public interface OnKeyboardHidden {
        public void onKeyboardHidden();
    }

    public void setOnKeyboardHidden(OnKeyboardHidden action) {
        mOnKeyboardHidden = action;
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // User has pressed Back key. So hide the keyboard
            InputMethodManager imm = (InputMethodManager) getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(this.getWindowToken(), 0);
            mOnKeyboardHidden.onKeyboardHidden();
        }
        return true;
    }
}



