package com.deeptruth.app.android.netutils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.deeptruth.app.android.interfaces.apiresponselistener;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.taskresult;
import com.deeptruth.app.android.utils.xdata;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;


public class xapipostjson extends AsyncTask<Void, Void, String> {

    JSONObject valuepairobject = new JSONObject();
    String action;
    Context mContext;
    String useurl = "";
    apiresponselistener listner;
    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;
    String JsonResponse = null;
    String baseUrl = "";
    Date starttime,endtime;

    public xapipostjson(Context context, String action, apiresponselistener responseListner) {
        this.action = action;
        this.mContext = context;
        this.listner = responseListner;
        this.useurl = common.setting_get(xdata.xapi_url);
        starttime = Calendar.getInstance().getTime();

    }

    public boolean add(String key, Object value) {
        try {
            valuepairobject.accumulate(key,value);
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

        baseUrl = useurl+"xapi_body=1&"+"action="+action;
        Log.d("URL>>", baseUrl);
        Log.d("REQUEST >>", valuepairobject.toString());

        try {

            URL url = new URL(baseUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
            writer.write(valuepairobject.toString());
            writer.close();
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String inputLine;
            while ((inputLine = reader.readLine()) != null)
                buffer.append(inputLine + "\n");
            if (buffer.length() == 0) {
                return null;
            }
            JsonResponse = buffer.toString();
            Log.e("TAG",JsonResponse);
            return JsonResponse;

        } catch (IOException e) {
            e.printStackTrace();
        }


        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("Error", "Error closing stream", e);
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String aVoid) {
        super.onPostExecute(aVoid);
        endtime = Calendar.getInstance().getTime();
        taskresult result = new taskresult();
        try {
            result.success(false);
            JSONObject jsonObject=new JSONObject(aVoid);
            if (jsonObject != null && jsonObject.has("result"))
            {
                JSONObject object = jsonObject.optJSONObject("result");

                common.setxapirequestresponses(baseUrl+"&"+valuepairobject.toString(),action,"xapi_body=1",
                        null,useurl,object,starttime,endtime, config.all_xapi_list);

                common.setxapirequestresponses(baseUrl+"&"+valuepairobject.toString(),action,"xapi_body=1",
                        null,useurl,object,starttime,endtime, config.sidecar_xapi_actions);

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

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (listner != null)
            listner.onResponse(result);
    }
}

