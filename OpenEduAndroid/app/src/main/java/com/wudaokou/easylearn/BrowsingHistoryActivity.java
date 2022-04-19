package com.wudaokou.easylearn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.wudaokou.easylearn.adapter.HomeCourseItemAdapter;
import com.wudaokou.easylearn.constant.Constant;
import com.wudaokou.easylearn.data.HomeCourseItem;
import com.wudaokou.easylearn.data.SearchResult;
import com.wudaokou.easylearn.databinding.ActivityBrowsingHistoryBinding;
import com.wudaokou.easylearn.fragment.HomePagerFragment;
import com.wudaokou.easylearn.retrofit.BackendObject;
import com.wudaokou.easylearn.retrofit.BackendService;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BrowsingHistoryActivity extends AppCompatActivity {

    ActivityBrowsingHistoryBinding binding;
    HomeCourseItemAdapter adapter;
    List<HomeCourseItem> homeCourseItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBrowsingHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HomeCourseItemAdapter(homeCourseItemList, true);
        adapter.setOnItemClickListener(new HomeCourseItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                HomeCourseItem homeCourseItem = homeCourseItemList.get(position);
                Intent intent = new Intent(BrowsingHistoryActivity.this, EntityInfoActivity.class);
                intent.putExtra("course", homeCourseItem.result.course);
                intent.putExtra("label", homeCourseItem.result.label);
                intent.putExtra("uri", homeCourseItem.result.uri);
                intent.putExtra("searchResult", homeCourseItem.result);
                startActivity(intent);
            }
        });
        binding.recyclerView.setAdapter(adapter);

        getHistoryEntity();
    }

    public void getBack(View view) {
        this.finish();
    }

    public void getHistoryEntity() {
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Constant.backendBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        BackendService backendService = retrofit.create(BackendService.class);
        backendService.getHistoryEntity(Constant.backendToken)
                .enqueue(new Callback<List<BackendObject>>() {
            @Override
            public void onResponse(@NotNull Call<List<BackendObject>> call,
                                   @NotNull Response<List<BackendObject>> response) {
                if (response.body() != null) {
                    homeCourseItemList = new ArrayList<>();
                    for (BackendObject backendObject : response.body()) {
                        SearchResult searchResult = new SearchResult(
                                backendObject.name, backendObject.category, backendObject.uri,
                                backendObject.course.toLowerCase(), backendObject.searchKey);
                        searchResult.hasStar = false;  // 后端传数据过来
                        searchResult.hasRead = true;
                        searchResult.id = backendObject.id;
                        HomeCourseItem item = new HomeCourseItem(searchResult, null);
                        item.createdAt = backendObject.createdAt;
                        homeCourseItemList.add(item);
                    }
                    adapter.updateData(homeCourseItemList);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<BackendObject>> call,
                                  @NotNull Throwable t) {

            }
        });
    }
}