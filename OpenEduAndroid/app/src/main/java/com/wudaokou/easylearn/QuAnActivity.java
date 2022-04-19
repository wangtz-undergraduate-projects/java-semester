package com.wudaokou.easylearn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wudaokou.easylearn.adapter.EntityLinkAdapter;
import com.wudaokou.easylearn.adapter.MsgAdapter;
import com.wudaokou.easylearn.constant.Constant;
import com.wudaokou.easylearn.constant.SubjectMapChineseToEnglish;
import com.wudaokou.easylearn.data.Msg;
import com.wudaokou.easylearn.retrofit.Answer;
import com.wudaokou.easylearn.retrofit.Answers;
import com.wudaokou.easylearn.retrofit.EduKGService;
import com.wudaokou.easylearn.retrofit.JSONArray;
import com.wudaokou.easylearn.retrofit.JSONObject;
import com.wudaokou.easylearn.retrofit.entityLink.EntityLinkObject;
import com.wudaokou.easylearn.retrofit.entityLink.JsonEntityLink;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class QuAnActivity extends AppCompatActivity {
    private static final String TAG = "QuAnActivity";
    private List<Msg> msgList = new ArrayList<>();
    private RecyclerView msgRecyclerView;
    private EditText inputText;
    private Button send;
    private LinearLayoutManager layoutManager;
    private MsgAdapter adapter;
    private String subject;
    private boolean subject_confirmed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qu_an);

        subject_confirmed = false;

        msgRecyclerView = findViewById(R.id.msg_recycler_view);
        inputText = findViewById(R.id.input_text);
        send = findViewById(R.id.send);
        layoutManager = new LinearLayoutManager(this);
        adapter = new MsgAdapter(msgList = getData());

        msgRecyclerView.setLayoutManager(layoutManager);
        msgRecyclerView.setAdapter(adapter);

        msgList.add(new Msg("请先发送想询问的学科！",Msg.TYPE_RECEIVED));
        adapter.notifyItemInserted(msgList.size()-1);
        msgRecyclerView.scrollToPosition(msgList.size()-1);

        msgList.add(new Msg("有以下学科可选择：",Msg.TYPE_RECEIVED));
        adapter.notifyItemInserted(msgList.size()-1);
        msgRecyclerView.scrollToPosition(msgList.size()-1);

        msgList.add(new Msg("chinese,english,math,physics,chemistry,",Msg.TYPE_RECEIVED));
        adapter.notifyItemInserted(msgList.size()-1);
        msgRecyclerView.scrollToPosition(msgList.size()-1);

        msgList.add(new Msg("biology,history,geo,politics.",Msg.TYPE_RECEIVED));
        adapter.notifyItemInserted(msgList.size()-1);
        msgRecyclerView.scrollToPosition(msgList.size()-1);
