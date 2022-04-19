package com.wudaokou.easylearn.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wudaokou.easylearn.R;
import com.wudaokou.easylearn.constant.Constant;
import com.wudaokou.easylearn.data.MyDatabase;
import com.wudaokou.easylearn.data.SearchResult;
import com.wudaokou.easylearn.retrofit.BackendObject;
import com.wudaokou.easylearn.retrofit.BackendService;
import com.wudaokou.easylearn.retrofit.HistoryParam;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.VH>{
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }

    @NonNull
    @NotNull
    @Override
    public VH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result_item, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull VH holder, int position) {
        SearchResult searchResult = data.get(position);
        holder.label.setText(searchResult.label);
        holder.category.setText(searchResult.category);
        if (searchResult.hasRead) {
            holder.label.setTextColor(holder.label.getContext()
                    .getResources().getColor(R.color.grey_500));
        } else {
            holder.label.setTextColor(holder.label.getContext()
                    .getResources().getColor(R.color.grey_900));
        }

        if (searchResult.hasStar) {
            holder.starButton.setImageResource(R.drawable.heart_fill);
        } else {
            holder.starButton.setImageResource(R.drawable.heart_blank);
        }

        holder.starButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchResult.hasStar = !searchResult.hasStar;

                if (searchResult.hasStar) {
                    holder.starButton.setImageResource(R.drawable.heart_fill);
                } else {
                    holder.starButton.setImageResource(R.drawable.heart_blank);
                }

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(Constant.backendBaseUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                BackendService service = retrofit.create(BackendService.class);

                if (searchResult.hasStar) {
                   service.starEntity(Constant.backendToken,
                           new HistoryParam(searchResult.course.toUpperCase(),
                            searchResult.label, searchResult.uri, searchResult.category, searchResult.searchKey))
                           .enqueue(new Callback<BackendObject>() {
                        @Override
                        public void onResponse(@NotNull Call<BackendObject> call,
                                               @NotNull Response<BackendObject> response) {
                            if (response.body() != null) {
                                searchResult.id = response.body().id;
                                Log.e("retrofit", "收藏成功!");
                                Toast.makeText(v.getContext(), "收藏成功!", Toast.LENGTH_LONG).show();
                                MyDatabase.databaseWriteExecutor.submit(new Runnable() {
                                    @Override
                                    public void run() {
                                        MyDatabase.getDatabase(v.getContext()).searchResultDAO()
                                                .updateSearchResult(searchResult);
                                    }
                                });
                            } else {
                                Log.e("retrofit", "null body!");
                                Log.e("retrofit", String.format("code: %d", response.code()));
                                Log.e("retrofit", String.format("error body: %s", response.errorBody()));
                                Log.e("retrofit", String.format("message: %s", response.message()));
                                Log.e("retrofit", String.format("body: %s", response.body()));
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<BackendObject> call,
                                              @NotNull Throwable t) {
                            Log.e("retrofit", "收藏失败!");
                            Toast.makeText(v.getContext(), "收藏失败!", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    service.cancelStarEntity(Constant.backendToken, searchResult.id)
                            .enqueue(new Callback<BackendObject>() {
                        @Override
                        public void onResponse(@NotNull Call<BackendObject> call,
                                               @NotNull Response<BackendObject> response) {
                            MyDatabase.databaseWriteExecutor.submit(new Runnable() {
                                @Override
                                public void run() {
                                    MyDatabase.getDatabase(v.getContext()).searchResultDAO()
                                            .updateSearchResult(searchResult);
                                    Log.e("retrofit", "取消收藏成功!");
                                    Toast.makeText(v.getContext(), "取消收藏成功!", Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                        @Override
                        public void onFailure(@NotNull Call<BackendObject> call,
                                              @NotNull Throwable t) {
                            Log.e("retrofit", "取消收藏失败!");
                            Toast.makeText(v.getContext(), "取消收藏失败!", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

        //判断是否设置了监听器
        if(mOnItemClickListener != null){
            //为ItemView设置监听器
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.itemView,position);
                    if (!searchResult.hasRead) {
                        searchResult.hasRead = true;
//                    holder.label.setTextColor(v.getResources().getColor(R.color.grey_300));
                        MyDatabase.databaseWriteExecutor.submit(new Runnable() {
                            @Override
                            public void run() {
                                MyDatabase.getDatabase(v.getContext()).searchResultDAO()
                                        .updateSearchResult(searchResult);
                            }
                        });
                    }
                }
            });
        }

        if(mOnItemLongClickListener != null){
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = holder.getLayoutPosition();
                    mOnItemLongClickListener.onItemLongClick(holder.itemView,position);
                    //返回true 表示消耗了事件 事件不会继续传递
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (data == null) {
            return 0;
        }
        return data.size();
    }

    public static class VH extends RecyclerView.ViewHolder{
        public final TextView category;
        public final TextView label;
        public final ImageButton starButton;
        public final ImageButton shareButton;
        public VH(View v) {
            super(v);
            category = (TextView) v.findViewById(R.id.searchResultLabel);
            label = (TextView) v.findViewById(R.id.searchResultText);
            starButton = (ImageButton) v.findViewById(R.id.starButton);
            shareButton = (ImageButton) v.findViewById(R.id.shareButton);
        }
    }

    private List<SearchResult> data;
    public SearchResultAdapter(List<SearchResult> data) {
        this.data = data;
    }
    public void updateData(List<SearchResult> data) {this.data = data;}
}
