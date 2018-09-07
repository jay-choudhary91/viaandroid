package com.cryptoserver.composer.netutils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.cryptoserver.composer.interfaces.ApiResponseListener;
import com.cryptoserver.composer.utils.TaskResult;
import com.cryptoserver.composer.utils.common;
import com.cryptoserver.composer.utils.logs;
import com.cryptoserver.composer.utils.xdata;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class xapi extends AsyncTask<Void, Void, String> {

    private Map<String, String> params = new HashMap<String, String>();
    String action;
    Context mcontext;
    String useurl = "";
    ApiResponseListener listner;


    public xapi(Context context, String action, ApiResponseListener responseListner) {
        this.action = action;
        this.mcontext = context;
        this.listner = responseListner;
        this.useurl = xdata.getinstance().getSetting(xdata.keybaseurl);
    }

    public boolean add(String key, String value) {
        try {
            value = URLEncoder.encode(value, "UTF-8");
            params.put(key, value);
        } catch (Exception e) {
            ;
        }
        return true;
    }

    public boolean setData(Map<String, String> params) {
        this.params=params;
        return true;
    }

    public static String getHttpClient(String URL) {
        String response = "";
        String baseUrl = URL;

        BufferedReader reader = null;
        try {
            Log.d("URL>>", baseUrl);
            URL url = new URL(baseUrl);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);
            response = buffer.toString();

            //buffer);
        } catch (Exception MalformedURLException) {
            System.out.print("Invalid URL:" + baseUrl);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    try {
                        throw new xexception("IO Excepton - " + e.getMessage());
                    } catch (xexception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
        return response;
    }

    public boolean reset() {
        params = new HashMap<String, String>();
        return true;
    }

    @Override
    protected String doInBackground(Void... voids) {
        String devicekey = "",response="";
        try
        {
            if (!common.isnetworkconnected(mcontext)) {
                return TaskResult.NO_INTERNET;
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        BufferedReader reader = null;

        try {
            String baseUrl = useurl + "action=" + action;
            for (String param : params.keySet()) {
                baseUrl += "&" + param + "=" + params.get(param);
            }

            Log.e("URL>>", baseUrl);
            logs.show("Params" + params.toString());
            URL url = new URL(baseUrl);
            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(7000);
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);
            response = buffer.toString();
            HttpURLConnection httpConnection = (HttpURLConnection) conn;
            int code = httpConnection.getResponseCode();
        } catch (Exception exception) {
            System.out.print("Invalid URL:" + useurl);
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
        return response;
    }

    @Override
    protected void onPostExecute(String aVoid) {
        super.onPostExecute(aVoid);
        TaskResult result = new TaskResult();
        JSONObject jsonObject = null;
        Log.d("Response>> ", aVoid);
        try {
            jsonObject = new JSONObject(aVoid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonObject != null && jsonObject.has("error")) {

            if (jsonObject != null && jsonObject.has("result")) {

                try
                {
                    String errormsg = jsonObject.getString("error");
                    result.success(false);
                    result.setMessage(errormsg);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else if(jsonObject.has("error"))
            {
                result.success(false);
                try {
                    result.setMessage((jsonObject.has("errormessage"))?jsonObject.getString("errormessage"):jsonObject.getString("error"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            if (listner != null) {
                listner.onResponse(result);
            }
        } else if (jsonObject != null && jsonObject.has("result")) {
            JSONObject object = jsonObject.optJSONObject("result");

            if (object != null) {
                if (object.has("savesetting")) {
                    try {
                        JSONObject saveSetting = object.getJSONObject("savesetting");
                        Iterator<String> myIter = saveSetting.keys();
                        while (myIter.hasNext()) {
                            String key = myIter.next();
                            String value = saveSetting.optString(key);
                            xdata.getinstance().saveSetting(key, value);
                        }
                    } catch (JSONException ex2) {
                        ex2.printStackTrace();
                    }

                }
            } else {

                sendServerNotResponding();
            }
        }
    }

    private void sendServerNotResponding() {
        TaskResult result = new TaskResult();
        result.success(false);
        result.code = TaskResult.CODE_SERVER_DOWN;
        result.setData(this.useurl);
        if(listner != null)
            listner.onResponse(result);
    }
}

