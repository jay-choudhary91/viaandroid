package com.deeptruth.app.android.utils;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.dropbox.core.DbxException;
import com.dropbox.core.util.IOUtil;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.WriteMode;
import com.dropbox.core.v2.sharing.DbxUserSharingRequests;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Async task to upload a file to a directory
 */
public class uploadfileatdropbox extends AsyncTask<String, Void, FileMetadata> {

    private final Context mContext;
    public final DbxClientV2 mDbxClient;
    public final Callback mCallback;
    public Exception mException;

    public interface Callback {
        void onUploadComplete(FileMetadata result);
        void onError(Exception e);
        void onProgress(int percentagecompleted);
    }

    public uploadfileatdropbox(Context context, DbxClientV2 dbxClient, Callback callback) {
        mContext = context;
        mDbxClient = dbxClient;
        mCallback = callback;
    }

    @Override
    public void onPostExecute(FileMetadata result) {
        super.onPostExecute(result);
        if (mException != null) {
            mCallback.onError(mException);
        } else if (result == null) {
            mCallback.onError(null);
        } else {
            mCallback.onUploadComplete(result);
        }
    }

    @Override
    public FileMetadata doInBackground(String... params) {
        File localFile=new File(params[0]);

        if (localFile != null) {
            //String remoteFolderPath = params[1];

            // Note - this is not ensuring the name is a valid dropbox file name
            String remoteFileName = localFile.getName();

            try (InputStream inputStream = new FileInputStream(localFile)) {
                publishProgress();
                return mDbxClient.files().uploadBuilder("/"+remoteFileName)
                        .withMode(WriteMode.OVERWRITE)
                        .uploadAndFinish(inputStream, new IOUtil.ProgressListener() {
                            @Override
                            public void onProgress(long bytesWritten) {
                                long totalbytes=localFile.length()+config.tail.length();;
                                double progresspercentage = (bytesWritten * 100) / totalbytes;
                                if(mCallback != null)
                                    mCallback.onProgress((int)progresspercentage);
                            }
                        });
            } catch (Exception e) {
                mException = e;
            }
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
