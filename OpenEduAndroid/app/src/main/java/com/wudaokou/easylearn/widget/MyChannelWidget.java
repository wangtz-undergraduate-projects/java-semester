package com.wudaokou.easylearn.widget;

import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.wudaokou.easylearn.R;
import com.wudaokou.easylearn.adapter.ChannelAdapter;
import com.wudaokou.easylearn.bean.SubjectChannelBean;
import com.wudaokou.easylearn.constant.Constant;
import com.wudaokou.easylearn.utils.EditModeHandler;
import com.wudaokou.easylearn.utils.IChannelType;

/**
 * 我的频道控件
 */

public class MyChannelWidget implements IChannelType {
    private RecyclerView mRecyclerView;
    private final EditModeHandler editModeHandler;

    public MyChannelWidget(EditModeHandler editModeHandler) {
        this.editModeHandler = editModeHandler;
    }

    @Override
    public ChannelAdapter.ChannelViewHolder createViewHolder(LayoutInflater mInflater, ViewGroup parent) {
        mRecyclerView = (RecyclerView) parent;
        return new MyChannelHeaderViewHolder(mInflater.inflate(R.layout.item_channel_my, parent, false));
    }

    @Override
    public void bindViewHolder(final ChannelAdapter.ChannelViewHolder holder, final int position, final SubjectChannelBean data) {
        final MyChannelHeaderViewHolder myHolder = (MyChannelHeaderViewHolder) holder;
        myHolder.mChannelTitleTv.setText(data.getTname());
        // 设置文字大小，通过判断tab中的文字长度，如果有4或者4个字以上则为16sp大小
        int textSize = data.getTname().length() >= 4 ? 14 : 16;
        myHolder.mChannelTitleTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        // 通过channelbean的type值设置其文字背景颜色
        int type = data.getTabType();
        // 设置文字背景颜色
        myHolder.mChannelTitleTv.setBackgroundResource(R.drawable.entity_label_background);
        // 根据tab状态设置文字颜色
        myHolder.mChannelTitleTv.setTextColor(type == Constant.ITEM_DEFAULT ? Color.RED :
                type == Constant.ITEM_UNEDIT ? Color.parseColor("#666666") : Color.parseColor("#333333"));
        // 设置右上角删除按钮是否可见
        myHolder.mDeleteIv.setVisibility(data.getEditStatus() == 1 ? View.VISIBLE : View.INVISIBLE);
        myHolder.mChannelTitleTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editModeHandler != null && data.getTabType() == Constant.ITEM_EDIT) {
                    editModeHandler.clickMyChannel(mRecyclerView, holder);
                }
            }
        });
        myHolder.mChannelTitleTv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (editModeHandler != null && data.getTabType() == Constant.ITEM_EDIT) {
                    editModeHandler.touchMyChannel(motionEvent, holder);
                }
                return false;
            }
        });
        myHolder.mChannelTitleTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (editModeHandler != null && data.getTabType() == Constant.ITEM_EDIT) {
                    editModeHandler.clickLongMyChannel(mRecyclerView, holder);
                }
                return true;
            }
        });
    }

    public static class MyChannelHeaderViewHolder extends ChannelAdapter.ChannelViewHolder {
        private final TextView mChannelTitleTv;
        private final ImageView mDeleteIv;

        private MyChannelHeaderViewHolder(View itemView) {
            super(itemView);
            mChannelTitleTv = (TextView) itemView.findViewById(R.id.id_channel_title);
            mDeleteIv = (ImageView) itemView.findViewById(R.id.id_delete_icon);
        }
    }
}
