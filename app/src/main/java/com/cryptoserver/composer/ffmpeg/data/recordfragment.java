package com.cryptoserver.composer.ffmpeg.data;

/**
 * Created by wanglei02 on 2016/1/22.
 */
public class recordfragment {
    private long starttimestamp;
    private long endtimestamp;

    public void setstarttimestamp(long starttimestamp) {
        this.starttimestamp = starttimestamp;
    }

    public long getstarttimestamp() {
        return starttimestamp;
    }

    public void setendtimestamp(long endtimestamp) {
        this.endtimestamp = endtimestamp;
    }

    public long getendtimestamp() {
        return endtimestamp;
    }

    public long getduration() {
        return endtimestamp - starttimestamp;
    }
}
