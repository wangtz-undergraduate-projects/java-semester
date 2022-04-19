package com.wudaokou.easylearn.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wudaokou.easylearn.R;
import com.wudaokou.easylearn.data.Msg;

import java.util.List;

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder>{

    private List<Msg> list;
    public MsgAdapter(List<Msg> list){
        this.list = list;
    }
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView left_msg;
        ImageView left_pic;

        TextView right_msg;
        ImageView right_pic;

        public ViewHolder(View view){
            super(view);
            left_msg = (TextView) view.findViewById(R.id.left_msg);
            left_pic = (ImageView) view.findViewById(R.id.left_pic);

            right_msg = view.findViewById(R.id.right_msg);
            right_pic = view.findViewById(R.id.right_pic);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Msg msg = list.get(position);
        if(msg.getType() == Msg.TYPE_RECEIVED){
            //如果是收到的消息，则显示左边的消息布局，将右边的消息布局隐藏
//            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.left_msg.setText(msg.getContent());

            //此处隐藏右面的消息布局用的是 View.GONE
//            holder.rightLayout.setVisibility(View.GONE);
            holder.right_pic.setVisibility(View.GONE);
            holder.right_msg.setVisibility(View.GONE);
        }else if(msg.getType() == Msg.TYPE_SEND){
            //如果是发出的消息，则显示右边的消息布局，将左边的消息布局隐藏
//            holder.rightLayout.setVisibility(View.VISIBLE);
//            holder.
            holder.right_msg.setText(msg.getContent());

            //同样使用View.GONE
//            holder.leftLayout.setVisibility(View.GONE);
            holder.left_pic.setVisibility(View.GONE);
            holder.left_msg.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

