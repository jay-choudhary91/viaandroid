package com.deeptruth.app.android.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.deeptruth.app.android.models.mediainfotablefields;
import com.deeptruth.app.android.models.mediametadatainfo;
import com.deeptruth.app.android.models.metadatahash;
import com.deeptruth.app.android.models.startmediainfo;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.xdata;

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

    public void updatestartmediainfo(mediainfotablefields mediainfo)
    {
        try {
            lock.lock();
            String query="update tblstartmediainfo set header='"+ mediainfo.getHeader() +"',type = '"+mediainfo.getType() +"',localkey='"+ mediainfo.getLocalkey() +"',token = '"+mediainfo.getToken() +"'" +
                ",videokey='"+ mediainfo.getMediakey() +"',sync = '"+mediainfo.getSync() +"',sync_date='"+ mediainfo.getDate() +"',action_type = '"+mediainfo.getAction_type() +"'," +
                "apirequestdevicedate='"+ mediainfo.getApirequestdevicedate() +"',videostartdevicedate = '"+mediainfo.getMediastartdevicedate() +"',devicetimeoffset='"+
                mediainfo.getDevicetimeoffset() +"',videocompletedevicedate = '"+mediainfo.getMediacompletedevicedate() +"',mediaduration= '" +mediainfo.getMediaduration()+"',"+
                "videostarttransactionid='"+ mediainfo.getMediastarttransactionid() +"',firsthash = '"+mediainfo.getFirsthash() +"',videoid='"+ mediainfo.getMediaid() +"'," +
                "status = '"+mediainfo.getStatus() +"',color = '"+mediainfo.getColor() +"',colorreason = '"+mediainfo.getColorreason() +"',completeddate = '"+mediainfo.getMediacompletedate() +"',media_name= '"+mediainfo.getMedianame()+"',"+
                "remainingframes='"+ mediainfo.getRemainingframes() +"',lastframe = '"+mediainfo.getLastframe() +"',framecount='"+ mediainfo.getFramecount() +"'," +
                "media_notes='"+ mediainfo.getMedianotes() +"'," +
                "sync_status = '"+mediainfo.getSyncstatus() +"' where location='"+mediainfo.getLocation()+"'";

            if(mDb == null)
                mDb = mDbHelper.getReadableDatabase();
            mDb.execSQL(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }
    }

    public void updatestartmediainfocomposer(String header,String type,String location,String localkey,
                                             String token,String videokey,String sync,String date , String action_type,
                                             String apirequestdevicedate,String videostartdevicedate,String devicetimeoffset,
                                             String videocompletedevicedate,String videostarttransactionid,String firsthash,String thumbnailpath,String videoid,
                                             String status,String remainingframes,String lastframe,String framecount,String sync_status,String medianame,
                                             String medianotes,String mediafolder,String color,String colorreason)
    {
        try {
            lock.lock();

            int index =  location.lastIndexOf('.');
            if(index >=0)
                medianame = location.substring(0, location.lastIndexOf('.'));

            String framegrabcompleted="0";
            if(type.equalsIgnoreCase("image"))
                framegrabcompleted="1";

            String query="update tblstartmediainfo set header='"+ header +"',type = '"+type +"',localkey='"+ localkey +"',token = '"+token +"'" +
                    ",videokey='"+ videokey +"',sync = '"+sync +"',sync_date='"+ date +"',action_type = '"+action_type +"'," +
                    "apirequestdevicedate='"+ apirequestdevicedate +"',videostartdevicedate = '"+videostartdevicedate +"',devicetimeoffset='"+
                    devicetimeoffset +"',videocompletedevicedate = '"+videocompletedevicedate +"'," +
                    "videostarttransactionid='"+ videostarttransactionid +"',firsthash = '"+firsthash +"',videoid='"+ videoid +"'," +
                    "status = '"+status +"'," +
                    "color = '"+color +"'," +"colorreason = '"+colorreason +"'," +
                    "framegrabcompleted = '"+framegrabcompleted +"'," +
                    "media_notes = '"+medianotes +"'," +
                    "thumbnailurl = '"+thumbnailpath +"'," +
                    "media_folder = '"+mediafolder +"'," +
                    "completeddate = 0," +
                    "remainingframes='"+ remainingframes +"',lastframe = '"+lastframe +"',framecount='"+ framecount +"'," +
                    "sync_status = '"+sync_status +"' where location='"+location+"'";
            if(mDb == null)
                mDb = mDbHelper.getReadableDatabase();
            mDb.execSQL(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }
    }

    public void insertstartvideoinfo(mediainfotablefields mediainfo)
    {
        try {
            lock.lock();

            ContentValues values = new ContentValues();
            values.put("header", "" + mediainfo.getHeader());
            values.put("type", ""+mediainfo.getType());
            values.put("location", mediainfo.getLocation());
            values.put("localkey", ""+mediainfo.getLocalkey());
            values.put("token", ""+mediainfo.getToken());
            values.put("videokey", ""+mediainfo.getMediakey());
            values.put("sync", ""+mediainfo.getSync());
            values.put("action_type", ""+mediainfo.getAction_type());
            values.put("sync_date",  mediainfo.getDate());
            values.put("apirequestdevicedate",  mediainfo.getApirequestdevicedate());
            values.put("videostartdevicedate",  mediainfo.getMediastartdevicedate());
            values.put("devicetimeoffset",  mediainfo.getDevicetimeoffset());
            values.put("videocompletedevicedate",  mediainfo.getMediacompletedevicedate());
            values.put("videostarttransactionid",  mediainfo.getMediastarttransactionid());
            values.put("videoid",""+mediainfo.getMediaid());
            values.put("status",""+mediainfo.getStatus());
            values.put("remainingframes",""+mediainfo.getRemainingframes());
            values.put("lastframe",""+mediainfo.getLastframe());
            values.put("firsthash",""+mediainfo.getFirsthash());
            values.put("framecount",""+mediainfo.getFramecount());
            values.put("sync_status",""+mediainfo.getSyncstatus());
            values.put("completeddate","0");
            values.put("media_name",  mediainfo.getMedianame());
            values.put("mediafilepath",  mediainfo.getMediafilepath());
            values.put("media_notes",  mediainfo.getMedianotes());
            values.put("media_folder",  mediainfo.getMediafolder());
            values.put("thumbnailurl",  mediainfo.getThumbnailurl());
            values.put("mediaduration",  mediainfo.getMediaduration());

            if(mDb == null)
                mDb = mDbHelper.getReadableDatabase();
            long l=mDb.insert("tblstartmediainfo", null, values);
            Log.e("Id ",""+l);

        } catch (SQLException mSQLException) {
            Log.e(TAG, "gettestdata >>" + mSQLException.toString());
            throw mSQLException;
        } finally {
            lock.unlock();
        }
    }

    public void insertstartvideoinfo(String header,String type,String location,String localkey,
                                     String token,String videokey,String sync,String date , String action_type,
                                     String apirequestdevicedate,String videostartdevicedate,String devicetimeoffset,
                                     String videocompletedevicedate,String videostarttransactionid,String firsthash,String videoid,
                                     String status,String remainingframes,String lastframe,String framecount,String sync_status,String medianame,
                                     String medianotes,String mediafolder,String mediaduration)
    {
        try {
            lock.lock();

            String framegrabcompleted="0";
            if(type.equalsIgnoreCase("image"))
                framegrabcompleted="1";

            String mediafilapth=location;
            location= common.getfilename(location);

            int index =  location.lastIndexOf('.');
            if(index >=0)
                medianame = location.substring(0, location.lastIndexOf('.'));

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
            values.put("completeddate","0");
            values.put("media_name",  medianame);
            values.put("mediafilepath",  mediafilapth);
            values.put("media_notes",  medianotes);
            values.put("media_folder",  mediafolder);
            values.put("thumbnailurl",  mediafilapth);
            values.put("mediaduration",  mediaduration);
            values.put("framegrabcompleted",  framegrabcompleted);

            if(mDb == null)
                mDb = mDbHelper.getReadableDatabase();
            long l=mDb.insert("tblstartmediainfo", null, values);
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
                                        String completehashvalue,String sequenceid,String videostarttransactionid,String metahash,
                                        String color,String latency,String colorreason)
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
            values.put("color",  color);
            values.put("latency",  latency);
            values.put("colorreason",  colorreason);


            if(mDb == null)
                mDb = mDbHelper.getReadableDatabase();
            long l=mDb.insert("tblmetadata", null, values);
            Log.e("Id ",""+l);

        } catch (SQLException mSQLException) {
            Log.e(TAG, "gettestdata >>" + mSQLException.toString());
            throw mSQLException;
        } finally {
            lock.unlock();
        }
    }

    public Cursor updatestartvideoinfo(String header , String localkey) {
        Cursor mCur=null;
        try {
            lock.lock();
            if(mDb == null)
                mDb = mDbHelper.getReadableDatabase();
            mDb.execSQL("update tblstartmediainfo set header='"+ header +"',framegrabcompleted = 1 where localkey='"+localkey+"'");;

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

            String sql = "SELECT * FROM tblmetadata where localkey = '"+finallocalkey+"'   AND rsequenceno = 0 LIMIT 50" ;
            //String sql = "SELECT * FROM tblmetadata";
            if(mDb == null)
                mDb = mDbHelper.getReadableDatabase();
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

    public Cursor fetchunsyncedmetaframe(String localkey) {

        Cursor mCur=null;
        try {
            lock.lock();

            String sql = "SELECT * FROM tblmetadata where sequenceid IS NULL OR sequenceid = '' AND localkey = '"+localkey+"'" ;
            if(mDb == null)
                mDb = mDbHelper.getReadableDatabase();
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

    public Cursor fetchmetacompletedata(int value,String finallocalkey) {

        Cursor mCur=null;
        String sql="";
        try {
            lock.lock();
            if(value==1){
                 sql = "SELECT * FROM tblmetadata where rsequenceno != 0 AND localkey = '"+finallocalkey+"'" ;
            }else{
                 sql = "SELECT * FROM tblmetadata where rsequenceno == 0 AND localkey = '"+finallocalkey+"'" ;
            }
            //String sql = "SELECT * FROM tblmetadata";
            if(mDb == null)
                mDb = mDbHelper.getReadableDatabase();
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

    public ArrayList<mediametadatainfo> setmediametadatainfo(String finallocalkey)
    {
        ArrayList<mediametadatainfo> mediametadatainfosarray = new ArrayList<>();
        Cursor cur = fetchmetadata(finallocalkey);
        if (cur != null && cur.moveToFirst() && cur.getCount() > 0)
        {
            do{
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
            }while(cur.moveToNext());
        }
        return mediametadatainfosarray;
    }

    public Cursor updatecompletehashvalue(String localkey,String completehashvalue) {
        Cursor mCur=null;
        try {
            lock.lock();
            if(mDb == null)
                mDb = mDbHelper.getReadableDatabase();
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

    public Cursor getallmetadatabylocalkey(String localkey)
    {
        Cursor mCur=null;
        try {
            lock.lock();

            String sql = "SELECT * FROM tblmetadata where localkey = '"+localkey+"'";
            //String sql = "SELECT * FROM tblmetadata";
            if(mDb == null)
                mDb = mDbHelper.getReadableDatabase();
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

    public Cursor updatevideoupdateapiresponse(String videoid, String sequence, String serverdate, String serverdictionaryhash,
                                               String sequenceid, String videoframetransactionid,String color,String latency,
                                               String mediahashvalue,String colorreason) {
        Cursor mCur=null;
        try {
            lock.lock();
            if(mDb == null)
                mDb = mDbHelper.getReadableDatabase();
            mDb.execSQL("update tblmetadata set rsequenceno = '"+sequence+
                    "',serverdate ='"+serverdate+
                    "',color ='"+color+
                    "',colorreason ='"+colorreason+
                    "',latency ='"+latency+
                    "', serverdictionaryhash = '"+serverdictionaryhash+
                    "', sequenceid = '"+sequenceid+"' , videostarttransactionid = '"+videoframetransactionid+"' where sequencehash='"+mediahashvalue+"'");
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

    public Cursor fetchunsyncedmediainfo() {

        Cursor mCur=null;
        try {
            lock.lock();

            String sql = "SELECT * FROM tblstartmediainfo where sync_date = 0 AND framegrabcompleted = 1 LIMIT 1";
            //String sql = "SELECT * FROM tblmetadata";
            if(mDb == null)
                mDb = mDbHelper.getReadableDatabase();
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

    public Cursor fetchcurrentlyprocessmedias() {

        Cursor mCur=null;
        try {
            lock.lock();

            String sql = "SELECT * FROM tblstartmediainfo where sync_date = 0 AND framegrabcompleted = 0 LIMIT 1";
            //String sql = "SELECT * FROM tblmetadata";
            if(mDb == null)
                mDb = mDbHelper.getReadableDatabase();
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
            Cursor cur = fetchunsyncedmediainfo();
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
                    String  media_name = "" + cur.getString(cur.getColumnIndex("media_name"));
                    String  action_type = "" + cur.getString(cur.getColumnIndex("action_type"));
                    String  sync_date = ""+ cur.getString(cur.getColumnIndex("sync_date"));
                    String  apirequestdevicedate = ""+ cur.getString(cur.getColumnIndex("apirequestdevicedate"));
                    String  videostartdevicedate = ""+ cur.getString(cur.getColumnIndex("videostartdevicedate"));
                    String  devicetimeoffset = ""+ cur.getString(cur.getColumnIndex("devicetimeoffset"));
                    String  videocompletedevicedate = ""+ cur.getString(cur.getColumnIndex("videocompletedevicedate"));
                    String  mediafilepath = ""+ cur.getString(cur.getColumnIndex("mediafilepath"));

                    marray.add(new startmediainfo(selectedid, header, type, location,localkey,token, videokey,
                            sync, action_type,sync_date,apirequestdevicedate,videostartdevicedate,
                            devicetimeoffset,videocompletedevicedate,mediafilepath,media_name));
                }while(cur.moveToNext());
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        return marray;
    }

    public Cursor updatevideokeytoken(String videoid,String videokey,String token , String transactionid) {
        Cursor mCur=null;
        try {
            lock.lock();
            if(mDb == null)
                mDb = mDbHelper.getReadableDatabase();
            mDb.execSQL("update tblstartmediainfo set videokey='"+videokey+"', token='"+ token+"' , videostarttransactionid='"+transactionid+"'where id='"+videoid+"'");
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


    public Cursor updatevideosyncdate(String localkey,String syncdate,String syncstatus,String color,String mediaid,String colorreason) {
        Cursor mCur=null;
        try {
            lock.lock();
            if(mDb == null)
                mDb = mDbHelper.getReadableDatabase();
            mDb.execSQL("update tblstartmediainfo set sync_date = '"+syncdate+
                    "',sync_status ='"+syncstatus+
                    "',color ='"+color+
                    "',colorreason ='"+colorreason+
                    "',videoid ='"+mediaid+
                    "' where localkey='"+localkey+"'");
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

    public Cursor updatemediaattempt(String location,String totalattempt,String status) {
        Cursor mCur=null;
        try {
            lock.lock();
            if(mDb == null)
                mDb = mDbHelper.getReadableDatabase();

            String color="";
            if(status.equalsIgnoreCase(config.sync_notfound))
                color="red";

            if(color.trim().isEmpty())
            {
                mDb.execSQL("update tblstartmediainfo set status = '"+status+
                        "',media_sync_attempt ='"+totalattempt+
                        "' where location='"+location+"'");
            }
            else
            {
                mDb.execSQL("update tblstartmediainfo set status = '"+status+
                        "',media_sync_attempt ='"+totalattempt+
                        "',color ='"+color+
                        "' where location='"+location+"'");
            }


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
            if(mDb == null)
                mDb = mDbHelper.getReadableDatabase();
            mDb.execSQL("update tblstartmediainfo set sync='"+sync+"' where id='"+selectedid+"'");
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

    public Cursor updatepublishmediastatus(String mediatoken) {
        Cursor mCur=null;
        try {
            lock.lock();
            if(mDb == null)
                mDb = mDbHelper.getReadableDatabase();
            mDb.execSQL("update tblstartmediainfo set mediapublished=1  where token='"+mediatoken+"'");
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

    public Cursor updatefilemediafolderdir(String sourcefile,String destinationfile,String destinationfolder) {
        Cursor mCur=null;
        try {
            String mediafilepath=destinationfile;
            sourcefile=common.getfilename(sourcefile);
            lock.lock();
            if(mDb == null)
                mDb = mDbHelper.getReadableDatabase();
            if(sourcefile.contains(".mp4") || sourcefile.contains(".jpg") || sourcefile.contains(".png") || sourcefile.contains(".jpeg"))
            {
                String query="update tblstartmediainfo set media_folder = '"+destinationfolder+
                        "',mediafilepath ='"+mediafilepath+
                        "',thumbnailurl = '"+mediafilepath +
                        "' where location='"+sourcefile+"'";
                mDb.execSQL(query);
            }
            else
            {
                mDb.execSQL("update tblstartmediainfo set media_folder = '"+destinationfolder+
                    "',mediafilepath ='"+mediafilepath+
                    "' where location='"+sourcefile+"'");
            }

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

    public ArrayList<metadatahash> getmediametadatabylocalkey(String localkey) {
        ArrayList<metadatahash> mitemlist=new ArrayList<>();
        if(! localkey.trim().isEmpty())
        {
            Cursor cur=null;
            try {
                lock.lock();
                String sql = "SELECT * FROM tblmetadata where localkey = '"+localkey+"'";
                if(mDb == null)
                    mDb = mDbHelper.getReadableDatabase();
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
                                cur.getString(cur.getColumnIndex("videostarttransactionid")),cur.getString(cur.getColumnIndex("serverdictionaryhash")),
                                cur.getString(cur.getColumnIndex("metahash")),cur.getString(cur.getColumnIndex("color")),
                                cur.getString(cur.getColumnIndex("latency")),cur.getString(cur.getColumnIndex("colorreason"))));

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
                if(mDb == null)
                    mDb = mDbHelper.getReadableDatabase();
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
                                cur.getString(cur.getColumnIndex("videostarttransactionid")),cur.getString(cur.getColumnIndex("serverdictionaryhash")),
                                cur.getString(cur.getColumnIndex("metahash")),cur.getString(cur.getColumnIndex("color")),
                                cur.getString(cur.getColumnIndex("latency")),cur.getString(cur.getColumnIndex("colorreason"))));

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

    public Cursor getallmediabyfolder(String selectedfolder) {
        Cursor cur=null;
        try {
            lock.lock();
            String sqlquery="";
            if(selectedfolder.equalsIgnoreCase(config.dirallmedia))
            {
                sqlquery = "SELECT * FROM tblstartmediainfo order by id DESC";
            }
            else
            {
                sqlquery = "SELECT * FROM tblstartmediainfo where media_folder= '"+selectedfolder+"' order by  id DESC";
            }

            if(mDb == null)
                mDb = mDbHelper.getReadableDatabase();
            cur = mDb.rawQuery(sqlquery, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }
        return cur;
    }

    public Cursor getallmediastartdata() {
        Cursor cur=null;
        try {
            lock.lock();
            String selectedfolder= xdata.getinstance().getSetting(config.selected_folder);
            String sqlquery="";
            if(selectedfolder.equalsIgnoreCase(config.dirallmedia))
            {
                sqlquery = "SELECT * FROM tblstartmediainfo order by id DESC";
            }
            else
            {
                sqlquery = "SELECT * FROM tblstartmediainfo where media_folder= '"+selectedfolder+"' order by  id DESC";
            }

            if(mDb == null)
                mDb = mDbHelper.getReadableDatabase();
            cur = mDb.rawQuery(sqlquery, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }
        return cur;
    }


    public Cursor fetchallmediastartinfo() {
        Cursor cur=null;
        try {
            lock.lock();
            String sqlquery = "SELECT * FROM tblstartmediainfo order by id DESC";

            if(mDb == null)
                mDb = mDbHelper.getReadableDatabase();
            cur = mDb.rawQuery(sqlquery, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }
        return cur;
    }


    public String getlocalkeybyfilename(String location) {
        String localkey="";
        Cursor cur=null;
        try {
            lock.lock();
            String sql = "SELECT * FROM tblstartmediainfo where location = '"+location+"'";
            if(mDb == null)
                mDb = mDbHelper.getReadableDatabase();
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

    public Cursor getstartmediainfo(String filename){
        Cursor cur=null;
        try {
            lock.lock();
            String sql = "SELECT * FROM tblstartmediainfo where location = '"+filename+"'";
            if(mDb == null)
                mDb = mDbHelper.getReadableDatabase();
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

    public void updateaudiothumbnail(String filename,String thumbnailurl) {
            try {
            lock.lock();
            if(mDb == null)
                mDb = mDbHelper.getReadableDatabase();

           // String file= common.getfilename(thumbnailurl);
            String query="update tblstartmediainfo set thumbnailurl ='"+thumbnailurl+"' where location='"+filename+"'";
            Log.e("Query ",query);
            mDb.execSQL(query);

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }
    }

    public void updatemedialocationname(String starttransactionid,String location) {
        try {
            lock.lock();
            if(mDb == null)
                mDb = mDbHelper.getReadableDatabase();
            mDb.execSQL("update tblstartmediainfo set location ='"+location+"' where videostarttransactionid='"+starttransactionid+"'");

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }
    }


    public void updatemediainfobylocalkey(String localkey, String location, String medianotes) {
        try {
            lock.lock();
            if(mDb == null)
                mDb = mDbHelper.getReadableDatabase();
            mDb.execSQL("update tblstartmediainfo set media_name ='"+location+"', media_notes ='"+medianotes+"' where localkey='"+localkey+"'");

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }
    }

    public void updatemediainfobymediakey(String mediakey, String medianame, String medianotes) {
        try {
            lock.lock();
            if(mDb == null)
                mDb = mDbHelper.getReadableDatabase();
            mDb.execSQL("update tblstartmediainfo set media_name ='"+medianame+"', media_notes ='"+medianotes+"' where videokey='"+mediakey+"'");

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }
    }

    public void deletefrommetadatabylocation(String location) {
        try {
            location=common.getfilename(location);
            lock.lock();
            if(mDb == null)
                mDb = mDbHelper.getReadableDatabase();
            mDb.execSQL("delete from tblstartmediainfo where location='"+location+"'");
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
            if(mDb == null)
                mDb = mDbHelper.getReadableDatabase();
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
            if(mDb == null)
                mDb = mDbHelper.getReadableDatabase();
            mDb.execSQL("delete from tblstartmediainfo where localkey='"+localkey+"'");
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }
    }

    public Cursor getmediainfobyfilename(String filename) {
        String videotoken="";
        Cursor cur=null;
        try {
            lock.lock();
            String sql = "SELECT * FROM tblstartmediainfo where location = '"+filename+"'";
            if(mDb == null)
                mDb = mDbHelper.getReadableDatabase();
            cur = mDb.rawQuery(sql, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }
        return  cur;
    }

    public Cursor getallsequencehashfrommetadata(String objectparentid) {
        String videotoken="";
        Cursor cur=null;
        try {
            lock.lock();
            String sql = "SELECT * FROM tblmetadata where localkey = '"+objectparentid+"'";
            if(mDb == null)
                mDb = mDbHelper.getReadableDatabase();

            cur = mDb.rawQuery(sql, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }
        return  cur;
    }

    public Cursor getmediacolor(String localkey) {
        String videotoken="";
        Cursor cur=null;
        try {
            lock.lock();
            String sql = "SELECT color,sequenceno,metricdata FROM tblmetadata where localkey = '"+localkey+"'";
            if(mDb == null)
                mDb = mDbHelper.getReadableDatabase();

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
            String sql="update tblstartmediainfo set lastframe = '"+lastframe+"',status ='"+syncstatus+"',remainingframes = '"+remainingframes+"' where token='"+videotoken+"'";
            Log.e("Sql tblstartmediainfo ",sql);
            if(mDb == null)
                mDb = mDbHelper.getReadableDatabase();
            mDb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }
    }
}
