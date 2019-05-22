package com.deeptruth.app.android.utils;

import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by root on 3/5/18.
 */

public class md5
{
    private static final String tag = "md5";

        public static boolean checkmd5(String md5, File updatefile) {
            if (TextUtils.isEmpty(md5) || updatefile == null) {
                Log.e(tag, "md5 string empty or updateFile null");
                return false;
            }

            String calculateddigest = calculatemd5(updatefile);
            if (calculateddigest == null) {
                Log.e(tag, "calculatedDigest null");
                return false;
            }

            Log.v(tag, "Calculated digest: " + calculateddigest);
            Log.v(tag, "Provided digest: " + md5);

            return calculateddigest.equalsIgnoreCase(md5);
        }

        public static String calculatemd5(File updateFile) {
            MessageDigest digest;
            try {
                digest = MessageDigest.getInstance("md5");
            } catch (NoSuchAlgorithmException e) {
                Log.e(tag, "Exception while getting digest", e);
                return null;
            }

            InputStream is;
            try {
                is = new FileInputStream(updateFile);
            } catch (FileNotFoundException e) {
                Log.e(tag, "Exception while getting FileInputStream", e);
                return null;
            }

            byte[] buffer = new byte[8192];
            int read;
            try {
                while ((read = is.read(buffer)) > 0) {
                    digest.update(buffer, 0, read);
                }
                byte[] md5sum = digest.digest();
                BigInteger bigint = new BigInteger(1, md5sum);
                String output = bigint.toString(16);
                // Fill to 32 chars
                output = String.format("%32s", output).replace(' ', '0');
                return output;
            } catch (IOException e) {
                throw new RuntimeException("Unable to process file for md5", e);
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    Log.e(tag, "Exception on closing md5 input stream", e);
                }
            }
        }

    public static String fileToMD5(String filePath) {
        String md5hash="";
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath);
            byte[] buffer = new byte[1024];
            MessageDigest digest = MessageDigest.getInstance("MD5");
            int numRead = 0;
            while (numRead != -1) {
                numRead = inputStream.read(buffer);
                if (numRead > 0)
                    digest.update(buffer, 0, numRead);
            }
            byte [] md5Bytes = digest.digest();
            md5hash= convertHashToString(md5Bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) { }
            }
        }
        if(! md5hash.trim().isEmpty())
            return md5hash;

        return fileToMD5(filePath);
    }

    private static String convertHashToString(byte[] md5Bytes) {
        String returnVal = "";
        for (int i = 0; i < md5Bytes.length; i++) {
            returnVal += Integer.toString(( md5Bytes[i] & 0xff ) + 0x100, 16).substring(1);
        }
        return returnVal;
    }
    public static String calculatebytemd5(byte[] array) {
        String md5 = "";
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("md5");
            digest.reset();
            digest.update(array);
            byte[] a = digest.digest();
            int len = a.length;
            StringBuilder sb = new StringBuilder(len << 1);
            for (int i = 0; i < len; i++) {
                sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));
                sb.append(Character.forDigit(a[i] & 0x0f, 16));
            }
            md5 = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5;
    }

    public static final String calculatestringtomd5(final String s) {
        final String md5 = "md5";
        try {
            // Create md5 Hash
            MessageDigest digest = MessageDigest
                    .getInstance(md5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}

