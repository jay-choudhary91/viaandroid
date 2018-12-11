package com.cryptoserver.composer.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.util.Log;

import com.cryptoserver.composer.applicationviavideocomposer;
import com.cryptoserver.composer.database.databasemanager;
import com.cryptoserver.composer.interfaces.apiresponselistener;
import com.cryptoserver.composer.models.dbitemcontainer;
import com.cryptoserver.composer.models.videomodel;
import com.cryptoserver.composer.netutils.xapipostjson;
import com.cryptoserver.composer.utils.common;
import com.cryptoserver.composer.utils.config;
import com.cryptoserver.composer.utils.ffmpegvideoframegrabber;
import com.cryptoserver.composer.utils.progressdialog;
import com.cryptoserver.composer.utils.taskresult;
import com.cryptoserver.composer.utils.xdata;
import com.google.gson.Gson;

import org.bytedeco.javacpp.avutil;
import org.bytedeco.javacv.Frame;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by root on 21/5/18.
 */

public class readmediadataservice extends Service {

    ArrayList<videomodel> framearraylist =new ArrayList<>();
    String mediapath="";
    int xapicounter = 0;
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
                mediapath = intent.getExtras().getString("mediapath");
                String keytype = intent.getExtras().getString("keytype");
                if(mediapath != null && (! mediapath.isEmpty()))
                {
                    File file=new File(mediapath);
                    if(file.exists())
                    {
                        long currentframenumber=0,frameduration =15;
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

                        String medianame=common.getfilename(mediapath);
                        Cursor cursor=mdbhelper.getsyncstatusbymedianame(medianame);
                        String status="",videotoken="";
                        if(cursor != null && cursor.getCount() > 0)
                        {
                            if (cursor.moveToFirst())
                            {
                                do{
                                    status = "" + cursor.getString(cursor.getColumnIndex("status"));
                                    videotoken = "" + cursor.getString(cursor.getColumnIndex("videotoken"));
                                }while(cursor.moveToNext());
                            }
                        }

                        try {
                            mdbhelper.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if(status.trim().isEmpty())
                        {
                            int count = 1;
                            currentframenumber=0;
                            currentframenumber = currentframenumber + frameduration;
                            try
                            {
                                ffmpegvideoframegrabber grabber = new ffmpegvideoframegrabber(mediapath);
                                grabber.setPixelFormat(avutil.AV_PIX_FMT_RGB24);
                                String format= common.getvideoformat(mediapath);
                                if(format.equalsIgnoreCase("mp4"))
                                    grabber.setFormat(format);

                                grabber.start();
                                for(int i = 0; i<grabber.getLengthInFrames(); i++){
                                    Frame frame = grabber.grabImage();
                                    if (frame == null)
                                        break;

                                    ByteBuffer buffer= ((ByteBuffer) frame.image[0].position(0));
                                    byte[] byteData = new byte[buffer.remaining()];
                                    buffer.get(byteData);
                                    String keyValue= common.getkeyvalue(byteData,keytype);

                                    framearraylist.add(new videomodel("Frame ", keytype,count,keyValue));
                                    if(count > 30)
                                        break;

                                    count++;
                                }
                                grabber.flush();

                            }catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                            runxapicounter();
                        }
                        else if(status.equalsIgnoreCase(config.sync_pending))
                        {
                            getvideoframes(videotoken);
                        }
                    }
                }
                applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressdialog.dismisswaitdialog();
                    }
                });
            }
        }).start();
        /**/
        return START_NOT_STICKY;
    }

    public void xapigetmediainfo(final String hashvalue)
    {

        HashMap<String,Object> mpairslist=new HashMap<String, Object>();

        mpairslist.put("hashvalue",hashvalue);

        xapipost_sendjson(getApplicationContext(),config.type_video_find,mpairslist, new apiresponselistener() {
            @Override
            public void onResponse(taskresult response) {
                if(response.isSuccess())
                {
                    String videotoken="";
                    try {
                        JSONObject object = (JSONObject) response.getData();

                        String videoid = (object.has("videoid")?object.getString("videoid"):"");
                        String videokey = (object.has("videokey")?object.getString("videokey"):"");
                        String videohashmethod = (object.has("videohashmethod")?object.getString("videohashmethod"):"");
                        String videohashvalue = (object.has("videohashvalue")?object.getString("videohashvalue"):"");
                        String videostartdevicedatetime = (object.has("videostartdevicedatetime")?object.getString("videostartdevicedatetime"):"");
                        String videostarttransactionid = (object.has("videostarttransactionid")?object.getString("videostarttransactionid"):"");
                        String videodevicetimeoffset = (object.has("videodevicetimeoffset")?object.getString("videodevicetimeoffset"):"");
                        String videopublickey = (object.has("videopublickey")?object.getString("videopublickey"):"");
                        String videotypeid = (object.has("videotypeid")?object.getString("videotypeid"):"");
                        String videocreateddate = (object.has("videocreateddate")?object.getString("videocreateddate"):"");
                        String videorank = (object.has("videorank")?object.getString("videorank"):"");
                        String videotypeshortname = (object.has("videotypeshortname")?object.getString("videotypeshortname"):"");
                        String savedsequencecount = (object.has("savedsequencecount")?object.getString("savedsequencecount"):"");
                        videotoken = (object.has("videotoken")?object.getString("videotoken"):"");

                        String medianame=common.getfilename(mediapath);

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

                        mdbhelper.insertfindmediainfo(videoid,videokey,videohashmethod,videohashvalue,videostartdevicedatetime,videostarttransactionid,
                                videodevicetimeoffset,videopublickey,videotypeid,videocreateddate,videorank,videotypeshortname,savedsequencecount,videotoken,
                                config.sync_pending,medianame,true);


                        try {
                            mdbhelper.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        getvideoframes(videotoken);

                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }else{

                    xapicounter++;
                    runxapicounter();
                }
            }
        });
    }

    public void getvideoframes(String videotoken)
    {
        HashMap<String,Object> mpairslist=new HashMap<String, Object>();
        mpairslist.put("videotoken",videotoken);
        mpairslist.put("framestart","1");
        mpairslist.put("maxframes","50");

        xapipost_sendjson(getApplicationContext(),config.type_videoframes_get,mpairslist, new apiresponselistener() {
            @Override
            public void onResponse(taskresult response) {
                if(response.isSuccess())
                {
                    String videotoken="";
                    try {
                        JSONObject jobject = (JSONObject) response.getData();
                        if(jobject.has("frames"))
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
                            JSONArray array=jobject.getJSONArray("frames");
                            for(int i=0;i<array.length();i++)
                            {
                                JSONObject object =array.getJSONObject(i);
                                String videoframeid = (object.has("videoframeid")?object.getString("videoframeid"):"");
                                String objectid = (object.has("objectid")?object.getString("objectid"):"");
                                String videoframenumber = (object.has("videoframenumber")?object.getString("videoframenumber"):"");
                                String videoframehashvalue = (object.has("videoframehashvalue")?object.getString("videoframehashvalue"):"");
                                String videoframehashmethod = (object.has("videoframehashmethod")?object.getString("videoframehashmethod"):"");
                                String videoframemeta = (object.has("videoframemeta")?object.getString("videoframemeta"):"");
                                String videoframemetahash = (object.has("videoframemetahash")?object.getString("videoframemetahash"):"");
                                String videoframemetahashmethod = (object.has("videoframemetahashmethod")?object.getString("videoframemetahashmethod"):"");
                                String videoframedevicedatetime = (object.has("videoframedevicedatetime")?object.getString("videoframedevicedatetime"):"");
                                String videoframetransactionid = (object.has("videoframetransactionid")?object.getString("videoframetransactionid"):"");
                                String videoid = (object.has("videoid")?object.getString("videoid"):"");
                                String videokey = (object.has("videokey")?object.getString("videokey"):"");
                                String videoduration = (object.has("videoduration")?object.getString("videoduration"):"");
                                String videohashmethod = (object.has("videohashmethod")?object.getString("videohashmethod"):"");
                                String videohashvalue = (object.has("videohashvalue")?object.getString("videohashvalue"):"");
                                String videostartdevicedatetime = (object.has("videostartdevicedatetime")?object.getString("videostartdevicedatetime"):"");
                                String videocompletedevicedatetime = (object.has("videocompletedevicedatetime")?object.getString("videocompletedevicedatetime"):"");
                                String videocompleteddate = (object.has("videocompleteddate")?object.getString("videocompleteddate"):"");
                                String videoframecount = (object.has("videoframecount")?object.getString("videoframecount"):"");
                                String videostarttransactionid = (object.has("videostarttransactionid")?object.getString("videostarttransactionid"):"");
                                String videocompletetransactionid = (object.has("videocompletetransactionid")?object.getString("videocompletetransactionid"):"");
                                String videostartmeta = (object.has("videostartmeta")?object.getString("videostartmeta"):"");
                                String videodevicetimeoffset = (object.has("videodevicetimeoffset")?object.getString("videodevicetimeoffset"):"");
                                String videoprivatekey = (object.has("videoprivatekey")?object.getString("videoprivatekey"):"");
                                String videopublickey = (object.has("videopublickey")?object.getString("videopublickey"):"");
                                String sequenceno = (object.has("sequenceno")?object.getString("sequenceno"):"");
                                String meta = (object.has("meta")?object.getString("meta"):"");
                                String hashvalue = (object.has("hashvalue")?object.getString("hashvalue"):"");
                                String hashmethod = (object.has("hashmethod")?object.getString("hashmethod"):"");
                                String metahash = (object.has("metahash")?object.getString("metahash"):"");
                                String metahashmethod = (object.has("metahashmethod")?object.getString("metahashmethod"):"");

                                mdbhelper.insertvideoframedata(videoframeid,objectid,videoframenumber,videoframehashvalue,videoframehashmethod,
                                        videoframemeta,videoframemetahash,videoframemetahashmethod,videoframedevicedatetime,videoframetransactionid
                                        ,videoid,videokey,videoduration,videohashmethod,videohashvalue,videostartdevicedatetime,videocompletedevicedatetime
                                        ,videocompleteddate,videoframecount,videostarttransactionid,videocompletetransactionid,videostartmeta
                                        ,videodevicetimeoffset,videoprivatekey,videopublickey,sequenceno,meta,hashvalue,hashmethod
                                        ,metahash,metahashmethod);
                            }

                            try {
                                mdbhelper.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            updatefindmediainfosyncstatus(true);
                        }

                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void updatefindmediainfosyncstatus(boolean isvideofound)
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
        String medianame=common.getfilename(mediapath);
        mdbhelper.updatefindmediainfosyncstatus(medianame,isvideofound);

        try {
            mdbhelper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public  void runxapicounter(){
        if(xapicounter != framearraylist.size())
        {
            String hashvalue =  framearraylist.get(xapicounter).getkeyvalue();
            xapigetmediainfo(hashvalue);
        }
    }

    public void xapipost_sendjson(Context mContext, String Action, HashMap<String,Object> mPairList, apiresponselistener mListener) {
        xapipostjson api = new xapipostjson(mContext,Action,mListener);
        Set keys = mPairList.keySet();
        Iterator itr = keys.iterator();
        while (itr.hasNext()) {
            String key = (String)itr.next();
            Object argvalue = mPairList.get(key);
            api.add(key,argvalue);
        }
        api.execute();
    }
}