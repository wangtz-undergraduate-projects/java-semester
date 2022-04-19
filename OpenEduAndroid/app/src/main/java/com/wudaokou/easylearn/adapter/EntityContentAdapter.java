package com.wudaokou.easylearn.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wudaokou.easylearn.R;
import com.wudaokou.easylearn.constant.Constant;
import com.wudaokou.easylearn.data.Content;
import com.wudaokou.easylearn.retrofit.BackendService;
import com.wudaokou.easylearn.retrofit.HistoryParam;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EntityContentAdapter
        extends RecyclerView.Adapter<EntityContentAdapter.VH>{

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private SearchResultAdapter.OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(SearchResultAdapter.OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public static class VH extends RecyclerView.ViewHolder{
        public final TextView subOrObjectLabel;
        public final TextView predicateLabel;
        public final ImageView relationImageView;
        public final ListView listView;
        public final ImageButton imageButton;
        public VH(View v) {
            super(v);
            subOrObjectLabel = (TextView) v.findViewById(R.id.subOrObjectLabel);
            predicateLabel = (TextView) v.findViewById(R.id.entityContentLabel);
            relationImageView = (ImageView) v.findViewById(R.id.relationImageView);
            imageButton = (ImageButton) v.findViewById(R.id.collapseButton);
            listView = (ListView) v.findViewById(R.id.collapseListView);
        }
    }

    @NonNull
    @NotNull
    @Override
    public EntityContentAdapter.VH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.entity_content_item, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull EntityContentAdapter.VH holder, int position) {
        Content content = data.get(position);
        holder.predicateLabel.setText(content.predicate_label);
        if (content.object_label != null) {
            holder.subOrObjectLabel.setText(content.object_label);
            holder.relationImageView.setImageResource(R.drawable.arrow_object);
        } else if (content.subject_label != null) {
            holder.subOrObjectLabel.setText(content.subject_label);
            holder.relationImageView.setImageResource(R.drawable.arrow_subject);
        }

        // todo 设置监听器
        // todo 设置监听器
        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.listView.getVisibility() == View.VISIBLE) {
                    holder.listView.setVisibility(View.INVISIBLE);
                    holder.imageButton.setImageResource(R.drawable.expand);
                } else {
                    holder.listView.setVisibility(View.VISIBLE);
                    holder.imageButton.setImageResource(R.drawable.collapse);
                }
            }
        });

        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    mOnItemClickListener.onItemClick(holder.itemView, position);
                }
            });
        }

//        holder.listView.setAdapter(new EntityContentCollapseAdapter(
//                data.get(position).entityFeatureList, R.layout.entity_content_collapse_item, inflater));
    }

    @Override
    public int getItemCount() {
        if (data != null) {
            return data.size();
        } else {
            return 0;
        }
    }

    private List<Content> data;
    private LayoutInflater inflater;
    public EntityContentAdapter(List<Content> data, LayoutInflater inflater) {
        this.data = data;
        this.inflater = inflater;
    }
    public void updateData(List<Content> data) {
        this.data = data;
    }
}
