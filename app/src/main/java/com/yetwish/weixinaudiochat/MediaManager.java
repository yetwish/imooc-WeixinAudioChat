package com.yetwish.weixinaudiochat;

import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;

/**
 * 播放录音相关类
 * Created by yetwish on 2015-05-07
 */

public class MediaManager {

    /**
     * 使用mediaPlayer 播放音频
     */
    private MediaPlayer mMediaPlayer;

    private boolean isPaused;

    /**
     * singleton
     */
    private static class MediaManagerHolder {
        private static final MediaManager instance = new MediaManager();
    }

    public static MediaManager getInstance() {
        return MediaManagerHolder.instance;
    }

    private MediaManager() {
    }

    /**
     * 播放音频
     *
     * @param filePath
     */
    public void playAudio(String filePath, MediaPlayer.OnCompletionListener listener) {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
                    mMediaPlayer.reset();
                    return false;
                }
            });
        } else {
            mMediaPlayer.reset();
        }
        try {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(listener);
            mMediaPlayer.setDataSource(filePath);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 暂停播放
     */
    public void pause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            isPaused = true;
        }
    }

    /**
     * 继续播放
     */
    public void resume() {
        if (mMediaPlayer != null && isPaused){
            mMediaPlayer.start();
            isPaused = false;
        }
    }

    /**
     * 释放资源
     */
    public void release(){
        if(mMediaPlayer !=null){
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
