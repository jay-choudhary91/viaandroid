package com.cryptoserver.composer.utils;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacpp.avformat;
import org.bytedeco.javacpp.avutil;
import org.bytedeco.javacpp.swresample;
import org.bytedeco.javacpp.swscale;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegLockCallback;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import static org.bytedeco.javacpp.avcodec.AV_PKT_FLAG_KEY;
import static org.bytedeco.javacpp.avcodec.av_packet_unref;
import static org.bytedeco.javacpp.avcodec.avcodec_alloc_context3;
import static org.bytedeco.javacpp.avcodec.avcodec_decode_audio4;
import static org.bytedeco.javacpp.avcodec.avcodec_decode_video2;
import static org.bytedeco.javacpp.avcodec.avcodec_find_decoder;
import static org.bytedeco.javacpp.avcodec.avcodec_flush_buffers;
import static org.bytedeco.javacpp.avcodec.avcodec_free_context;
import static org.bytedeco.javacpp.avcodec.avcodec_open2;
import static org.bytedeco.javacpp.avcodec.avcodec_parameters_to_context;
import static org.bytedeco.javacpp.avcodec.avcodec_register_all;
import static org.bytedeco.javacpp.avdevice.avdevice_register_all;
import static org.bytedeco.javacpp.avformat.AVSEEK_FLAG_BACKWARD;
import static org.bytedeco.javacpp.avformat.av_dump_format;
import static org.bytedeco.javacpp.avformat.av_find_input_format;
import static org.bytedeco.javacpp.avformat.av_guess_sample_aspect_ratio;
import static org.bytedeco.javacpp.avformat.av_read_frame;
import static org.bytedeco.javacpp.avformat.av_register_all;
import static org.bytedeco.javacpp.avformat.avformat_alloc_context;
import static org.bytedeco.javacpp.avformat.avformat_close_input;
import static org.bytedeco.javacpp.avformat.avformat_find_stream_info;
import static org.bytedeco.javacpp.avformat.avformat_free_context;
import static org.bytedeco.javacpp.avformat.avformat_network_init;
import static org.bytedeco.javacpp.avformat.avformat_open_input;
import static org.bytedeco.javacpp.avformat.avformat_seek_file;
import static org.bytedeco.javacpp.avformat.avio_alloc_context;
import static org.bytedeco.javacpp.avutil.AVMEDIA_TYPE_AUDIO;
import static org.bytedeco.javacpp.avutil.AVMEDIA_TYPE_VIDEO;
import static org.bytedeco.javacpp.avutil.AV_LOG_INFO;
import static org.bytedeco.javacpp.avutil.AV_NOPTS_VALUE;
import static org.bytedeco.javacpp.avutil.AV_PICTURE_TYPE_I;
import static org.bytedeco.javacpp.avutil.AV_PIX_FMT_BGR24;
import static org.bytedeco.javacpp.avutil.AV_PIX_FMT_GRAY8;
import static org.bytedeco.javacpp.avutil.AV_PIX_FMT_NONE;
import static org.bytedeco.javacpp.avutil.AV_SAMPLE_FMT_DBL;
import static org.bytedeco.javacpp.avutil.AV_SAMPLE_FMT_DBLP;
import static org.bytedeco.javacpp.avutil.AV_SAMPLE_FMT_FLT;
import static org.bytedeco.javacpp.avutil.AV_SAMPLE_FMT_FLTP;
import static org.bytedeco.javacpp.avutil.AV_SAMPLE_FMT_NONE;
import static org.bytedeco.javacpp.avutil.AV_SAMPLE_FMT_S16;
import static org.bytedeco.javacpp.avutil.AV_SAMPLE_FMT_S16P;
import static org.bytedeco.javacpp.avutil.AV_SAMPLE_FMT_S32;
import static org.bytedeco.javacpp.avutil.AV_SAMPLE_FMT_S32P;
import static org.bytedeco.javacpp.avutil.AV_SAMPLE_FMT_U8;
import static org.bytedeco.javacpp.avutil.AV_SAMPLE_FMT_U8P;
import static org.bytedeco.javacpp.avutil.AV_TIME_BASE;
import static org.bytedeco.javacpp.avutil.av_d2q;
import static org.bytedeco.javacpp.avutil.av_dict_free;
import static org.bytedeco.javacpp.avutil.av_dict_get;
import static org.bytedeco.javacpp.avutil.av_dict_set;
import static org.bytedeco.javacpp.avutil.av_frame_alloc;
import static org.bytedeco.javacpp.avutil.av_frame_free;
import static org.bytedeco.javacpp.avutil.av_frame_get_best_effort_timestamp;
import static org.bytedeco.javacpp.avutil.av_frame_unref;
import static org.bytedeco.javacpp.avutil.av_free;
import static org.bytedeco.javacpp.avutil.av_get_bytes_per_sample;
import static org.bytedeco.javacpp.avutil.av_get_default_channel_layout;
import static org.bytedeco.javacpp.avutil.av_get_pix_fmt_name;
import static org.bytedeco.javacpp.avutil.av_image_fill_arrays;
import static org.bytedeco.javacpp.avutil.av_image_get_buffer_size;
import static org.bytedeco.javacpp.avutil.av_log_get_level;
import static org.bytedeco.javacpp.avutil.av_malloc;
import static org.bytedeco.javacpp.avutil.av_sample_fmt_is_planar;
import static org.bytedeco.javacpp.avutil.av_samples_get_buffer_size;
import static org.bytedeco.javacpp.swresample.swr_alloc_set_opts;
import static org.bytedeco.javacpp.swresample.swr_convert;
import static org.bytedeco.javacpp.swresample.swr_free;
import static org.bytedeco.javacpp.swresample.swr_get_out_samples;
import static org.bytedeco.javacpp.swresample.swr_init;
import static org.bytedeco.javacpp.swscale.SWS_BILINEAR;
import static org.bytedeco.javacpp.swscale.sws_freeContext;
import static org.bytedeco.javacpp.swscale.sws_getCachedContext;
import static org.bytedeco.javacpp.swscale.sws_scale;

