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
import com.cryptoserver.composer.utils.ffmpegvideoframegrabber;
import com.cryptoserver.composer.utils.randomstring;
import com.cryptoserver.composer.utils.xdata;
import com.google.gson.Gson;

import org.bytedeco.javacpp.avutil;
import org.bytedeco.javacv.Frame;

import java.nio.ByteBuffer;
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
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        ArrayList<dbitemcontainer> mdbstartitemcontainer = (ArrayList<dbitemcontainer>) intent.getSerializableExtra("liststart");
        ArrayList<dbitemcontainer> mdbmiddleitemcontainer = (ArrayList<dbitemcontainer>) intent.getSerializableExtra("listmiddle");
        String mediapath = intent.getExtras().getString("mediapath");
        String keytype = intent.getExtras().getString("keytype");

        //getmediahashes(mediapath,keytype);

        long currentframenumber=0,frameduration =15;

        if(! xdata.getinstance().getSetting(config.framecount).trim().isEmpty())
            frameduration=Integer.parseInt(xdata.getinstance().getSetting(config.framecount));

        int count = 1;
        String videokey="",firsthash="";
        final ArrayList<videomodel> mvideoframes =new ArrayList<>();
        currentframenumber = currentframenumber + frameduration;
        try
        {
            ffmpegvideoframegrabber grabber = new ffmpegvideoframegrabber(mediapath);
            grabber.setPixelFormat(avutil.AV_PIX_FMT_RGB24);
            String format= common.getvideoformat(mediapath);
            if(format.equalsIgnoreCase("mp4"))
                grabber.setFormat(format);

            grabber.start();
            videomodel lastframehash=null;
            for(int i = 0; i<grabber.getLengthInFrames(); i++){
                Frame frame = grabber.grabImage();
                if (frame == null)
                    break;

                ByteBuffer buffer= ((ByteBuffer) frame.image[0].position(0));
                byte[] byteData = new byte[buffer.remaining()];
                buffer.get(byteData);
                String keyValue= common.getkeyvalue(byteData,keytype);

                // update 1st frame into database
                if(videokey.trim().isEmpty() && mdbstartitemcontainer != null && mdbstartitemcontainer.size() > 0)
                {
                    randomstring gen = new randomstring(20, ThreadLocalRandom.current());
                    videokey=gen.nextString();

                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("fps","30");
                    map.put("firsthash", keyValue);
                    map.put("hashmethod",keytype);
                    map.put("name","");
                    map.put("duration","");
                    map.put("frmaecounts","");
                    map.put("finalhash","");
                    Gson gson = new Gson();
                    String json = gson.toJson(map);

                    mdbstartitemcontainer.get(0).setItem1(json);
                    mdbstartitemcontainer.get(0).setItem4(videokey);
                    updatestartframes(mdbstartitemcontainer);
                }

                if (count == currentframenumber) {
                    lastframehash=null;
                    mvideoframes.add(new videomodel("Frame ", keytype, currentframenumber,keyValue));
                    currentframenumber = currentframenumber + frameduration;

                    // update 1st duration frame in database
                    if(count == frameduration)
                        firsthash = keyValue;

                    // update every duration frame in database
                    for(int j=0;j<mdbmiddleitemcontainer.size();j++)
                    {
                        if(mdbmiddleitemcontainer.get(j).getItem8().isEmpty())
                        {
                            mdbmiddleitemcontainer.get(j).setItem4(videokey);
                            mdbmiddleitemcontainer.get(j).setItem8(keyValue);
                            updatemiddleframes(mdbmiddleitemcontainer.get(j));
                            Log.e("final frames ",""+ mdbmiddleitemcontainer.get(j).getItem8());
                            break;
                        }
                    }
                }
                else
                {
                    lastframehash=new videomodel("Last Frame ",keytype,count,keyValue);
                }
                count++;
            }

            if(lastframehash != null)
                mvideoframes.add(lastframehash);

            if(mvideoframes.size() > 1){
                String updatecompletedate[] = common.getcurrentdatewithtimezone();
                String completeddate = updatecompletedate[0];
                String filename = common.getfilename(mediapath);
                String lastframe = mvideoframes.get(mvideoframes.size() -1).getkeyvalue();

                // update last frame in database
                updateendvideoinfo(firsthash,filename,completeddate,lastframe,"" + count,mediapath,keytype,videokey);
                mvideoframes.get(mvideoframes.size()-1).settitle("Last Frame ");
            }
            grabber.flush();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        /**/
        return START_NOT_STICKY;
    }


    // Calling when frames are getting from ffmpegframegrabber
    /*public void updatestartvideoinfo(String updatefirsthash, String file_name,String completeddate,String lastframe,String lastcount,String videourl)
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
            mdbenditemcontainer.add(new dbitemcontainer(json,videokey,completeddate));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

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
                        ,mdbstartitemcontainer.get(i).getItem12(),mdbstartitemcontainer.get(i).getItem13());
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
    }

    public void getmediahashes(String mediapath,String keytype) {


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}