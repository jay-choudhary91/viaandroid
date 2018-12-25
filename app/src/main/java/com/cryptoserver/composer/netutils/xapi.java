package com.cryptoserver.composer.netutils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.cryptoserver.composer.interfaces.apiresponselistener;
import com.cryptoserver.composer.utils.taskresult;
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
    apiresponselistener listner;


    public xapi(Context context, String action, apiresponselistener responseListner) {
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

    public static String gethttpresponse(String URL) {
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
                return taskresult.NO_INTERNET;
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
        taskresult result = new taskresult();
        JSONObject jsonObject = null;
       // Log.d("Response>> ", aVoid);
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