/**
 * Created by root on 24/9/18.
 */

public class ffmpegaudioframegrabber extends FrameGrabber {
    public static String[] getDeviceDescriptions() throws Exception {
        tryLoad();
        throw new UnsupportedOperationException("Device enumeration not support by FFmpeg.");
    }

    public static ffmpegaudioframegrabber createDefault(File deviceFile)   throws Exception { return new ffmpegaudioframegrabber(deviceFile); }
    public static ffmpegaudioframegrabber createDefault(String devicePath) throws Exception { return new ffmpegaudioframegrabber(devicePath); }
    public static ffmpegaudioframegrabber createDefault(int deviceNumber)  throws Exception { throw new Exception(ffmpegaudioframegrabber.class + " does not support device numbers."); }

    private static Exception loadingException = null;
    public static void tryLoad() throws Exception {
        if (loadingException != null) {
            throw loadingException;
        } else {
            try {
                Loader.load(avutil.class);
                Loader.load(swresample.class);
                Loader.load(avcodec.class);
                Loader.load(avformat.class);
                Loader.load(swscale.class);

                // Register all formats and codecs
                avcodec_register_all();
                av_register_all();
                avformat_network_init();

                Loader.load(org.bytedeco.javacpp.avdevice.class);
                avdevice_register_all();
            } catch (Throwable t) {
                if (t instanceof Exception) {
                    throw loadingException = (Exception)t;
                } else {
                    throw loadingException = new Exception("Failed to load " + ffmpegaudioframegrabber.class, t);
                }
            }
        }
    }

    static {
        try {
            tryLoad();
            FFmpegLockCallback.init();
        } catch (Exception ex) { }
    }

    public ffmpegaudioframegrabber(File file) {
        this(file.getAbsolutePath());
    }
    public ffmpegaudioframegrabber(String filename) {
        this.filename = filename;
        this.pixelFormat = AV_PIX_FMT_NONE;
        this.sampleFormat = AV_SAMPLE_FMT_NONE;
    }
    public ffmpegaudioframegrabber(InputStream inputStream) {
        this.inputStream = inputStream;
        this.pixelFormat = AV_PIX_FMT_NONE;
        this.sampleFormat = AV_SAMPLE_FMT_NONE;
    }
    public void release() throws Exception {
        // synchronized (org.bytedeco.javacpp.avcodec.class) {
        releaseUnsafe();
        // }
    }
    void releaseUnsafe() throws Exception {
        if (pkt != null && pkt2 != null) {
            if (pkt2.size() > 0) {
                av_packet_unref(pkt);
            }
            pkt = pkt2 = null;
        }

        // Free the RGB image
        if (image_ptr != null) {
            for (int i = 0; i < image_ptr.length; i++) {
                av_free(image_ptr[i]);
            }
            image_ptr = null;
        }
        if (picture_rgb != null) {
            av_frame_free(picture_rgb);
            picture_rgb = null;
        }

        // Free the native format picture frame
        if (picture != null) {
            av_frame_free(picture);
            picture = null;
        }

        // Close the video codec
        if (video_c != null) {
            avcodec_free_context(video_c);
            video_c = null;
        }

        // Free the audio samples frame
        if (samples_frame != null) {
            av_frame_free(samples_frame);
            samples_frame = null;
        }

        // Close the audio codec
        if (audio_c != null) {
            avcodec_free_context(audio_c);
            audio_c = null;
        }

        // Close the video file
        if (inputStream == null && oc != null && !oc.isNull()) {
            avformat_close_input(oc);
            oc = null;
        }

        if (img_convert_ctx != null) {
            sws_freeContext(img_convert_ctx);
            img_convert_ctx = null;
        }

        if (samples_ptr_out != null) {
            for (int i = 0; i < samples_ptr_out.length; i++) {
                av_free(samples_ptr_out[i].position(0));
            }
            samples_ptr_out = null;
            samples_buf_out = null;
        }

        if (samples_convert_ctx != null) {
            swr_free(samples_convert_ctx);
            samples_convert_ctx = null;
        }

        got_frame     = null;
        frameGrabbed  = false;
        frame         = null;
        timestamp     = 0;
        frameNumber   = 0;

        if (inputStream != null) {
            try {
                if (oc == null) {
                    // when called a second time
                    inputStream.close();
                } else {
                    inputStream.reset();
                }
            } catch (IOException ex) {
                throw new Exception("Error on InputStream.close(): ", ex);
            } finally {
                inputStreams.remove(oc);
                if (avio != null) {
                    if (avio.buffer() != null) {
                        av_free(avio.buffer());
                        avio.buffer(null);
                    }
                    av_free(avio);
                    avio = null;
                }
                if (oc != null) {
                    avformat_free_context(oc);
                    oc = null;
                }
            }
        }
    }
    @Override protected void finalize() throws Throwable {
        super.finalize();
        release();
    }

