package com.deeptruth.app.android.netutils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.deeptruth.app.android.interfaces.apiresponselistener;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.taskresult;
import com.deeptruth.app.android.utils.xdata;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;


public class xapipostfile extends AsyncTask<Void, Void, String> {

    String serverurl;
    Context mContext;
    String filepath = "";
    apiresponselistener listner;


    HttpURLConnection conn = null;
    DataOutputStream dos = null;
    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";
    int bytesRead, bytesAvailable, bufferSize;
    byte[] buffer;
    int maxBufferSize = 1 * 1024 * 1024;
    String serverresponsemessage = "";
    int serverresponsecode = 0;


    //private final String boundary;
    private static final String LINE_FEED = "\r\n";
    private HttpURLConnection httpConn;
    private String charset;
    private OutputStream outputStream;
    private PrintWriter writer;


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

        // reference link -> https://gist.github.com/luankevinferreira/5221ea62e874a9b29d86b13a2637517b
        File sourceFile = new File(filepath);
        if (!sourceFile.isFile()) {
            Log.e("xapipostfile", "Source file doesn't exist!");
            return "";
        }

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
            int i;
            // read byte by byte until end of stream
            while ((i = bis.read()) > 0) {
                bos.write(i);
            }
            bis.close();
            bos.close();
            System.out.println(((HttpURLConnection) urlconnection).getResponseMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {

            InputStream inputStream;
            int responseCode = ((HttpURLConnection) urlconnection).getResponseCode();
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

        try { // open a URL connection to the Servlet






                    // open a URL connection to the Servlet
                    /*FileInputStream fileInputStream = new FileInputStream(sourceFile);
                    URL url = new URL(serverurl);

                    boundary = "===" + System.currentTimeMillis() + "===";
                    httpConn = (HttpURLConnection) url.openConnection();
                    httpConn.setUseCaches(false);
                    httpConn.setDoOutput(true);    // indicates POST method
                    httpConn.setDoInput(true);
                    httpConn.setRequestMethod("PUT");
                    httpConn.setRequestProperty("Content-Type",
                            "multipart/form-data; boundary=" + boundary);
                    outputStream = httpConn.getOutputStream();
                    writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"),
                    true);

                    writer.append("--" + boundary).append(LINE_FEED);
                    writer.append("Content-Disposition: form-data; name=\"" + common.getfilename(filepath) + "\"")
                            .append(LINE_FEED);
                    writer.append("Content-Type: text/plain; charset=" + charset).append(
                            LINE_FEED);
                    writer.append(LINE_FEED);
                    writer.append("video").append(LINE_FEED);
                    writer.flush();


                    writer.append("--" + boundary).append(LINE_FEED);
                    writer.append(
                            "Content-Disposition: form-data; name=\"" + common.getfilename(filepath)
                                    + "\"; filename=\"" + common.getfilename(filepath) + "\"")
                            .append(LINE_FEED);
                    writer.append(
                            "Content-Type: "
                                    + URLConnection.guessContentTypeFromName(common.getfilename(filepath)))
                            .append(LINE_FEED);
                    writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
                    writer.append(LINE_FEED);
                    writer.flush();

                    FileInputStream inputStream = new FileInputStream(sourceFile);
                    byte[] buffer = new byte[4096];
                    int bytesRead = -1;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    outputStream.flush();
                    inputStream.close();
                    writer.append(LINE_FEED);
                    writer.flush();

                    List<String> response = new ArrayList<String>();
                    writer.append(LINE_FEED).flush();
                    writer.append("--" + boundary + "--").append(LINE_FEED);
                    writer.close();

                    // checks server's status code first
                    int status = httpConn.getResponseCode();

                    if (status == HttpURLConnection.HTTP_OK) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(
                                httpConn.getInputStream()));
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            response.add(line);
                        }
                        reader.close();
                        httpConn.disconnect();
                    } else {
                        throw new IOException("Server returned non-OK status: " + status);
                    }

                    serverresponsemessage =  response.toString();*/

                   /* // Open a HTTP  connection to  the URL
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true); // Allow Inputs
                    conn.setDoOutput(true); // Allow Outputs
                    conn.setUseCaches(false); // Don't use a Cached Copy
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    conn.setRequestProperty("uploaded_file", common.getfilename(serverurl));

                    dos = new DataOutputStream(conn.getOutputStream());

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""+ common.getfilename(serverurl) + "\"" + lineEnd);
                            dos.writeBytes(lineEnd);

                    // create a buffer of  maximum size
                    bytesAvailable = fileInputStream.available();

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

                    Log.i("uploadFile", "HTTP Response is : "
                            + serverresponsemessage + ": " + serverresponsecode);

                    if(serverresponsecode == 200){

                        *//*runOnUiThread(new Runnable() {
                            public void run() {

                                String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
                                        +" http://www.androidexample.com/media/uploads/"
                                        +uploadFileName;

                                messageText.setText(msg);
                                Toast.makeText(UploadToServer.this, "File Upload Complete.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });*//*
                    }

                    //close the streams //
                    fileInputStream.close();
                    dos.flush();
                    dos.close();*/

                } catch (Exception e) {

                    e.printStackTrace();
                }
                return serverresponsemessage;
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (listner != null)
            listner.onResponse(result);
    }
}

