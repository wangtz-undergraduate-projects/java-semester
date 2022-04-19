package com.wudaokou.easylearn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

import com.wudaokou.easylearn.adapter.SearchRecordAdapter;
import com.wudaokou.easylearn.adapter.SearchResultAdapter;
import com.wudaokou.easylearn.constant.Constant;
import com.wudaokou.easylearn.constant.SubjectMap;
import com.wudaokou.easylearn.constant.SubjectMapChineseToEnglish;
import com.wudaokou.easylearn.data.MyDatabase;
import com.wudaokou.easylearn.data.SearchRecord;
import com.wudaokou.easylearn.databinding.ActivitySearchableBinding;
import com.wudaokou.easylearn.retrofit.BackendObject;
import com.wudaokou.easylearn.retrofit.BackendService;
import com.wudaokou.easylearn.retrofit.HistoryParam;

import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchableActivity extends AppCompatActivity
            implements PopupMenu.OnMenuItemClickListener{

    public ActivitySearchableBinding binding;
    public SearchView searchView;
    public MyDatabase myDatabase;
    SharedPreferences sharedPreferences;
    public Button typeButton;
    HashMap<String, String> map;

    SearchRecordAdapter popularAdapter;
    List<SearchRecord> popularList;
    BackendService backendService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);

        binding = ActivitySearchableBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        typeButton = binding.subjectButton;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String selectedType = sharedPreferences.getString("searchType", "chinese");
        map = SubjectMap.getMap();
        typeButton.setText(map.get(selectedType));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.backendBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        backendService = retrofit.create(BackendService.class);

        initSearchView();

        // 先获取数据库
        myDatabase = MyDatabase.getDatabase(this);

        // 设置热门搜索记录
        getPopularSearchKeys();
        binding.popularRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        popularAdapter = new SearchRecordAdapter(popularList, true);
        popularAdapter.setOnItemClickListener(new SearchRecordAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SearchRecord searchRecord = popularList.get(position);
                if (searchRecord != null) {
                    doMySearch(searchRecord.subject, searchRecord.content);
                }
            }
        });
        binding.popularRecyclerView.setAdapter(popularAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initSearchRecord();
    }

    private void getPopularSearchKeys() {
        backendService.getPopularSearchKeys().enqueue(new Callback<List<BackendObject>>() {
            @Override
            public void onResponse(@NotNull Call<List<BackendObject>> call,
                                   @NotNull Response<List<BackendObject>> response) {
                if (response.body() != null) {
                    popularList = new ArrayList<>();
                    for (BackendObject backendObject : response.body()) {
                        String timestamp = backendObject.createdAt.replace('T', ' ');
                        long ts = Timestamp.valueOf(timestamp).getTime();
                        SearchRecord record = new SearchRecord(ts, backendObject.name,
                                backendObject.course.toLowerCase());
                        popularList.add(record);
                    }
                    if (popularAdapter != null) {
                        popularAdapter.updateData(popularList);
                        popularAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<BackendObject>> call,
                                  @NotNull Throwable t) {

            }
        });
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.search_type, popup.getMenu());
        popup.setOnMenuItemClickListener(this);
        popup.show();
    }

    public void getBack(View v) {
        SearchableActivity.this.finish();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String course = item.toString();
        typeButton.setText(course);
        editor.putString("searchType", SubjectMapChineseToEnglish.getMap().get(course));
        editor.apply();
        return false;
    }

    public void initSearchView() {
        searchView = binding.searchView2;
        searchView.setActivated(true);
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("搜索知识点");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.e("SearchableActivity", String.format("query string: %s", query));
                String subject = sharedPreferences.getString("searchType", "chinese");
                long timestamp = System.currentTimeMillis();
                SearchRecord searchRecord = new SearchRecord(timestamp, query, subject);

                // 往数据库线程池中添加任务插入搜索记录
                MyDatabase.databaseWriteExecutor.submit(new Runnable() {
                    @Override
                    public void run() {
                        myDatabase.searchRecordDAO().insertRecord(searchRecord);
                    }
                });

                // 将搜索历史记录传递给后端

                backendService.postHistorySearch(Constant.backendToken,
                        new HistoryParam(subject.toUpperCase(), query))
                        .enqueue(new Callback<List<BackendObject>>() {
                    @Override
                    public void onResponse(@NotNull Call<List<BackendObject>> call,
                                           @NotNull Response<List<BackendObject>> response) {
                        Log.e("backend", "post search history ok");
                    }

                    @Override
                    public void onFailure(@NotNull Call<List<BackendObject>> call,
                                          @NotNull Throwable t) {
                        Log.e("backend", "post search history error");
                    }
                });

                // todo 执行搜搜
                doMySearch(subject, query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // 文本搜索框发生变化时调用
                return false;
            }
        });
    }

    private void initSearchRecord() {
        Log.e("SearchableActivity", "initSearchRecord");
        Future<List<SearchRecord>> future = MyDatabase.databaseWriteExecutor.submit(
                new Callable<List<SearchRecord>>() {
                    @Override
                    public List<SearchRecord> call() throws Exception {
                        return myDatabase.searchRecordDAO().loadLimitedRecords(Constant.maxSearchRecordCount);
                    }
                }
        );
        try {
            List<SearchRecord> searchRecordList = future.get();

            Log.e("SearchableActivity", String.format("record total: %d", searchRecordList.size()));

            SearchRecordAdapter adapter = new SearchRecordAdapter(searchRecordList, false);
            adapter.setOnItemClickListener(new SearchRecordAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    SearchRecord searchRecord = searchRecordList.get(position);
                    if (searchRecord != null) {
                        doMySearch(searchRecord.subject, searchRecord.content);
                    }
                }
            });
            binding.recordRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
            binding.recordRecyclerView.setAdapter(adapter);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void clearSearchRecord(View view) {
        MyDatabase.databaseWriteExecutor.submit(new Runnable() {
            @Override
            public void run() {
                myDatabase.searchRecordDAO().deleteAllRecord();
            }
        });
        initSearchRecord();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.backendBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        BackendService backendService = retrofit.create(BackendService.class);

        backendService.deleteAllHistorySearch(Constant.backendToken).enqueue(new Callback<BackendObject>() {
            @Override
            public void onResponse(@NotNull Call<BackendObject> call,
                                   @NotNull Response<BackendObject> response) {
            }

            @Override
            public void onFailure(@NotNull Call<BackendObject> call,
                                  @NotNull Throwable t) {
            }
        });
    }

    private void doMySearch(String queryType, String queryContent) {
        // 执行搜索操作
        Intent intent = new Intent(this, SearchResultActivity.class);
        intent.putExtra("queryType", queryType);
        intent.putExtra("queryContent", queryContent);
        startActivity(intent);
    }
}