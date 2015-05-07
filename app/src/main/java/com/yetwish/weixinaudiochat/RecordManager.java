package com.yetwish.weixinaudiochat;

import android.media.MediaRecorder;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 管理录音相关操作
 * Created by yetwish on 2015-05-07
 */

public class RecordManager {

    /**
     *  最长录音时间
     */
    public static final int MAX_RECORD_TIME = 60;
    /**
     * 录音相关类
     */
    private MediaRecorder mMediaRecorder;

    /**
     * 保存录音文件的包路径
     */
    private String mDir;

    /**
     * 当前录音文件的路径
     */
    private String mCurrentFilePath;

    /**
     * 已经准备好MediaRecorder
     */
    private boolean isPrepared;

    private OnRecordPreparedListener mListener;

    public void setOnRecordPreparedListener(OnRecordPreparedListener listener){
        this.mListener = listener;
    }

    /**
     * singleton
     */
    private static RecordManager instance = null;

    public static RecordManager getInstance(String dir){
        if(instance == null){
            synchronized (RecordManager.class){
                if(instance == null){
                    instance = new RecordManager(dir);
                }
            }
        }
        return instance;
    }

    private RecordManager(String dir){
        mMediaRecorder = new MediaRecorder();
        this.mDir = dir;
    }

    /**
     * 准备录音
     */
    public void prepareAudio(){
       try {
           //生成包
           File dir = new File(mDir);
           if(!dir.exists()){
               dir.mkdirs();
           }
           String fileName = generateFileName();
           File file = new File(dir,fileName);
           mCurrentFilePath = file.getAbsolutePath();
           mMediaRecorder = new MediaRecorder();
           //设置音频源为麦克风
           mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
           //设置输出文件
           mMediaRecorder.setOutputFile(file.getAbsolutePath());
           //设置音频格式
           mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
           //设置音频编码
           mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
           //准备录音
           mMediaRecorder.prepare();
           //开始录音
           mMediaRecorder.start();
            //准备结束
           isPrepared = true;

           if(mListener != null){
               //通知监听者已经准备好了
               mListener.onPrepareCompleted(mCurrentFilePath);
           }

       }catch (IllegalStateException e){
            e.printStackTrace();
       }catch (IOException e){
            e.printStackTrace();
       }
    }

    private String generateFileName() {
        return UUID.randomUUID().toString()+".amr";
    }

    /**
     * 释放资源
     */
    public void release(){
        mMediaRecorder.stop();
        mMediaRecorder.reset();
        mMediaRecorder.release();
        mMediaRecorder = null;
    }

    public void cancel(){
        release();
        if(mCurrentFilePath != null){
            File file = new File(mCurrentFilePath);
            file.delete();
            mCurrentFilePath = null;
        }
    }

    public int getVoiceLevel(int maxLevel){
        if(isPrepared){
            try {//可能会报错 getMaxAmplitude():1~32767
                return maxLevel * mMediaRecorder.getMaxAmplitude()/32768+1;
            }catch (Exception e){
            }
        }
        return 1;
    }


    public interface OnRecordPreparedListener{
        void onPrepareCompleted(String currentFilePath);
    }
}
