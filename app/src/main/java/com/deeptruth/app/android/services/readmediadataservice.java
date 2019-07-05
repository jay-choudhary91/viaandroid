package com.deeptruth.app.android.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.util.Log;

import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.database.databasemanager;
import com.deeptruth.app.android.interfaces.apiresponselistener;
import com.deeptruth.app.android.models.mediainfotablefields;
import com.deeptruth.app.android.models.videomodel;
import com.deeptruth.app.android.netutils.xapipostjson;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.md5;
import com.deeptruth.app.android.utils.taskresult;
import com.deeptruth.app.android.utils.xdata;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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
    FFmpeg ffmpeg;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

    new Thread(new Runnable() {
        @Override
        public void run()
        {

        try
        {

        if (ffmpeg == null) {
            Log.d("ffmpeg", "ffmpeg : is loading..");
            ffmpeg = FFmpeg.getInstance(getApplicationContext());
        }
        ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
            @Override
            public void onFailure() {
                Log.d("ffmpeg", " onFailure");
                // showUnsupportedExceptionDialog();
            }

            @Override
            public void onSuccess() {

                xapicounter = 0;
                framearraylist =new ArrayList<>();
                mediapath = intent.getExtras().getString("mediapath");
                final String keytype = intent.getExtras().getString("keytype");
                firsthash = intent.getExtras().getString("firsthash");
                mediatype = intent.getExtras().getString("mediatype");

                if(mediapath != null && (! mediapath.isEmpty()))
                {
                    xdata.getinstance().saveSetting(config.ismediadataservicerunning,"1");
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

                        Cursor cursor=mdbhelper.getmediainfobyfilename(common.getfilename(mediapath));
                        String status="",mediatoken="",remainingframes="50",lastframe="1",completeddate = "",localkey="";
                        if(cursor != null && cursor.getCount() > 0)
                        {
                            if (cursor.moveToFirst())
                            {
                                do{
                                    status = "" + cursor.getString(cursor.getColumnIndex("status"));
                                    mediatoken = "" + cursor.getString(cursor.getColumnIndex("token"));
                                    remainingframes = "" + cursor.getString(cursor.getColumnIndex("remainingframes"));
                                    lastframe = "" + cursor.getString(cursor.getColumnIndex("lastframe"));
                                    firsthash = "" + cursor.getString(cursor.getColumnIndex("firsthash"));
                                    completeddate = "" + cursor.getString(cursor.getColumnIndex("completeddate"));
                                    localkey = "" + cursor.getString(cursor.getColumnIndex("localkey"));
                                    completeddate = "" + cursor.getString(cursor.getColumnIndex("completeddate"));
                                }while(cursor.moveToNext());
                            }
                        }

                        try {
                            mdbhelper.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if(status.trim().isEmpty() || status.equalsIgnoreCase(config.sync_pending) ||
                                mediatoken.trim().isEmpty() || completeddate.equalsIgnoreCase("null"))
                        {
                            int count = 1;
                            try
                            {
                                if(mediatype.equalsIgnoreCase("video") || mediatype.equalsIgnoreCase("audio"))
                                {
                                    if(common.ishashfileexist(mediapath))
                                    {
                                        BufferedReader br=null;
                                        try {
                                            String destinationpath=common.getexisthashfilepath(mediapath);
                                            if(destinationpath.trim().isEmpty())
                                                destinationpath = common.gettempfileforhash().getAbsolutePath();

                                            br = new BufferedReader(new FileReader(new File(destinationpath)));
                                            String line;
                                            while ((line = br.readLine()) != null)
                                            {
                                                if(line.trim().length() > 0)
                                                {
                                                    String[] splitarray=line.split(",");
                                                    if(splitarray.length > 5)
                                                    {
                                                        String hash=splitarray[splitarray.length-1];
                                                        if(hash.trim().length() > 20 && splitarray[0].equalsIgnoreCase("0")
                                                                && (! hash.trim().equalsIgnoreCase
                                                                ("c99a74c555371a433d121f551d6c6398")))
                                                        {
                                                            if(framearraylist.size() == 0)
                                                                firsthash=hash.trim().toString();

                                                            framearraylist.add(new videomodel("Frame ", keytype,framearraylist.size()
                                                                    ,hash.trim().toString()));
                                                            if(framearraylist.size() >= maximumframechecklimit)
                                                                break;
                                                        }
                                                    }
                                                }
                                            }
                                            runxapicounter(mediapath);
                                        }
                                        catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        finally{
                                            try {
                                                if(br != null)
                                                    br.close();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                    else
                                    {
                                        final String destinationpath = common.createtempfileofmedianameforhash(mediapath).getAbsolutePath();
                                        String[] complexcommand = {"-i", mediapath,"-f", "framemd5" ,destinationpath};

                                        try {
                                            ffmpeg.execute(complexcommand, new ExecuteBinaryResponseHandler() {
                                                @Override
                                                public void onFailure(String s) {
                                                    Log.e("Failure with output : ","IN onFailure");
                                                }

                                                @Override
                                                public void onSuccess(String s) {
                                                    Log.e("SUCCESS with output : ",s);

                                                    BufferedReader br=null;
                                                    try {
                                                        br = new BufferedReader(new FileReader(new File(destinationpath)));
                                                        String line;
                                                        while ((line = br.readLine()) != null)
                                                        {
                                                            if(line.trim().length() > 0)
                                                            {
                                                                String[] splitarray=line.split(",");
                                                                if(splitarray.length > 5)
                                                                {
                                                                    String hash=splitarray[splitarray.length-1];
                                                                    if(hash.trim().length() > 20 && splitarray[0].equalsIgnoreCase("0")
                                                                            && (! hash.trim().equalsIgnoreCase
                                                                            ("c99a74c555371a433d121f551d6c6398")))
                                                                    {
                                                                        if(framearraylist.size() == 0)
                                                                            firsthash=hash.trim().toString();

                                                                        framearraylist.add(new videomodel("Frame ", keytype,
                                                                                framearraylist.size(),hash.trim().toString()));
                                                                        if(framearraylist.size() >= maximumframechecklimit)
                                                                            break;
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        runxapicounter(mediapath);
                                                    }
                                                    catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                    finally{
                                                        try {
                                                            if(br != null)
                                                                br.close();
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onProgress(String s) {
                                                    Log.e( "Progress bar : " , "In Progress");
                                                    Date initialdate=new Date();
                                                    xdata.getinstance().saveSetting("initialdatereaderapi",""+initialdate.getTime());
                                                }

                                                @Override
                                                public void onStart() {
                                                    Log.e("Start with output : ","IN START");
                                                }

                                                @Override
                                                public void onFinish() {
                                                    Log.e("Start with output : ","IN Finish");
                                                }
                                            });
                                        }catch (Exception e)
                                        {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                else if(mediatype.equalsIgnoreCase("image"))
                                {
                                    firsthash= md5.fileToMD5(mediapath);
                                    framearraylist.add(new videomodel("Frame ", keytype,count,firsthash));
                                    runxapicounter(mediapath);
                                }
                            }catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                        else if(status.equalsIgnoreCase(config.sync_inprogress))
                        {
                            int framestart=0;
                            if(! localkey.trim().isEmpty())
                                framestart=getlastsavedsequence(localkey);

                            framestart=framestart+1;
                            int maxframes=framestart+50;
                            getmediaframes(mediatoken,framestart,maxframes,mediapath);
                        }
                        else if(status.equalsIgnoreCase(config.sync_complete))
                        {
                            sendbroadcastreader();
                        }
                    }
                }
            }
        });
    }catch (Exception e)
    {
        e.printStackTrace();
    }
   }
     }).start();

      return START_NOT_STICKY;
    }

    public void xapigetmediainfo(final String hashvalue, final String mediafilepath)
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
                            remainingframes="50",lastframe="0", mediastartdevicedatetime = "",mediadevicetimeoffset = "",savedsequencecount="",
                    mediacompleteddevicedatetime="",mediatarttransactionid = "",medianame = "",mediacompleteddate="",color="",
                            mediaduration="",mediadescrption="";

                    try {
                        JSONObject object = (JSONObject) response.getData();
                        if(mediatype.equalsIgnoreCase("video"))
                        {
                            mediaid = (object.has("videoid")?object.getString("videoid"):"");
                            mediakey = (object.has("videokey")?object.getString("videokey"):"");
                            framecount = (object.has("videoframecount")?object.getString("videoframecount"):"1");
                            mediaduration = (object.has("videoduration")?object.getString("videoduration"):"");
                            String videohashmethod = (object.has("videohashmethod")?object.getString("videohashmethod"):"");
                            String videohashvalue = (object.has("videohashvalue")?object.getString("videohashvalue"):"");
                            mediastartdevicedatetime = (object.has("videostartdevicedatetime")?object.getString("videostartdevicedatetime"):"");
                            mediatarttransactionid = (object.has("videostarttransactionid")?object.getString("videostarttransactionid"):"");
                            mediadevicetimeoffset = (object.has("videodevicetimeoffset")?object.getString("videodevicetimeoffset"):"");
                            String videopublickey = (object.has("videopublickey")?object.getString("videopublickey"):"");
                            String videotypeid = (object.has("videotypeid")?object.getString("videotypeid"):"");
                            mediacompleteddevicedatetime = (object.has("videocompletedevicedatetime")?object.getString("videocompletedevicedatetime"):"");
                            String videocreateddate = (object.has("videocreateddate")?object.getString("videocreateddate"):"");
                            String videorank = (object.has("videorank")?object.getString("videorank"):"");
                            medianame = (object.has("videoname")?object.getString("videoname"):"");
                            mediadescrption = (object.has("videodescription")?object.getString("videodescription"):"");
                            mediatypeshortname = (object.has("videotypeshortname")?object.getString("videotypeshortname"):"");
                            mediatoken = (object.has("videotoken")?object.getString("videotoken"):"");
                            mediatitle = (object.has("videotitle")?object.getString("videotitle"):"");
                            mediacompleteddate = (object.has("videocompleteddate")?object.getString("videocompleteddate"):"");
                            savedsequencecount = (object.has("savedsequencecount")?object.getString("savedsequencecount"):"");
                            color = (object.has("color")?object.getString("color"):"");

                            actiontype = config.type_videoframes_get;
                        }
                        else if(mediatype.equalsIgnoreCase("audio"))
                        {
                            mediaid = (object.has("audioid")?object.getString("audioid"):"");
                            mediakey = (object.has("audiokey")?object.getString("audiokey"):"");
                            framecount = (object.has("audioframecount")?object.getString("audioframecount"):"1");
                            mediaduration = (object.has("audioduration")?object.getString("audioduration"):"");
                            String audiohashmethod = (object.has("audiohashmethod")?object.getString("audiohashmethod"):"");
                            String audiohashvalue = (object.has("audiohashvalue")?object.getString("audiohashvalue"):"");
                            mediastartdevicedatetime = (object.has("audiostartdevicedatetime")?object.getString("audiostartdevicedatetime"):"");
                            mediacompleteddevicedatetime = (object.has("audiocompletedevicedatetime")?object.getString("audiocompletedevicedatetime"):"");
                            mediatarttransactionid = (object.has("audiostarttransactionid")?object.getString("audiostarttransactionid"):"");
                            String audiocompletetransactionid = (object.has("audiocompletetransactionid")?object.getString("audiocompletetransactionid"):"");
                            mediadevicetimeoffset = (object.has("audiodevicetimeoffset")?object.getString("audiodevicetimeoffset"):"");
                            String audiopublickey = (object.has("audiopublickey")?object.getString("audiopublickey"):"");
                            String audiotypeid = (object.has("audiotypeid")?object.getString("audiotypeid"):"");
                            String audiocreateddate = (object.has("audiocreateddate")?object.getString("audiocreateddate"):"");
                            String audioupdateddate = (object.has("audioupdateddate")?object.getString("audioupdateddate"):"");
                            String audiorank = (object.has("audiorank")?object.getString("audiorank"):"");
                            medianame = (object.has("audioname")?object.getString("audioname"):"");
                            mediadescrption = (object.has("audiodescription")?object.getString("audiodescription"):"");
                            mediatypeshortname = (object.has("audiotypeshortname")?object.getString("audiotypeshortname"):"");
                            savedsequencecount = (object.has("savedsequencecount")?object.getString("savedsequencecount"):"");
                            mediatoken = (object.has("audiotoken")?object.getString("audiotoken"):"");
                           // mediatitle = (object.has("audioname")?object.getString("audioname"):"");
                            mediatitle = (object.has("audiotitle")?object.getString("audiotitle"):"");
                            mediacompleteddate = (object.has("audiocompleteddate")?object.getString("audiocompleteddate"):"");
                            color = (object.has("color")?object.getString("color"):"");

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
                            mediatarttransactionid = (object.has("imagestarttransactionid")?object.getString("imagestarttransactionid"):"");
                            String imagecompletetransactionid = (object.has("imagecompletetransactionid")?object.getString("imagecompletetransactionid"):"");
                            mediadevicetimeoffset = (object.has("imagedevicetimeoffset")?object.getString("imagedevicetimeoffset"):"");
                            String imagepublickey = (object.has("imagepublickey")?object.getString("imagepublickey"):"");
                            String imagetypeid = (object.has("imagetypeid")?object.getString("imagetypeid"):"");
                            String imagecreateddate = (object.has("imagecreateddate")?object.getString("imagecreateddate"):"");
                            String imageupdateddate = (object.has("imageupdateddate")?object.getString("imageupdateddate"):"");
                            String imagerank = (object.has("imagerank")?object.getString("imagerank"):"");
                            medianame = (object.has("imagename")?object.getString("imagename"):"");
                            mediadescrption = (object.has("imagedescription")?object.getString("imagedescription"):"");
                            mediatypeshortname = (object.has("imagetypeshortname")?object.getString("imagetypeshortname"):"");
                            savedsequencecount = (object.has("savedsequencecount")?object.getString("savedsequencecount"):"");
                            mediatoken = (object.has("imagetoken")?object.getString("imagetoken"):"");
                            color = (object.has("color")?object.getString("color"):"");
                            //mediacompleteddate = (object.has("imagecompleteddate")?object.getString("imagecompleteddate"):"");
                            //mediatitle = (object.has("imagename")?object.getString("imagename"):"");
                            mediacompleteddate="xyz";
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

                        if((! mediacompleteddate.trim().isEmpty() && (! mediacompleteddate.equalsIgnoreCase("null"))))
                        {
                            String syncdate[] = common.getcurrentdatewithtimezone();
                            mdbhelper.updatestartmediainfo(new mediainfotablefields("",mediatype,common.getfilename(mediapath),mediaid,
                                    mediatoken,mediakey,"",syncdate[0]  , actiontype,
                                    "",mediastartdevicedatetime,mediadevicetimeoffset,mediacompleteddevicedatetime,mediatarttransactionid,
                                    firsthash,mediaid,config.sync_inprogress,remainingframes,lastframe
                                    ,framecount,"",medianame,mediadescrption,xdata.getinstance().getSetting(config.selected_folder),
                                    "","",mediaduration,mediacompleteddate,color,""));

                            int framestart=1;int maxframes=50;
                            getmediaframes(mediatoken,framestart,maxframes,mediafilepath);
                        }
                        else
                        {
                            String syncdate[] = common.getcurrentdatewithtimezone();
                            mdbhelper.updatestartmediainfo(new mediainfotablefields("",mediatype,common.getfilename(mediapath),mediaid,
                                    mediatoken,mediakey,"",syncdate[0]  , actiontype,
                                    "",mediastartdevicedatetime,mediadevicetimeoffset,mediacompleteddevicedatetime,mediatarttransactionid,
                                    firsthash,mediaid,config.sync_inprogress,remainingframes,lastframe
                                    ,framecount,"",medianame,mediadescrption,xdata.getinstance().getSetting(config.selected_folder),
                                    "","",mediaduration,mediacompleteddate,color,""));

                            sendbroadcastreader();
                        }

                        try {
                            mdbhelper.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }else{

                    xapicounter++;
                    runxapicounter(mediafilepath);
                }
            }
        });
    }

    public void getmediaframes(final String mediatoken, final int framestart, final int maxframes,String mediafilepath)
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
                                    String metahash = (object.has("videoframedevicemetahashvalue")?object.getString("videoframedevicemetahashvalue"):"");
                                    String metahashmethod = (object.has("videoframeservermetahashmethod")?object.getString("videoframeservermetahashmethod"):"");
                                    String color = (object.has("color")?object.getString("color"):"");
                                    String latency = (object.has("latency")?object.getString("latency"):"");

                                    meta=common.refactordegreequotesformat(meta);

                                    dbmanager.insertframemetricesinfo("",metahash,hashmethod,objectparentid,
                                            meta,videoframedevicedatetime,hashmethod,hashvalue,
                                            sequenceno,"",videoframedevicedatetime,"",
                                            "","0",videoframetransactionid,metahash,color,latency);
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
                                    String metahash = (object.has("audioframedevicemetahashvalue")?object.getString("audioframedevicemetahashvalue"):"");
                                    String metahashmethod = (object.has("audioframeservermetahashmethod")?object.getString("audioframeservermetahashmethod"):"");
                                    String color = (object.has("color")?object.getString("color"):"");
                                    String latency = (object.has("latency")?object.getString("latency"):"");

                                    meta=common.refactordegreequotesformat(meta);

                                    dbmanager.insertframemetricesinfo("",metahash,hashmethod,objectparentid,
                                            meta,videoframedevicedatetime,hashmethod,hashvalue,
                                            sequenceno,"",videoframedevicedatetime,"",
                                            "","0",videoframetransactionid,metahash,color,latency);
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
                                    String metahash = (object.has("imageframedevicemetahashvalue")?object.getString("imageframedevicemetahashvalue"):"");
                                    String metahashmethod = (object.has("imageframeservermetahashmethod")?object.getString("imageframeservermetahashmethod"):"");
                                    String color = (object.has("color")?object.getString("color"):"");
                                    String latency = (object.has("latency")?object.getString("latency"):"");

                                    if(sequenceno == null || sequenceno.equalsIgnoreCase("null"))
                                        sequenceno="1";

                                    meta=common.refactordegreequotesformat(meta);

                                    dbmanager.insertframemetricesinfo("",metahash,hashmethod,objectparentid,
                                            meta,devicedatetime,hashmethod,hashvalue,
                                            sequenceno,"",devicedatetime,"",
                                            "","0",frametransactionid,metahash,color,latency);
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
                                int newmaxframes=maxframes+50;
                                updatefindmediainfosyncstatus(mediatoken,""+lastframe,""+remainingframes,config.sync_inprogress);
                            }
                            else
                            {
                                updatefindmediainfosyncstatus(mediatoken,""+lastframe,""+remainingframes,config.sync_complete);
                                sendbroadcastreader();

                                if(xdata.getinstance().getSetting(config.selectedsyncsetting).
                                        equalsIgnoreCase(config.selectedsyncsetting_0))
                                    common.shownotification(applicationviavideocomposer.getactivity());


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

    public int getlastsavedsequence(String parentobjectid)
    {
        int sequenceno=0;
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

        try {
            Cursor cursor=mdbhelper.getallsequencehashfrommetadata(parentobjectid);
            if(cursor != null && cursor.getCount() > 0)
            {
                if (cursor.moveToLast())
                {
                    do{
                        String data = "" + cursor.getString(cursor.getColumnIndex("sequenceno"));
                        try {
                            if(! data.trim().isEmpty())
                                sequenceno=Integer.parseInt(data);
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }while(cursor.moveToNext());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mdbhelper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  sequenceno;
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
        xdata.getinstance().saveSetting(config.ismediadataservicerunning,"0");
        Intent i = new Intent(config.composer_service_getencryptionmetadata);
        sendBroadcast(i);
    }
    //* End of broadcast register


    public  void runxapicounter(String mediafilepath){
        if(xapicounter < framearraylist.size() && xapicounter <= maximumframechecklimit)
        {
            String hashvalue =  framearraylist.get(xapicounter).getkeyvalue();
            xapigetmediainfo(hashvalue,mediafilepath);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        xdata.getinstance().saveSetting(config.ismediadataservicerunning,"0");
    }
}