    static Map<Pointer,InputStream> inputStreams = Collections.synchronizedMap(new HashMap<Pointer,InputStream>());

    static class ReadCallback extends avformat.Read_packet_Pointer_BytePointer_int {
        @Override public int call(Pointer opaque, BytePointer buf, int buf_size) {
            try {
                byte[] b = new byte[buf_size];
                InputStream is = inputStreams.get(opaque);
                int size = is.read(b, 0, buf_size);
                if (size < 0) {
                    return 0;
                } else {
                    buf.put(b, 0, size);
                    return size;
                }
            }
            catch (Throwable t) {
                System.err.println("Error on InputStream.read(): " + t);
                return -1;
            }
        }
    }

    static class SeekCallback extends avformat.Seek_Pointer_long_int {
        @Override public long call(Pointer opaque, long offset, int whence) {
            try {
                InputStream is = inputStreams.get(opaque);
                switch (whence) {
                    case 0: is.reset(); break;
                    case 1: break;
                    default: return -1;
                }
                long remaining = offset;
                while (remaining > 0) {
                    long skipped = is.skip(remaining);
                    if (skipped == 0) break; // end of the stream
                    remaining -= skipped;
                }
                return 0;
            } catch (Throwable t) {
                System.err.println("Error on InputStream.reset() or skip(): " + t);
                return -1;
            }
        }
    }

    static ReadCallback readCallback = new ReadCallback();
    static SeekCallback seekCallback = new SeekCallback();

    private InputStream     inputStream;
    private avformat.AVIOContext avio;
    private String          filename;
    private avformat.AVFormatContext oc;
    private avformat.AVStream video_st, audio_st;
    private avcodec.AVCodecContext video_c, audio_c;
    private avutil.AVFrame picture, picture_rgb;
    private BytePointer[]   image_ptr;
    private Buffer[]        image_buf;
    private avutil.AVFrame samples_frame;
    private BytePointer[]   samples_ptr;
    private Buffer[]        samples_buf;
    private BytePointer[]   samples_ptr_out;
    private Buffer[]        samples_buf_out;
    private avcodec.AVPacket pkt, pkt2;
    private int             sizeof_pkt;
    private int[]           got_frame;
    private swscale.SwsContext img_convert_ctx;
    private swresample.SwrContext samples_convert_ctx;
    private int             samples_channels, samples_format, samples_rate;
    private boolean         frameGrabbed;
    private Frame frame;

    /**
     * Is there an audio stream?
     * @return  {@code audio_st!=null;}
     */
    public boolean hasAudio() {
        return audio_st!=null;
    }

    @Override public double getGamma() {
        // default to a gamma of 2.2 for cheap Webcams, DV cameras, etc.
        if (gamma == 0.0) {
            return 2.2;
        } else {
            return gamma;
        }
    }

    @Override public String getFormat() {
        if (oc == null) {
            return super.getFormat();
        } else {
            return oc.iformat().name().getString();
        }
    }

    @Override public int getAudioChannels() {
        return audioChannels > 0 || audio_c == null ? super.getAudioChannels() : audio_c.channels();
    }

    @Override public double getAspectRatio() {
        if (video_st == null) {
            return super.getAspectRatio();
        } else {
            avutil.AVRational r = av_guess_sample_aspect_ratio(oc, video_st, picture);
            double a = (double)r.num() / r.den();
            return a == 0.0 ? 1.0 : a;
        }
    }

