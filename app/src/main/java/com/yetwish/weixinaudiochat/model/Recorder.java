package com.yetwish.weixinaudiochat.model;

/**
 * 录音信息类
 * Created by yetwish on 2015-05-07
 */

public class Recorder {
    private float time;
    private String filePath;

    public Recorder(float time, String filePath) {
        this.time = time;
        this.filePath = filePath;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
