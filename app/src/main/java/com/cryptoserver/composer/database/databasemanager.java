package com.cryptoserver.composer.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.IOException;
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


    public void insertframemetricesinfo(String blockchain,String valuehash,String hashmethod,String localkey,
                                        String metricdata,String recordate,String rsequenceno,String sequencehash,
                                        String sequenceno,String serverdate,String sequencedevicedate,String serverdictionaryhash)
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

            long l=mDb.insert("tblmetadata", null, values);
            Log.e("Id ",""+l);

        } catch (SQLException mSQLException) {
            Log.e(TAG, "gettestdata >>" + mSQLException.toString());
            throw mSQLException;
        } finally {
            lock.unlock();
        }
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

    public Cursor updatesyncstatus(String id) {
        Cursor mCur=null;
        try {
            lock.lock();
            mDb.execSQL("update tblmetadata set hassync=1 where id='"+id+"'");
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


    public Cursor fetchlogdata(String user_id, String to_user_id, int limit) {

        Cursor mCur=null;
        try {
            lock.lock();

            String sql = "SELECT * FROM tbllog where user_id='" + user_id + "' and to_user_id ='" + to_user_id +
                    "' or user_id ='" + to_user_id + "' and to_user_id ='" + user_id +"' ORDER BY date_time desc LIMIT "+limit+";";
            mCur = mDb.rawQuery(sql, null);
            if (mCur != null) {
                mCur.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return  mCur;
    }

    public Cursor updatevideoupdateapiresponce(String videoid,String sequence,String serverdate,String serverdictionaryhash) {
        Cursor mCur=null;
        try {
            lock.lock();
            mDb.execSQL("update tblmetadata set rsequenceno = '"+sequence+"',serverdate ='"+serverdate+"', serverdictionaryhash = '"+serverdictionaryhash+"' where id='"+videoid+"'");
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
                                     String apirequestdevicedate,String videostartdevicedate,String devicetimeoffset)
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

    public Cursor updatevideokeytoken(String videoid,String videokey,String token) {
        Cursor mCur=null;
        try {
            lock.lock();
            mDb.execSQL("update tbstartvideoinfo set videokey='"+videokey+"', token='"+token+"' where id='"+videoid+"'");
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


    public Cursor updatestartvideoinfo(String header , String id ) {
        Cursor mCur=null;
        try {
            lock.lock();
            mDb.execSQL("update tbstartvideoinfo set header='"+ header +"' where localkey='"+id+"'");;

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

}