    /**Estimation of audio frames per second
     *
     * @return (double) getSampleRate()) / samples_frame.nb_samples()
     * if samples_frame.nb_samples() is not zero, otherwise return 0
     */
    public double getAudioFrameRate() {
        if (audio_st == null) {
            return 0.0;
        } else {
            if (samples_frame == null || samples_frame.nb_samples() == 0) {
                try {
                    grabFrame(true, false, true, false);
                    frameGrabbed = true;
                } catch (Exception e) {
                    return 0.0;
                }
            }
            if (samples_frame != null || samples_frame.nb_samples() != 0)
                return ((double) getSampleRate()) / samples_frame.nb_samples();
            else return 0.0;

        }
    }

    @Override public int getAudioCodec() {
        return audio_c == null ? super.getAudioCodec() : audio_c.codec_id();
    }

    @Override public int getAudioBitrate() {
        return audio_c == null ? super.getAudioBitrate() : (int)audio_c.bit_rate();
    }

    @Override public int getSampleFormat() {
        if (sampleMode == SampleMode.SHORT || sampleMode == SampleMode.FLOAT) {
            if (sampleFormat == AV_SAMPLE_FMT_NONE) {
                return sampleMode == SampleMode.SHORT ? AV_SAMPLE_FMT_S16 : AV_SAMPLE_FMT_FLT;
            } else {
                return sampleFormat;
            }
        } else if (audio_c != null) { // RAW
            return audio_c.sample_fmt();
        } else {
            return super.getSampleFormat();
        }
    }

    @Override public int getSampleRate() {
        return sampleRate > 0 || audio_c == null ? super.getSampleRate() : audio_c.sample_rate();
    }

    @Override public String getMetadata(String key) {
        if (oc == null) {
            return super.getMetadata(key);
        }
        avutil.AVDictionaryEntry entry = av_dict_get(oc.metadata(), key, null, 0);
        return entry == null || entry.value() == null ? null : entry.value().getString();
    }

    @Override public String getAudioMetadata(String key) {
        if (audio_st == null) {
            return super.getAudioMetadata(key);
        }
        avutil.AVDictionaryEntry entry = av_dict_get(audio_st.metadata(), key, null, 0);
        return entry == null || entry.value() == null ? null : entry.value().getString();
    }

    /** setTimestamp with disregard of the resulting frame type, video or audio */
    @Override public void setTimestamp(long timestamp) throws Exception {
        setTimestamp(timestamp, EnumSet.of(Frame.Type.VIDEO, Frame.Type.AUDIO));
    }

    /** setTimestamp with resulting audio frame type */
    public void setAudioTimestamp(long timestamp) throws Exception {
        setTimestamp(timestamp, EnumSet.of(Frame.Type.AUDIO));
    }

