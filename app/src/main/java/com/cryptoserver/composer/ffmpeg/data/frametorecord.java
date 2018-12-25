package com.cryptoserver.composer.ffmpeg.data;

import org.bytedeco.javacv.Frame;

/**
 * Created by wanglei02 on 2016/1/21.
 */
public class frametorecord {
    private long timestamp;
    private Frame frame;

    public frametorecord(long timestamp, Frame frame) {
        this.timestamp = timestamp;
        this.frame = frame;
    }

    public long gettimestamp() {
        return timestamp;
    }

    public void settimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Frame getframe() {
        return frame;
    }

    public void setframe(Frame frame) {
        this.frame = frame;
    }
}
