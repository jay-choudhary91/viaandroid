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
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class xapipostfile extends AsyncTask<Void, Void, String> {

    String serverurl;
    Context mContext;
    String filepath = "";
    apiresponselistener listner;

    HttpURLConnection conn = null;
    DataOutputStream dos = null;
    DataInputStream inStream = null;
    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";
    int bytesRead, bytesAvailable, bufferSize;
    byte[] buffer;
    int maxBufferSize = 1 * 1024 * 1024;
    String serverresponsemessage = "";
    int serverresponsecode = 0;


    public xapipostfile(Context context, String serverurl, String filepath , apiresponselistener responseListner) {
        this.serverurl = serverurl;
        this.filepath = filepath;
        this.mContext = context;
        this.listner = responseListner;
    }

    @Override
    protected String doInBackground(Void... voids) {

        if (!common.isnetworkconnected(mContext)) {
            return taskresult.NO_INTERNET;
        }

        File sourceFile = new File(filepath);
        if (!sourceFile.isFile()) {
            Log.e("Huzza", "Source File Does not exist");
            return "";
        }
        try { // open a URL connection to the Servlet
            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            URL url = new URL(serverurl);
            conn = (HttpURLConnection) url.openConnection(); // Open a HTTP  connection to  the URL
            conn.setDoInput(true); // Allow Inputs
            conn.setDoOutput(true); // Allow Outputs
            conn.setUseCaches(false); // Don't use a Cached Copy
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("uploaded_file", filepath);
            dos = new DataOutputStream(conn.getOutputStream());

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""+ filepath + "\"" + lineEnd);
            dos.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available(); // create a buffer of  maximum size
            Log.i("Huzza", "Initial .available : " + bytesAvailable);

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // read file and write it into form...
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            // send multipart form data necesssary after file data...
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Responses from the server (code and message)
            serverresponsecode = conn.getResponseCode();
            serverresponsemessage = conn.getResponseMessage();

            Log.i("Upload file to server", "HTTP Response is : " + serverresponsemessage + ": " + serverresponsecode);
            // close streams
            Log.i("Upload file to server", filepath + " File is written");
            fileInputStream.close();
            dos.flush();
            dos.close();
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
        } catch (Exception e) {
            e.printStackTrace();
        }
//this block will give the response of upload link
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn
                    .getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                Log.i("Huzza", "RES Message: " + line);
            }
            rd.close();
        } catch (IOException ioex) {
            Log.e("Huzza", "error: " + ioex.getMessage(), ioex);
        }
        return serverresponsemessage;  // like 200 (Ok)
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