    /** setTimestamp with a priority the resulting frame should be:
     *  video (frameTypesToSeek contains only Frame.Type.VIDEO),
     *  audio (frameTypesToSeek contains only Frame.Type.AUDIO),
     *  or any (frameTypesToSeek contains both)
     */
    private void setTimestamp(long timestamp, EnumSet<Frame.Type> frameTypesToSeek) throws Exception {
        int ret;
        if (oc == null) {
            super.setTimestamp(timestamp);
        } else {
            timestamp = timestamp * AV_TIME_BASE / 1000000L;
            /* add the stream start time */
            if (oc.start_time() != AV_NOPTS_VALUE) {
                timestamp += oc.start_time();
            }
            if ((ret = avformat_seek_file(oc, -1, Long.MIN_VALUE, timestamp, Long.MAX_VALUE, AVSEEK_FLAG_BACKWARD)) < 0) {
                throw new Exception("avformat_seek_file() error " + ret + ": Could not seek file to timestamp " + timestamp + ".");
            }
            if (video_c != null) {
                avcodec_flush_buffers(video_c);
            }
            if (audio_c != null) {
                avcodec_flush_buffers(audio_c);
            }

            FFmpegFrameGrabber ss;

            if (pkt2.size() > 0) {
                pkt2.size(0);
                av_packet_unref(pkt);
            }
            /*     After the call of ffmpeg's avformat_seek_file(...) with the flag set to AVSEEK_FLAG_BACKWARD
             * the decoding position should be located before the requested timestamp in a closest position
             * from which all the active streams can be decoded successfully.
             * The following seeking consists of two stages:
             * 1. Grab frames till the frame corresponding to that "closest" position
             * (the first frame containing decoded data).
             *
             * 2. Grab frames till the desired timestamp is reached. The number of steps is restricted
             * by doubled estimation of frames between that "closest" position and the desired position.
             *
             * frameTypesToSeek parameter sets the preferred type of frames to seek.
             * It can be chosen from three possible types: VIDEO, AUDIO or ANY.
             * The setting means only a preference in the type. That is, if VIDEO or AUDIO is
             * specified but the file does not have video or audio stream - ANY type will be used instead.
             *
             *
             * TODO
             *  Sometimes the ffmpeg's avformat_seek_file(...) function brings us not to a position before
             *  the desired but few frames after.... What can be a the solution in this case if we really need
             *  a frame-precision seek? Probably we may try to request even earlier timestamp and look if this
             *  will bring us before the desired position.
             *
            */

            boolean has_video = false;
            boolean has_audio = hasAudio();

            if (has_video || has_audio) {
                if ((frameTypesToSeek.contains(Frame.Type.VIDEO) && !has_video ) ||
                        (frameTypesToSeek.contains(Frame.Type.AUDIO) && !has_audio ))
                    frameTypesToSeek = EnumSet.of(Frame.Type.VIDEO, Frame.Type.AUDIO);

                long initialSeekPosition = Long.MIN_VALUE;
                long maxSeekSteps = 0;
                long count = 0;
                Frame seekFrame = null;

                while(count++ < 1000) { //seek to a first frame containing video or audio after avformat_seek_file(...)
                    seekFrame = grabFrame(true, true, false, false);
                    if (seekFrame == null) return; //is it better to throw NullPointerException?
                    EnumSet<Frame.Type> frameTypes = seekFrame.getTypes();
                    frameTypes.retainAll(frameTypesToSeek);
                    if (!frameTypes.isEmpty()) {
                        initialSeekPosition = seekFrame.timestamp;
                        //the position closest to the requested timestamp from which it can be reached by sequential grabFrame calls
                        break;
                    }
                }
                if (has_video && this.getFrameRate() > 0) {
                    //estimation of video frame duration
                    double deltaTimeStamp = 1000000.0/this.getFrameRate();
                    if (initialSeekPosition < timestamp - deltaTimeStamp/2)
                        maxSeekSteps = (long)(10*(timestamp - initialSeekPosition)/deltaTimeStamp);
                } else if (has_audio && this.getAudioFrameRate() > 0) {
                    //estimation of audio frame duration
                    double deltaTimeStamp = 1000000.0/this.getAudioFrameRate();
                    if (initialSeekPosition < timestamp - deltaTimeStamp/2)
                        maxSeekSteps = (long)(10*(timestamp - initialSeekPosition)/deltaTimeStamp);
                } else
                    //zero frameRate
                    if (initialSeekPosition < timestamp - 1L) maxSeekSteps = 1000;

                count = 0;
                while(count < maxSeekSteps) {
                    seekFrame = grabFrame(true, true, false, false);
                    if (seekFrame == null) return; //is it better to throw NullPointerException?
                    EnumSet<Frame.Type> frameTypes = seekFrame.getTypes();
                    frameTypes.retainAll(frameTypesToSeek);
                    if (!frameTypes.isEmpty()) {
                        count++;
                        if (this.timestamp >= timestamp - 1) break;
                    }
                }

                frameGrabbed = true;
            }
        }
    }

    @Override public long getLengthInTime() {
        return oc.duration() * 1000000L / AV_TIME_BASE;
    }

