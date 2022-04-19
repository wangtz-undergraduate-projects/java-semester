package com.wudaokou.easylearn.widget;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.wudaokou.easylearn.R;
import com.wudaokou.easylearn.adapter.ChannelAdapter;
import com.wudaokou.easylearn.bean.SubjectChannelBean;
import com.wudaokou.easylearn.utils.EditModeHandler;
import com.wudaokou.easylearn.utils.IChannelType;

/**
 * 我的频道文字控件，右侧有编辑/完成按键
 */

public class MyChannelHeaderWidget implements IChannelType {
    private RecyclerView mRecyclerView;
    private EditModeHandler editModeHandler;

    public MyChannelHeaderWidget(EditModeHandler handler) {
        this.editModeHandler = handler;
    }

    @Override
    public ChannelAdapter.ChannelViewHolder createViewHolder(LayoutInflater mInflater, ViewGroup parent) {
        mRecyclerView = (RecyclerView) parent;
        return new MyChannelHeaderViewHolder(mInflater.inflate(R.layout.item_channel_my_header, parent, false));
    }

    @Override
    public void bindViewHolder(final ChannelAdapter.ChannelViewHolder holder, int position, SubjectChannelBean data) {
        final MyChannelHeaderViewHolder viewHolder = (MyChannelHeaderViewHolder) holder;
        // 右侧按键点击时改变样式，如编辑-》完成
        viewHolder.mEditModeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!viewHolder.mEditModeTv.isSelected()) {

                    if (editModeHandler != null)
                        editModeHandler.startEditMode(mRecyclerView);
                    viewHolder.mEditModeTv.setText("完成");
                } else {
                    if (editModeHandler != null)
                        editModeHandler.cancelEditMode(mRecyclerView);
                    viewHolder.mEditModeTv.setText("编辑");
                }
                viewHolder.mEditModeTv.setSelected(!viewHolder.mEditModeTv.isSelected());
            }
        });
    }

    public static class MyChannelHeaderViewHolder extends ChannelAdapter.ChannelViewHolder {
        private TextView mEditModeTv;

        public MyChannelHeaderViewHolder(View itemView) {
            super(itemView);
            mEditModeTv = (TextView) itemView.findViewById(R.id.id_edit_mode);
        }
    }
}
