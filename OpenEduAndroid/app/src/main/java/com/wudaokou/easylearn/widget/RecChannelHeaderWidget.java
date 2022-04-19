package com.wudaokou.easylearn.widget;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wudaokou.easylearn.R;
import com.wudaokou.easylearn.adapter.ChannelAdapter;
import com.wudaokou.easylearn.bean.SubjectChannelBean;
import com.wudaokou.easylearn.utils.IChannelType;

/**
 * 推荐分类（文字界面）
 */

public class RecChannelHeaderWidget implements IChannelType {
    @Override
    public ChannelAdapter.ChannelViewHolder createViewHolder(LayoutInflater mInflater, ViewGroup parent) {
        return new MyChannelHeaderViewHolder(mInflater.inflate(R.layout.item_channel_rec_header, parent, false));
    }

    @Override
    public void bindViewHolder(ChannelAdapter.ChannelViewHolder holder, int position, SubjectChannelBean data) {

    }

    public static class MyChannelHeaderViewHolder extends ChannelAdapter.ChannelViewHolder {

        public MyChannelHeaderViewHolder(View itemView) {
            super(itemView);
        }
    }
}