    public void start() throws Exception {
        // synchronized (org.bytedeco.javacpp.avcodec.class) {
        startUnsafe();
        // }
    }
    void startUnsafe() throws Exception {
        int ret;
        img_convert_ctx = null;
        oc              = new avformat.AVFormatContext(null);
        video_c         = null;
        audio_c         = null;
        pkt             = new avcodec.AVPacket();
        pkt2            = new avcodec.AVPacket();
        sizeof_pkt      = pkt.sizeof();
        got_frame       = new int[1];
        frameGrabbed    = false;
        frame           = new Frame();
        timestamp       = 0;
        frameNumber     = 0;

        pkt2.size(0);

        // Open video file
        avformat.AVInputFormat f = null;
        if (format != null && format.length() > 0) {
            if ((f = av_find_input_format(format)) == null) {
                throw new Exception("av_find_input_format() error: Could not find input format \"" + format + "\".");
            }
        }
        avutil.AVDictionary options = new avutil.AVDictionary(null);
        if (frameRate > 0) {
            avutil.AVRational r = av_d2q(frameRate, 1001000);
            av_dict_set(options, "framerate", r.num() + "/" + r.den(), 0);
        }
        if (pixelFormat >= 0) {
            av_dict_set(options, "pixel_format", av_get_pix_fmt_name(pixelFormat).getString(), 0);
        } else if (imageMode != ImageMode.RAW) {
            av_dict_set(options, "pixel_format", imageMode == ImageMode.COLOR ? "bgr24" : "gray8", 0);
        }
        if (imageWidth > 0 && imageHeight > 0) {
            av_dict_set(options, "video_size", imageWidth + "x" + imageHeight, 0);
        }
        if (sampleRate > 0) {
            av_dict_set(options, "sample_rate", "" + sampleRate, 0);
        }
        if (audioChannels > 0) {
            av_dict_set(options, "channels", "" + audioChannels, 0);
        }
        for (Map.Entry<String, String> e : this.options.entrySet()) {
            av_dict_set(options, e.getKey(), e.getValue(), 0);
        }
        if (inputStream != null) {
            if (!inputStream.markSupported()) {
                inputStream = new BufferedInputStream(inputStream);
            }
            inputStream.mark(Integer.MAX_VALUE - 8); // so that the whole input stream is seekable
            oc = avformat_alloc_context();
            avio = avio_alloc_context(new BytePointer(av_malloc(4096)), 4096, 0, oc, readCallback, null, seekCallback);
            oc.pb(avio);

            filename = inputStream.toString();
            inputStreams.put(oc, inputStream);
        }
        if ((ret = avformat_open_input(oc, filename, f, options)) < 0) {
            av_dict_set(options, "pixel_format", null, 0);
            if ((ret = avformat_open_input(oc, filename, f, options)) < 0) {
                throw new Exception("avformat_open_input() error " + ret + ": Could not open input \"" + filename + "\". (Has setFormat() been called?)");
            }
        }
        av_dict_free(options);

        oc.max_delay(maxDelay);

        // Retrieve stream information
        if ((ret = avformat_find_stream_info(oc, (PointerPointer)null)) < 0) {
            throw new Exception("avformat_find_stream_info() error " + ret + ": Could not find stream information.");
        }

        if (av_log_get_level() >= AV_LOG_INFO) {
            // Dump information about file onto standard error
            av_dump_format(oc, 0, filename, 0);
        }

        // Find the first video and audio stream, unless the user specified otherwise
        video_st = audio_st = null;
        avcodec.AVCodecParameters video_par = null, audio_par = null;
        int nb_streams = oc.nb_streams();
        for (int i = 0; i < nb_streams; i++) {
            avformat.AVStream st = oc.streams(i);
            // Get a pointer to the codec context for the video or audio stream
            avcodec.AVCodecParameters par = st.codecpar();
            if (video_st == null && par.codec_type() == AVMEDIA_TYPE_VIDEO && (videoStream < 0 || videoStream == i)) {
                video_st = st;
                video_par = par;
                videoStream = i;
            } else if (audio_st == null && par.codec_type() == AVMEDIA_TYPE_AUDIO && (audioStream < 0 || audioStream == i)) {
                audio_st = st;
                audio_par = par;
                audioStream = i;
            }
        }
        if (video_st == null && audio_st == null) {
            throw new Exception("Did not find a video or audio stream inside \"" + filename
                    + "\" for videoStream == " + videoStream + " and audioStream == " + audioStream + ".");
        }

        if (audio_st != null) {
            // Find the decoder for the audio stream
            avcodec.AVCodec codec = avcodec_find_decoder(audio_par.codec_id());
            if (codec == null) {
                throw new Exception("avcodec_find_decoder() error: Unsupported audio format or codec not found: " + audio_par.codec_id() + ".");
            }

            /* Allocate a codec context for the decoder */
            if ((audio_c = avcodec_alloc_context3(codec)) == null) {
                throw new Exception("avcodec_alloc_context3() error: Could not allocate audio decoding context.");
            }

            /* copy the stream parameters from the muxer */
            if ((ret = avcodec_parameters_to_context(audio_c, audio_st.codecpar())) < 0) {
                release();
                throw new Exception("avcodec_parameters_to_context() error: Could not copy the audio stream parameters.");
            }

            options = new avutil.AVDictionary(null);
            for (Map.Entry<String, String> e : audioOptions.entrySet()) {
                av_dict_set(options, e.getKey(), e.getValue(), 0);
            }
            // Open audio codec
            if ((ret = avcodec_open2(audio_c, codec, options)) < 0) {
                throw new Exception("avcodec_open2() error " + ret + ": Could not open audio codec.");
            }
            av_dict_free(options);

            // Allocate audio samples frame
            if ((samples_frame = av_frame_alloc()) == null) {
                throw new Exception("av_frame_alloc() error: Could not allocate audio frame.");
            }

            samples_ptr = new BytePointer[] { null };
            samples_buf = new Buffer[] { null };
        }
    }

    public void stop() throws Exception {
        release();
    }

    public void trigger() throws Exception {
        if (oc == null || oc.isNull()) {
            throw new Exception("Could not trigger: No AVFormatContext. (Has start() been called?)");
        }
        if (pkt2.size() > 0) {
            pkt2.size(0);
            av_packet_unref(pkt);
        }
        for (int i = 0; i < numBuffers+1; i++) {
            if (av_read_frame(oc, pkt) < 0) {
                return;
            }
            av_packet_unref(pkt);
        }
    }

    @Override
    public Frame grab() throws Exception {
        return null;
    }

