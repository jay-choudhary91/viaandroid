package com.deeptruth.app.android.netutils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.deeptruth.app.android.interfaces.apiresponselistener;
import com.deeptruth.app.android.interfaces.progressupdate;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.taskresult;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Date;


public class xapipostfile extends AsyncTask<Void, String, String> {

    String serverurl;
    Context mContext;
    String filepath = "";
    apiresponselistener listner;
    progressupdate progressupdate;

    String serverresponsemessage = "";
    Date starttime,endtime;
    File sourceFile = null;
    long totalfilelength=0;


    //private final String boundary;
    private static final String LINE_FEED = "\r\n";
    private HttpURLConnection httpConn;
    private String charset;
    private OutputStream outputStream;
    private PrintWriter writer;
    private boolean cancelbackgroundprogress=false;


    public xapipostfile(Context context, String serverurl, String filepath , apiresponselistener responseListner,
                        progressupdate progressupdate) {
        this.serverurl = serverurl;
        this.filepath = filepath;
        this.mContext = context;
        this.listner = responseListner;
        this.progressupdate = progressupdate;
        starttime = Calendar.getInstance().getTime();
        cancelbackgroundprogress=false;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        sourceFile = new File(filepath);
    }

    public void cancelbackgroundprocess(boolean cancelbackgroundprogress)
    {
        this.cancelbackgroundprogress=cancelbackgroundprogress;
    }

    @Override
    public void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        if(progressupdate != null)
            progressupdate.onupdateprogress(values[0]);
    }

    @Override
    protected void onCancelled(String s) {
        super.onCancelled(s);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        this.cancelbackgroundprogress=true;
    }

    @Override
    protected String doInBackground(Void... voids) {

        if (!common.isnetworkconnected(mContext)) {
            return taskresult.NO_INTERNET;
        }

        // reference link -> https://gist.github.com/luankevinferreira/5221ea62e874a9b29d86b13a2637517b
        if (!sourceFile.isFile()) {
            return "";
        }

        totalfilelength=sourceFile.length();

        URLConnection urlconnection = null;
        try {
            File file = new File(filepath);
            URL url = new URL(serverurl);
            urlconnection = url.openConnection();
            urlconnection.setDoOutput(true);
            urlconnection.setDoInput(true);

            if (urlconnection instanceof HttpURLConnection) {
                ((HttpURLConnection) urlconnection).setRequestMethod("PUT");
                ((HttpURLConnection) urlconnection).setRequestProperty("Content-type", "text/plain");
                ((HttpURLConnection) urlconnection).connect();
            }

            BufferedOutputStream bos = new BufferedOutputStream(urlconnection.getOutputStream());
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            int bytesRead=0;
            long progress = 0;
            // read byte by byte until end of stream
            /*while ((i = bis.read()) > 0)
            {
                progress += i;
                Log.e("PreUploadProgress ",""+progress);
                //publishProgress((int)getcurrentprogress(progress,totalfilelength));
                int aaa=(int)getcurrentprogress(progress,totalfilelength);
                Log.e("PostUploadProgress ",""+(int)getcurrentprogress(progress,totalfilelength));
                bos.write(i);
            }*/
            byte buf[] = new byte[1024];
            int flag=0;
            while ((bytesRead = bis.read(buf)) != -1)
            {
                bos.write(buf, 0, bytesRead);
                bos.flush();
                if(isCancelled() || cancelbackgroundprogress)
                {
                    flag=1;
                    Log.e("flagButton ","flagButton");
                    break;
                }
                progress += bytesRead;
                publishProgress(""+(int)getcurrentprogress(progress,totalfilelength));
            }
            bis.close();
            bos.flush();
            bos.close();
            System.out.println(((HttpURLConnection) urlconnection).getResponseMessage());
            if(flag == 1)
                return "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {

            InputStream inputStream;
            int responseCode = ((HttpURLConnection) urlconnection).getResponseCode();
            endtime = Calendar.getInstance().getTime();
            common.setxapirequestresponses(serverurl,"","",null,"","",
                    starttime,endtime, config.all_xapi_list);

            if (responseCode == 200)
            {
                inputStream = ((HttpURLConnection) urlconnection).getInputStream();
                int j;
                while ((j = inputStream.read()) > 0) {
                    System.out.println(j);
                }

                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String inputLine;
                while ((inputLine = reader.readLine()) != null)
                    buffer.append(inputLine + "\n");

                //serverresponsemessage = buffer.toString();
                serverresponsemessage = "200";

            } else {
                inputStream = ((HttpURLConnection) urlconnection).getErrorStream();
            }
            ((HttpURLConnection) urlconnection).disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverresponsemessage;
    }

    private double getcurrentprogress(long transferred,long total) {
        return (transferred * 100) / total;
    }

    @Override
    protected void onPostExecute(String aVoid) {
        super.onPostExecute(aVoid);
        taskresult result = new taskresult();
        JSONObject jsonObject = null;
        //Log.d("Response>> ", aVoid);
        try {
            result.success(false);
            if(aVoid.equalsIgnoreCase("200"))
            {
                result.success(true);
                result.setData("");
            }
            else if(aVoid.trim().length() > 0)
            {
                jsonObject=new JSONObject(aVoid);
                if (jsonObject != null && jsonObject.has("result"))
                {
                    JSONObject object = jsonObject.optJSONObject("result");
                    result.success(true);
                    result.setData(object);
                }
            }
            else
            {
                result.success(false);
                result.setData("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (listner != null)
            listner.onResponse(result);
    }
}

