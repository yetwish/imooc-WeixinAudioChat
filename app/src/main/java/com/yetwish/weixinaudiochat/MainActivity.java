package com.yetwish.weixinaudiochat;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.yetwish.weixinaudiochat.adapter.RecorderAdapter;
import com.yetwish.weixinaudiochat.model.Recorder;
import com.yetwish.weixinaudiochat.widget.RecorderButton;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity  {

    private ListView lvChat;
    private ArrayAdapter<Recorder> mAdapter;
    private RecorderButton btnRecorder;
    private List<Recorder> mData = new ArrayList<>();
    private View mAnimView;
    private MediaManager mMediaManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        lvChat = (ListView) findViewById(R.id.main_lv_chat);
        btnRecorder = (RecorderButton) findViewById(R.id.main_btn_recorder);
        btnRecorder.setOnRecordCompleteListener(new RecorderButton.OnRecordCompleteListener() {
            @Override
            public void onRecordComplete(String filePath, float time) {
                //录音完成
                Recorder recorder = new Recorder(time, filePath);
                mData.add(recorder);
                mAdapter.notifyDataSetChanged();
                lvChat.setSelection(mData.size() - 1);
            }
        });
        mAdapter = new RecorderAdapter(this, mData);
        lvChat.setAdapter(mAdapter);
        mMediaManager = MediaManager.getInstance();
        lvChat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //动画相关
                if (mAnimView != null) {
                    mAnimView.setBackgroundResource(R.drawable.adj);
                    mAnimView = null;
                }
                mAnimView = view.findViewById(R.id.item_recorder_anim_view);
                mAnimView.setBackgroundResource(R.drawable.anim_play_recorder);
                AnimationDrawable anim = (AnimationDrawable)mAnimView.getBackground();
                anim.start();
                mMediaManager.playAudio(mAdapter.getItem(i).getFilePath(), new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        if (mAnimView != null) {
                            mAnimView.setBackgroundResource(R.drawable.adj);
                        }
                    }
                });
            }
        });
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaManager != null) {
            mMediaManager.release();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mMediaManager != null) {
            mMediaManager.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMediaManager != null) {
            mMediaManager.resume();
        }
    }
}
