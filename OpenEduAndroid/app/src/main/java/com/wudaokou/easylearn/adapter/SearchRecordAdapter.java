package com.wudaokou.easylearn.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.wudaokou.easylearn.R;
import com.wudaokou.easylearn.constant.SubjectMap;
import com.wudaokou.easylearn.data.SearchRecord;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

public class SearchRecordAdapter extends RecyclerView.Adapter<SearchRecordAdapter.VH> {

    @NonNull
    @NotNull
    @Override
    public VH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_record_item, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull VH holder, int position) {
        SearchRecord searchRecord = data.get(position);
        HashMap<String, String> map = SubjectMap.getMap();
        holder.recordCourse.setText(map.get(searchRecord.subject));
        holder.recordSearchKey.setText(searchRecord.content);

        if (isPopular) {
            holder.recordCourse.setTextColor(holder.recordCourse.
                    getResources().getColor(R.color.amber_700));
            holder.recordCourse.setBackground(holder.recordCourse
            .getResources().getDrawable(R.drawable.popular_search_key_bg));
        }

        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(
                    v -> mOnItemClickListener.onItemClick(holder.itemView, position));
        }
    }

    @Override
    public int getItemCount() {
        if (data != null)
            return data.size();
        return 0;
    }

    public SearchRecord getItem(int position) {
        return data.get(position);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public static class VH extends RecyclerView.ViewHolder{
        public final TextView recordCourse;
        public final TextView recordSearchKey;
        public VH(View v) {
            super(v);
            recordCourse = (TextView) v.findViewById(R.id.recordCourse);
            recordSearchKey = (TextView) v.findViewById(R.id.recordSearchKey);
        }
    }

    private List<SearchRecord> data;
    private boolean isPopular;
    public SearchRecordAdapter(List<SearchRecord> data, boolean isPopular) {
        this.isPopular = isPopular;
        this.data = data;}
    public void updateData(List<SearchRecord> data) {
        this.data = data;
    }
}
