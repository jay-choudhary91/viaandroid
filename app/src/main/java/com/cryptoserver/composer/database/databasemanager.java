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
                                     String apirequestdevicedate,String videostartdevicedate,String devicetimeoffset,
                                     String videocompletedevicedate,String videostarttransactionid,String firsthash,String videoid,
                                     String status,String remainingframes,String lastframe,String framecount,String sync_status)
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
            values.put("videoid",""+videoid);
            values.put("status",""+status);
            values.put("remainingframes",""+remainingframes);
            values.put("lastframe",""+lastframe);
            values.put("firsthash",""+firsthash);
            values.put("framecount",""+framecount);
            values.put("sync_status",""+sync_status);

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
                                        String completehashvalue,String sequenceid,String videostarttransactionid,String metahash)
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
            values.put("metahash",  metahash);

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

            mDb.execSQL("update tbstartvideoinfo set header='"+ header +"',videocompletedevicedate = '"+
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


    public ArrayList<startmediainfo> fetchunsynceddata(){

        ArrayList<startmediainfo> marray=new ArrayList<>();

        try {
            Cursor cur = fatchstartvideoinfo();
            if (cur != null && cur.moveToFirst())
            {
                do{
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
                }while(cur.moveToNext());
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
            mDb.execSQL("update tbstartvideoinfo set videokey='"+videokey+"', token='"+ token+"' , videostarttransactionid='"+transactionid+"'where id='"+videoid+"'");
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


    public Cursor updatevideosyncdate(String localkey,String syncdate,String syncstatus) {
        Cursor mCur=null;
        try {
            lock.lock();
            mDb.execSQL("update tbstartvideoinfo set sync_date ='"+syncdate+"',sync_status ='"+syncstatus+"' where localkey='"+localkey+"'");
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


    public ArrayList<metadatahash> getmediametadata(String firsthash) {
        ArrayList<metadatahash> mitemlist=new ArrayList<>();
        String localkey=getlocalkeybyfirsthash(firsthash);
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
                                cur.getString(cur.getColumnIndex("videostarttransactionid")),cur.getString(cur.getColumnIndex("serverdictionaryhash")), cur.getString(cur.getColumnIndex("metahash"))));
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

    public ArrayList<metadatahash> getmediametadatabyfilename(String filename) {
        ArrayList<metadatahash> mitemlist=new ArrayList<>();
        String localkey=getlocalkeybyfilename(filename);
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
                                cur.getString(cur.getColumnIndex("videostarttransactionid")),cur.getString(cur.getColumnIndex("serverdictionaryhash")), cur.getString(cur.getColumnIndex("metahash"))));
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

    public String[] getlocalkeybylocation(String filename) {
        String[] localkey ={"",""};
        Cursor cur=null;
        try {
            lock.lock();
            String sql = "SELECT  sync_status, videostarttransactionid FROM tbstartvideoinfo where location = '"+filename+"'";
            cur = mDb.rawQuery(sql, null);
            if (cur.moveToFirst())
            {
                do{
                    localkey[0] = "" + cur.getString(cur.getColumnIndex("sync_status"));
                    localkey[1] = "" + cur.getString(cur.getColumnIndex("videostarttransactionid"));
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

    public String[] getreaderlocalkeybylocation(String filename) {
        String[] localkey ={"",""};
        Cursor cur=null;
        try {
            lock.lock();
            String sql = "SELECT  status, videostarttransactionid FROM tbstartvideoinfo where location = '"+filename+"'";
            cur = mDb.rawQuery(sql, null);
            if (cur.moveToFirst())
            {
                do{
                    localkey[0] = "" + cur.getString(cur.getColumnIndex("status"));
                    localkey[1] = "" + cur.getString(cur.getColumnIndex("videostarttransactionid"));
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

    public String getlocalkeybyfirsthash(String firsthash) {
        String localkey="";
        Cursor cur=null;
        try {
            lock.lock();
            String sql = "SELECT localkey FROM tbstartvideoinfo where firsthash = '"+firsthash+"'";
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

    public String getlocalkeybyfilename(String location) {
        String localkey="";
        Cursor cur=null;
        try {
            lock.lock();
            String sql = "SELECT localkey FROM tbstartvideoinfo where location = '"+location+"'";
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

    public String getcompletedate(String firsthash){

        String completedevicedate = "";
        Cursor cur=null;
        try {
            lock.lock();
            String sql = "SELECT videocompletedevicedate FROM tbstartvideoinfo where firsthash = '"+firsthash+"'";
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

    public Cursor getstartmediainfo(String filename){
        Cursor cur=null;
        try {
            lock.lock();
            String sql = "SELECT * FROM tbstartvideoinfo where location = '"+filename+"'";
            cur = mDb.rawQuery(sql, null);
            return cur;
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }
        return null;
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


    //* call from reader. Checking sync status of requested media.

    public Cursor getmediainfobyfirsthash(String firsthash) {
        String videotoken="";
        Cursor cur=null;
        try {
            lock.lock();
            String sql = "SELECT * FROM tbstartvideoinfo where firsthash = '"+firsthash+"'";
            cur = mDb.rawQuery(sql, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }
        return  cur;
    }

    public Cursor getmediainfobyfilename(String filename) {
        String videotoken="";
        Cursor cur=null;
        try {
            lock.lock();
            String sql = "SELECT * FROM tbstartvideoinfo where location = '"+filename+"'";
            cur = mDb.rawQuery(sql, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }
        return  cur;
    }


    public Cursor readallmetabyvideoid(String objectparentid) {
        String videotoken="";
        Cursor cur=null;
        try {
            lock.lock();
            String sql = "SELECT * FROM tblmetadata where localkey = '"+objectparentid+"'";
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
    public void updatefindmediainfosyncstatus(String videotoken,String lastframe,String remainingframes,String syncstatus) {
        Cursor cur=null;
        try {
            lock.lock();
            String sql="update tbstartvideoinfo set lastframe = '"+lastframe+"',status ='"+syncstatus+"',remainingframes = '"+remainingframes+"' where token='"+videotoken+"'";
            Log.e("Sql tbstartvideoinfo ",sql);
            mDb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }
    }
}
