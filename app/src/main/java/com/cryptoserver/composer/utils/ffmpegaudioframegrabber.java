package com.cryptoserver.composer.utils;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacpp.avformat;
import org.bytedeco.javacpp.avutil;
import org.bytedeco.javacpp.swresample;
import org.bytedeco.javacpp.swscale;
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
import java.util.HashMap;
import java.util.Map;

import static org.bytedeco.javacpp.avcodec.AV_PKT_FLAG_KEY;
import static org.bytedeco.javacpp.avcodec.av_packet_unref;
import static org.bytedeco.javacpp.avcodec.avcodec_alloc_context3;
import static org.bytedeco.javacpp.avcodec.avcodec_decode_audio4;
import static org.bytedeco.javacpp.avcodec.avcodec_decode_video2;
import static org.bytedeco.javacpp.avcodec.avcodec_find_decoder;
import static org.bytedeco.javacpp.avcodec.avcodec_free_context;
import static org.bytedeco.javacpp.avcodec.avcodec_open2;
import static org.bytedeco.javacpp.avcodec.avcodec_parameters_to_context;
import static org.bytedeco.javacpp.avcodec.avcodec_register_all;
import static org.bytedeco.javacpp.avdevice.avdevice_register_all;
import static org.bytedeco.javacpp.avformat.av_dump_format;
import static org.bytedeco.javacpp.avformat.av_find_input_format;
import static org.bytedeco.javacpp.avformat.av_read_frame;
import static org.bytedeco.javacpp.avformat.av_register_all;
import static org.bytedeco.javacpp.avformat.avformat_alloc_context;
import static org.bytedeco.javacpp.avformat.avformat_close_input;
import static org.bytedeco.javacpp.avformat.avformat_find_stream_info;
import static org.bytedeco.javacpp.avformat.avformat_free_context;
import static org.bytedeco.javacpp.avformat.avformat_network_init;
import static org.bytedeco.javacpp.avformat.avformat_open_input;
import static org.bytedeco.javacpp.avformat.avio_alloc_context;
import static org.bytedeco.javacpp.avutil.AVMEDIA_TYPE_AUDIO;
import static org.bytedeco.javacpp.avutil.AV_LOG_INFO;
import static org.bytedeco.javacpp.avutil.AV_NOPTS_VALUE;
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
import static org.bytedeco.javacpp.avutil.av_log_get_level;
import static org.bytedeco.javacpp.avutil.av_malloc;
import static org.bytedeco.javacpp.avutil.av_sample_fmt_is_planar;
import static org.bytedeco.javacpp.avutil.av_samples_get_buffer_size;
import static org.bytedeco.javacpp.swresample.swr_alloc_set_opts;
import static org.bytedeco.javacpp.swresample.swr_convert;
import static org.bytedeco.javacpp.swresample.swr_free;
import static org.bytedeco.javacpp.swresample.swr_get_out_samples;
import static org.bytedeco.javacpp.swresample.swr_init;
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

    public static ffmpegaudioframegrabber createdefault(File deviceFile)   throws Exception { return new ffmpegaudioframegrabber(deviceFile); }
    public static ffmpegaudioframegrabber createdefault(String devicePath) throws Exception { return new ffmpegaudioframegrabber(devicePath); }
    public static ffmpegaudioframegrabber createdefault(int deviceNumber)  throws Exception { throw new Exception(ffmpegaudioframegrabber.class + " does not support device numbers."); }

    private static Exception loadingexception = null;
    public static void tryLoad() throws Exception {
        if (loadingexception != null) {
            throw loadingexception;
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
                    throw loadingexception = (Exception)t;
                } else {
                    throw loadingexception = new Exception("Failed to load " + ffmpegaudioframegrabber.class, t);
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
    public void release() throws Exception {
        releaseUnsafe();
    }

    // finish init and all process
    void releaseUnsafe() throws Exception {
        if (pkt != null && pkt2 != null) {
            if (pkt2.size() > 0) {
                av_packet_unref(pkt);
            }
            pkt = pkt2 = null;
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
        if (inputstream == null && oc != null && !oc.isNull()) {
            avformat_close_input(oc);
            oc = null;
        }

        if (samples_ptr_out != null) {
            for (int i = 0; i < samples_ptr_out.length; i++) {
                av_free(samples_ptr_out[i].position(0));
            }
            samples_ptr_out = null;
            samples_buf_out = null;
        }

        if (samplesconvertctx != null) {
            swr_free(samplesconvertctx);
            samplesconvertctx = null;
        }

        got_frame     = null;
        framegrabbed = false;
        frame         = null;
        timestamp     = 0;
        frameNumber   = 0;

        if (inputstream != null) {
            try {
                if (oc == null) {
                    // when called a second time
                    inputstream.close();
                } else {
                    inputstream.reset();
                }
            } catch (IOException ex) {
                throw new Exception("Error on InputStream.close(): ", ex);
            } finally {
                inputstreams.remove(oc);
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

    static Map<Pointer,InputStream> inputstreams = Collections.synchronizedMap(new HashMap<Pointer,InputStream>());

    static class ReadCallback extends avformat.Read_packet_Pointer_BytePointer_int {
        @Override public int call(Pointer opaque, BytePointer buf, int buf_size) {
            try {
                byte[] b = new byte[buf_size];
                InputStream is = inputstreams.get(opaque);
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
                InputStream is = inputstreams.get(opaque);
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

    static ReadCallback readcallback = new ReadCallback();
    static SeekCallback seekcallback = new SeekCallback();

    private InputStream inputstream;
    private avformat.AVIOContext avio;
    private String          filename;
    private avformat.AVFormatContext oc;
    private avformat.AVStream audio_st;
    private avcodec.AVCodecContext audio_c;
    private avutil.AVFrame samples_frame;
    private BytePointer[]   samples_ptr;
    private Buffer[]        samples_buf;
    private BytePointer[]   samples_ptr_out;
    private Buffer[]        samples_buf_out;
    private avcodec.AVPacket pkt, pkt2;
    private int             sizeof_pkt;
    private int[]           got_frame;
    private swresample.SwrContext samplesconvertctx;
    private int sampleschannels, samplesformat, samplesrate;
    private boolean framegrabbed;
    private Frame frame;


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

    /**Estimation of audio frames per second
     *
     * @return (double) getSampleRate()) / samples_frame.nb_samples()
     * if samples_frame.nb_samples() is not zero, otherwise return 0
     */
    public double getaudioframerate() {
        if (audio_st == null) {
            return 0.0;
        } else {
            if (samples_frame == null || samples_frame.nb_samples() == 0) {
                try {
                    grabframe(true, false, false, false);
                    framegrabbed = true;
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
                // return sampleMode == SampleMode.SHORT ? AV_SAMPLE_FMT_S16 : AV_SAMPLE_FMT_FLT;
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

    @Override public long getLengthInTime() {
        return oc.duration() * 1000000L / AV_TIME_BASE;
    }

    public int getlengthinaudioframes() {
        // best guess...
        double afr = getaudioframerate();
        if (afr > 0) return (int) (getLengthInTime() * afr / 1000000L);
        else return 0;
    }
    public void start() throws Exception {
        startunsafe();
    }
    void startunsafe() throws Exception {
        int ret;
        oc              = new avformat.AVFormatContext(null);
        audio_c         = null;
        pkt             = new avcodec.AVPacket();
        pkt2            = new avcodec.AVPacket();
        sizeof_pkt      = pkt.sizeof();
        got_frame       = new int[1];
        framegrabbed = false;
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
        if (inputstream != null) {
            if (!inputstream.markSupported()) {
                inputstream = new BufferedInputStream(inputstream);
            }
            inputstream.mark(Integer.MAX_VALUE - 8); // so that the whole input stream is seekable
            oc = avformat_alloc_context();
            avio = avio_alloc_context(new BytePointer(av_malloc(4096)), 4096, 0, oc, readcallback, null, seekcallback);
            oc.pb(avio);

            filename = inputstream.toString();
            inputstreams.put(oc, inputstream);
        }

        // open input
        if ((ret = avformat_open_input(oc, filename, f, options)) < 0) {
            av_dict_set(options, "pixel_format", null, 0);
            if ((ret = avformat_open_input(oc, filename, f, options)) < 0) {
                throw new Exception("avformat_open_input() error " + ret + ": Could not open input \"" + filename + "\". (Has setFormat() been called?)");
            }
        }
        av_dict_free(options);

        oc.max_delay(maxDelay);

        // Analyze stream information
        if ((ret = avformat_find_stream_info(oc, (PointerPointer)null)) < 0) {
            throw new Exception("avformat_find_stream_info() error " + ret + ": Could not find stream information.");
        }

        if (av_log_get_level() >= AV_LOG_INFO) {
            // Dump information about file onto standard error
            av_dump_format(oc, 0, filename, 0);
        }

        // find audio stream
        avcodec.AVCodecParameters audio_par = null;
        int nb_streams = oc.nb_streams();
        for (int i = 0; i < nb_streams; i++) {
            avformat.AVStream st = oc.streams(i);
            // Get a pointer to the codec context for the video or audio stream
            avcodec.AVCodecParameters par = st.codecpar();
            if (audio_st == null && par.codec_type() == AVMEDIA_TYPE_AUDIO && (audioStream < 0 || audioStream == i)) {
                audio_st = st;
                audio_par = par;
                audioStream = i;
            }
        }
        if (audio_st == null) {
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

    private void processsamples() throws Exception {
        int ret;

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
                    case AV_SAMPLE_FMT_S16P: samples_buf[i] = b.asShortBuffer();  break;
                    case AV_SAMPLE_FMT_S32:
                    case AV_SAMPLE_FMT_S32P: samples_buf[i] = b.asIntBuffer();    break;
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
            if (samplesconvertctx == null || sampleschannels != getAudioChannels() || samplesformat != getSampleFormat() || samplesrate != getSampleRate()) {
                samplesconvertctx = swr_alloc_set_opts(samplesconvertctx, av_get_default_channel_layout(getAudioChannels()), getSampleFormat(), getSampleRate(),
                        av_get_default_channel_layout(audio_c.channels()), audio_c.sample_fmt(), audio_c.sample_rate(), 0, null);
                if (samplesconvertctx == null) {
                    throw new Exception("swr_alloc_set_opts() error: Cannot allocate the conversion context.");
                } else if ((ret = swr_init(samplesconvertctx)) < 0) {
                    throw new Exception("swr_init() error " + ret + ": Cannot initialize the conversion context.");
                }
                sampleschannels = getAudioChannels();
                samplesformat = getSampleFormat();
                samplesrate = getSampleRate();
            }

            int sample_size_in = samples_frame.nb_samples();
            int planes_out = av_sample_fmt_is_planar(samplesformat) != 0 ? (int)samples_frame.channels() : 1;
            int sample_size_out = swr_get_out_samples(samplesconvertctx, sample_size_in);
            int sample_bytes_out = av_get_bytes_per_sample(samplesformat);
            int buffer_size_out = sample_size_out * sample_bytes_out * (planes_out > 1 ? 1 : sampleschannels);
            if (samples_buf_out == null || samples_buf.length != planes_out || samples_ptr_out[0].capacity() < buffer_size_out) {
                for (int i = 0; samples_ptr_out != null && i < samples_ptr_out.length; i++) {
                    av_free(samples_ptr_out[i].position(0));
                }
                samples_ptr_out = new BytePointer[planes_out];
                samples_buf_out = new Buffer[planes_out];

                for (int i = 0; i < planes_out; i++) {
                    samples_ptr_out[i] = new BytePointer(av_malloc(buffer_size_out)).capacity(buffer_size_out);
                    ByteBuffer b = samples_ptr_out[i].asBuffer();
                    switch (samplesformat) {
                        case AV_SAMPLE_FMT_U8:
                        case AV_SAMPLE_FMT_U8P:  samples_buf_out[i] = b; break;
                        case AV_SAMPLE_FMT_S16:
                        case AV_SAMPLE_FMT_S16P: samples_buf_out[i] = b.asShortBuffer();  break;
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
            frame.sampleRate = samplesrate;
            frame.audioChannels = sampleschannels;
            frame.samples = samples_buf_out;

            if ((ret = swr_convert(samplesconvertctx, new PointerPointer(samples_ptr_out), sample_size_out, new PointerPointer(samples_ptr), sample_size_in)) < 0) {
                throw new Exception("swr_convert() error " + ret + ": Cannot convert audio samples.");
            }
            for (int i = 0; i < planes_out; i++) {
                samples_ptr_out[i].position(0).limit(ret * (planes_out > 1 ? 1 : sampleschannels));
                samples_buf_out[i].position(0).limit(ret * (planes_out > 1 ? 1 : sampleschannels));
            }
        }
    }
    public Frame grabaudio() throws Exception {
        return grabframe(true, false, true, false);
    }
    public Frame grabframe(boolean doAudio, boolean doVideo, boolean doProcessing, boolean keyFrames) throws Exception {
        if (oc == null || oc.isNull()) {
            throw new Exception("Could not grab: No AVFormatContext. (Has start() been called?)");
        } else if ((!doVideo) && (!doAudio || audio_st == null)) {
            return null;
        }
        boolean audioFrameGrabbed = framegrabbed && frame.samples != null;
        framegrabbed = false;
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
                processsamples();
            }
            frame.keyFrame = samples_frame.key_frame() != 0;
            frame.opaque = samples_frame;
            return frame;
        }
        boolean done = false;
        while (!done) {
            if (pkt2.size() <= 0) {
                if (av_read_frame(oc, pkt) < 0) {
                    if (doVideo) {
                        // The video codec may have buffered some frames
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
                        long pts = av_frame_get_best_effort_timestamp(samples_frame);
                        avutil.AVRational time_base = audio_st.time_base();
                        timestamp = 1000000L * pts * time_base.num() / time_base.den();
                        frame.samples = samples_buf;
                        /* if a frame has been decoded, output it */
                        if (doProcessing) {
                            processsamples();
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
}

