package com.wudaokou.easylearn;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayoutMediator;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.common.UiError;
import com.sina.weibo.sdk.openapi.IWBAPI;
import com.sina.weibo.sdk.openapi.WBAPIFactory;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.wudaokou.easylearn.constant.Constant;
import com.wudaokou.easylearn.data.Content;
import com.wudaokou.easylearn.data.EntityInfo;
import com.wudaokou.easylearn.data.MyDatabase;
import com.wudaokou.easylearn.data.Property;
import com.wudaokou.easylearn.data.Question;
import com.wudaokou.easylearn.data.SearchResult;
import com.wudaokou.easylearn.databinding.ActivityEntityInfoBinding;
import com.wudaokou.easylearn.fragment.EntityContentFragment;
import com.wudaokou.easylearn.fragment.EntityPropertyFragment;
import com.wudaokou.easylearn.fragment.EntityQuestionFragment;
import com.wudaokou.easylearn.retrofit.BackendObject;
import com.wudaokou.easylearn.retrofit.BackendService;
import com.wudaokou.easylearn.retrofit.EduKGService;
import com.wudaokou.easylearn.retrofit.HistoryParam;
import com.wudaokou.easylearn.retrofit.JSONArray;
import com.wudaokou.easylearn.retrofit.JSONObject;
import com.wudaokou.easylearn.utils.LoadingDialog;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EntityInfoActivity extends AppCompatActivity implements WbShareCallback {

    private ActivityEntityInfoBinding binding;
    private EntityPropertyFragment entityPropertyFragment;
    private EntityContentFragment entityContentFragment;
    private EntityQuestionFragment entityQuestionFragment;

    String course, label, uri;
    IWBAPI mWBAPI;

    SearchResult searchResult;
    BackendService backendService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initWeiboSdk();

        binding = ActivityEntityInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 获取实体信息
        Intent intent = getIntent();
        course = intent.getStringExtra("course");
        label = intent.getStringExtra("label");
        uri = intent.getStringExtra("uri");
        Log.e("entity_info_activity", String.format("course == null? %s",
                Boolean.toString(course == null)));
        if (label != null) {
            if (label.length() < 10) {
                binding.title.setText(label);
            } else {
                binding.title.setText(String.format("%s...", label.substring(0, 10)));
            }
        }
        searchResult = (SearchResult) intent.getSerializableExtra("searchResult");
        if (searchResult == null) {
            Log.e("entity_info_activity", "get null searchResult from intent");

            Future<SearchResult> searchResultFuture = MyDatabase.databaseWriteExecutor.submit(
                    new Callable<SearchResult>() {
                @Override
                public SearchResult call() throws Exception {
                    return MyDatabase.getDatabase(EntityInfoActivity.this)
                            .searchResultDAO().loadSearchResultByUri(uri);
                }
            });

            try {
                searchResult = searchResultFuture.get();
                if (searchResult != null) {
                    Log.e("entity_info_activity", "get a searchResult from database");
                } else {
                    Log.e("entity_info_activity", "get null searchResult from database");
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (searchResult == null) {
            Log.e("entity_info_activity", "null searchResult after get from database");
        }

        if (searchResult != null && searchResult.hasStar) {
            binding.imageButtonStar.setImageResource(R.drawable.star_fill);
        }
        setStarListener();
        bindPagerWithTab();

        Retrofit backendRetrofit = new Retrofit.Builder()
                .baseUrl(Constant.backendBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        backendService = backendRetrofit.create(BackendService.class);

        if (searchResult != null) {
            postClickEntity();
        } else {
            Log.e("entity_info_activity", "null searchResult");
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    public void setStarListener() {
        binding.imageButtonStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("entity_info_activity", "on click star");
                searchResult.hasStar = !searchResult.hasStar;

                Log.e("entity_info_activity", "after modify hasStar");
                if (searchResult.hasStar) {
                    binding.imageButtonStar.setImageResource(R.drawable.star_fill);
                } else {
                    binding.imageButtonStar.setImageResource(R.drawable.star_blank);
                }

                Log.e("entity_info_activity", "after set star logo");

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(Constant.backendBaseUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                BackendService service = retrofit.create(BackendService.class);

                if (searchResult.hasStar) {
                    Log.e("entity_info_activity", "send star");
                    service.starEntity(Constant.backendToken,
                            new HistoryParam(searchResult.course.toUpperCase(), searchResult.label,
                                    searchResult.uri, searchResult.category, searchResult.searchKey))
                            .enqueue(new Callback<BackendObject>() {
                                @Override
                                public void onResponse(@NotNull Call<BackendObject> call,
                                                       @NotNull Response<BackendObject> response) {
                                    if (response.body() != null) {
                                        searchResult.id = response.body().id;
                                        MyDatabase.databaseWriteExecutor.submit(new Runnable() {
                                            @Override
                                            public void run() {
                                                MyDatabase.getDatabase(v.getContext()).searchResultDAO()
                                                        .updateSearchResult(searchResult);
                                            }
                                        });
                                        Snackbar.make(v, "收藏成功", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                        Log.e("retrofit", "收藏成功!");
                                    }
                                }

                                @Override
                                public void onFailure(@NotNull Call<BackendObject> call,
                                                      @NotNull Throwable t) {
                                    Log.e("retrofit", "收藏失败!");
                                    Snackbar.make(v, "收藏失败!", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }
                            });
                } else {
                    Log.e("entity_info_activity", "send unstar");
                    service.cancelStarEntity(Constant.backendToken,
                            searchResult.id).enqueue(new Callback<BackendObject>() {
                        @Override
                        public void onResponse(@NotNull Call<BackendObject> call,
                                               @NotNull Response<BackendObject> response) {
                            MyDatabase.databaseWriteExecutor.submit(new Runnable() {
                                @Override
                                public void run() {
                                    MyDatabase.getDatabase(v.getContext()).searchResultDAO()
                                            .updateSearchResult(searchResult);
                                    Log.e("retrofit", "取消收藏成功!");
                                    Snackbar.make(v, "取消收藏成功!", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }
                            });
                        }

                        @Override
                        public void onFailure(@NotNull Call<BackendObject> call,
                                              @NotNull Throwable t) {
                            Log.e("retrofit", "取消收藏失败!");
                            Snackbar.make(v, "取消收藏失败!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    });
                }
            }
        });
    }

    public void bindPagerWithTab() {
        binding.viewPager2.setAdapter(new FragmentStateAdapter(getSupportFragmentManager(), getLifecycle()) {
            @NonNull
            @NotNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0:
                        // 实体属性
                        if (entityPropertyFragment == null) {
                            entityPropertyFragment = new EntityPropertyFragment(course, label, uri);
                        }
                        return entityPropertyFragment;
                    case 1:
                        // 实体关联
                        if (entityContentFragment == null) {
                            entityContentFragment = new EntityContentFragment(course, label);
                        }
                        return entityContentFragment;
                    default:
                        // 实体相关习题列表
                        if (entityQuestionFragment == null) {
                            entityQuestionFragment = new EntityQuestionFragment(course, label);
                        }
                        return entityQuestionFragment;
                }
            }
            @Override
            public int getItemCount() {
                return 3;
            }
        });

        new TabLayoutMediator(binding.tabs, binding.viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull @NotNull TabLayout.Tab tab, int position) {
                TextView tabView = new TextView(EntityInfoActivity.this);
                tabView.setGravity(Gravity.CENTER);
                String[] titles = {"知识属性", "知识关联", "相关习题"} ;
                tabView.setText(titles[position]);
                tab.setCustomView(tabView);
            }
        }).attach();
    }

    public void postClickEntity() {
        // 向后端发送该实体已被浏览
        backendService.postClickEntity(Constant.backendToken,
                new HistoryParam(course.toUpperCase(),
                        label, uri, searchResult.category, searchResult.searchKey))
                .enqueue(new Callback<BackendObject>() {
                    @Override
                    public void onResponse(@NotNull Call<BackendObject> call,
                                           @NotNull Response<BackendObject> response) {
                        if (response.code() == 200) {
                            Log.e("home", "post click ok");
                        } else {
                            Log.e("home", "post click fail");
                            Log.e("home", String.format("code: %d", response.code()));
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<BackendObject> call,
                                          @NotNull Throwable t) {
                        Log.e("home", "post click error");
                    }
                });
    }

    public void goBack(View view) {
        EntityInfoActivity.this.finish();
    }

    // 微博sdk初始化
    private void initWeiboSdk() {
        AuthInfo authInfo = new AuthInfo(this, "949341693", "", "");
        mWBAPI = WBAPIFactory.createWBAPI(this);
        mWBAPI.registerApp(this, authInfo);

    }

    // 微博分享回调
    @Override
    public void onComplete() {
        Toast.makeText(EntityInfoActivity.this, "分享成功",
                Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onError(UiError error) {
        Toast.makeText(EntityInfoActivity.this, "分享失败:" + error.errorMessage,
                Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onCancel() {
        Toast.makeText(EntityInfoActivity.this, "分享取消",
                Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mWBAPI != null) {
            mWBAPI.doResultIntent(data, this);
        }
    }

    public void shareToWeibo(View view) {
        WeiboMultiMessage message = new WeiboMultiMessage();
        TextObject textObject = new TextObject();
        textObject.text = shareText(entityPropertyFragment.data);
        message.textObject = textObject;
        mWBAPI.shareMessage(message, false);
    }

    String shareText(List<Property> data){
        StringBuilder sb = new StringBuilder();
        sb.append(label).append("：\n\n");
        for(Property p : data)
            sb.append(p.toString());
        return sb.toString();
    }
}