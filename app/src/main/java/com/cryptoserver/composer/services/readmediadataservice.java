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
import com.cryptoserver.composer.utils.md5;
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
    String mediapath="",firsthash="",mediatype="",actiontype = "";
    int xapicounter = 0,maximumframechecklimit=30;


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
                            String status="",mediatoken="",remainingframes="10",lastframe="1";
                            if(cursor != null && cursor.getCount() > 0)
                            {
                                if (cursor.moveToFirst())
                                {
                                    do{
                                        status = "" + cursor.getString(cursor.getColumnIndex("status"));
                                        mediatoken = "" + cursor.getString(cursor.getColumnIndex("token"));
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
                                            if(count >= maximumframechecklimit)
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

                                                if(count > 10)
                                                {
                                                    ShortBuffer shortbuff = ((ShortBuffer) frame.samples[0].position(0));
                                                    java.nio.ByteBuffer bb = java.nio.ByteBuffer.allocate(shortbuff.capacity() * 4);
                                                    bb.asShortBuffer().put(shortbuff);
                                                    byte[] byteData = bb.array();
                                                    String hash = common.getkeyvalue(byteData, keytype);

                                                    framearraylist.add(new videomodel("Frame ", keytype,count,hash));
                                                    if(count >= maximumframechecklimit)
                                                        break;
                                                }
                                                count++;
                                            }
                                            grabber.flush();
                                        }catch (Exception e)
                                        {
                                            e.printStackTrace();
                                        }
                                    }
                                    else if(mediatype.equalsIgnoreCase("image"))
                                    {
                                        framearraylist.add(new videomodel("Frame ", keytype,count,firsthash));
                                    }
                                }catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                                runxapicounter();
                            }
                            else if(status.equalsIgnoreCase(config.sync_inprogress))
                            {
                                int framestart=Integer.parseInt(lastframe);
                                framestart=framestart+1;
                                int maxframes=framestart+10;
                                getvideoframes(mediatoken,framestart,maxframes);
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

        if(mediatype.equalsIgnoreCase("video"))
        {
            actiontype = config.type_video_find;
        }
        else if(mediatype.equalsIgnoreCase("audio"))
        {
            actiontype = config.type_audio_find;
        }
        else if(mediatype.equalsIgnoreCase("image"))
        {
            actiontype = config.type_image_find;
        }

        xapipost_sendjson(getApplicationContext(),actiontype,mpairslist, new apiresponselistener() {
            @Override
            public void onResponse(taskresult response) {
                if(response.isSuccess())
                {
                    Log.e("Found hash ",""+hashvalue);
                    String framecount="",mediatypeshortname = "",mediatitle = "",mediakey = "",mediatoken = "",mediaid = "",
                            remainingframes="10",lastframe="0", mediastartdevicedatetime = "",mediadevicetimeoffset = "",
                    mediacompleteddevicedatetime="";

                    try {
                        JSONObject object = (JSONObject) response.getData();
                        if(mediatype.equalsIgnoreCase("video"))
                        {
                            mediaid = (object.has("videoid")?object.getString("videoid"):"");
                            mediakey = (object.has("videokey")?object.getString("videokey"):"");
                            framecount = (object.has("videoframecount")?object.getString("videoframecount"):"1");
                            String videohashmethod = (object.has("videohashmethod")?object.getString("videohashmethod"):"");
                            String videohashvalue = (object.has("videohashvalue")?object.getString("videohashvalue"):"");
                            mediastartdevicedatetime = (object.has("videostartdevicedatetime")?object.getString("videostartdevicedatetime"):"");
                            String videostarttransactionid = (object.has("videostarttransactionid")?object.getString("videostarttransactionid"):"");
                            mediadevicetimeoffset = (object.has("videodevicetimeoffset")?object.getString("videodevicetimeoffset"):"");
                            String videopublickey = (object.has("videopublickey")?object.getString("videopublickey"):"");
                            String videotypeid = (object.has("videotypeid")?object.getString("videotypeid"):"");
                            mediacompleteddevicedatetime = (object.has("videocreateddate")?object.getString("videocreateddate"):"");
                            String videorank = (object.has("videorank")?object.getString("videorank"):"");
                            mediatypeshortname = (object.has("videotypeshortname")?object.getString("videotypeshortname"):"");
                            String savedsequencecount = (object.has("savedsequencecount")?object.getString("savedsequencecount"):"");
                            mediatoken = (object.has("videotoken")?object.getString("videotoken"):"");
                            mediatitle = (object.has("videotitle")?object.getString("videotitle"):"");

                            actiontype = config.type_videoframes_get;
                        }
                        else if(mediatype.equalsIgnoreCase("audio"))
                        {
                            mediaid = (object.has("audioid")?object.getString("audioid"):"");
                            mediakey = (object.has("audiokey")?object.getString("audiokey"):"");
                            framecount = (object.has("audioframecount")?object.getString("audioframecount"):"1");
                            String audiohashmethod = (object.has("audiohashmethod")?object.getString("audiohashmethod"):"");
                            String audiohashvalue = (object.has("audiohashvalue")?object.getString("audiohashvalue"):"");
                            mediastartdevicedatetime = (object.has("audiostartdevicedatetime")?object.getString("audiostartdevicedatetime"):"");
                            mediacompleteddevicedatetime = (object.has("audiocompletedevicedatetime")?object.getString("audiocompletedevicedatetime"):"");
                            String audiostarttransactionid = (object.has("audiostarttransactionid")?object.getString("audiostarttransactionid"):"");
                            String audiocompletetransactionid = (object.has("audiocompletetransactionid")?object.getString("audiocompletetransactionid"):"");
                            mediadevicetimeoffset = (object.has("audiodevicetimeoffset")?object.getString("audiodevicetimeoffset"):"");
                            String audiopublickey = (object.has("audiopublickey")?object.getString("audiopublickey"):"");
                            String audiotypeid = (object.has("audiotypeid")?object.getString("audiotypeid"):"");
                            String audiocreateddate = (object.has("audiocreateddate")?object.getString("audiocreateddate"):"");
                            String audioupdateddate = (object.has("audioupdateddate")?object.getString("audioupdateddate"):"");
                            String audiorank = (object.has("audiorank")?object.getString("audiorank"):"");
                            String audioname = (object.has("audioname")?object.getString("audioname"):"");
                            mediatypeshortname = (object.has("audiotypeshortname")?object.getString("audiotypeshortname"):"");
                            String savedsequencecount = (object.has("savedsequencecount")?object.getString("savedsequencecount"):"");
                            mediatoken = (object.has("audiotoken")?object.getString("audiotoken"):"");
                            mediatitle = (object.has("audioname")?object.getString("audioname"):"");

                            actiontype = config.type_audioframes_get;
                        }
                        else if(mediatype.equalsIgnoreCase("image"))
                        {

                            mediaid = (object.has("imageid")?object.getString("imageid"):"");
                            mediakey = (object.has("imagekey")?object.getString("imagekey"):"");
                            framecount = (object.has("imageframecount")?object.getString("imageframecount"):"1");
                            String imagehashmethod = (object.has("imagehashmethod")?object.getString("imagehashmethod"):"");
                            String imagehashvalue = (object.has("imagehashvalue")?object.getString("imagehashvalue"):"");
                            mediastartdevicedatetime = (object.has("imagestartdevicedatetime")?object.getString("imagestartdevicedatetime"):"");
                            mediacompleteddevicedatetime = (object.has("imagecompletedevicedatetime")?object.getString("imagecompletedevicedatetime"):"");
                            String imagestarttransactionid = (object.has("imagestarttransactionid")?object.getString("imagestarttransactionid"):"");
                            String imagecompletetransactionid = (object.has("imagecompletetransactionid")?object.getString("imagecompletetransactionid"):"");
                            mediadevicetimeoffset = (object.has("imagedevicetimeoffset")?object.getString("imagedevicetimeoffset"):"");
                            String imagepublickey = (object.has("imagepublickey")?object.getString("imagepublickey"):"");
                            String imagetypeid = (object.has("imagetypeid")?object.getString("imagetypeid"):"");
                            String imagecreateddate = (object.has("imagecreateddate")?object.getString("imagecreateddate"):"");
                            String imageupdateddate = (object.has("imageupdateddate")?object.getString("imageupdateddate"):"");
                            String imagerank = (object.has("imagerank")?object.getString("imagerank"):"");
                            String imagename = (object.has("imagename")?object.getString("imagename"):"");
                            mediatypeshortname = (object.has("imagetypeshortname")?object.getString("imagetypeshortname"):"");
                            String savedsequencecount = (object.has("savedsequencecount")?object.getString("savedsequencecount"):"");
                            mediatoken = (object.has("imagetoken")?object.getString("imagetoken"):"");
                            mediatitle = (object.has("imagename")?object.getString("imagename"):"");

                            actiontype = config.type_imageframes_get;
                        }

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

                        String syncdate[] = common.getcurrentdatewithtimezone();

                        mdbhelper.insertstartvideoinfo(firsthash,mediatypeshortname,mediatitle,mediaid,
                                mediatoken,mediakey,"",syncdate[0] , actiontype,
                                "",mediastartdevicedatetime,mediadevicetimeoffset,mediacompleteddevicedatetime,
                                firsthash,mediaid,config.sync_inprogress,remainingframes,lastframe,framecount,"");


                        try {
                            mdbhelper.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                       int framestart=1;int maxframes=10;
                       getvideoframes(mediatoken,framestart,maxframes);

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

    public void getvideoframes(final String mediatoken, final int framestart, final int maxframes)
    {

        HashMap<String,Object> mpairslist=new HashMap<String, Object>();
        if(mediatype.equalsIgnoreCase("video"))
        {
            actiontype = config.type_videoframes_get;
            mpairslist.put("videotoken",mediatoken);
        }
        else if(mediatype.equalsIgnoreCase("audio"))
        {
            actiontype = config.type_audioframes_get;
            mpairslist.put("audiotoken",mediatoken);
        }
        else if(mediatype.equalsIgnoreCase("image"))
        {
            actiontype = config.type_imageframes_get;
            mpairslist.put("imagetoken",mediatoken);
        }
        mpairslist.put("framestart",""+framestart);
        mpairslist.put("maxframes",""+maxframes);

        xapipost_sendjson(getApplicationContext(),actiontype,mpairslist, new apiresponselistener() {
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
                            databasemanager dbmanager=null;
                            if (dbmanager == null) {
                                dbmanager = new databasemanager(getApplicationContext());
                                dbmanager.createDatabase();
                            }

                            try {
                                dbmanager.open();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            JSONArray array=new JSONArray();
                            array=jobject.getJSONArray("frames");

                            if(mediatype.equalsIgnoreCase("video"))
                            {
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
                                    String metahash = (object.has("videoframeservermetahashvalue")?object.getString("videoframeservermetahashvalue"):"");
                                    String metahashmethod = (object.has("videoframeservermetahashmethod")?object.getString("videoframeservermetahashmethod"):"");

                                    meta=common.refactordegreequotesformat(meta);

                                    dbmanager.insertframemetricesinfo("","",hashmethod,objectparentid,
                                            meta,videoframedevicedatetime,hashmethod,hashvalue,
                                            sequenceno,"",videoframedevicedatetime,"",
                                            "","0",videoframetransactionid,metahash);
                                }
                            }
                            else if(mediatype.equalsIgnoreCase("audio"))
                            {
                                for(int i=0;i<array.length();i++)
                                {
                                    JSONObject object =array.getJSONObject(i);
                                    String videoframeid = (object.has("audioframeid")?object.getString("audioframeid"):"");
                                    String objectid = (object.has("objectid")?object.getString("objectid"):"");
                                    String objectparentid = (object.has("objectparentid")?object.getString("objectparentid"):"");
                                    String videoframenumber = (object.has("audioframenumber")?object.getString("audioframenumber"):"");
                                    String videoframehashvalue = (object.has("audioframehashvalue")?object.getString("audioframehashvalue"):"");
                                    String videoframehashmethod = (object.has("audioframehashmethod")?object.getString("audioframehashmethod"):"");
                                    String videoframemeta = (object.has("audioframemeta")?object.getString("audioframemeta"):"");
                                    String videoframemetahash = (object.has("audioframemetahash")?object.getString("audioframemetahash"):"");
                                    String videoframemetahashmethod = (object.has("audioframemetahashmethod")?object.getString("audioframemetahashmethod"):"");
                                    String videoframedevicedatetime = (object.has("audioframedevicedatetime")?object.getString("audioframedevicedatetime"):"");
                                    String videoframetransactionid = (object.has("audioframetransactionid")?object.getString("audioframetransactionid"):"");
                                    String sequenceno = (object.has("sequenceno")?object.getString("sequenceno"):"");
                                    String meta = (object.has("meta")?object.getString("meta"):"");
                                    String hashvalue = (object.has("hashvalue")?object.getString("hashvalue"):"");
                                    String hashmethod = (object.has("hashmethod")?object.getString("hashmethod"):"");
                                    String metahash = (object.has("audioframeservermetahashvalue")?object.getString("audioframeservermetahashvalue"):"");
                                    String metahashmethod = (object.has("audioframeservermetahashmethod")?object.getString("audioframeservermetahashmethod"):"");

                                    meta=common.refactordegreequotesformat(meta);

                                    dbmanager.insertframemetricesinfo("","",hashmethod,objectparentid,
                                            meta,videoframedevicedatetime,hashmethod,hashvalue,
                                            sequenceno,"",videoframedevicedatetime,"",
                                            "","0",videoframetransactionid,metahash);
                                }
                            }
                            else if(mediatype.equalsIgnoreCase("image"))
                            {
                                for(int i=0;i<array.length();i++) {
                                    JSONObject object = array.getJSONObject(i);
                                    String frameid = (object.has("imageframeid")?object.getString("imageframeid"):"");
                                    String objectid = (object.has("objectid")?object.getString("objectid"):"");
                                    String objectparentid = (object.has("objectparentid")?object.getString("objectparentid"):"");
                                    String framenumber = (object.has("imageframenumber")?object.getString("imageframenumber"):"");
                                    String framehashvalue = (object.has("imageframehashvalue")?object.getString("imageframehashvalue"):"");
                                    String framehashmethod = (object.has("imageframehashmethod")?object.getString("imageframehashmethod"):"");
                                    String framemeta = (object.has("imageframemeta")?object.getString("imageframemeta"):"");
                                    String framemetahash = (object.has("imageframemetahash")?object.getString("imageframemetahash"):"");
                                    String framemetahashmethod = (object.has("imageframemetahashmethod")?object.getString("imageframemetahashmethod"):"");
                                    String devicedatetime = (object.has("imageframedevicedatetime")?object.getString("imageframedevicedatetime"):"");
                                    String frametransactionid = (object.has("imageframetransactionid")?object.getString("imageframetransactionid"):"");
                                    String sequenceno = (object.has("sequenceno")?object.getString("sequenceno"):"");
                                    String meta = (object.has("meta")?object.getString("meta"):"");
                                    String hashvalue = (object.has("hashvalue")?object.getString("hashvalue"):"");
                                    String hashmethod = (object.has("hashmethod")?object.getString("hashmethod"):"");
                                    String metahash = (object.has("imageframeservermetahashvalue")?object.getString("imageframeservermetahashvalue"):"");
                                    String metahashmethod = (object.has("imageframeservermetahashmethod")?object.getString("imageframeservermetahashmethod"):"");

                                    if(sequenceno == null || sequenceno.equalsIgnoreCase("null"))
                                        sequenceno="1";

                                    meta=common.refactordegreequotesformat(meta);

                                    dbmanager.insertframemetricesinfo("","",hashmethod,objectparentid,
                                            meta,devicedatetime,hashmethod,hashvalue,
                                            sequenceno,"",devicedatetime,"",
                                            "","0",frametransactionid,metahash);
                                }
                            }

                            try {
                                dbmanager.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            int lastframe=maxframes;
                            if(remainingframes > 0)
                            {
                                int newframestart=maxframes+1;
                                int newmaxframes=maxframes+10;
                                updatefindmediainfosyncstatus(mediatoken,""+lastframe,""+remainingframes,config.sync_inprogress);
                                getvideoframes(mediatoken,newframestart,newmaxframes);
                            }
                            else
                            {
                                updatefindmediainfosyncstatus(mediatoken,""+lastframe,""+remainingframes,config.sync_complete);
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
        if(xapicounter < framearraylist.size() && xapicounter <= maximumframechecklimit)
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