    private void processSamples() throws Exception {
        int ret;

        int codecid=audio_c.codec_id();
        avcodec.AVCodec codecc=audio_c.codec();
        int type=audio_c.codec_type();
        int tag=audio_c.codec_tag();

        int sample_format = samples_frame.format();
        int planes = av_sample_fmt_is_planar(sample_format) != 0 ? (int)samples_frame.channels() : 1;
        int data_size = av_samples_get_buffer_size((IntPointer)null, audio_c.channels(),
                samples_frame.nb_samples(), audio_c.sample_fmt(), 1) / planes;
        if (samples_buf == null || samples_buf.length != planes) {
            samples_ptr = new BytePointer[planes];
            samples_buf = new Buffer[planes];
        }
        frame.sampleRate = audio_c.sample_rate();
        frame.audioChannels = audio_c.channels();
        frame.samples = samples_buf;
        int sample_size = data_size / av_get_bytes_per_sample(sample_format);
        for (int i = 0; i < planes; i++) {
            BytePointer p = samples_frame.data(i);
            if (!p.equals(samples_ptr[i]) || samples_ptr[i].capacity() < data_size) {
                samples_ptr[i] = p.capacity(data_size);
                ByteBuffer b   = p.asBuffer();
                switch (sample_format) {
                    case AV_SAMPLE_FMT_U8:
                    case AV_SAMPLE_FMT_U8P:  samples_buf[i] = b; break;
                    case AV_SAMPLE_FMT_S16:
                    case AV_SAMPLE_FMT_S16P: samples_buf[i] = b.asShortBuffer(); break;
                    case AV_SAMPLE_FMT_S32:
                    case AV_SAMPLE_FMT_S32P: samples_buf[i] = b.asIntBuffer();   break;
                    case AV_SAMPLE_FMT_FLT:
                    case AV_SAMPLE_FMT_FLTP: samples_buf[i] = b.asFloatBuffer();  break;
                    case AV_SAMPLE_FMT_DBL:
                    case AV_SAMPLE_FMT_DBLP: samples_buf[i] = b.asDoubleBuffer(); break;
                    default: assert false;
                }
            }
            samples_buf[i].position(0).limit(sample_size);
        }

        if (audio_c.channels() != getAudioChannels() || audio_c.sample_fmt() != getSampleFormat() || audio_c.sample_rate() != getSampleRate()) {
            if (samples_convert_ctx == null || samples_channels != getAudioChannels() || samples_format != getSampleFormat() || samples_rate != getSampleRate()) {

                int a=getSampleFormat();
                int b=getAudioChannels();
                int c=getSampleRate();

                int d=audio_c.sample_fmt();
                int e=audio_c.channels();
                int f=audio_c.sample_fmt();

                samples_convert_ctx = swr_alloc_set_opts(samples_convert_ctx, av_get_default_channel_layout(getAudioChannels()), getSampleFormat(), getSampleRate(),
                        av_get_default_channel_layout(audio_c.channels()), audio_c.sample_fmt(), audio_c.sample_rate(), 0, null);
                if (samples_convert_ctx == null) {
                    throw new Exception("swr_alloc_set_opts() error: Cannot allocate the conversion context.");
                } else if ((ret = swr_init(samples_convert_ctx)) < 0) {
                    throw new Exception("swr_init() error " + ret + ": Cannot initialize the conversion context.");
                }
                samples_channels = getAudioChannels();
                samples_format = getSampleFormat();
                samples_rate = getSampleRate();
            }

            int sample_size_in = samples_frame.nb_samples();
            int planes_out = av_sample_fmt_is_planar(samples_format) != 0 ? (int)samples_frame.channels() : 1;
            int sample_size_out = swr_get_out_samples(samples_convert_ctx, sample_size_in);
            int sample_bytes_out = av_get_bytes_per_sample(samples_format);
            int buffer_size_out = sample_size_out * sample_bytes_out * (planes_out > 1 ? 1 : samples_channels);
            if (samples_buf_out == null || samples_buf.length != planes_out || samples_ptr_out[0].capacity() < buffer_size_out) {
                for (int i = 0; samples_ptr_out != null && i < samples_ptr_out.length; i++) {
                    av_free(samples_ptr_out[i].position(0));
                }
                samples_ptr_out = new BytePointer[planes_out];
                samples_buf_out = new Buffer[planes_out];

                for (int i = 0; i < planes_out; i++) {
                    samples_ptr_out[i] = new BytePointer(av_malloc(buffer_size_out)).capacity(buffer_size_out);
                    ByteBuffer b = samples_ptr_out[i].asBuffer();
                    switch (samples_format) {
                        case AV_SAMPLE_FMT_U8:
                        case AV_SAMPLE_FMT_U8P:  samples_buf_out[i] = b; break;
                        case AV_SAMPLE_FMT_S16:
                        case AV_SAMPLE_FMT_S16P: samples_buf_out[i] = b.asShortBuffer(); break;
                        case AV_SAMPLE_FMT_S32:
                        case AV_SAMPLE_FMT_S32P: samples_buf_out[i] = b.asIntBuffer();    break;
                        case AV_SAMPLE_FMT_FLT:
                        case AV_SAMPLE_FMT_FLTP: samples_buf_out[i] = b.asFloatBuffer();  break;
                        case AV_SAMPLE_FMT_DBL:
                        case AV_SAMPLE_FMT_DBLP: samples_buf_out[i] = b.asDoubleBuffer(); break;
                        default: assert false;
                    }
                }
            }
            frame.sampleRate = samples_rate;
            frame.audioChannels = samples_channels;
            frame.samples = samples_buf_out;
            Buffer[] samplee=frame.samples;

            if ((ret = swr_convert(samples_convert_ctx, new PointerPointer(samples_ptr_out), sample_size_out, new PointerPointer(samples_ptr), sample_size_in)) < 0) {
                throw new Exception("swr_convert() error " + ret + ": Cannot convert audio samples.");
            }
            for (int i = 0; i < planes_out; i++) {
                samples_ptr_out[i].position(0).limit(ret * (planes_out > 1 ? 1 : samples_channels));
                samples_buf_out[i].position(0).limit(ret * (planes_out > 1 ? 1 : samples_channels));
            }
        }
    }
    public Frame grabSamples() throws Exception {
        return grabFrame(true, false, true, false);
    }
    public Frame grabFrame(boolean doAudio, boolean doVideo, boolean doProcessing, boolean keyFrames) throws Exception {
        if (oc == null || oc.isNull()) {
            throw new Exception("Could not grab: No AVFormatContext. (Has start() been called?)");
        } else if ((!doVideo || video_st == null) && (!doAudio || audio_st == null)) {
            return null;
        }
        boolean videoFrameGrabbed = frameGrabbed && frame.image != null;
        boolean audioFrameGrabbed = frameGrabbed && frame.samples != null;
        frameGrabbed = false;
        frame.keyFrame = false;
        frame.imageWidth = 0;
        frame.imageHeight = 0;
        frame.imageDepth = 0;
        frame.imageChannels = 0;
        frame.imageStride = 0;
        frame.image = null;
        frame.sampleRate = 0;
        frame.audioChannels = 0;
        frame.samples = null;
        frame.opaque = null;
        if (doAudio && audioFrameGrabbed) {
            if (doProcessing) {
                processSamples();
            }
            frame.keyFrame = samples_frame.key_frame() != 0;
            frame.opaque = samples_frame;
            return frame;
        }
        boolean done = false;
        while (!done) {
            if (pkt2.size() <= 0) {
                int av_readdata=av_read_frame(oc, pkt);
                if (av_read_frame(oc, pkt) < 0) {
                    if (doVideo && video_st != null) {
                        // The video codec may have buffered some frames
                        pkt.stream_index(video_st.index());
                        pkt.flags(AV_PKT_FLAG_KEY);
                        pkt.data(null);
                        pkt.size(0);
                    } else {
                        return null;
                    }
                }
            }

            if (doAudio && audio_st != null && pkt.stream_index() == audio_st.index()) {
                if (pkt2.size() <= 0) {
                    // HashMap is unacceptably slow on Android
                    // pkt2.put(pkt);
                    BytePointer.memcpy(pkt2, pkt, sizeof_pkt);
                }
                av_frame_unref(samples_frame);
                // Decode audio frame
                int len = avcodec_decode_audio4(audio_c, samples_frame, got_frame, pkt2);
                if (len <= 0) {
                    // On error, trash the whole packet
                    pkt2.size(0);
                } else {
                    pkt2.data(pkt2.data().position(len));
                    pkt2.size(pkt2.size() - len);
                    if (got_frame[0] != 0) {

                        /*try {
                            BytePointer pp=samples_frame.data(0);
                            ByteBuffer bytebuffer   = pp.asBuffer();
                            byte[] byteData = bytebuffer.array();
                            String value= md5.calculatebytemd5(byteData);
                            Log.e("MD5 audio ",value);
                        }catch (java.lang.Exception e)
                        {
                            e.printStackTrace();
                        }*/

                        long pts = av_frame_get_best_effort_timestamp(samples_frame);
                        avutil.AVRational time_base = audio_st.time_base();
                        timestamp = 1000000L * pts * time_base.num() / time_base.den();
                        frame.samples = samples_buf;
                        /* if a frame has been decoded, output it */
                        if (doProcessing) {
                            processSamples();
                        }
                        done = true;
                        frame.timestamp = timestamp;
                        frame.keyFrame = samples_frame.key_frame() != 0;
                        frame.opaque = samples_frame;
                    }
                }
            }

            /*Log.e("Packet size ",""+pkt.size());
            Log.e("Packet2 size ",""+pkt2.size());*/
            if (pkt2.size() <= 0) {
                // Free the packet that was allocated by av_read_frame
                av_packet_unref(pkt);
            }
        }
        return frame;
    }

    public avcodec.AVPacket grabPacket() throws Exception {
        if (oc == null || oc.isNull()) {
            throw new Exception("Could not trigger: No AVFormatContext. (Has start() been called?)");
        }

        // Return the next frame of a stream.
        if (av_read_frame(oc, pkt) < 0) {
            return null;
        }

        return pkt;
    }
}

