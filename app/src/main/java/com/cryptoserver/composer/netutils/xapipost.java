package com.cryptoserver.composer.netutils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


import com.cryptoserver.composer.interfaces.apiresponselistener;
import com.cryptoserver.composer.utils.common;
import com.cryptoserver.composer.utils.taskresult;
import com.cryptoserver.composer.utils.xdata;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class xapipost extends AsyncTask<Void, Void, String> {

    List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
    String action;
    Context mContext;
    String useurl = "";
    apiresponselistener listner;


    public xapipost(Context context, String action, apiresponselistener responseListner) {
        this.action = action;
        this.mContext = context;
        this.listner = responseListner;
        this.useurl = xdata.getinstance().getSetting(xdata.keybaseurl);
    }

    public boolean add(String key, String value) {
        try {
            nameValuePairList.add(new BasicNameValuePair(key, value));
        } catch (Exception e) {
            ;
        }
        return true;
    }

    @Override
    protected String doInBackground(Void... voids) {
        if (!common.isnetworkconnected(mContext)) {
            return taskresult.NO_INTERNET;
        }
        String responseString = "";

        String baseUrl = useurl+"action="+action;
        Log.d("URL>>", baseUrl);
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(baseUrl);

        try {
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairList));
            HttpResponse response = httpclient.execute(httppost);

            // According to the JAVA API, InputStream constructor do nothing.
            //So we can't initialize InputStream although it is not an interface
            InputStream inputStream = response.getEntity().getContent();

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder stringBuilder = new StringBuilder();

            String bufferedStrChunk = null;

            while((bufferedStrChunk = bufferedReader.readLine()) != null){
                stringBuilder.append(bufferedStrChunk);
            }

            responseString=stringBuilder.toString();
        } catch (ClientProtocolException e) {

        } catch (IOException e) {

        }
        return responseString;
    }

    @Override
    protected void onPostExecute(String aVoid) {
        super.onPostExecute(aVoid);
        taskresult result = new taskresult();
        JSONObject jsonObject = null;
        Log.d("Response>> ", aVoid);
        try {
            result.success(false);
            jsonObject=new JSONObject(aVoid);
            if (jsonObject != null && jsonObject.has("result"))
            {
                JSONObject object = jsonObject.optJSONObject("result");
                result.success(true);
                result.setData(object);
            }
            if (listner != null)
                listner.onResponse(result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

