package com.deeptruth.app.android.utils;

/**
 * Created by Ejaz on 2/3/2017.
 */

public class taskresult {
    public String message;
    public Object data;
    public Object completedata;

    public static final String NO_INTERNET = "No internet";
    public static final int CODE_FAILURE = 1;
    public static final int CODE_SUCCESS = 2;
    public static final int CODE_SERVER_DOWN = 3;
    public int code = CODE_FAILURE;

    public boolean isSuccess() {
        if (code == CODE_SUCCESS) {
            return true;
        }
        return false;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public void setcompletedata(Object data) {
        this.completedata = data;
    }

    public Object getcompletedata() {
        return completedata;
    }

    public void success(boolean b) {
        if (b) {
            code = CODE_SUCCESS;
        } else {
            code = CODE_FAILURE;
        }
    }

    public Object getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
