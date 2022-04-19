package com.wudaokou.easylearn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.wudaokou.easylearn.adapter.EntityLinkAdapter;
import com.wudaokou.easylearn.constant.Constant;
import com.wudaokou.easylearn.constant.SubjectMapChineseToEnglish;
import com.wudaokou.easylearn.retrofit.EduKGService;
import com.wudaokou.easylearn.retrofit.JSONObject;
import com.wudaokou.easylearn.retrofit.entityLink.EntityLinkObject;
import com.wudaokou.easylearn.retrofit.entityLink.JsonEntityLink;


import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.TimeUnit;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EntityLinkResultActivity extends AppCompatActivity {

    TextView searchSubjectTextView, searchTextTextView;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entity_link_result);

        searchSubjectTextView = findViewById(R.id.searchSubject);
        searchTextTextView = findViewById(R.id.searchText);
        recyclerView = findViewById(R.id.entityLinkResults);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        String subject = intent.getStringExtra("subject");
        String text = intent.getStringExtra("text");

        searchSubjectTextView.setText(subject);
        searchTextTextView.setText(text);

        doSearch(subject, text);

    }


    private void doSearch(String subject, String text) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.eduKGBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        EduKGService service = retrofit.create(EduKGService.class);

        String subjectInEnglish = SubjectMapChineseToEnglish.getMap().get(subject);
        Call<JSONObject<JsonEntityLink>> call = service.linkInstance(Constant.eduKGId, subjectInEnglish, text);

        call.enqueue(new Callback<JSONObject<JsonEntityLink>>() {
            @Override
            public void onResponse(@NotNull Call<JSONObject<JsonEntityLink>> call, @NotNull Response<JSONObject<JsonEntityLink>> response) {
                JSONObject<JsonEntityLink> rsp = response.body();
                // 返回错误，服务器错误
                if(rsp == null || !rsp.getCode().equals("0")){
                    Toast.makeText(EntityLinkResultActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 实体列表
                List<EntityLinkObject> data = rsp.getData().getResults();
                // 搜索结果为空
                if(data.isEmpty()){
                    recyclerView.setAdapter(new EntityLinkAdapter(List.of(
                            new EntityLinkObject("抱歉", "", 0, 0, "暂时找不到您想要的结果")
                    )));
                    return;
                }
                // 试题列表 RecyclerView 的 Adapter
                EntityLinkAdapter entityLinkAdapter = new EntityLinkAdapter(data);
                // 点击实体跳转到详情
                entityLinkAdapter.setOnItemClickListener(
                        (view, position) -> jumpToEntityInfo(data, position, subjectInEnglish));
                // 设置Adapter
                recyclerView.setAdapter(entityLinkAdapter);
                // 高亮文本中的实体名称
                textHighlighting(data, text, entityLinkAdapter);
            }

            @Override
            public void onFailure(@NotNull Call<JSONObject<JsonEntityLink>> call, @NotNull Throwable t) {
                Toast.makeText(EntityLinkResultActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 高亮文本中的实体名称
    private void textHighlighting(List<EntityLinkObject> data, String text, EntityLinkAdapter entityLinkAdapter) {
        SpannableString spannableString = new SpannableString(text + " ");  // 最后加空格以便点击空白处可使最后一个单词被高亮时被取消focus
        for(int i = 0; i < data.size(); i++){
            EntityLinkObject o = data.get(i);
            final int index = i;  // 在闭包中访问
            spannableString.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    onClickHighlightedText(entityLinkAdapter, index);
                }
            }, o.getStart_index(), o.getEnd_index()+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        searchTextTextView.setText(spannableString);
        searchTextTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    // 高亮文本的点击事件
    private void onClickHighlightedText(EntityLinkAdapter adapter, int index) {
        // 滑动recyclerView到选择的实体
        RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(EntityLinkResultActivity.this){
            @Override protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };
        smoothScroller.setTargetPosition(index);
        recyclerView.getLayoutManager().startSmoothScroll(smoothScroller);
        // 高亮试题列表item
        itemHighlighting(adapter, index);
    }

    // 高亮试题列表item
    private void itemHighlighting(EntityLinkAdapter adapter, int index) {
        adapter.setSelectedPos(index);
        adapter.notifyItemChanged(index);
        new Thread(()->{
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> {
                adapter.setSelectedPos(RecyclerView.NO_POSITION);
                adapter.notifyItemChanged(index);
            });
        }).start();
    }

    private void jumpToEntityInfo(List<EntityLinkObject> data, int position, String course) {
        EntityLinkObject o = data.get(position);
        Intent intent = new Intent(EntityLinkResultActivity.this, EntityInfoActivity.class);
        intent.putExtra("course", course);
        intent.putExtra("label", o.getEntity());
        startActivity(intent);
    }

    public void getBack(View view) {
        this.finish();
    }
}