package com.cryptoserver.composer.metadata;

import com.coremedia.iso.IsoFile;
import com.cryptoserver.composer.utils.Path;
import com.googlecode.mp4parser.boxes.apple.AppleAlbumBox;
import com.googlecode.mp4parser.boxes.apple.AppleDescriptionBox;
import com.googlecode.mp4parser.boxes.apple.AppleNameBox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Change metadata and make sure chunkoffsets are corrected.
 */
public class metadataread {


    /*public static void main(String[] args) throws IOException {
        metadataread cmd = new metadataread();
        String xml = cmd.read("C:\\content\\Mobile_H264.mp4");
        System.err.println(xml);
    }*/

    public static String readmetadata(String videoFilePath) throws IOException {

        File videoFile = new File(videoFilePath);
        if (!videoFile.exists()) {
            throw new FileNotFoundException("File " + videoFilePath + " not exists");
        }

        if (!videoFile.canRead()) {
            throw new IllegalStateException("No read permissions to file " + videoFilePath);
        }
        String xml="";
        try {
            IsoFile isoFile = new IsoFile(videoFilePath);
            AppleAlbumBox nam = Path.getPath(isoFile, "/moov[0]/udta[0]/meta[0]/ilst/©nam");
            xml = nam.getValue();
            isoFile.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        return xml;
    }
}
