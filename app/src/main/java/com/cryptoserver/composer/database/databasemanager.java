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


    public void insertframemetricesinfo(String videokey,String metrickeyname,String metrickeyvalue,String selected,String hashvalue,
                                        String framenumber,String metricdata)
    {
        try {
            lock.lock();

            ContentValues values = new ContentValues();
            values.put("videokey", ""+videokey);
            values.put("metrickeyname", ""+metrickeyname);
            values.put("metrickeyvalue", ""+metrickeyvalue);
            values.put("selected", ""+selected);
            values.put("hashvalue", ""+hashvalue);
            values.put("framenumber", ""+framenumber);
            values.put("metricdata", ""+metricdata);

            long l=mDb.insert("tblmetadata", null, values);
            Log.e("Id ",""+l);

        } catch (SQLException mSQLException) {
            Log.e(TAG, "gettestdata >>" + mSQLException.toString());
            throw mSQLException;
        } finally {
            lock.unlock();
        }
    }

    public Cursor fetchallmetadata() {

        Cursor mCur=null;
        try {
            lock.lock();

            String sql = "SELECT * FROM tblmetadata;";
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

}
