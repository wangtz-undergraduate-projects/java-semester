package com.wudaokou.easylearn.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wudaokou.easylearn.R;
import com.wudaokou.easylearn.data.HomeCourseItem;
import com.wudaokou.easylearn.data.MyDatabase;
import com.wudaokou.easylearn.data.SearchResult;

import org.jetbrains.annotations.NotNull;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class HomeCourseItemAdapter
        extends RecyclerView.Adapter<HomeCourseItemAdapter.VH>{
    boolean forHistory;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public static class VH extends RecyclerView.ViewHolder{
        public final TextView entityLabel;
        public final TextView entityDescription;
        public final TextView entityKeyWord;
        public VH(View v) {
            super(v);
            entityLabel = (TextView) v.findViewById(R.id.entityLabel);
            entityDescription = (TextView) v.findViewById(R.id.entityDescriptionLabel);
            entityKeyWord = (TextView) v.findViewById(R.id.entityKeyWord);
        }
    }

    @NonNull
    @NotNull
    @Override
    public HomeCourseItemAdapter.VH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_course_item, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull HomeCourseItemAdapter.VH holder, int position) {
        // todo
        if (data != null) {
            HomeCourseItem homeCourseItem = data.get(position);
            if (homeCourseItem.result != null) {
                holder.entityLabel.setText(homeCourseItem.result.label);

                if (forHistory) {
                    holder.entityKeyWord.setText(homeCourseItem.createdAt.replace('T', ' '));
                } else {
                    holder.entityKeyWord.setText(String.format("关键词: %s", homeCourseItem.result.searchKey));
                }
                String category = homeCourseItem.result.category;
                if (category != null && category.length() > 10) {
                    category = category.substring(0, 9) + "...";
                }
                holder.entityDescription.setText(String.format("分类: %s", category));
            } else {
                holder.entityLabel.setText("Label");
                holder.entityDescription.setText("Category");
                holder.entityKeyWord.setText("KeyWord");
            }

            if (homeCourseItem.result.hasRead) {
                holder.entityLabel.setTextColor(holder.entityLabel.getContext()
                    .getResources().getColor(R.color.orange_700));
                holder.entityKeyWord.setTextColor(holder.entityKeyWord.getContext()
                        .getResources().getColor(R.color.grey_500));
                holder.entityDescription.setTextColor(holder.entityDescription.getContext()
                        .getResources().getColor(R.color.grey_500));
            } else {
                holder.entityLabel.setTextColor(holder.entityLabel.getContext()
                        .getResources().getColor(R.color.orange_900));
                holder.entityKeyWord.setTextColor(holder.entityKeyWord.getContext()
                        .getResources().getColor(R.color.grey_800));
                holder.entityDescription.setTextColor(holder.entityDescription.getContext()
                        .getResources().getColor(R.color.grey_800));
            }

        } else {
            holder.entityLabel.setText("Label");
            holder.entityDescription.setText("Description");
        }

        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SearchResult searchResult = data.get(position).result;
                    searchResult.hasRead = true;
                    int position = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.itemView, position);
                    MyDatabase.databaseWriteExecutor.submit(new Runnable() {
                        @Override
                        public void run() {
                            MyDatabase.getDatabase(v.getContext()).searchResultDAO()
                                    .updateSearchResult(searchResult);
                        }
                    });
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (data != null)
            return data.size();
        return 1;
    }

    List<HomeCourseItem> data;
    public void updateData(List<HomeCourseItem> data) {
        this.data = data;
    }

    public HomeCourseItemAdapter(List<HomeCourseItem> data, boolean forHistory) {
        this.forHistory = forHistory;
        this.data = data;
    }

    public HomeCourseItemAdapter() {
//        this.data = new ArrayList<>();
//        data.add();
    }
}
