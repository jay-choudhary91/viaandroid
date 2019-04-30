package com.deeptruth.app.android.netutils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.deeptruth.app.android.interfaces.apiresponselistener;
import com.deeptruth.app.android.utils.taskresult;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.logs;
import com.deeptruth.app.android.utils.xdata;

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
    Context mcontext;
    String useurl = "";
    apiresponselistener listner;


    public xapi(Context context, apiresponselistener responseListner) {
        this.mcontext = context;
        this.listner = responseListner;
        this.useurl = common.setting_get(xdata.xapi_url);
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
            String baseUrl = useurl;
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
                if(object.has("settings_set"))
                {
                    JSONObject saveSetting = object.getJSONObject("settings_set");
                    Iterator<String> myIter = saveSetting.keys();
                    while (myIter.hasNext()) {
                        String key = myIter.next();
                        String value = saveSetting.optString(key);
                        common.setting_set(key, value);
                    }
                }
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