/*       说明：by dhw ：为button建立一个监听器，将编辑框的内容发送到 RecyclerView 上：
            ①获取内容，将需要发送的消息添加到 List 当中去。
            ②调用适配器的notifyItemInserted方法，通知有新的数据加入了，赶紧将这个数据加到 RecyclerView 上面去。
            ③调用RecyclerView的scrollToPosition方法，以保证一定可以看的到最后发出的一条消息。*/
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = inputText.getText().toString();
                if(!content.equals("")) {
                    if (content.equals("chinese") || content.equals("english") || content.equals("math")
                    || content.equals("physics") || content.equals("chemistry") || content.equals("biology")
                    || content.equals("history") || content.equals("geo") || content.equals("politics")){
                        subject = content;
                        subject_confirmed = true;
                        msgList.add(new Msg(content,Msg.TYPE_SEND));
                        adapter.notifyItemInserted(msgList.size()-1);
                        msgRecyclerView.scrollToPosition(msgList.size()-1);
                        inputText.setText("");//清空输入框中的内容
                        msgList.add(new Msg("学科选择成功!",Msg.TYPE_RECEIVED));
                        adapter.notifyItemInserted(msgList.size()-1);
                        msgRecyclerView.scrollToPosition(msgList.size()-1);
                    }
                    else {
                        msgList.add(new Msg(content,Msg.TYPE_SEND));
                        adapter.notifyItemInserted(msgList.size()-1);
                        msgRecyclerView.scrollToPosition(msgList.size()-1);
                        inputText.setText("");//清空输入框中的内容

                        //向后端请求
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(Constant.eduKGBaseUrl)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();
                        EduKGService service = retrofit.create(EduKGService.class);

                        Call<JSONArray<Answer>> call = service.eduInputQuestion(Constant.eduKGId, subject, content);

                        call.enqueue(new Callback<JSONArray<Answer>>() {
                            @Override
                            public void onResponse(@NotNull Call<JSONArray<Answer>> call, @NotNull Response<JSONArray<Answer>> response) {
                                JSONArray<Answer> rsp = response.body();
                                // 返回错误，服务器错误
                                if(rsp == null){
                                    msgList.add(new Msg("服务器错误！请稍后再试！",Msg.TYPE_RECEIVED));
                                    adapter.notifyItemInserted(msgList.size()-1);
                                    msgRecyclerView.scrollToPosition(msgList.size()-1);
                                    return;
                                }
                                // 实体列表
                                List<Answer> data = rsp.getData();
                                // 搜索结果为空
                                if(data.isEmpty()){
                                    msgList.add(new Msg("抱歉，暂时无法为您解答这个问题。",Msg.TYPE_RECEIVED));
                                    adapter.notifyItemInserted(msgList.size()-1);
                                    msgRecyclerView.scrollToPosition(msgList.size()-1);
                                    return;
                                }
                                else{
                                    Double max = 0.0;
                                    String best = "以上是所有回答！";
                                    for(Integer i = 0; i < data.size(); i++){
                                        Answer a = data.get(i);
                                        Double score = a.getScore();
                                        String answer = a.getValue();
                                        msgList.add(new Msg(answer,Msg.TYPE_RECEIVED));
                                        adapter.notifyItemInserted(msgList.size()-1);
                                        msgRecyclerView.scrollToPosition(msgList.size()-1);
//                                        if (score > max){
//                                            max = score;
//                                            best = a.getValue();
//                                        }
                                    }
                                    msgList.add(new Msg(best,Msg.TYPE_RECEIVED));
                                    adapter.notifyItemInserted(msgList.size()-1);
                                    msgRecyclerView.scrollToPosition(msgList.size()-1);
                                }
                            }

                            @Override
                            public void onFailure(@NotNull Call<JSONArray<Answer>> call, @NotNull Throwable t) {
                                msgList.add(new Msg("网络错误！请检查网络设置！",Msg.TYPE_RECEIVED));
                                adapter.notifyItemInserted(msgList.size()-1);
                                msgRecyclerView.scrollToPosition(msgList.size()-1);
                            }
                        });
                    }
                }
//                先自定义一问一答 之后在此处实现通信完成信息的通讯
                /*if(msgList.size() == 0) {
                    msgList.add(new Msg("Please send subject first,then send your question.",Msg.TYPE_RECEIVED));
                    adapter.notifyItemInserted(msgList.size()-1);
                    msgRecyclerView.scrollToPosition(msgList.size()-1);
                }
                if(msgList.size() == 2){
                    msgList.add(new Msg("What's your name?",Msg.TYPE_RECEIVED));
                    adapter.notifyItemInserted(msgList.size()-1);
                    msgRecyclerView.scrollToPosition(msgList.size()-1);
                }
                if(msgList.size() == 4){
                    msgList.add(new Msg("Nice to meet you,Bye!",Msg.TYPE_RECEIVED));
                    adapter.notifyItemInserted(msgList.size()-1);
                    msgRecyclerView.scrollToPosition(msgList.size()-1);
                }*/
            }
        });
    }

    private List<Msg> getData(){
        List<Msg> list = new ArrayList<>();
        list.add(new Msg("Hello",Msg.TYPE_RECEIVED));
        return list;
    }
}