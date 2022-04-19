package com.wudaokou.easylearn.widget;

import android.util.TypedValue;
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
 * 更多频道控件
 */

public class RecChannelWidget implements IChannelType {
    private final EditModeHandler editModeHandler;
    private RecyclerView mRecyclerView;

    public RecChannelWidget(EditModeHandler editModeHandler) {
        this.editModeHandler = editModeHandler;
    }

    @Override
    public ChannelAdapter.ChannelViewHolder createViewHolder(LayoutInflater mInflater, ViewGroup parent) {
        this.mRecyclerView = (RecyclerView) parent;
        return new RecChannelHeaderViewHolder(mInflater.inflate(R.layout.item_channel_rec, parent, false));
    }

    @Override
    public void bindViewHolder(final ChannelAdapter.ChannelViewHolder holder, int position, SubjectChannelBean data) {
        RecChannelHeaderViewHolder recHolder = (RecChannelHeaderViewHolder) holder;
        recHolder.mChannelTitleTv.setText(data.getTname());
        int textSize = data.getTname().length() >= 4 ? 14 : 16;
        recHolder.mChannelTitleTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        recHolder.mChannelTitleTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editModeHandler != null) {
                    editModeHandler.clickRecChannel(mRecyclerView, holder);
                }
            }
        });
    }

    private static class RecChannelHeaderViewHolder extends ChannelAdapter.ChannelViewHolder {
        private final TextView mChannelTitleTv;

        private RecChannelHeaderViewHolder(View itemView) {
            super(itemView);
            mChannelTitleTv = (TextView) itemView.findViewById(R.id.id_channel_title);
        }
    }
}
