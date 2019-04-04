package com.deeptruth.app.android.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.views.customfontedittext;

import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class verifiedemail extends Activity implements View.OnClickListener {

    @BindView(R.id.edt_codebox1)
    customfontedittext edtcodebox1;
    @BindView(R.id.edt_codebox2)
    customfontedittext edtcodebox2;
    @BindView(R.id.edt_codebox3)
    customfontedittext edtcodebox3;
    @BindView(R.id.edt_codebox4)
    customfontedittext edtcodebox4;
    @BindView(R.id.edt_codebox5)
    customfontedittext edtcodebox5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifiedemail);
        ButterKnife.bind(verifiedemail.this);

        edtcodebox1.addTextChangedListener(new mytextwatcher());
        edtcodebox2.addTextChangedListener(new mytextwatcher());
        edtcodebox3.addTextChangedListener(new mytextwatcher());
        edtcodebox4.addTextChangedListener(new mytextwatcher());
        edtcodebox5.addTextChangedListener(new mytextwatcher());

        /*edtcodebox1.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if(edtcodebox1.getText().length()==1)
                {
                    edtcodebox1.clearFocus();
                    edtcodebox2.requestFocus();
                    edtcodebox2.setCursorVisible(true);
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){
            }

            public void afterTextChanged(Editable s) {
            }
        });
        edtcodebox2.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if(edtcodebox2.getText().length()==1)
                {
                    edtcodebox2.clearFocus();
                    edtcodebox3.requestFocus();
                    edtcodebox3.setCursorVisible(true);
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){
            }

            public void afterTextChanged(Editable s) {
            }
        });
        edtcodebox3.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if(edtcodebox3.getText().length()==1)
                {
                    edtcodebox3.clearFocus();
                    edtcodebox4.requestFocus();
                    edtcodebox4.setCursorVisible(true);
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){
            }

            public void afterTextChanged(Editable s) {
            }
        });
        edtcodebox4.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if(edtcodebox4.getText().length()==1)
                {
                    edtcodebox4.clearFocus();
                    edtcodebox5.requestFocus();
                    edtcodebox5.setCursorVisible(true);
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){
            }

            public void afterTextChanged(Editable s) {
            }
        });
        edtcodebox5.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if(edtcodebox5.getText().length()==1)
                {
                    edtcodebox5.clearFocus();
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){
            }

            public void afterTextChanged(Editable s) {
            }
        });*/
    }

    @Override
    public void onClick(View v) {

    }

    class mytextwatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (edtcodebox1.getText().hashCode() == s.hashCode())
            {
                edtcodebox2.requestFocus();
            }
            else if (edtcodebox2.getText().hashCode() == s.hashCode())
            {
                edtcodebox3.requestFocus();
            }
            else if (edtcodebox3.getText().hashCode() == s.hashCode())
            {
                edtcodebox4.requestFocus();
            }
            else if (edtcodebox4.getText().hashCode() == s.hashCode())
            {
                edtcodebox5.requestFocus();
            }
            else if (edtcodebox5.getText().hashCode() == s.hashCode())
            {

            }
        }

        @Override
        public void afterTextChanged(Editable s)
        {

        }
    }

    public void setedittextfocus(final customfontedittext edtcodebox, final customfontedittext edtcodeboxnext){

        edtcodebox.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if(edtcodebox.getText().length()==1)
                {
                    edtcodebox.clearFocus();
                    edtcodeboxnext.requestFocus();
                    edtcodeboxnext.setCursorVisible(true);
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){
            }

            public void afterTextChanged(Editable s) {
            }
        });
    }
}
