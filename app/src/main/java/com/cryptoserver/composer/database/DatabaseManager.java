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


public class DatabaseManager {
    protected static final String TAG = "DatabaseManager";

    private final Context mContext;
    private SQLiteDatabase mDb;
    private DataBaseHelper mDbHelper;


    private ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private Lock lock = rwl.writeLock();

    public DatabaseManager(Context context) {
        this.mContext = context;
        mDbHelper = new DataBaseHelper(mContext);
    }

    public DatabaseManager createDatabase() throws SQLException {
        try {
            mDbHelper.createDataBase();
        } catch (IOException mIOException) {
            Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase");
            throw new Error("UnableToCreateDatabase");
        }
        return this;
    }

    public DatabaseManager open() throws SQLException {
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


    public void insertLog(String datetime, String logitem, String logvalue, String timestamp,String selected)
    {
        try {
            lock.lock();

            ContentValues values = new ContentValues();
            values.put("datetime", ""+datetime);
            values.put("logitem", ""+logitem);
            values.put("logvalue", ""+logvalue);
            values.put("timestamp", ""+timestamp);
            values.put("selected", ""+selected);

            long l=mDb.insert("tbllog", null, values);
            long a=l;

        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        } finally {
            lock.unlock();
        }
    }


    public Cursor fetchAllLogData() {

        Cursor mCur=null;
        try {
            lock.lock();

            String sql = "SELECT * FROM tbllog;";
            mCur = mDb.rawQuery(sql, null);
            if (mCur != null) {
                mCur.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return  mCur;
    }

    public Cursor fetchLogData(String user_id,String to_user_id,int limit) {

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
