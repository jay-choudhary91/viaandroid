package com.cryptoserver.composer.utils;

import java.io.IOException;
import java.util.Date;
import java.util.Random;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;


/**
 * Created by root on 13/6/18.
 */

public class salt
{
    private static Random random = new Random((new Date()).getTime());
    /**
     * Encrypts the string along with salt
     * @param value
     * @return
     * @throws Exception
     */
    public static String encrypt(String value) {
        BASE64Encoder encoder = new BASE64Encoder();

        // let's create some dummy salt
        byte[] salt = new byte[8];
        random.nextBytes(salt);
        return encoder.encode(salt)+
                encoder.encode(value.getBytes());
    }


    /**
     * Decrypts the string and removes the salt
     * @param encryptKey
     * @return
     * @throws Exception
     */
    public static String decrypt(String encryptKey) {
        // let's ignore the salt
        if (encryptKey.length() > 12) {
            String cipher = encryptKey.substring(12);
            BASE64Decoder decoder = new BASE64Decoder();
            try {
                return new String(decoder.decodeBuffer(cipher));
            } catch (IOException e) {
                //  throw new InvalidImplementationException(
                //    "Failed to perform decryption for key ["+encryptKey+"]",e);
            }
        }
        return null;
    }
}
