package com.wudaokou.easylearn.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wudaokou.easylearn.EntityInfoActivity;
import com.wudaokou.easylearn.R;
import com.wudaokou.easylearn.SearchResultActivity;
import com.wudaokou.easylearn.adapter.EntityContentAdapter;
import com.wudaokou.easylearn.adapter.SearchResultAdapter;
import com.wudaokou.easylearn.constant.Constant;
import com.wudaokou.easylearn.data.Content;
import com.wudaokou.easylearn.data.EntityInfo;
import com.wudaokou.easylearn.data.KnowledgeCard;
import com.wudaokou.easylearn.data.MyDatabase;
import com.wudaokou.easylearn.data.SearchResult;
import com.wudaokou.easylearn.databinding.FragmentEntityContentBinding;
import com.wudaokou.easylearn.retrofit.EduKGService;
import com.wudaokou.easylearn.retrofit.JSONObject;
import com.wudaokou.easylearn.utils.LoadingDialog;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EntityContentFragment extends Fragment {

    public List<Content> data;
    private FragmentEntityContentBinding binding;
    private EntityContentAdapter adapter;
    private LoadingDialog loadingDialog;
    private String course;
    private String label;
    EduKGService service;
    SearchResult searchResult;

    public EntityContentFragment (final String course, final String label) {
        this.course = course;
        this.label = label;
    }

    public void updateData(List<Content> data) {
        this.data = data;
        if (adapter != null) {
            adapter.updateData(data);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEntityContentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.eduKGBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(EduKGService.class);

//        loadingDialog = new LoadingDialog(requireContext());
//        loadingDialog.show();
        checkDatabase();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EntityContentAdapter(data, getLayoutInflater());
        adapter.setOnItemClickListener(new SearchResultAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.e("entity_content", "click");
                Content content = data.get(position);
                String course = content.course;
                String label, uri;
                if (content.subject_label != null) {
                    label = content.subject_label;
                    uri = content.subject;
                } else {
                    label = content.object_label;
                    uri = content.object;
                }
                Intent intent = new Intent(getActivity(), EntityInfoActivity.class);
                intent.putExtra("course", course);
                intent.putExtra("label", label);
                intent.putExtra("uri", uri);
                Future<SearchResult> searchResultFuture = MyDatabase.databaseWriteExecutor.submit(
                        new Callable<SearchResult>() {
                    @Override
                    public SearchResult call() throws Exception {
                        return MyDatabase.getDatabase(getContext()).searchResultDAO()
                                .loadSearchResultByUri(uri);
                    }
                });
                try {
                    searchResult = searchResultFuture.get();
                    if (searchResult != null) {
                        Log.e("entity_content", "load searchResult ok");
                    } else {
                        Log.e("entity_content", "load null searchResult from database");
                    }

                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    Log.e("entity_content", "load searchResult fail");
                } finally {
                    // 不能在catch里初始化searchResult
                    if (searchResult == null) {
                        searchResult = new SearchResult(label, null ,uri);
                        searchResult.course = course;
                        searchResult.category = null;
                        searchResult.hasRead = false;
                        searchResult.hasStar = false;
                    }
                    MyDatabase.databaseWriteExecutor.submit(new Runnable() {
                        @Override
                        public void run() {
                            MyDatabase.getDatabase(getContext()).searchResultDAO()
                                    .insertSearchResult(searchResult);
                        }
                    });
                    intent.putExtra("searchResult", searchResult);
                    Log.e("entity_content", String.format("searchResult == null ? %s",
                            Boolean.toString(searchResult == null)));
                    startActivity(intent);
                }
            }
        });
        binding.recyclerView.setAdapter(adapter);
        return root;
    }

    public void checkDatabase() {
        Future<List<Content>> listFuture = MyDatabase.databaseWriteExecutor.submit(new Callable<List<Content>>() {
            @Override
            public List<Content> call() throws Exception {
                return MyDatabase.getDatabase(getContext())
                        .contentDAO().loadContentByCourseAndLabel(course, label);
            }
        });
        try {
            List<Content> localList = listFuture.get();
            if (localList != null && localList.size() != 0) {
                data = localList;
                updateData(data);
            } else {
                getEntityInfo();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void getEntityInfo() {
        Call<JSONObject<EntityInfo>> call = service.infoByInstanceName(Constant.eduKGId, course, label);
        call.enqueue(new Callback<JSONObject<EntityInfo>>() {
            @Override
            public void onResponse(@NotNull Call<JSONObject<EntityInfo>> call,
                                   @NotNull Response<JSONObject<EntityInfo>> response) {
                JSONObject<EntityInfo> jsonObject = response.body();
                Log.e("retrofit content", "http ok");
                if (jsonObject != null) {
                    if (jsonObject.data.content != null) {
                        Log.e("retrofit content", String.format("content size: %s",
                                jsonObject.data.content.size()));
                        data = jsonObject.data.content;
                        for (Content content : data) {
                            content.course = course;
                            content.label = label;
                            content.hasRead = false;
                            content.hasStar = false;
                        }
                        updateData(data);
//                        getExtraKnowledge();

                        // 本地缓存
                        for (Content content : data) {
                            MyDatabase.databaseWriteExecutor.submit(new Runnable() {
                                @Override
                                public void run() {
                                    MyDatabase.getDatabase(getContext()).contentDAO()
                                            .insertContent(content);

                                }
                            });
                        }
                    }
                }
//                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(@NotNull Call<JSONObject<EntityInfo>> call,
                                  @NotNull Throwable t) {
                Log.e("retrofit", "http error");
//                loadingDialog.dismiss();
            }
        });
    }

    public void getExtraKnowledge() {
        if (data == null)
            return;
        for (Content content : data) {
            String uri = content.subject;
            if (uri == null) {
                uri = content.object;
            }
            if (uri != null) {
                Call<JSONObject<KnowledgeCard>> call = service.getKnowledgeCard(Constant.eduKGId,
                        course, uri);
                call.enqueue(new Callback<JSONObject<KnowledgeCard>>() {
                    @Override
                    public void onResponse(@NotNull Call<JSONObject<KnowledgeCard>> call,
                                           @NotNull Response<JSONObject<KnowledgeCard>> response) {
//                        if (response.body() != null && response.body().data != null) {
//                            content.entityFeatureList = response.body().data.entity_features;
//                            updateData(data);
//                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<JSONObject<KnowledgeCard>> call,
                                          @NotNull Throwable t) {
                    }
                });
            }
        }
    }
}