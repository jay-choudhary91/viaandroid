package com.cryptoserver.composer.interfaces;


import com.cryptoserver.composer.utils.TaskResult;

public interface ApiResponseListener {

    public void onResponse(TaskResult response);
}
