package com.wudaokou.easylearn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wudaokou.easylearn.constant.Constant;
import com.wudaokou.easylearn.data.Content;
import com.wudaokou.easylearn.data.EntityInfo;
import com.wudaokou.easylearn.data.Property;
import com.wudaokou.easylearn.retrofit.EduKGService;
import com.wudaokou.easylearn.retrofit.JSONObject;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OrganizeResultActivity extends AppCompatActivity {

    EntityInfo entityInfo;
    LinearLayout outlineLayout;
    ScrollView outlineScroll;
    FloatingActionButton popupButton;
    ListPopupWindow listPopupWindow;
    List<String> tableContents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organize_result);

        outlineLayout = findViewById(R.id.outlineLayout);
        outlineScroll = findViewById(R.id.outlineScroll);
        popupButton = findViewById(R.id.popup_table_contents_button);

        Intent intent = getIntent();
        getEntityInfo(intent.getStringExtra("course"),
                intent.getStringExtra("name"));
    }

    String indentSpaces(int n){
        if(n == 2)
            return " \u25cf ";
        if(n == 3)
            return "     \u25cb ";
        return "";
    }

    void newTextView(String text){
        LayoutInflater.from(this).inflate(R.layout.organize_text_view, outlineLayout);
        TextView newTextView =  (TextView) outlineLayout.getChildAt(outlineLayout.getChildCount()-1);
        newTextView.setText(Html.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT));
    }

    void process(){
        if(entityInfo == null) {
            Toast.makeText(this, "数据请求失败", Toast.LENGTH_SHORT).show();
            return;
        }
        // text
        newTextView(String.format("<h1>%s</h1>", entityInfo.label));
        tableContents.add(entityInfo.label);
        // property
        newTextView("<h2>知识点</h2>");
        tableContents.add(indentSpaces(2) + "知识点");
        Map<String, List<Property>> properties = entityInfo.property.stream()
                .filter(p -> !List.of("出处", "图片").contains(p.predicateLabel))
                .filter(p -> !(p.object.startsWith("http") && p.objectLabel==null))
                .peek(p -> p.objectLabel = p.objectLabel==null ? p.object : p.objectLabel)
                .collect(Collectors.groupingBy(p -> p.predicateLabel));
        properties.forEach((k, v) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("<p><b>%s：</b>", k));
            v.forEach(p -> sb.append(p.objectLabel).append("，"));
            sb.deleteCharAt(sb.length()-1);
            sb.append("</p>");
            newTextView(sb.toString());
            tableContents.add(indentSpaces(3) + k);
        });

        // link
        newTextView("<h2>关联知识</h2>");
        tableContents.add(indentSpaces(2) + "关联知识");

        Map<String, List<Content>> contentsAsSubject = entityInfo.content.stream()
                .filter(c -> c.subject_label != null)
                .collect(Collectors.groupingBy(c -> c.predicate_label));
        contentsAsSubject.forEach((k, v) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("<p><b>%s</b> %s：", k, entityInfo.label));
            v.forEach(p -> sb.append(p.subject_label).append("，"));
            sb.deleteCharAt(sb.length()-1);
            sb.append("</p>");
            newTextView(sb.toString());
            tableContents.add(indentSpaces(3) + k);
        });

        Map<String, List<Content>> contentAsObject = entityInfo.content.stream()
                .filter(c -> c.object_label != null)
                .collect(Collectors.groupingBy(c -> c.predicate_label));
        contentAsObject.forEach((k, v) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("<p><b>%s：</b> ", k));
            v.forEach(p -> sb.append(p.object_label).append("，"));
            sb.deleteCharAt(sb.length()-1);
            sb.append("</p>");
            newTextView(sb.toString());
            tableContents.add(indentSpaces(3) + k);
        });
        newTextView("<br><br><br><br>");

        listPopupWindow = new ListPopupWindow(this, null, R.attr.listPopupWindowStyle);
        listPopupWindow.setAnchorView(popupButton);
        listPopupWindow.setContentWidth(outlineScroll.getWidth() / 2);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.organize_table_contents_item, tableContents);
        listPopupWindow.setAdapter(adapter);
        listPopupWindow.setOnItemClickListener((parent, view, position, id) -> {
            listPopupWindow.dismiss();
            View correspondingText = outlineLayout.getChildAt(position);
            outlineScroll.smoothScrollTo(0, (correspondingText.getTop() + correspondingText.getBottom() - outlineScroll.getBottom())/2);
            correspondingText.setActivated(true);
            new Thread(() -> {
                try{
                    TimeUnit.MILLISECONDS.sleep(500);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                runOnUiThread(() -> correspondingText.setActivated(false));
            }).start();
        });

        popupButton.setOnClickListener(v -> listPopupWindow.show());

    }

    public void getBack(View view) {
        finish();
    }

    public void getEntityInfo(String course, String label) {
        Call<JSONObject<EntityInfo>> call = new Retrofit.Builder()
                .baseUrl(Constant.eduKGBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(EduKGService.class)
                .infoByInstanceName(Constant.eduKGId, course, label);

        call.enqueue(new Callback<JSONObject<EntityInfo>>() {
            @Override
            public void onResponse(@NotNull Call<JSONObject<EntityInfo>> call,
                                   @NotNull Response<JSONObject<EntityInfo>> response) {
                JSONObject<EntityInfo> jsonObject = response.body();
                if (jsonObject == null) return;
                entityInfo = jsonObject.getData();
//                Log.d("asd", jsonObject.msg + " " + jsonObject.code + " " + entityInfo.label);
                process();
            }

            @Override
            public void onFailure(@NotNull Call<JSONObject<EntityInfo>> call,
                                  @NotNull Throwable t) {
                Log.e("retrofit", "http error");
            }
        });
    }

}