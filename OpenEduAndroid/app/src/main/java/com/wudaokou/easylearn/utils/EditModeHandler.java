package com.wudaokou.easylearn.utils;

import android.view.MotionEvent;

import androidx.recyclerview.widget.RecyclerView;

import com.wudaokou.easylearn.adapter.ChannelAdapter;

public abstract class EditModeHandler {
    public void startEditMode(RecyclerView mRecyclerView) {
    }

    public void cancelEditMode(RecyclerView mRecyclerView) {
    }

    public void clickMyChannel(RecyclerView mRecyclerView, ChannelAdapter.ChannelViewHolder holder) {
    }

    public void clickLongMyChannel(RecyclerView mRecyclerView, ChannelAdapter.ChannelViewHolder holder) {
    }

    public void touchMyChannel(MotionEvent motionEvent, ChannelAdapter.ChannelViewHolder holder) {
    }

    public void clickRecChannel(RecyclerView mRecyclerView, ChannelAdapter.ChannelViewHolder holder) {
    }
}
