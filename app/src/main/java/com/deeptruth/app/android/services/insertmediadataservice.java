package com.deeptruth.app.android.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.database.databasemanager;
import com.deeptruth.app.android.models.dbitemcontainer;
import com.deeptruth.app.android.models.videomodel;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.md5;
import com.deeptruth.app.android.utils.xdata;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by root on 21/5/18.
 */

public class insertmediadataservice extends Service {

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

    new Thread(new Runnable() {
        @Override
        public void run() {

            try {

                Log.e("ServiceClass","Service "+xdata.getinstance().getSetting(config.servicedata_mediapath));

                if(xdata.getinstance().getSetting(config.servicedata_liststart).trim().isEmpty())
                    return;

                final String mediapath = xdata.getinstance().getSetting(config.servicedata_mediapath);
                final String keytype = xdata.getinstance().getSetting(config.servicedata_keytype);
                final Gson gsonobject = new Gson();
                final String list1 = xdata.getinstance().getSetting(config.servicedata_liststart);
                final String list2 = xdata.getinstance().getSetting(config.servicedata_listmiddle);

                Type type = new TypeToken<ArrayList<dbitemcontainer>>() {
                }.getType();

                final ArrayList<dbitemcontainer> mdbstartitemcontainer = gsonobject.fromJson(list1, type);
                final ArrayList<dbitemcontainer> mdbmiddleitemcontainer = gsonobject.fromJson(list2, type);

                if (mediapath != null && (!mediapath.isEmpty()) && (!list1.trim().isEmpty())) {
                    if (mdbstartitemcontainer != null && mdbstartitemcontainer.size() > 0 && (mdbstartitemcontainer.get(0).
                            getItem2().equalsIgnoreCase("video") || mdbstartitemcontainer.get(0).
                            getItem2().equalsIgnoreCase("audio"))) {
                        final String destinationpath = common.gettempfileforhash().getAbsolutePath();
                        String[] complexcommand = {"-i", mediapath, "-f", "framemd5", destinationpath};

                        applicationviavideocomposer.ffmpeg.execute(complexcommand, new ExecuteBinaryResponseHandler() {
                            @Override
                            public void onFailure(String s) {
                                Log.e("Failure with output : ", "IN onFailure");
                            }

                            @Override
                            public void onSuccess(String s) {
                                Log.e("SUCCESS with output : ", s);

                                ArrayList<String> hasharray = new ArrayList<>();
                                BufferedReader br = null;
                                try {
                                    br = new BufferedReader(new FileReader(new File(destinationpath)));
                                    String line;
                                    while ((line = br.readLine()) != null) {
                                        if (line.trim().length() > 0) {
                                            String[] splitarray = line.split(",");
                                            if (splitarray.length > 5) {
                                                String hash = splitarray[splitarray.length - 1];
                                                if (hash.trim().length() > 20 && splitarray[0].equalsIgnoreCase("0")
                                                        && (!hash.trim().equalsIgnoreCase("c99a74c555371a433d121f551d6c6398")))
                                                    hasharray.add(hash.trim().toString());
                                            }
                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } finally {
                                    try {
                                        if (br != null)
                                            br.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }

                                xdata.getinstance().saveSetting(config.ismediadataservicerunning, "1");
                                File file = new File(mediapath);
                                if (file.exists()) {
                                    long currentframenumber = 0, frameduration = 15;
                                    if (!xdata.getinstance().getSetting(config.framecount).trim().isEmpty())
                                        frameduration = Integer.parseInt(xdata.getinstance().getSetting(config.framecount));

                                    int count = 1, arrayupdator = 0;
                                    String videokey = "", firsthash = "";
                                    final ArrayList<videomodel> mvideoframes = new ArrayList<>();
                                    currentframenumber = currentframenumber + frameduration;
                                    try {
                                        if (mdbstartitemcontainer.size() > 0) {
                                            videomodel lastframehash = null;
                                            String interim_identifying_hashes = "";
                                            for (int i = 0; i < hasharray.size(); i++) {

                                                String keyValue = hasharray.get(i);
                                                mvideoframes.add(new videomodel("Frame ", keytype, currentframenumber, keyValue));

                                                if (i == 0 && mdbstartitemcontainer != null && mdbstartitemcontainer.size() > 0) {
                                                    String medianame = common.getfilename(mediapath);
                                                    String[] split = medianame.split("\\.");
                                                    if (split.length > 0)
                                                        medianame = split[0];

                                                    HashMap<String, String> map = new HashMap<String, String>();
                                                    map.put("fps", "30");
                                                    map.put("firsthash", keyValue);
                                                    map.put("hashmethod", keytype);
                                                    map.put("name", medianame);
                                                    map.put("duration", "");
                                                    map.put("framecounts", "");
                                                    map.put("finalhash", "");
                                                    Gson gson = new Gson();
                                                    String json = gson.toJson(map);

                                                    firsthash = keyValue;

                                                    videokey = mdbstartitemcontainer.get(0).getItem4();

                                                    mdbstartitemcontainer.get(0).setItem1(json);
                                                    mdbstartitemcontainer.get(0).setItem3(medianame);
                                                    mdbstartitemcontainer.get(0).setItem14(firsthash);
                                                    updatestartframes(mdbstartitemcontainer);
                                                }

                                                if (interim_identifying_hashes.trim().isEmpty()) {
                                                    interim_identifying_hashes = keyValue;
                                                } else {
                                                    interim_identifying_hashes = interim_identifying_hashes + "," + keyValue;
                                                }


                                                if (count == currentframenumber) {
                                                    lastframehash = null;
                                                    currentframenumber = currentframenumber + frameduration;

                                                    // update 1st duration frame in database
                                                    if (count == frameduration)
                                                        firsthash = keyValue;

                                                    // update every duration frame in database
                                                    if (mdbmiddleitemcontainer.size() > 0 && arrayupdator == mdbmiddleitemcontainer.size() - 1) {
                                                        mdbmiddleitemcontainer.add(mdbmiddleitemcontainer.get(mdbmiddleitemcontainer.size() - 1));
                                                    }

                                                    if (mdbmiddleitemcontainer.size() > 0 && arrayupdator < mdbmiddleitemcontainer.size()) {
                                                        try {
                                                            JSONArray metricesarray = new JSONArray(mdbmiddleitemcontainer.get(arrayupdator).getItem5());
                                                            if (metricesarray != null && metricesarray.length() > 0) {
                                                                JSONObject arrayobject = metricesarray.getJSONObject(0);
                                                                arrayobject.put("interim_identifying_hashes", interim_identifying_hashes);
                                                            }
                                                            String metrichash = md5.calculatestringtomd5(metricesarray.toString());
                                                            mdbmiddleitemcontainer.get(arrayupdator).setItem2(metrichash);
                                                            mdbmiddleitemcontainer.get(arrayupdator).setItem5(metricesarray.toString());

                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                        mdbmiddleitemcontainer.get(arrayupdator).setItem4(videokey);
                                                        mdbmiddleitemcontainer.get(arrayupdator).setItem8(keyValue);
                                                        mdbmiddleitemcontainer.get(arrayupdator).setItem9("" + count);
                                                        updatemiddleframes(mdbmiddleitemcontainer.get(arrayupdator));
                                                        interim_identifying_hashes = "";
                                                    }
                                                    arrayupdator++;
                                                } else {
                                                    lastframehash = new videomodel("Last Frame ", keytype, count, keyValue);
                                                }
                                                count++;
                                            }

                                            if (lastframehash != null) {
                                                mvideoframes.add(lastframehash);
                                                // update every duration frame in database
                                                if (mdbmiddleitemcontainer.size() > 0 && arrayupdator == mdbmiddleitemcontainer.size() - 1) {
                                                    mdbmiddleitemcontainer.add(mdbmiddleitemcontainer.get(mdbmiddleitemcontainer.size() - 1));
                                                }

                                                if (mdbmiddleitemcontainer.size() > 0 && arrayupdator < mdbmiddleitemcontainer.size()) {
                                                    try {
                                                        JSONArray metricesarray = new JSONArray(mdbmiddleitemcontainer.get(arrayupdator).getItem5());
                                                        if (metricesarray != null && metricesarray.length() > 0) {
                                                            JSONObject arrayobject = metricesarray.getJSONObject(0);
                                                            arrayobject.put("interim_identifying_hashes", interim_identifying_hashes);
                                                        }
                                                        String metrichash = md5.calculatestringtomd5(metricesarray.toString());
                                                        mdbmiddleitemcontainer.get(arrayupdator).setItem2(metrichash);
                                                        mdbmiddleitemcontainer.get(arrayupdator).setItem5(metricesarray.toString());
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }

                                                    mdbmiddleitemcontainer.get(arrayupdator).setItem4(videokey);
                                                    mdbmiddleitemcontainer.get(arrayupdator).setItem8(lastframehash.getkeyvalue());
                                                    mdbmiddleitemcontainer.get(arrayupdator).setItem9("" + count);
                                                    updatemiddleframes(mdbmiddleitemcontainer.get(arrayupdator));
                                                }
                                            }

                                            if (mvideoframes.size() > 0) {
                                                String lastframe = "";
                                                if (mvideoframes.size() > 0)
                                                    lastframe = mvideoframes.get(mvideoframes.size() - 1).getkeyvalue();

                                                // update last frame in database
                                                updateendvideoinfo(firsthash, lastframe, "" + count, mediapath, keytype, videokey);
                                                mvideoframes.get(mvideoframes.size() - 1).settitle("Last Frame ");
                                            }

                                            // code for generate audio thumbnail

                                            try {

                                                if (mdbstartitemcontainer != null && mdbstartitemcontainer.size() > 0 &&
                                                        mdbstartitemcontainer.get(0).getItem2().equalsIgnoreCase("audio")) {

                                                    updateaudiothumbnail(mediapath, mdbstartitemcontainer.get(0).getItem15());
                                                    medialistitemaddbroadcast();
                                                }

                                            } catch (Exception e) {
                                                // do nothing for now
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                if(mediapath.equalsIgnoreCase(xdata.getinstance().getSetting(config.servicedata_mediapath)))
                                {
                                    xdata.getinstance().saveSetting(config.servicedata_liststart,"");
                                    xdata.getinstance().saveSetting(config.servicedata_listmiddle,"");
                                    xdata.getinstance().saveSetting(config.servicedata_mediapath,"");
                                    xdata.getinstance().saveSetting(config.servicedata_keytype,"");
                                }

                            }

                            @Override
                            public void onProgress(String s) {
                                Log.e("Progress bar : ", "In Progress");
                            }

                            @Override
                            public void onStart() {
                                Log.e("Start with output : ", "IN START");
                            }

                            @Override
                            public void onFinish() {
                                Log.e("Start with output : ", "IN Finish");
                            }
                        });
                    } else if (mdbstartitemcontainer != null && mdbstartitemcontainer.size() > 0 && (mdbstartitemcontainer.get(0).
                            getItem2().equalsIgnoreCase("image"))) {
                        updatestartframes(mdbstartitemcontainer);
                        updatemiddleframes(mdbmiddleitemcontainer.get(0));

                        if(mediapath.equalsIgnoreCase(xdata.getinstance().getSetting(config.servicedata_mediapath)))
                        {
                            xdata.getinstance().saveSetting(config.servicedata_liststart,"");
                            xdata.getinstance().saveSetting(config.servicedata_listmiddle,"");
                            xdata.getinstance().saveSetting(config.servicedata_mediapath,"");
                            xdata.getinstance().saveSetting(config.servicedata_keytype,"");
                        }

                    }

                }

            } catch (FFmpegCommandAlreadyRunningException e) {
                // do nothing for now
            }
       /* }
        });*/
    /*} catch (FFmpegNotSupportedException e) {
        //showUnsupportedExceptionDialog();
            Log.d("ffmpeg", "FFmpegNotSupportedException : " + e);
    } catch (Exception e) {
        Log.d("ffmpeg", "EXception no controlada : " + e);
    }*/

         xdata.getinstance().saveSetting(config.ismediadataservicerunning,"0");
        }
    }).start();

        return START_NOT_STICKY;
    }

    public void updatestartframes(ArrayList<dbitemcontainer> mdbstartitemcontainer)
    {
        if(mdbstartitemcontainer != null && mdbstartitemcontainer.size() > 0)
        {
            databasemanager mdbhelper=null;
            if (mdbhelper == null) {
                mdbhelper = new databasemanager(getApplicationContext());
                mdbhelper.createDatabase();
            }

            try {
                mdbhelper.open();
            } catch (Exception e) {
                e.printStackTrace();
            }

            for(int i=0;i<mdbstartitemcontainer.size();i++)
            {
                mdbhelper.updatestartmediainfocomposer(mdbstartitemcontainer.get(i).getItem1(),mdbstartitemcontainer.get(i).getItem2()
                ,mdbstartitemcontainer.get(i).getItem3(),mdbstartitemcontainer.get(i).getItem4(),mdbstartitemcontainer.get(i).getItem5()
                ,mdbstartitemcontainer.get(i).getItem6(),mdbstartitemcontainer.get(i).getItem7(),mdbstartitemcontainer.get(i).getItem8(),
                mdbstartitemcontainer.get(i).getItem9(),mdbstartitemcontainer.get(i).getItem10(),mdbstartitemcontainer.get(i).getItem11()
                ,mdbstartitemcontainer.get(i).getItem12(),mdbstartitemcontainer.get(i).getItem13(),"",mdbstartitemcontainer.get(i).getItem14(),
                mdbstartitemcontainer.get(i).getItem15(),"0","sync_pending","","","0","inprogress",mdbstartitemcontainer.get(i).getItem3(),"",
                mdbstartitemcontainer.get(i).getItem16(),"");
            }

            try {
                mdbhelper.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void updatemiddleframes(dbitemcontainer mdbmiddleitemcontainer)
    {
        if(mdbmiddleitemcontainer != null)
        {
            databasemanager mdbhelper=null;
            if (mdbhelper == null) {
                mdbhelper = new databasemanager(getApplicationContext());
                mdbhelper.createDatabase();
            }

            try {
                mdbhelper.open();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mdbhelper.insertframemetricesinfo(mdbmiddleitemcontainer.getItem1(),mdbmiddleitemcontainer.getItem2()
                    ,mdbmiddleitemcontainer.getItem3(),mdbmiddleitemcontainer.getItem4(),mdbmiddleitemcontainer.getItem5()
                    ,mdbmiddleitemcontainer.getItem6(),mdbmiddleitemcontainer.getItem7(),mdbmiddleitemcontainer.getItem8(),
                    mdbmiddleitemcontainer.getItem9(),mdbmiddleitemcontainer.getItem10(),mdbmiddleitemcontainer.getItem11()
                    ,mdbmiddleitemcontainer.getItem12(),mdbmiddleitemcontainer.getItem13(),mdbmiddleitemcontainer.getItem14(),
                    mdbmiddleitemcontainer.getItem15(),mdbmiddleitemcontainer.getItem16(),mdbmiddleitemcontainer.getItem17(),
                    mdbmiddleitemcontainer.getItem18());
            try {
                mdbhelper.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Calling when frames are getting from ffmpegframegrabber
    public void updateendvideoinfo(String updatefirsthash, String lastframe,String lastcount,
                                     String videourl,String keytype,String videokey)
    {
        String duration = "";
        if(!videourl.isEmpty())
            duration = common.getvideotimefromurl(applicationviavideocomposer.getactivity(),videourl);

        String medianame = common.getfilename(videourl);
        String[] split=medianame.split("\\.");
        if(split.length > 0)
            medianame=split[0];

        try {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("fps","30");
            map.put("firsthash", updatefirsthash);
            map.put("hashmethod",keytype);
            map.put("name",medianame);
            map.put("duration",duration);
            map.put("framecounts",lastcount);
            map.put("finalhash",lastframe);
            Gson gson = new Gson();
            String json = gson.toJson(map);

            databasemanager mdbhelper=null;
            if (mdbhelper == null) {
                mdbhelper = new databasemanager(getApplicationContext());
                mdbhelper.createDatabase();
            }

            try {
                mdbhelper.open();
            } catch (Exception e) {
                e.printStackTrace();
            }

            mdbhelper.updatestartvideoinfo(json,videokey);

            try {
                mdbhelper.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent i = new Intent(config.composer_service_savemetadata);
        sendBroadcast(i);
    }

    public void updateaudiothumbnail(String filepath,String thumbnailurl)
    {
        databasemanager mdbhelper=null;
        if (mdbhelper == null) {
            mdbhelper = new databasemanager(applicationviavideocomposer.getactivity());
            mdbhelper.createDatabase();
        }

        try {
            mdbhelper.open();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        try
        {
            mdbhelper.updateaudiothumbnail(common.getfilename(filepath),thumbnailurl);
            mdbhelper.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        xdata.getinstance().saveSetting(config.ismediadataservicerunning,"0");
    }

    public void medialistitemaddbroadcast()
    {
        Intent intent = new Intent(config.broadcast_medialistnewitem);
        applicationviavideocomposer.getactivity().sendBroadcast(intent);
    }
}