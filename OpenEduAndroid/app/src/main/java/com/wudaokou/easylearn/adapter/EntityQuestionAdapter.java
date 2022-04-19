package com.wudaokou.easylearn.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wudaokou.easylearn.R;
import com.wudaokou.easylearn.data.Content;
import com.wudaokou.easylearn.data.Question;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

public class EntityQuestionAdapter
        extends RecyclerView.Adapter<EntityQuestionAdapter.VH>{

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private SearchResultAdapter.OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(SearchResultAdapter.OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @NonNull
    @NotNull
    @Override
    public VH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.entity_question_item, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull VH holder, int position) {
        Question question = data.get(position);
        // todo 将问题拆分为问题和选项
        String qBody = question.qBody;
        int splitPos = qBody.lastIndexOf("A.");
        if (splitPos == -1) {
            splitPos = qBody.lastIndexOf("A、");
        }
        if (splitPos == -1) {
            splitPos = qBody.lastIndexOf("A．");
        }
        String questionText;
        if (splitPos != -1) {
            questionText = qBody.substring(0, splitPos);
        } else {
            questionText = qBody;
        }
        holder.qBodyText.setText(questionText);

        holder.correctCount.setText(String.format(Locale.CHINA, "做对%d次", question.totalCount - question.wrongCount));
        holder.wrongCount.setText(String.format(Locale.CHINA,"做错%d次", question.wrongCount));
        //判断是否设置了监听器
        if(mOnItemClickListener != null){
            //为ItemView设置监听器
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getLayoutPosition(); // 1
                    mOnItemClickListener.onItemClick(holder.itemView,position); // 2
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (data != null) {
            return data.size();
        } else {
            return 0;
        }
    }

    public static class VH extends RecyclerView.ViewHolder{
        public final TextView qBodyText;
        public final TextView wrongCount;
        public final TextView correctCount;
        public VH(View v) {
            super(v);
            qBodyText = (TextView) v.findViewById(R.id.qBodyText);
            wrongCount = (TextView) v.findViewById(R.id.wrongCount);
            correctCount = (TextView) v.findViewById(R.id.correctCount);
        }
    }

    private List<Question> data;
    public EntityQuestionAdapter(List<Question> data) {
        this.data = data;
    }
    public void updateData(List<Question> data) {
        this.data = data;
    }
}
