package com.yetwish.weixinaudiochat;

import android.app.Dialog;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by yetwish on 2015-05-06
 */

public class DialogManager {
    /**
     * dialog对象
     */
    private Dialog mDialog;

    /**
     * 左边图标imageView
     */
    private ImageView ivIcon;

    /**
     * 显示声音imageView
     */
    private ImageView ivVoice;

    /**
     * 底层标签textView
     */
    private TextView tvLabel;

    /**
     * 上下文对象，用于获取布局View 和资源文件
     */
    private Context mContext;


    public DialogManager(Context context) {
        mContext = context;
    }

    /**
     * 初始化/创建 recordDialog
     */
    public void initRecordingDialog() {
        mDialog = new Dialog(mContext, R.style.Theme_AudioDialog);
        View layout =LayoutInflater.from(mContext).inflate(R.layout.dialog_record, null);
        mDialog.setContentView(layout);
        ivIcon = (ImageView) layout.findViewById(R.id.dialog_iv_icon);
        ivVoice = (ImageView) layout.findViewById(R.id.dialog_iv_voice);
        tvLabel = (TextView) layout.findViewById(R.id.dialog_tv_label);
        mDialog.show();
    }

    /**
     * dialog转换成record 视图
     */
    public void record() {
        if (mDialog != null && mDialog.isShowing()) {
            ivIcon.setImageResource(R.drawable.recorder);
            ivVoice.setVisibility(View.VISIBLE);
            tvLabel.setText(R.string.dialog_label_record);
            tvLabel.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));//设置背景色透明
        }
    }

    /**
     * dialog转换为wantCancel视图
     */
    public void wantCancel() {
        if (mDialog != null && mDialog.isShowing()) {
            ivIcon.setImageResource(R.drawable.cancel);
            ivVoice.setVisibility(View.GONE);
            tvLabel.setText(R.string.dialog_label_cancel);
            tvLabel.setBackgroundColor((mContext.getResources().getColor(R.color.dialog_label_bg)));
        }
    }

    /**
     * dialog转换为too_short视图
     */
    public void tooShort() {
        if (mDialog != null && mDialog.isShowing()) {
            ivIcon.setImageResource(R.drawable.voice_to_short);
            ivVoice.setVisibility(View.GONE);
            tvLabel.setText(R.string.dialog_label_too_short);
        }
    }

    /**
     * dismissDialog dialog
     */
    public void dismissDialog() {
        if(mDialog != null && mDialog.isShowing()){
            mDialog.dismiss();
            mDialog = null;
        }
    }

    /**
     * 根据level参数动态加载要显示的drawable文件
     * @param level
     */
    public void updateVoiceLevel(int level){
        if(mDialog!=null && mDialog.isShowing()){
            int resId = mContext.getResources().getIdentifier("v"+level,"drawable",mContext.getPackageName());
            ivVoice.setImageResource(resId);
        }
    }
}
