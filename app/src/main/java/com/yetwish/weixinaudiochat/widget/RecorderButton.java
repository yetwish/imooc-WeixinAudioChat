package com.yetwish.weixinaudiochat.widget;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.yetwish.weixinaudiochat.DialogManager;
import com.yetwish.weixinaudiochat.R;
import com.yetwish.weixinaudiochat.RecordManager;

/**
 * TODO 增加震动
 * Created by yetwish on 2015-05-06
 */

public class RecorderButton extends Button {

    public static final String TAG = RecorderButton.class.getSimpleName();

    /**
     * want to cancel的y轴偏移量
     */
    private static final int TRANSLATION_Y = 100;

    private static final int MAX_VOICE_LEVEL = 7;
    //button的状态
    private static final int STATE_NORMAL = 1;
    private static final int STATE_RECORDING = 2;
    private static final int STATE_WANT_CANCEL = 3;

    /**
     * BUTTON的当前状态
     */
    private int currentState = STATE_NORMAL;

    /**
     * 记录是否已开始录音
     */
    private boolean isRecording;

    /**
     * 录音完成回调接口
     */
    private OnRecordCompleteListener mListener;

    /**
     * 记录录音时间
     */
    private float recordTime;

    private String mRecordFilePath;

    private String mDir;

    /**
     * 用以管理dialog
     */
    private DialogManager mDialogManager;

    /**
     * 管理录音相关操作
     */
    private RecordManager mRecordManager;

    /**
     * 判断recordManager是否已经准备好
     */
    private boolean isReady;

    public void setOnRecordCompleteListener(OnRecordCompleteListener listener) {
        mListener = listener;
    }

    //msgs
    private static final int MSG_RECORD_PREPARED = 0x101;
    private static final int MSG_DIALOG_DISMISS = 0x102;
    private static final int MSG_VOICE_CHANGED = 0x103;
    private static final int MSG_OVER_RECORD_TIME = 0x104;

    private Runnable mGetVoiceRunnable = new Runnable() {
        @Override
        public void run() {
            //每100ms监测一次
            try {
                while(isRecording){
                    Thread.sleep(100L);
                    mHandler.sendEmptyMessage(MSG_VOICE_CHANGED);
                    recordTime += 0.1f;
                    if(Math.round(recordTime ) >= RecordManager.MAX_RECORD_TIME ){
                        //判断如果当前时间已经超过规定最长录音时间则结束录音 完成录音
                        mHandler.sendEmptyMessage(MSG_OVER_RECORD_TIME);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_RECORD_PREPARED:
                    //开始录音
                    mDialogManager.initRecordingDialog();
                    isRecording = true;
                    //开启一个线程用以实时监测消息
                    if (mRecordManager != null){
                        new Thread(mGetVoiceRunnable).start();
                    }
                    break;
                case MSG_DIALOG_DISMISS:
                    mDialogManager.dismissDialog();
                    break;
                case MSG_VOICE_CHANGED:
                    mDialogManager.updateVoiceLevel(mRecordManager.getVoiceLevel(MAX_VOICE_LEVEL));
                    break;
                case MSG_OVER_RECORD_TIME:
                    notifyRecordCompleted();
                    reset();
                    break;
            }
        }
    };

    public RecorderButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        //获取dialogManager对象
        mDialogManager = new DialogManager(getContext());
        //获取sd卡装载情况

        //获取recordManager对象
        if (checkSdCard()) {
            mRecordManager = RecordManager.getInstance(mDir);
            mRecordManager.setOnRecordPreparedListener(new RecordManager.OnRecordPreparedListener() {
                @Override
                public void onPrepareCompleted(String currentFilePath) {
                    mRecordFilePath = currentFilePath;
                    isReady = true;
                    //发送已经准备完成的消息
                    mHandler.sendEmptyMessage(MSG_RECORD_PREPARED);
                }
            });
        }
        //setLongClickListener
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //调用AudioManager开始录音
                if (mRecordManager != null) {
                    mRecordManager.prepareAudio();
                }
                isReady = true;
                return false;
            }
        });
    }

    private boolean checkSdCard() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //未转载
            Toast.makeText(getContext(), "请检查你的SD卡", Toast.LENGTH_LONG).show();
            Log.w(TAG,"sdCard hasn't been mounted");
            return false;
        }
        else {
            mDir = Environment.getExternalStorageDirectory()+"/weixin_demo";
            Log.w(TAG,"sdCard has been mounted");
            return true;
        }
    }

    public RecorderButton(Context context) {
        super(context, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                changeState(STATE_RECORDING);
                break;
            case MotionEvent.ACTION_MOVE:
                if (isRecording) {
                    if (wantCancel(event.getX(), event.getY())) {
                        changeState(STATE_WANT_CANCEL);
                    } else {
                        changeState(STATE_RECORDING);
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                if(!isReady){
                    //还未触发onLongClick 无需释放资源
                    reset();
                    return super.onTouchEvent(event);
                }
                if(!isRecording || recordTime<0.6f){
                    //还未获得recordPrepareCompleted回调,或录音时间过短
                    if(mRecordManager != null){
                        mDialogManager.tooShort();
                        mRecordManager.cancel();
                        //1s后通知handler取消dialog的显示
                        mHandler.sendEmptyMessageDelayed(MSG_DIALOG_DISMISS,1000L);
                    }
                }
                else if (currentState == STATE_RECORDING) {
                    //完成录音
                   notifyRecordCompleted();
                } else if (currentState == STATE_WANT_CANCEL) {
                    //取消录音
                    mDialogManager.dismissDialog();
                    mRecordManager.cancel();
                }
                reset();
                break;
        }
        return super.onTouchEvent(event);
    }

    private void notifyRecordCompleted(){
        //完成录音
        if (mListener != null) {
            //将文件路径和录音时间回调给activity
            mListener.onRecordComplete(mRecordFilePath, recordTime);
        }
        //释放mRecordManager占用资源
        mRecordManager.release();
        //将dialog撤销
        mDialogManager.dismissDialog();
    }


    /**
     * 状态初始化，清除标识位
     */
    private void reset() {
        changeState(STATE_NORMAL);
        isRecording = false;
        isReady = false;
        recordTime = 0;
    }

    private boolean wantCancel(float x, float y) {
        if (isRecording) {
            if (x < 0 || x > getWidth())
                return true;
            if (y < -TRANSLATION_Y || y > getHeight() + TRANSLATION_Y)
                return true;
        }
        return false;
    }

    private void changeState(int state) {
        if (currentState != state) {
            currentState = state;
            switch (state) {
                case STATE_NORMAL:
                    setBackgroundResource(R.drawable.btn_normal_shape);
                    setText(R.string.btn_normal);
                    break;
                case STATE_RECORDING:
                    setBackgroundResource(R.drawable.btn_recorder_shape);
                    setText(R.string.btn_recording);
                    mDialogManager.record();
                    break;
                case STATE_WANT_CANCEL:
                    setBackgroundResource(R.drawable.btn_recorder_shape);
                    setText(R.string.btn_want_to_cancel);
                    mDialogManager.wantCancel();
                    break;
            }

        }
    }

    public interface OnRecordCompleteListener {
        void onRecordComplete(String filePath, float time);
    }
}
