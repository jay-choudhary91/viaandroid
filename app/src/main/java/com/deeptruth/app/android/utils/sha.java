package com.deeptruth.app.android.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by root on 13/6/18.
 */

public class sha
{
    public static String sha1(final String text) {
        try {
            MessageDigest md;
            md = MessageDigest.getInstance("sha-1");
            md.update(text.getBytes("UTF-8"),
                    0, text.length());
            byte[] sha1hash = md.digest();

            return tohex(sha1hash);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String sha1(byte[] data) {
        try {
            MessageDigest md;
            md = MessageDigest.getInstance("sha-1");
            md.update(data);
            byte[] sha1hash = md.digest();

            return tohex(sha1hash);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String tohex(final byte[] buf) {
        if (buf == null) return "";

        int l = buf.length;
        StringBuffer result = new StringBuffer(2 * l);

        for (int i = 0; i < buf.length; i++) {
            appendhex(result, buf[i]);
        }
        return result.toString();
    }

    private final static String hex = "0123456789ABCDEF";

    private static void appendhex(final StringBuffer sb, final byte b) {
        sb.append(hex.charAt((b >> 4) & 0x0f))
                .append(hex.charAt(b & 0x0f));
    }
}
