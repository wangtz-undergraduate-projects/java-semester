package com.wudaokou.easylearn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.wudaokou.easylearn.adapter.SearchRecordAdapter;
import com.wudaokou.easylearn.adapter.SearchResultAdapter;
import com.wudaokou.easylearn.constant.Constant;
import com.wudaokou.easylearn.constant.SubjectMap;
import com.wudaokou.easylearn.data.MyDatabase;
import com.wudaokou.easylearn.data.SearchResult;
import com.wudaokou.easylearn.databinding.ActivitySearchResultBinding;
import com.wudaokou.easylearn.retrofit.BackendObject;
import com.wudaokou.easylearn.retrofit.BackendService;
import com.wudaokou.easylearn.retrofit.EduKGService;
import com.wudaokou.easylearn.retrofit.HistoryParam;
import com.wudaokou.easylearn.retrofit.JSONArray;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;


public class SearchResultActivity extends AppCompatActivity {

    public ActivitySearchResultBinding binding;
    HashMap<String, String> map;
    public String course, searchKey;
    SearchResultAdapter adapter;
    List<SearchResult> data;  // 原始数据
    List<SearchResult> activeData; // 经筛选和排序后的数据
    private String selectedMethod = "默认",
            selectedFilter = "全部";
    private List<String> filterMethods;
    EduKGService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        binding = ActivitySearchResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        map = SubjectMap.getMap();
        Intent intent = getIntent();
        course = intent.getStringExtra("queryType");
        binding.subjectButton.setText(map.get(course));
        searchKey = intent.getStringExtra("queryContent");
        binding.searchLine.setText(searchKey);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 筛选选项，其他选项在doSearch函数完成数据传输后填入
        filterMethods = new ArrayList<>();
        filterMethods.add("全部");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.eduKGBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(EduKGService.class);
        // 为recyclerView设置adapter
        adapter = new SearchResultAdapter(activeData);
        adapter.setOnItemClickListener(new SearchResultAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // 跳转到实体详情页
                SearchResult searchResult = activeData.get(position);

                Intent intent = new Intent(SearchResultActivity.this, EntityInfoActivity.class);
                intent.putExtra("course", course);
                intent.putExtra("label", searchResult.label);
                intent.putExtra("uri", searchResult.uri);
                intent.putExtra("searchResult", searchResult);
                Log.e("searchResultActivity", String.format("searchResult == null ? %s",
                        Boolean.toString(searchResult == null)));
                startActivity(intent);
            }
        });

        adapter.setOnItemLongClickListener(new SearchResultAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                // todo 待长按搜索结果的功能
                Toast.makeText(getApplicationContext(), "长按选项", Toast.LENGTH_LONG).show();
            }
        });
        binding.recyclerView.setAdapter(adapter);

        // 为排序按钮设置adapter
        String[] sortMethods = getResources().getStringArray(R.array.resultSortMethod);
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(
                this, R.layout.dropdown_subject_item, sortMethods);
        binding.chooseResultOrder.setAdapter(sortAdapter);
        binding.chooseResultOrder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedMethod = binding.chooseResultOrder.getText().toString();
                doSortAndFilter();
            }
        });

        // 为筛选按钮设置adapter
