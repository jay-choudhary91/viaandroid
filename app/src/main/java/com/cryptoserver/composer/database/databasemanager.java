package com.cryptoserver.composer.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cryptoserver.composer.models.mediametadatainfo;
import com.cryptoserver.composer.models.metadatahash;
import com.cryptoserver.composer.models.startmediainfo;
import com.cryptoserver.composer.utils.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class databasemanager {
    protected static final String TAG = "databasemanager";

    private final Context mContext;
    private SQLiteDatabase mDb;
    private databasehelper mDbHelper;


    private ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private Lock lock = rwl.writeLock();

    public databasemanager(Context context) {
        this.mContext = context;
        mDbHelper = new databasehelper(mContext);
    }

    public databasemanager createDatabase() throws SQLException {
        try {
            mDbHelper.createDataBase();
        } catch (IOException mIOException) {
            Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase");
            throw new Error("UnableToCreateDatabase");
        }
        return this;
    }

    public databasemanager open() throws SQLException {
        try {
            mDbHelper.openDataBase();
            mDbHelper.close();
            mDb = mDbHelper.getReadableDatabase();
        } catch (SQLException mSQLException) {
            Log.e(TAG, "open >>" + mSQLException.toString());
            throw mSQLException;
        }
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public void insertstartvideoinfo(String header,String type,String location,String localkey,
                                     String token,String videokey,String sync,String date , String action_type,
                                     String apirequestdevicedate,String videostartdevicedate,String devicetimeoffset,String videocompletedevicedate)
    {
        try {
            lock.lock();

            ContentValues values = new ContentValues();
            values.put("header", "" + header);
            values.put("type", ""+type);
            values.put("location", location);
            values.put("localkey", ""+localkey);
            values.put("token", ""+token);
            values.put("videokey", ""+videokey);
            values.put("sync", ""+sync);
            values.put("action_type", ""+action_type);
            values.put("sync_date",  date);
            values.put("apirequestdevicedate",  apirequestdevicedate);
            values.put("videostartdevicedate",  videostartdevicedate);
            values.put("devicetimeoffset",  devicetimeoffset);
            values.put("videocompletedevicedate",  videocompletedevicedate);

            long l=mDb.insert("tbstartvideoinfo", null, values);
            Log.e("Id ",""+l);

        } catch (SQLException mSQLException) {
            Log.e(TAG, "gettestdata >>" + mSQLException.toString());
            throw mSQLException;
        } finally {
            lock.unlock();
        }
    }

    public void insertframemetricesinfo(String blockchain,String valuehash,String hashmethod,String localkey,
                                        String metricdata,String recordate,String rsequenceno,String sequencehash,
                                        String sequenceno,String serverdate,String sequencedevicedate,String serverdictionaryhash,
                                        String completehashvalue,String sequenceid,String videostarttransactionid)
    {
        try {
            lock.lock();

            ContentValues values = new ContentValues();
            values.put("blockchain", ""+blockchain);
            values.put("valuehash", ""+valuehash);
            values.put("hashmethod", hashmethod);
            values.put("localkey", ""+localkey);
            values.put("metricdata", ""+metricdata);
            values.put("recordate", ""+recordate);
            values.put("rsequenceno", ""+rsequenceno);
            values.put("sequencehash", ""+sequencehash);
            values.put("sequenceno", ""+sequenceno);
            values.put("serverdate", ""+serverdate);
            values.put("sequencedevicedate", ""+sequencedevicedate);
            values.put("serverdictionaryhash", ""+serverdictionaryhash);
            values.put("completehashvalue", ""+completehashvalue);
            values.put("sequenceid", ""+sequenceid);
            values.put("videostarttransactionid",  videostarttransactionid);

            long l=mDb.insert("tblmetadata", null, values);
            Log.e("Id ",""+l);

        } catch (SQLException mSQLException) {
            Log.e(TAG, "gettestdata >>" + mSQLException.toString());
            throw mSQLException;
        } finally {
            lock.unlock();
        }
    }

    public Cursor updatestartvideoinfo(String header , String localkey ,String completedate) {
        Cursor mCur=null;
        try {
            lock.lock();

            mDb.execSQL("update tbstartvideoinfo set header='"+ header +"' , videocompletedevicedate = '"+
                    completedate +"' where localkey='"+localkey+"'");;

            if (mCur != null)
                mCur.moveToNext();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }
        return  mCur;
    }

    public Cursor fetchmetadata(String finallocalkey) {

        Cursor mCur=null;
        try {
            lock.lock();

            String sql = "SELECT * FROM tblmetadata where localkey = '"+finallocalkey+"'   AND rsequenceno = 0 " ;

            //String sql = "SELECT * FROM tblmetadata";
            mCur = mDb.rawQuery(sql, null);
            if (mCur != null) {
                mCur.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }

        return  mCur;
    }

    /*public ArrayList<metadatahash> getlocalkeyfrommedia(String frame) {
        ArrayList<metadatahash> mitemlist=new ArrayList<>();
        Cursor cur=null;
        try {
            lock.lock();
            String sql = "SELECT * FROM tblgetvideoinfo where frames = '"+frame;
            cur = mDb.rawQuery(sql, null);
            if (cur != null) {
                String id = "" + cur.getString(cur.getColumnIndex("id"));
                String video_info = "" + cur.getString(cur.getColumnIndex("video_info"));
                String frames = "" + cur.getString(cur.getColumnIndex("frames"));

                mitemlist.add(new metadatahash(video_info,frames));
                cur.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }
        return  mitemlist;
    }*/


    public ArrayList<mediametadatainfo> setmediametadatainfo(String finallocalkey) {


        ArrayList<mediametadatainfo> mediametadatainfosarray = new ArrayList<>();

        Cursor cur = fetchmetadata(finallocalkey);

        if (cur != null && cur.getCount() > 0) {

                String selectedid = "" + cur.getString(cur.getColumnIndex("id"));
                String blockchain = "" + cur.getString(cur.getColumnIndex("blockchain"));
                String valuehash = "" + cur.getString(cur.getColumnIndex("valuehash"));
                String hashmethod = "" + cur.getString(cur.getColumnIndex("hashmethod"));
                String localkey = "" + cur.getString(cur.getColumnIndex("localkey"));
                String metricdata = "" + cur.getString(cur.getColumnIndex("metricdata"));
                String recordate = "" + cur.getString(cur.getColumnIndex("recordate"));
                String rsequenceno = "" + cur.getString(cur.getColumnIndex("rsequenceno"));
                String sequencehash = "" + cur.getString(cur.getColumnIndex("sequencehash"));
                String sequenceno = "" + cur.getString(cur.getColumnIndex("sequenceno"));
                String serverdate = "" + cur.getString(cur.getColumnIndex("serverdate"));
                String sequencedevicedate = "" + cur.getString(cur.getColumnIndex("sequencedevicedate"));

            mediametadatainfosarray.add(new mediametadatainfo(selectedid,blockchain,valuehash,hashmethod,localkey,
                    metricdata,recordate,rsequenceno,sequencehash,sequenceno,serverdate,sequencedevicedate));

        }
        return mediametadatainfosarray;
    }

    public Cursor updatevideokey(String videoid,String videokey) {
        Cursor mCur=null;
        try {
            lock.lock();
            mDb.execSQL("update tblmetadata set videokey='"+videokey+"' where videoid='"+videoid+"'");
            if (mCur != null)
                mCur.moveToNext();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }
        return  mCur;
    }


    public Cursor updatecompletehashvalue(String localkey,String completehashvalue) {
        Cursor mCur=null;
        try {
            lock.lock();
            mDb.execSQL("update tblmetadata set completehashvalue='"+completehashvalue+"' where localkey='"+localkey+"'");
            if (mCur != null)
                mCur.moveToNext();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }
        return  mCur;
    }

    public Cursor deletemetadata(String id) {

        Cursor mCur=null;
        try {
            lock.lock();

            // String sql = "SELECT * FROM tblmetadata;";
            //  mCur = mDb.rawQuery(sql, null);

            mDb.execSQL("delete from tblmetadata where id='"+id+"'");

            if (mCur != null) {
                mCur.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }

        return  mCur;
    }

    public Cursor getlocalkeycount(String localkey)
    {
        Cursor mCur=null;
        try {
            lock.lock();

            String sql = "SELECT * FROM tblmetadata where localkey = '"+localkey;
            //String sql = "SELECT * FROM tblmetadata";
            mCur = mDb.rawQuery(sql, null);
            if (mCur != null) {
                mCur.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }

        return  mCur;
    }

    public Cursor updatevideoupdateapiresponse(String videoid, String sequence, String serverdate, String serverdictionaryhash, String sequenceid, String videoframetransactionid) {
        Cursor mCur=null;
        try {
            lock.lock();
            mDb.execSQL("update tblmetadata set rsequenceno = '"+sequence+"',serverdate ='"+serverdate+"', serverdictionaryhash = '"+serverdictionaryhash+"', sequenceid = '"+sequenceid+"' , videostarttransactionid = '"+videoframetransactionid+"' where id='"+videoid+"'");
            if (mCur != null)
                mCur.moveToNext();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }
        return  mCur;
    }

    public void insertstartvideoinfo(String header,String type,String location,String localkey,
                                      String token,String videokey,String sync,String date , String action_type,
                                     String apirequestdevicedate,String videostartdevicedate,String devicetimeoffset,String videocompletedevicedate,String videostarttransactionid)
    {
        try {
            lock.lock();

            ContentValues values = new ContentValues();
            values.put("header", "" + header);
            values.put("type", ""+type);
            values.put("location", location);
            values.put("localkey", ""+localkey);
            values.put("token", ""+token);
            values.put("videokey", ""+videokey);
            values.put("sync", ""+sync);
            values.put("action_type", ""+action_type);
            values.put("sync_date",  date);
            values.put("apirequestdevicedate",  apirequestdevicedate);
            values.put("videostartdevicedate",  videostartdevicedate);
            values.put("devicetimeoffset",  devicetimeoffset);
            values.put("videocompletedevicedate",  videocompletedevicedate);
            values.put("videostarttransactionid",  videostarttransactionid);

            long l=mDb.insert("tbstartvideoinfo", null, values);
            Log.e("Id ",""+l);

        } catch (SQLException mSQLException) {
            Log.e(TAG, "gettestdata >>" + mSQLException.toString());
            throw mSQLException;
        } finally {
            lock.unlock();
        }
    }

    public Cursor fatchstartvideoinfo() {

        Cursor mCur=null;
        try {
            lock.lock();

            String sql = "SELECT * FROM tbstartvideoinfo where sync_date = 0 ";
            //String sql = "SELECT * FROM tblmetadata";
            mCur = mDb.rawQuery(sql, null);
            if (mCur != null) {
                mCur.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }

        return  mCur;
    }


    public ArrayList<startmediainfo> setmediainfo(){

        ArrayList<startmediainfo> marray=new ArrayList<>();

        try {
            Cursor cur = fatchstartvideoinfo();

            if (cur != null) {
              {

                  String  selectedid= "" + cur.getString(cur.getColumnIndex("id"));
                  String  header = "" + cur.getString(cur.getColumnIndex("header"));
                  String  type = "" + cur.getString(cur.getColumnIndex("type"));
                  String  location = "" + cur.getString(cur.getColumnIndex("location"));
                  String  localkey = "" + cur.getString(cur.getColumnIndex("localkey"));
                  String  token = "" + cur.getString(cur.getColumnIndex("token"));
                  String  videokey = "" + cur.getString(cur.getColumnIndex("videokey"));
                  String  sync = "" + cur.getString(cur.getColumnIndex("sync"));
                  String  action_type = "" + cur.getString(cur.getColumnIndex("action_type"));
                  String  sync_date = ""+ cur.getString(cur.getColumnIndex("sync_date"));
                  String  apirequestdevicedate = ""+ cur.getString(cur.getColumnIndex("apirequestdevicedate"));
                  String  videostartdevicedate = ""+ cur.getString(cur.getColumnIndex("videostartdevicedate"));
                  String  devicetimeoffset = ""+ cur.getString(cur.getColumnIndex("devicetimeoffset"));
                  String  videocompletedevicedate = ""+ cur.getString(cur.getColumnIndex("videocompletedevicedate"));

                    marray.add(new startmediainfo(selectedid, header, type, location,localkey,token, videokey,
                            sync, action_type,sync_date,apirequestdevicedate,videostartdevicedate,
                            devicetimeoffset,videocompletedevicedate));

                  Log.e("Marray =", "" + marray);
                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        return marray;
    }

    public Cursor fatchstartvideokey(String id) {

        Cursor mCur=null;
        try {
            lock.lock();

            String sql = "SELECT * FROM tbstartvideoinfo where sync_date = 0 ";
            //String sql = "SELECT * FROM tblmetadata";
            mCur = mDb.rawQuery(sql, null);
            if (mCur != null) {
                mCur.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }

        return  mCur;
    }

    public Cursor updatevideokeytoken(String videoid,String videokey,String token , String transactionid) {
        Cursor mCur=null;
        try {
            lock.lock();
            mDb.execSQL("update tbstartvideoinfo set videokey='"+videokey+"', token='"+token+"' , videostarttransactionid='"+transactionid+"'where id='"+videoid+"'");
            if (mCur != null)
                mCur.moveToNext();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }
        return  mCur;
    }


    public Cursor updatevideosyncdate(String localkey,String syncdate) {
        Cursor mCur=null;
        try {
            lock.lock();
            mDb.execSQL("update tbstartvideoinfo set sync_date ='"+syncdate+"' where localkey='"+localkey+"'");
            if (mCur != null)
                mCur.moveToNext();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }
        return  mCur;
    }

    public Cursor updatesyncvalue(String sync,String selectedid) {
        Cursor mCur=null;
        try {
            lock.lock();
            mDb.execSQL("update tbstartvideoinfo set sync='"+sync+"' where id='"+selectedid+"'");
            if (mCur != null)
                mCur.moveToNext();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }
        return  mCur;
    }

    public void insertreadergetvideoinfo(String framecount,String videotoken,String video,String remainingframes,
                                     String frames)
    {
        try {
            lock.lock();

            ContentValues values = new ContentValues();
            values.put("framecount", "" + framecount);
            values.put("videotoken", ""+videotoken);
            values.put("video_info", video);
            values.put("remainingframes", ""+remainingframes);
            values.put("frames", ""+frames);

            long l=mDb.insert("tblgetvideoinfo", null, values);
            Log.e("Id ",""+l);

        } catch (SQLException mSQLException) {
            Log.e(TAG, "gettestdata >>" + mSQLException.toString());
            throw mSQLException;
        } finally {
            lock.unlock();
        }
    }

    //=========================  Methods for reader implementation


    public ArrayList<metadatahash> getmediametadata(String filelocation) {
        ArrayList<metadatahash> mitemlist=new ArrayList<>();
        String localkey=getlocalkeybylocation(filelocation);
        if(! localkey.trim().isEmpty())
        {
            Cursor cur=null;
            try {
                lock.lock();
                String sql = "SELECT * FROM tblmetadata where localkey = '"+localkey+"'";
                cur = mDb.rawQuery(sql, null);
                if (cur.moveToFirst())
                {
                    do{
                        mitemlist.add(new metadatahash(cur.getString(cur.getColumnIndex("id")),cur.getString(cur.getColumnIndex("blockchain")),
                                cur.getString(cur.getColumnIndex("valuehash")),cur.getString(cur.getColumnIndex("hashmethod")),
                                cur.getString(cur.getColumnIndex("localkey")),cur.getString(cur.getColumnIndex("recordate")),
                                cur.getString(cur.getColumnIndex("metricdata")),cur.getString(cur.getColumnIndex("rsequenceno")),
                                cur.getString(cur.getColumnIndex("sequencehash")),cur.getString(cur.getColumnIndex("sequenceno")),
                                cur.getString(cur.getColumnIndex("serverdate")),cur.getString(cur.getColumnIndex("sequencedevicedate")),
                                cur.getString(cur.getColumnIndex("videostarttransactionid"))));
                    }while(cur.moveToNext());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                lock.unlock();
            }
        }
        return  mitemlist;
    }

    public String getlocalkeybylocation(String filename) {
        String localkey="";
        Cursor cur=null;
        try {
            lock.lock();
            String sql = "SELECT localkey FROM tbstartvideoinfo where location = '"+filename+"'";
            cur = mDb.rawQuery(sql, null);
            if (cur.moveToFirst())
            {
                do{
                    localkey = "" + cur.getString(cur.getColumnIndex("localkey"));
                }while(cur.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }
        return  localkey;
    }

    public String getcompletedate(String filename){

        String completedevicedate = "";
        Cursor cur=null;
        try {
            lock.lock();
            String sql = "SELECT videocompletedevicedate FROM tbstartvideoinfo where location = '"+filename+"'";
            cur = mDb.rawQuery(sql, null);
            if (cur.moveToFirst())
            {
                do{
                    completedevicedate = "" + cur.getString(cur.getColumnIndex("videocompletedevicedate"));
                }while(cur.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }
        return  completedevicedate;
    }

    public void updatemedialocationname(String localkey,String location) {
        try {
            lock.lock();
            mDb.execSQL("update tbstartvideoinfo set location ='"+location+"' where localkey='"+localkey+"'");

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }
    }

    public void deletefrommetadatabylocalkey(String localkey) {
        try {
            lock.lock();
            mDb.execSQL("delete from tblmetadata where localkey='"+localkey+"'");
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }
    }

    public void deletefromstartvideoinfobylocalkey(String localkey) {
        try {
            lock.lock();
            mDb.execSQL("delete from tbstartvideoinfo where localkey='"+localkey+"'");
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }
    }

    public void insertfindmediainfo(String videoid,String videokey,String videohashmethod,String videohashvalue,
                                     String videostartdevicedatetime,String videostarttransactionid,String videodevicetimeoffset,String videotypeid , String videopublickey,
                                     String videocreateddate,String videorank,String videotypeshortname,String framecount,String videotoken,
                                    String status,String medianame,boolean isvideofound)
    {
        try {
            lock.lock();

            ContentValues values = new ContentValues();
            values.put("videoid", "" + videoid);
            values.put("videokey", ""+videokey);
            values.put("videohashmethod", videohashmethod);
            values.put("videohashvalue", ""+videohashvalue);
            values.put("videostartdevicedatetime", ""+videostartdevicedatetime);
            values.put("videostarttransactionid", ""+videostarttransactionid);
            values.put("videodevicetimeoffset", ""+videodevicetimeoffset);
            values.put("videopublickey", ""+videopublickey);
            values.put("videotypeid",  videotypeid);
            values.put("videocreateddate",  videocreateddate);
            values.put("videorank",  videorank);
            values.put("videotypeshortname",  videotypeshortname);
            values.put("framecount",  framecount);
            values.put("videotoken",  videotoken);
            values.put("status", status);
            values.put("medianame", medianame);
            values.put("isvideofound", isvideofound);

            long l=mDb.insert("tblfindmediainfo", null, values);
            Log.e("Id ",""+l);

        } catch (SQLException mSQLException) {
            Log.e(TAG, "gettestdata >>" + mSQLException.toString());
            throw mSQLException;
        } finally {
            lock.unlock();
        }
    }

    public void insertvideoframedata(String videoframeid,String objectid,String videoframenumber,String videoframehashvalue,String videoframehashmethod,
             String videoframemeta,String videoframemetahash,String videoframemetahashmethod,String videoframedevicedatetime,String videoframetransactionid
            ,String videoid,String videokey,String  videoduration,String videohashmethod,String videohashvalue,String videostartdevicedatetime,String videocompletedevicedatetime
            ,String videocompleteddate,String videoframecount,String videostarttransactionid,String videocompletetransactionid,String videostartmeta
            ,String videodevicetimeoffset,String videoprivatekey,String videopublickey,String sequenceno,String meta,String hashvalue,String hashmethod
            ,String metahash,String metahashmethod)
    {
        try {
            lock.lock();

            ContentValues values = new ContentValues();
            values.put("videoframeid", videoframeid);
            values.put("objectid", objectid);
            values.put("videoframenumber", videoframenumber);
            values.put("videoframehashvalue", videoframehashvalue);
            values.put("videoframehashmethod", videoframehashmethod);
            values.put("videoframemeta", videoframemeta);
            values.put("videoframemetahash", videoframemetahash);
            values.put("videoframemetahashmethod", videoframemetahashmethod);
            values.put("videoframedevicedatetime", videoframedevicedatetime);
            values.put("videoframetransactionid", videoframetransactionid);
            values.put("videoid", videoid);
            values.put("videokey", videokey);
            values.put("videoduration", videoduration);
            values.put("videohashmethod", videohashmethod);
            values.put("videohashvalue", videohashvalue);
            values.put("videostartdevicedatetime", videostartdevicedatetime);
            values.put("videocompletedevicedatetime", videocompletedevicedatetime);
            values.put("videocompleteddate", videocompleteddate);
            values.put("videoframecount", videoframecount);
            values.put("videostarttransactionid", videostarttransactionid);
            values.put("videocompletetransactionid", videocompletetransactionid);
            values.put("videostartmeta", videostartmeta);
            values.put("videodevicetimeoffset", videodevicetimeoffset);
            values.put("videoprivatekey", videoprivatekey);
            values.put("videopublickey", videopublickey);
            values.put("sequenceno", sequenceno);
            values.put("meta", meta);
            values.put("hashvalue", hashvalue);
            values.put("hashmethod", hashmethod);
            values.put("metahash", metahash);
            values.put("metahashmethod", metahashmethod);

            long l=mDb.insert("tblreadermetadata", null, values);
            Log.e("Id ",""+l);

        } catch (SQLException mSQLException) {
            Log.e(TAG, "gettestdata >>" + mSQLException.toString());
            throw mSQLException;
        } finally {
            lock.unlock();
        }
    }

    //* call from reader. Checking sync status of requested media.

    public Cursor getsyncstatusbymedianame(String filename) {
        String videotoken="";
        Cursor cur=null;
        try {
            lock.lock();
            String sql = "SELECT * FROM tblfindmediainfo where medianame = '"+filename+"'";
            cur = mDb.rawQuery(sql, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }
        return  cur;
    }

    //* call from reader. Update staus from sync_pending to sync_complete.
    public void updatefindmediainfosyncstatus(String filename,boolean isvideofound) {
        Cursor cur=null;
        try {
            lock.lock();
            mDb.execSQL("update tblfindmediainfo set status ='"+config.sync_complete+"' where medianame='"+filename+"'");
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }
    }
}
