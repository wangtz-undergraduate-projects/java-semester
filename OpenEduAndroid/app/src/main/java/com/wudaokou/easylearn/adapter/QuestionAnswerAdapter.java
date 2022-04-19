package com.wudaokou.easylearn.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wudaokou.easylearn.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class QuestionAnswerAdapter extends RecyclerView.Adapter<QuestionAnswerAdapter.VH> {
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    List<Integer> data; // -1 for unselected, 0 for false, 1 for true
    boolean hasSubmit;

    public static class VH extends RecyclerView.ViewHolder{
        public final Button button;
        public VH(View v) {
            super(v);
            button = v.findViewById(R.id.questionIdButton);
        }
    }

    @NonNull
    @NotNull
    @Override
    public QuestionAnswerAdapter.VH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_show_answer, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull QuestionAnswerAdapter.VH holder, int position) {
        int status = data.get(position);
        holder.button.setText(String.valueOf(position + 1));
        if (status == -1) { // 该题未做
            holder.button.setBackgroundColor(holder.button.getResources().getColor(R.color.grey_200));
            holder.button.setTextColor(holder.button.getResources().getColor(R.color.grey_700));
        } else if (!hasSubmit) { // 题目已做，尚未提交
            holder.button.setBackgroundColor(holder.button.getResources().getColor(R.color.blue_500));
            holder.button.setTextColor(holder.button.getResources().getColor(R.color.white));
        } else if (hasSubmit && status == 0) { // 题目已做且提交，答案错误
            holder.button.setBackgroundColor(holder.button.getResources().getColor(R.color.red_400));
            holder.button.setTextColor(holder.button.getResources().getColor(R.color.white));
        } else if (hasSubmit && status == 1) { // 题目已做且提交，答案正确
            holder.button.setBackgroundColor(holder.button.getResources().getColor(R.color.green_400));
            holder.button.setTextColor(holder.button.getResources().getColor(R.color.white));
        } else {
            holder.button.setBackgroundColor(holder.button.getResources().getColor(R.color.grey_200));
            holder.button.setTextColor(holder.button.getResources().getColor(R.color.grey_700));
        }

        if (mOnItemClickListener != null) {
            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(holder.itemView, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public QuestionAnswerAdapter(List<Integer> data, boolean hasSubmit) {
        this.data = data;
        this.hasSubmit = hasSubmit;
    }

    public void onSubmit() {
        this.hasSubmit = true;
    }

    public void updateData(List<Integer> data) {
        this.data = data;
    }
}
