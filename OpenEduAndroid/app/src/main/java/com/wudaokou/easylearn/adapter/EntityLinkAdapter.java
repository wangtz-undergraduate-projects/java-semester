package com.wudaokou.easylearn.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wudaokou.easylearn.R;
import com.wudaokou.easylearn.retrofit.entityLink.EntityLinkObject;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EntityLinkAdapter extends RecyclerView.Adapter<EntityLinkAdapter.VH> {
    private List<EntityLinkObject> data;
    private int selectedPos = RecyclerView.NO_POSITION;

    public void setSelectedPos(int selectedPos) {
        this.selectedPos = selectedPos;
    }

    public EntityLinkAdapter(List<EntityLinkObject> data) {
        this.data = data;
    }

    // 点击函数，由EntityLinkResultActivity设置
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
    OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @NotNull
    @Override
    public VH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.entity_link_item, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull VH holder, int position) {
        EntityLinkObject o = data.get(position);
        holder.type.setText(o.getEntity_type());
        holder.name.setText(o.getEntity());
        holder.itemView.setActivated(selectedPos == position);

        if(onItemClickListener != null){
            holder.itemView.setOnClickListener(v -> {
                int position1 = holder.getLayoutPosition();
                onItemClickListener.onItemClick(holder.itemView, position1);
            });
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class VH extends RecyclerView.ViewHolder{
        public final TextView type;
        public final TextView name;
        public VH(View v) {
            super(v);
            type = (TextView) v.findViewById(R.id.entityLinkType);
            name = (TextView) v.findViewById(R.id.entityLinkName);
        }
    }
}
