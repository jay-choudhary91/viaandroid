package com.cryptoserver.composer.utils;

import android.widget.LinearLayout;

import uk.co.jakelee.vidsta.listeners.FullScreenClickListener;
import uk.co.jakelee.vidsta.listeners.VideoStateListeners;
import uk.co.jakelee.vidsta.VidstaPlayer;

public class listeners {
    final private LinearLayout messagesContainer;

    public listeners(LinearLayout container) {
        this.messagesContainer = container;
    }

    final public VideoStateListeners.OnVideoBufferingListener bufferingListener = new VideoStateListeners.OnVideoBufferingListener() {
        @Override
        public void OnVideoBuffering(VidstaPlayer evp, int buffPercent) {
            //listenerhelper.createListenerLog(messagesContainer, "Video buffering " + buffPercent + "%!");
        }
    };

    final public VideoStateListeners.OnVideoErrorListener errorListener = new VideoStateListeners.OnVideoErrorListener() {
        @Override
        public void OnVideoError(int what, int extra) {
            //listenerhelper.createListenerLog(messagesContainer, "Video errored!");
        }
    };

    final public VideoStateListeners.OnVideoFinishedListener finishedListener = new VideoStateListeners.OnVideoFinishedListener() {
        @Override
        public void OnVideoFinished(VidstaPlayer evp) {
            //listenerhelper.createListenerLog(messagesContainer, "Video finished!");
        }
    };

    final public VideoStateListeners.OnVideoPausedListener pausedListener = new VideoStateListeners.OnVideoPausedListener() {
        @Override
        public void OnVideoPaused(VidstaPlayer evp) {
           // listenerhelper.createListenerLog(messagesContainer, "Video paused!");
        }
    };

    final public VideoStateListeners.OnVideoRestartListener restartListener = new VideoStateListeners.OnVideoRestartListener() {
        @Override
        public void OnVideoRestart(VidstaPlayer evp) {
            //listenerhelper.createListenerLog(messagesContainer, "Video restarted!");
        }
    };

    final public VideoStateListeners.OnVideoStartedListener startedListener = new VideoStateListeners.OnVideoStartedListener() {
        @Override
        public void OnVideoStarted(VidstaPlayer evp) {
            //listenerhelper.createListenerLog(messagesContainer, "Video started!");
        }
    };

    final public VideoStateListeners.OnVideoStoppedListener stoppedListener = new VideoStateListeners.OnVideoStoppedListener() {
        @Override
        public void OnVideoStopped(VidstaPlayer evp) {
            //listenerhelper.createListenerLog(messagesContainer, "Video stopped!");
        }
    };

    final public FullScreenClickListener fullScreenListener = new FullScreenClickListener() {
        @Override
        public void onToggleClick(boolean isFullscreen) {
           // listenerhelper.createListenerLog(messagesContainer, "Video set to " + (isFullscreen ? "not " : "") + " fullscreen!");
        }
    };
}
