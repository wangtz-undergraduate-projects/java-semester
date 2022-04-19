package com.wudaokou.easylearn;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import com.stfalcon.chatkit.utils.DateFormatter;
import com.wudaokou.easylearn.bean.Author;
import com.wudaokou.easylearn.bean.Message;
import com.wudaokou.easylearn.constant.Constant;
import com.wudaokou.easylearn.constant.SubjectMapChineseToEnglish;
import com.wudaokou.easylearn.databinding.ActivityKnowledgeQueryBinding;
import com.wudaokou.easylearn.retrofit.Answer;
import com.wudaokou.easylearn.retrofit.EduKGService;
import com.wudaokou.easylearn.retrofit.JSONArray;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class KnowledgeQueryActivity extends AppCompatActivity {

    ActivityKnowledgeQueryBinding binding;
    MessagesList messagesList;
    AutoCompleteTextView chooseSubjectText;
    TextInputEditText inputEditText;
    Button sendButton;
    MessagesListAdapter<Message> adapter;
    TextInputLayout inputLayout;
    Map<String, String> map;

    final String userId = "0";
    final String serverId = "1";

    final String userAvatar = null;
    final String serverAvatar = "https://z3.ax1x.com/2021/09/08/hbeaid.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityKnowledgeQueryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        messagesList = binding.messageList;
        chooseSubjectText = binding.chooseQuerySubject;
        inputEditText = binding.inputEditText;
        sendButton = binding.textButton;
        inputLayout = binding.typeContentLayout;

        map = SubjectMapChineseToEnglish.getMap();

        String[] subjects = getResources().getStringArray(R.array.subjects);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                this, R.layout.dropdown_subject_item, subjects);
        chooseSubjectText.setAdapter(arrayAdapter);

        // todo 后续再加载头像
        adapter = new MessagesListAdapter<>(userId, new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView,
                                  @Nullable @org.jetbrains.annotations.Nullable String url,
                                  @Nullable @org.jetbrains.annotations.Nullable Object payload) {
                assert url != null;
                Picasso.get().load(url).into(imageView);
            }
        });
        adapter.setDateHeadersFormatter(new DateFormatter.Formatter() {
            @Override
            public String format(Date date) {
                return date.toLocaleString();
            }
        });
        messagesList.setAdapter(adapter);
        sendGreetings();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmit();
            }
        });
    }


    public void sendGreetings() {
        Message message1 = new Message(serverId, "欢迎向我提问",
                new Date(), new Author(serverId, "EduKG", serverAvatar));
        Message message2 = new Message(serverId, "你可以在左下角选择提问的科目哦",
                new Date(), new Author(serverId, "EduKG", serverAvatar));
        List<Message> messages = new ArrayList<>();
        messages.add(message1);
        messages.add(message2);
        adapter.addToEnd(messages, true);
    }

    public void onSubmit() {
        String queryText = inputEditText.getText().toString();
        if (queryText.equals("")) {
            Toast.makeText(this, "输入文本不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        sendQuery(queryText);
        // 清除文本
        inputEditText.setText("");

        String subject = chooseSubjectText.getText().toString();
        String subjectInEnglish = map.get(subject);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.eduKGBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        EduKGService service = retrofit.create(EduKGService.class);
        service.eduInputQuestion(Constant.eduKGId, subjectInEnglish, queryText)
                .enqueue(new Callback<JSONArray<Answer>>() {
            @Override
            public void onResponse(@NotNull Call<JSONArray<Answer>> call,
                                   @NotNull Response<JSONArray<Answer>> response) {
                if (response.body() != null && response.body().data != null
                && !response.body().data.isEmpty()) {
                    List<Answer> answerList = response.body().data;
                    double maxScore = -1;
                    Answer bestAnswer = null;
                    for (Answer answer : answerList) {
                        if (answer.getScore() > maxScore) {
                            maxScore = answer.getScore();
                            bestAnswer = answer;
                        }
                    }
                    String value = bestAnswer.getValue().equals("") ?
                            bestAnswer.getMessage() : bestAnswer.getValue();
                    sendAnswer(value);
                } else {
                    sendAnswer("暂时找不到你想要的答案");
                }
            }

            @Override
            public void onFailure(@NotNull Call<JSONArray<Answer>> call,
                                  @NotNull Throwable t) {
                sendAnswer("网络错误，请稍后再试");
            }
        });

    }

    public void sendQuery(final String queryText) {
        Message message = new Message(userId, queryText, new Date(),
                new Author(userId, "me", userAvatar));
        adapter.addToStart(message, true);
    }

    public void sendAnswer(final String queryText) {
        Message message = new Message(serverId, queryText, new Date(),
                new Author(serverId, "me", serverAvatar));
        adapter.addToStart(message, true);
    }
}