package com.deeptruth.app.android.netutils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.deeptruth.app.android.interfaces.apiresponselistener;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.taskresult;
import com.deeptruth.app.android.utils.xdata;

import org.apache.http.NameValuePair;
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
import java.util.ArrayList;
import java.util.List;


public class xapipostjson extends AsyncTask<Void, Void, String> {

    List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();

    JSONObject valuepairobject = new JSONObject();
    String jsondata = "";

    String action;
    Context mContext;
    String useurl = "";
    apiresponselistener listner;

    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;
    String JsonResponse = null;


    public xapipostjson(Context context, String action, apiresponselistener responseListner) {
        this.action = action;
        this.mContext = context;
        this.listner = responseListner;
        this.useurl = xdata.getinstance().getSetting(xdata.keybaseurl);
    }

    /*public boolean add(String key, String value) {
        try {
            nameValuePairList.add(new BasicNameValuePair(key, value));
        } catch (Exception e) {
            ;
        }
        return true;
    }*/

    public boolean add(String key, Object value) {
        try {
            valuepairobject.accumulate(key,value);

           // nameValuePairList.add(new BasicNameValuePair(key, value));
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

        String baseUrl = useurl+"action="+action;
        Log.d("URL>>", baseUrl);
        Log.d("REQUEST >>", valuepairobject.toString());

        try {

            URL url = new URL(baseUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            // is output buffer writter
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            //set headers and method
            Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
            writer.write(valuepairobject.toString());
            // json data
            writer.close();
            InputStream inputStream = urlConnection.getInputStream();
            //input stream
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String inputLine;
            while ((inputLine = reader.readLine()) != null)
                buffer.append(inputLine + "\n");
            if (buffer.length() == 0) {
                // Stream was empty. No point in parsing.
                return null;
            }
            JsonResponse = buffer.toString();
            //response data
            Log.e("TAG",JsonResponse);
            //send to post execute
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
        taskresult result = new taskresult();
        JSONObject jsonObject = null;
        //Log.d("Response>> ", aVoid);
        try {
            result.success(false);
            jsonObject=new JSONObject(aVoid);
            if (jsonObject != null && jsonObject.has("result"))
            {
                JSONObject object = jsonObject.optJSONObject("result");
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