//        String[] filterMethods = getResources().getStringArray(R.array.resultFilterMethod);

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkDatabase();
//        if (adapter != null) {
//            // 强制刷新，及时更新点击过后的选项为灰色
//            adapter.notifyDataSetChanged();
//        }
    }

    public void setFilter() {
        ArrayAdapter<String> filterAdapter = new ArrayAdapter<>(
                this, R.layout.dropdown_subject_item, filterMethods);
        binding.setResultFilter.setAdapter(filterAdapter);
        binding.setResultFilter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedFilter = binding.setResultFilter.getText().toString();
                doSortAndFilter();
            }
        });
    }

    public void doSortAndFilter() {
        if (data == null) {
            return;
        }
        activeData = new ArrayList<>();
        if (selectedFilter.equals("全部")) {
            activeData.addAll(data);
        } else {
            // 筛选
            for (SearchResult result : data) {
                if (selectedFilter.equals(result.category)) {
                    activeData.add(result);
                }
            }
        }

        if (selectedMethod.equals("标签长度升序")) {
            Collections.sort(activeData, new Comparator<SearchResult>() {
                @Override
                public int compare(SearchResult o1, SearchResult o2) {
                    return o1.label.length() - o2.label.length();
                }
            });
        } else if (selectedMethod.equals("标签长度降序")) {
            Collections.sort(activeData, new Comparator<SearchResult>() {
                @Override
                public int compare(SearchResult o1, SearchResult o2) {
                    return o2.label.length() - o1.label.length();
                }
            });
        } else if (selectedMethod.equals("标签首字母排序") ||
        selectedMethod.equals("类别首字母排序")) {
            Collections.sort(activeData, new Comparator<SearchResult>() {
                @Override
                public int compare(SearchResult o1, SearchResult o2) {
                    HanyuPinyinOutputFormat outputFormat = new HanyuPinyinOutputFormat();
                    outputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
                    String label1, label2;
                    if (selectedMethod.equals("标签首字母排序")) {
                        label1 = o1.label;
                        label2 = o2.label;
                    } else {
                        label1 = o1.category;
                        label2 = o2.category;
                    }

                    for (int i = 0; i != label1.length() && i != label2.length(); i++) {
                        int codePoint1 = label1.charAt(i);
                        int codePoint2 = label2.charAt(i);

                        if (Character.isSupplementaryCodePoint(codePoint1) ||
                        Character.isSupplementaryCodePoint(codePoint2)) {
                            i++;
                        }

                        if (codePoint1 != codePoint2) {
                            String pinyin1 = PinyinHelper.toHanyuPinyinStringArray((char) codePoint1) == null
                                    ? null : PinyinHelper.toHanyuPinyinStringArray((char) codePoint1)[0];
                            String pinyin2 = PinyinHelper.toHanyuPinyinStringArray((char) codePoint2) == null
                                    ? null : PinyinHelper.toHanyuPinyinStringArray((char) codePoint2)[0];
                            if (pinyin1 != null && pinyin2 != null) { // 两个字符都是汉字
                                if (!pinyin1.equals(pinyin2)) {
                                    return pinyin1.compareTo(pinyin2);
                                }
                            } else {
                                return codePoint1 - codePoint2;
                            }
                        }
                    }
                    return label1.length() - label2.length();
                }
            });
        }

        if (adapter != null) {
            adapter.updateData(activeData);
            adapter.notifyDataSetChanged();
        }
    }

    public void getBack(View v) {
        SearchResultActivity.this.finish();
    }

    public void checkDatabase() {
        Future<List<SearchResult>> futureList = MyDatabase.databaseWriteExecutor.submit(new Callable<List<SearchResult>>() {
            @Override
            public List<SearchResult> call() throws Exception {
                return MyDatabase.getDatabase(SearchResultActivity.this)
                        .searchResultDAO()
                        .loadSearchResultByCourseAndLabel(course, searchKey);
            }
        });
        try {
            List<SearchResult> localList = futureList.get();
            if (localList != null && localList.size() != 0) {
                Log.e("database", "search result success");
                Log.e("database", String.format("list size: %s", localList.size()));
                data = localList;
                activeData = data;
                Set<String> set = new HashSet<>();  //去重
                for (SearchResult result : data) {
                    if (!set.contains(result.category)) {
                        set.add(result.category);
                        filterMethods.add(result.category);
                    }
                }
                if (adapter != null) {
                    adapter.updateData(activeData);
                    adapter.notifyDataSetChanged();
                }
                setFilter();
            } else {
                doSearch();
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void doSearch() {
        Call<JSONArray<SearchResult>> call = service.instanceList(Constant.eduKGId, course, searchKey);


        call.enqueue(new Callback<JSONArray<SearchResult>>() {
            @Override
            public void onResponse(@NotNull Call<JSONArray<SearchResult>> call,
                                   @NotNull Response<JSONArray<SearchResult>> response) {
                Log.e("retrofit", "search result success");
                JSONArray<SearchResult> jsonArray = response.body();

                if (jsonArray != null && jsonArray.code.equals("0")) {
                    data = jsonArray.data;
                    activeData = data;
                    Set<String> set = new HashSet<>();  //去重
                    for (SearchResult result : data) {
                        result.course = course;
                        result.searchKey = searchKey;
                        result.hasRead = false;
                        result.hasStar = false;
                        if (!set.contains(result.category)) {
                            set.add(result.category);
                            filterMethods.add(result.category);
                        }
                    }

                    for (SearchResult searchResult : data) {
                        MyDatabase.databaseWriteExecutor.submit(new Runnable() {
                            @Override
                            public void run() {
                                MyDatabase.getDatabase(SearchResultActivity.this)
                                        .searchResultDAO().insertSearchResult(searchResult);
                            }
                        });
                    }

                } else {
                    Log.e("retrofit", "error request");
                    data = new ArrayList<SearchResult>();
                    data.add(new SearchResult("暂时找不到您想要的结果", "抱歉", ""));
                    activeData = data;
                }

                if (adapter != null) {
                    adapter.updateData(activeData);
                    adapter.notifyDataSetChanged();
                }
                setFilter();
            }

            @Override
            public void onFailure(@NotNull Call<JSONArray<SearchResult>> call,
                                  @NotNull Throwable t) {
                Log.e("retrofit", "connect error");
                // todo
                setFilter();
            }
        });
    }
}