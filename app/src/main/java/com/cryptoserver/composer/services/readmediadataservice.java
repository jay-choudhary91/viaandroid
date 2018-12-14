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
import com.cryptoserver.composer.models.videomodel;
import com.cryptoserver.composer.netutils.xapipostjson;
import com.cryptoserver.composer.utils.common;
import com.cryptoserver.composer.utils.config;
import com.cryptoserver.composer.utils.ffmpegaudioframegrabber;
import com.cryptoserver.composer.utils.ffmpegvideoframegrabber;
import com.cryptoserver.composer.utils.progressdialog;
import com.cryptoserver.composer.utils.taskresult;

import org.bytedeco.javacpp.avutil;
import org.bytedeco.javacv.Frame;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by root on 21/5/18.
 */

public class readmediadataservice extends Service {

    ArrayList<videomodel> framearraylist =new ArrayList<>();
    String mediapath="",firsthash="",mediatype="";
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
                try {
                    xapicounter = 0;
                    framearraylist =new ArrayList<>();
                    mediapath = intent.getExtras().getString("mediapath");
                    String keytype = intent.getExtras().getString("keytype");
                    firsthash = intent.getExtras().getString("firsthash");
                    mediatype = intent.getExtras().getString("mediatype");
                    if(mediapath != null && (! mediapath.isEmpty()))
                    {
                        File file=new File(mediapath);
                        if(file.exists())
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

                            Cursor cursor=mdbhelper.getmediainfobyfirsthash(firsthash);
                            String status="",videotoken="",remainingframes="50",lastframe="1";
                            if(cursor != null && cursor.getCount() > 0)
                            {
                                if (cursor.moveToFirst())
                                {
                                    do{
                                        status = "" + cursor.getString(cursor.getColumnIndex("status"));
                                        videotoken = "" + cursor.getString(cursor.getColumnIndex("token"));
                                        remainingframes = "" + cursor.getString(cursor.getColumnIndex("remainingframes"));
                                        lastframe = "" + cursor.getString(cursor.getColumnIndex("lastframe"));
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
                                try
                                {
                                    if(mediatype.equalsIgnoreCase("video"))
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
                                    }
                                    else if(mediatype.equalsIgnoreCase("audio"))
                                    {
                                        try {
                                            ffmpegaudioframegrabber grabber = new ffmpegaudioframegrabber(new File(mediapath));
                                            grabber.start();
                                            for(int i = 0; i<grabber.getLengthInAudioFrames(); i++) {
                                                Frame frame = grabber.grabAudio();
                                                if (frame == null)
                                                    break;

                                                ShortBuffer shortbuff = ((ShortBuffer) frame.samples[0].position(0));
                                                java.nio.ByteBuffer bb = java.nio.ByteBuffer.allocate(shortbuff.capacity() * 4);
                                                bb.asShortBuffer().put(shortbuff);
                                                byte[] byteData = bb.array();
                                                String hash = common.getkeyvalue(byteData, keytype);

                                                framearraylist.add(new videomodel("Frame ", keytype,count,hash));
                                                if(count > 30)
                                                    break;
                                            }
                                            grabber.flush();
                                        }catch (Exception e)
                                        {
                                            e.printStackTrace();
                                        }
                                    }
                                    else if(mediatype.equalsIgnoreCase("image"))
                                    {

                                    }

                                }catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                                runxapicounter();
                            }
                            else if(status.equalsIgnoreCase(config.sync_pending))
                            {
                                int framestart=Integer.parseInt(lastframe);
                                framestart=framestart+1;
                                int maxframes=framestart+50;
                                getvideoframes(videotoken,framestart,maxframes);
                            }
                            else if(status.equalsIgnoreCase(config.sync_complete))
                            {
                                sendbroadcastreader();
                            }
                        }
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }


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
                    Log.e("Found hash ",""+hashvalue);
                    String videotoken="",remainingframes="50",lastframe="0",videotitle="";
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
                        videotitle = (object.has("videotitle")?object.getString("videotitle"):"");

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

                        /*mdbhelper.insertfindmediainfo(videoid,videokey,videohashmethod,videohashvalue,videostartdevicedatetime,videostarttransactionid,
                                videodevicetimeoffset,videopublickey,videotypeid,videocreateddate,videorank,videotypeshortname,savedsequencecount,videotoken,
                                config.sync_pending,medianame,true,remainingframes,lastframe);*/

                        String syncdate[] = common.getcurrentdatewithtimezone();

                        mdbhelper.insertstartvideoinfo(firsthash,videotypeshortname,videotitle,videokey,
                                videotoken,videokey,"",syncdate[0] , "videoframe_get",
                                "",videostartdevicedatetime,videodevicetimeoffset,"",
                                firsthash,videoid,config.sync_pending,remainingframes,lastframe);


                        try {
                            mdbhelper.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                       int framestart=1;int maxframes=50;
                       getvideoframes(videotoken,framestart,maxframes);

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

    public void getvideoframes(final String videotoken, final int framestart, final int maxframes)
    {
        HashMap<String,Object> mpairslist=new HashMap<String, Object>();
        mpairslist.put("videotoken",videotoken);
        mpairslist.put("framestart",""+framestart);
        mpairslist.put("maxframes",""+maxframes);

        xapipost_sendjson(getApplicationContext(),config.type_videoframes_get,mpairslist, new apiresponselistener() {
            @Override
            public void onResponse(taskresult response) {
                if(response.isSuccess())
                {
                    int remainingframes=0;
                    try {
                        JSONObject jobject = (JSONObject) response.getData();
                        if(jobject.has("remainingframes"))
                            remainingframes=jobject.getInt("remainingframes");

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
                                String objectparentid = (object.has("objectparentid")?object.getString("objectparentid"):"");
                                String videoframenumber = (object.has("videoframenumber")?object.getString("videoframenumber"):"");
                                String videoframehashvalue = (object.has("videoframehashvalue")?object.getString("videoframehashvalue"):"");
                                String videoframehashmethod = (object.has("videoframehashmethod")?object.getString("videoframehashmethod"):"");
                                String videoframemeta = (object.has("videoframemeta")?object.getString("videoframemeta"):"");
                                String videoframemetahash = (object.has("videoframemetahash")?object.getString("videoframemetahash"):"");
                                String videoframemetahashmethod = (object.has("videoframemetahashmethod")?object.getString("videoframemetahashmethod"):"");
                                String videoframedevicedatetime = (object.has("videoframedevicedatetime")?object.getString("videoframedevicedatetime"):"");
                                String videoframetransactionid = (object.has("videoframetransactionid")?object.getString("videoframetransactionid"):"");
                                String sequenceno = (object.has("sequenceno")?object.getString("sequenceno"):"");
                                String meta = (object.has("meta")?object.getString("meta"):"");
                                String hashvalue = (object.has("hashvalue")?object.getString("hashvalue"):"");
                                String hashmethod = (object.has("hashmethod")?object.getString("hashmethod"):"");
                                String metahash = (object.has("metahash")?object.getString("metahash"):"");
                                String metahashmethod = (object.has("metahashmethod")?object.getString("metahashmethod"):"");

                                meta=meta.replace("u00b0","°");
                                meta=meta.replace("&#39;","\'");
                                meta=meta.replace("&#36;","\'");
                                meta=meta.replace("&#34;","");

                                mdbhelper.insertframemetricesinfo("","",hashmethod,objectparentid,
                                        meta,videoframedevicedatetime,hashmethod,hashvalue,
                                        sequenceno,"",videoframedevicedatetime,"",
                                        "","0",videoframetransactionid);
                            }

                            try {
                                mdbhelper.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            int lastframe=maxframes;
                            if(remainingframes > 0)
                            {
                                int newframestart=maxframes+1;
                                int newmaxframes=maxframes+50;
                                updatefindmediainfosyncstatus(videotoken,""+lastframe,""+remainingframes,config.sync_pending);
                                getvideoframes(videotoken,newframestart,newmaxframes);
                            }
                            else
                            {
                                updatefindmediainfosyncstatus(videotoken,""+lastframe,""+remainingframes,config.sync_complete);
                                sendbroadcastreader();
                            }
                        }
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void updatefindmediainfosyncstatus(String videotoken,String lastframe,String remainingframes,String syncstatus)
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
        mdbhelper.updatefindmediainfosyncstatus(videotoken,lastframe,remainingframes,syncstatus);

        try {
            mdbhelper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //* Broadcast for notify reader controller to get data from database
    public void sendbroadcastreader()
    {
        Intent i = new Intent(config.reader_service_getmetadata);
        sendBroadcast(i);
    }
    //* End of broadcast register


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