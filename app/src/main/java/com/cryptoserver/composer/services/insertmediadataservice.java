package com.cryptoserver.composer.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.cryptoserver.composer.applicationviavideocomposer;
import com.cryptoserver.composer.database.databasemanager;
import com.cryptoserver.composer.models.dbitemcontainer;
import com.cryptoserver.composer.models.videomodel;
import com.cryptoserver.composer.utils.common;
import com.cryptoserver.composer.utils.config;
import com.cryptoserver.composer.utils.ffmpegaudioframegrabber;
import com.cryptoserver.composer.utils.ffmpegvideoframegrabber;
import com.cryptoserver.composer.utils.progressdialog;
import com.cryptoserver.composer.utils.randomstring;
import com.cryptoserver.composer.utils.xdata;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.bytedeco.javacpp.avutil;
import org.bytedeco.javacv.Frame;

import java.io.File;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

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

                Gson gsonobject = new Gson();
                String list1 = xdata.getinstance().getSetting("liststart");
                String list2 = xdata.getinstance().getSetting("listmiddle");

                if(! list1.trim().isEmpty() && (! list2.trim().isEmpty()))
                {
                    Type type = new TypeToken<ArrayList<dbitemcontainer>>(){}.getType();
                    ArrayList<dbitemcontainer> mdbstartitemcontainer = gsonobject.fromJson(list1, type);
                    ArrayList<dbitemcontainer> mdbmiddleitemcontainer = gsonobject.fromJson(list2, type);

                    // ArrayList<dbitemcontainer> mdbstartitemcontainer = (ArrayList<dbitemcontainer>) intent.getSerializableExtra("liststart");
                    //  ArrayList<dbitemcontainer> mdbmiddleitemcontainer = (ArrayList<dbitemcontainer>) intent.getSerializableExtra("listmiddle");
                    String mediapath = xdata.getinstance().getSetting("mediapath");
                    String keytype = xdata.getinstance().getSetting("keytype");

                    //getmediahashes(mediapath,keytype);

                    if(mediapath != null && (! mediapath.isEmpty()))
                    {
                        xdata.getinstance().saveSetting("mediadatainsertion","1");
                        File file=new File(mediapath);
                        if(file.exists())
                        {
                            long currentframenumber=0,frameduration =15;

                            if(! xdata.getinstance().getSetting(config.framecount).trim().isEmpty())
                                frameduration=Integer.parseInt(xdata.getinstance().getSetting(config.framecount));

                            int count = 1,arrayupdator=0;
                            String videokey="",firsthash="";
                            final ArrayList<videomodel> mvideoframes =new ArrayList<>();
                            currentframenumber = currentframenumber + frameduration;
                            try
                            {
                                if(mdbstartitemcontainer.size() > 0)
                                {
                                    ffmpegvideoframegrabber videograbber = null;
                                    ffmpegaudioframegrabber audiograbber = null;
                                    videomodel lastframehash=null;
                                    if(mdbstartitemcontainer.get(0).getItem2().equalsIgnoreCase("video"))
                                    {
                                        videograbber = new ffmpegvideoframegrabber(mediapath);
                                        videograbber.setPixelFormat(avutil.AV_PIX_FMT_RGB24);
                                        String format= common.getvideoformat(mediapath);
                                        if(format.equalsIgnoreCase("mp4"))
                                            videograbber.setFormat(format);

                                        videograbber.start();
                                        for(int i = 0; i<videograbber.getLengthInFrames(); i++){
                                            Frame frame = videograbber.grabImage();
                                            if (frame == null)
                                                break;

                                            ByteBuffer buffer= ((ByteBuffer) frame.image[0].position(0));
                                            byte[] byteData = new byte[buffer.remaining()];
                                            buffer.get(byteData);
                                            String keyValue= common.getkeyvalue(byteData,keytype);
                                            mvideoframes.add(new videomodel("Frame ", keytype, currentframenumber,keyValue));

                                            if(i == 0 && mdbstartitemcontainer != null && mdbstartitemcontainer.size() > 0)
                                            {
                                                String filename = common.getfilename(mediapath);
                                                HashMap<String, String> map = new HashMap<String, String>();
                                                map.put("fps","30");
                                                map.put("firsthash", keyValue);
                                                map.put("hashmethod",keytype);
                                                map.put("name",filename);
                                                map.put("duration","");
                                                map.put("frmaecounts","");
                                                map.put("finalhash","");
                                                Gson gson = new Gson();
                                                String json = gson.toJson(map);

                                                firsthash=keyValue;

                                                videokey=mdbstartitemcontainer.get(0).getItem4();

                                                mdbstartitemcontainer.get(0).setItem1(json);
                                                mdbstartitemcontainer.get(0).setItem3(filename);
                                                mdbstartitemcontainer.get(0).setItem14(firsthash);
                                                updatestartframes(mdbstartitemcontainer);
                                            }

                                            if (count == currentframenumber) {
                                                lastframehash=null;
                                                currentframenumber = currentframenumber + frameduration;

                                                // update 1st duration frame in database
                                                if(count == frameduration)
                                                    firsthash = keyValue;

                                                // update every duration frame in database
                                                if(mdbmiddleitemcontainer.size() > 0 && arrayupdator == mdbmiddleitemcontainer.size()-1)
                                                {
                                                    mdbmiddleitemcontainer.add(mdbmiddleitemcontainer.get(mdbmiddleitemcontainer.size()-1));
                                                }

                                                if(mdbmiddleitemcontainer.size() > 0 && arrayupdator < mdbmiddleitemcontainer.size())
                                                {
                                                    mdbmiddleitemcontainer.get(arrayupdator).setItem4(videokey);
                                                    mdbmiddleitemcontainer.get(arrayupdator).setItem8(keyValue);
                                                    mdbmiddleitemcontainer.get(arrayupdator).setItem9(""+count);
                                                    updatemiddleframes(mdbmiddleitemcontainer.get(arrayupdator));
                                                }
                                                arrayupdator++;
                                            }
                                            else
                                            {
                                                lastframehash=new videomodel("Last Frame ",keytype,count,keyValue);
                                            }
                                            count++;
                                        }
                                        videograbber.flush();
                                    }
                                    else if(mdbstartitemcontainer.get(0).getItem2().equalsIgnoreCase("audio"))
                                    {
                                        audiograbber = new ffmpegaudioframegrabber(mediapath);
                                        audiograbber.start();
                                        for(int i = 0; i<audiograbber.getLengthInAudioFrames(); i++){
                                            Frame frame = audiograbber.grabAudio();
                                            if (frame == null)
                                                break;

                                            ShortBuffer shortbuff= ((ShortBuffer) frame.samples[0].position(0));
                                            java.nio.ByteBuffer bb = java.nio.ByteBuffer.allocate(shortbuff.capacity() * 4);
                                            bb.asShortBuffer().put(shortbuff);

                                            byte[] byteData = bb.array();
                                            String keyValue= common.getkeyvalue(byteData,keytype);
                                            Log.e("md5 ",""+count+" "+keyValue);
                                            mvideoframes.add(new videomodel("Frame ", keytype, currentframenumber,keyValue));

                                            if(i == 10 && mdbstartitemcontainer != null && mdbstartitemcontainer.size() > 0)
                                            {
                                                String filename = common.getfilename(mediapath);
                                                HashMap<String, String> map = new HashMap<String, String>();
                                                map.put("fps","30");
                                                map.put("firsthash", keyValue);
                                                map.put("hashmethod",keytype);
                                                map.put("name",filename);
                                                map.put("duration","");
                                                map.put("frmaecounts","");
                                                map.put("finalhash","");
                                                Gson gson = new Gson();
                                                String json = gson.toJson(map);

                                                firsthash=keyValue;

                                                videokey=mdbstartitemcontainer.get(0).getItem4();

                                                mdbstartitemcontainer.get(0).setItem1(json);
                                                mdbstartitemcontainer.get(0).setItem3(filename);
                                                mdbstartitemcontainer.get(0).setItem14(firsthash);
                                                updatestartframes(mdbstartitemcontainer);
                                            }

                                            if (count == currentframenumber) {
                                                lastframehash=null;
                                                currentframenumber = currentframenumber + frameduration;

                                                // update 1st duration frame in database
                                                if(count == frameduration)
                                                    firsthash = keyValue;

                                                // update every duration frame in database
                                                if(mdbmiddleitemcontainer.size() > 0 && arrayupdator == mdbmiddleitemcontainer.size()-1)
                                                {
                                                    mdbmiddleitemcontainer.add(mdbmiddleitemcontainer.get(mdbmiddleitemcontainer.size()-1));
                                                }

                                                if(mdbmiddleitemcontainer.size() > 0 && arrayupdator < mdbmiddleitemcontainer.size())
                                                {
                                                    mdbmiddleitemcontainer.get(arrayupdator).setItem4(videokey);
                                                    mdbmiddleitemcontainer.get(arrayupdator).setItem8(keyValue);
                                                    mdbmiddleitemcontainer.get(arrayupdator).setItem9(""+count);
                                                    updatemiddleframes(mdbmiddleitemcontainer.get(arrayupdator));
                                                }
                                                arrayupdator++;
                                            }
                                            else
                                            {
                                                lastframehash=new videomodel("Last Frame ",keytype,count,keyValue);
                                            }
                                            count++;
                                        }
                                        audiograbber.flush();
                                    }
                                    else if(mdbstartitemcontainer.get(0).getItem2().equalsIgnoreCase("image"))
                                    {
                                        updatestartframes(mdbstartitemcontainer);
                                        updatemiddleframes(mdbmiddleitemcontainer.get(arrayupdator));

                                        lastframehash=null;
                                        mvideoframes.clear();
                                    }

                                    if(lastframehash != null)
                                    {
                                        mvideoframes.add(lastframehash);
                                        // update every duration frame in database
                                        if(mdbmiddleitemcontainer.size() > 0 && arrayupdator == mdbmiddleitemcontainer.size()-1)
                                        {
                                            mdbmiddleitemcontainer.add(mdbmiddleitemcontainer.get(mdbmiddleitemcontainer.size()-1));
                                        }

                                        if(mdbmiddleitemcontainer.size() > 0 && arrayupdator < mdbmiddleitemcontainer.size())
                                        {
                                            mdbmiddleitemcontainer.get(arrayupdator).setItem4(videokey);
                                            mdbmiddleitemcontainer.get(arrayupdator).setItem8(lastframehash.getkeyvalue());
                                            mdbmiddleitemcontainer.get(arrayupdator).setItem9(""+count);
                                            updatemiddleframes(mdbmiddleitemcontainer.get(arrayupdator));
                                        }
                                    }

                                    if(mvideoframes.size() > 0)
                                    {
                                        String updatecompletedate[] = common.getcurrentdatewithtimezone();
                                        String completeddate = updatecompletedate[0];
                                        String filename = common.getfilename(mediapath);
                                        String lastframe="";
                                        if(mvideoframes.size() > 0)
                                        {
                                            lastframe = mvideoframes.get(mvideoframes.size() -1).getkeyvalue();
                                        }
                                        // update last frame in database
                                        updateendvideoinfo(firsthash,filename,completeddate,lastframe,"" + count,mediapath,keytype,videokey);
                                        mvideoframes.get(mvideoframes.size()-1).settitle("Last Frame ");
                                    }
                                }
                            }catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                xdata.getinstance().saveSetting("mediadatainsertion","0");
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
                mdbhelper.insertstartvideoinfo(mdbstartitemcontainer.get(i).getItem1(),mdbstartitemcontainer.get(i).getItem2()
                        ,mdbstartitemcontainer.get(i).getItem3(),mdbstartitemcontainer.get(i).getItem4(),mdbstartitemcontainer.get(i).getItem5()
                        ,mdbstartitemcontainer.get(i).getItem6(),mdbstartitemcontainer.get(i).getItem7(),mdbstartitemcontainer.get(i).getItem8(),
                        mdbstartitemcontainer.get(i).getItem9(),mdbstartitemcontainer.get(i).getItem10(),mdbstartitemcontainer.get(i).getItem11()
                        ,mdbstartitemcontainer.get(i).getItem12(),mdbstartitemcontainer.get(i).getItem13(),mdbstartitemcontainer.get(i).getItem14()
                        ,"0","sync_pending","","");
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
                    mdbmiddleitemcontainer.getItem15());
            try {
                mdbhelper.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Calling when frames are getting from ffmpegframegrabber
    public void updateendvideoinfo(String updatefirsthash, String file_name,String completeddate,String lastframe,String lastcount,
                                     String videourl,String keytype,String videokey)
    {
        String duration = "";
        if(!videourl.isEmpty())
            duration = common.getvideotimefromurl(videourl);

        try {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("fps","30");
            map.put("firsthash", updatefirsthash);
            map.put("hashmethod",keytype);
            map.put("name",file_name);
            map.put("duration",duration);
            map.put("frmaecounts",lastcount);
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

            mdbhelper.updatestartvideoinfo(json,videokey,completeddate);

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        xdata.getinstance().saveSetting("mediadatainsertion","0");
    }
}