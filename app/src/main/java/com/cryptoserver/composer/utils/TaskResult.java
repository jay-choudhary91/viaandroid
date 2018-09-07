package com.cryptoserver.composer.utils;

/**
 * Created by Ejaz on 2/3/2017.
 */

public class TaskResult {
    public String message;
    public Object data;

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
