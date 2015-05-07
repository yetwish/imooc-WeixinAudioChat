package com.yetwish.weixinaudiochat.adapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.yetwish.weixinaudiochat.R;
import com.yetwish.weixinaudiochat.RecordManager;
import com.yetwish.weixinaudiochat.model.Recorder;

import java.util.List;

/**
 * Created by yetwish on 2015-05-07
 */

public class RecorderAdapter extends ArrayAdapter<Recorder> {

    /**
     * lengthView 的最小长度
     */
    private int mMinWidth;

    /**
     * lengthView 的最大增长长度
     */
    private int mMaxWidth;

    private Context mContext;

    public RecorderAdapter(Context context,List<Recorder> data){
        //使用自己的item_layout
        this(context,-1,data);
        mContext = context;
        //获取屏幕宽度
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager  wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        //设置lengthView 最短 最长长度
        mMinWidth = (int)(dm.widthPixels*0.15);
        mMaxWidth = (int)(dm.widthPixels*0.7);

    }


    public RecorderAdapter(Context context, int resource, List<Recorder> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_recorder,parent,false);
            holder = new ViewHolder();
            holder.lengthView = (FrameLayout)convertView.findViewById(R.id.item_recorder_fl_length);
            holder.tvTime = (TextView)convertView.findViewById(R.id.item_recorder_tv_time);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        holder.lengthView.setMinimumWidth((int)(mMinWidth +
                mMaxWidth* getItem(position).getTime()/ RecordManager.MAX_RECORD_TIME));
        holder.tvTime.setText(Math.round(getItem(position).getTime())+"\"");
        return convertView;
    }

    private static class ViewHolder{
        FrameLayout lengthView ;
        TextView tvTime;
    }
}
