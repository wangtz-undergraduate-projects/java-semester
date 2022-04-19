package com.wudaokou.easylearn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.wudaokou.easylearn.constant.Constant;
import com.wudaokou.easylearn.constant.SubjectMapChineseToEnglish;
import com.wudaokou.easylearn.data.MyDatabase;
import com.wudaokou.easylearn.data.Question;
import com.wudaokou.easylearn.retrofit.EduKGService;
import com.wudaokou.easylearn.retrofit.JSONArray;
import com.wudaokou.easylearn.utils.LoadingDialog;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TestActivity extends AppCompatActivity {

    TextInputLayout chooseSubjectLayout;
    AutoCompleteTextView chooseSubject;
    LinearLayout keywordLayout;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        chooseSubject = findViewById(R.id.chooseSubject);
        keywordLayout = findViewById(R.id.keyword_layout);
        chooseSubjectLayout = findViewById(R.id.chooseSubjectLayout);
        loadingDialog = new LoadingDialog(this);

        // 选择学科
        String[] subjects = getResources().getStringArray(R.array.subjects);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                this, R.layout.dropdown_subject_item, subjects);
        chooseSubject.setAdapter(arrayAdapter);

        chooseSubject.setOnItemClickListener((parent, view, position, id) -> chooseSubjectLayout.setError(null));

        increaseKeywordLayout();
    }

    public void getBack(View view) {
        finish();
    }

    public void increaseKeywordLayout(View view) {
        increaseKeywordLayout().requestFocus();
    }

    public EditText increaseKeywordLayout() {
        LayoutInflater.from(this).inflate(R.layout.test_keyword_layout, keywordLayout);
        final int newIndex = keywordLayout.getChildCount() - 1;
        LinearLayout newLayout = (LinearLayout) keywordLayout.getChildAt(newIndex);

        TextInputLayout newInputLayout = (TextInputLayout) newLayout.getChildAt(0);
        newInputLayout.setHint( "知识点" + keywordLayout.getChildCount() );

        Button newButton = (Button) newLayout.getChildAt(1);
        newButton.setOnClickListener(this::deleteRowOnClick);

        EditText newEditText = newInputLayout.getEditText();
        assert newEditText != null;
        newEditText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            public void afterTextChanged(Editable s) {
                newInputLayout.setError(null);
            }
        });
        return newEditText;
    }

    private void deleteRowOnClick(View v) {
        if(keywordLayout.getChildCount() == 1){
            Toast.makeText(this, "知识点不得少于1项", Toast.LENGTH_SHORT).show();
            return;
        }
        keywordLayout.removeViewAt(keywordLayout.indexOfChild((LinearLayout) v.getParent()));
        for(int i = 0; i < keywordLayout.getChildCount(); i++){
            LinearLayout iLayout = (LinearLayout) keywordLayout.getChildAt(i);
            TextInputLayout iInputLayout = (TextInputLayout) iLayout.getChildAt(0);
            iInputLayout.setHint( "知识点" + (i+1) );
        }
    }


    public void generate(View view) {
        boolean valid = true;
        String subject = chooseSubject.getText().toString();
        String subjectInEnglish = SubjectMapChineseToEnglish.getMap().get(subject);
        if(subject.equals("")){
            chooseSubjectLayout.setError("请选择学科");
            valid = false;
        }
        List<String> labels = new ArrayList<>();
        for(int i = 0; i < keywordLayout.getChildCount(); i++){
            LinearLayout iLayout = (LinearLayout) keywordLayout.getChildAt(i);
            TextInputLayout iInputLayout = (TextInputLayout) iLayout.getChildAt(0);
            EditText iEditText = iInputLayout.getEditText();
            assert iEditText != null;
            String text = iEditText.getText().toString();
            if(text.isEmpty()){
                iInputLayout.setError("知识点为空");
                valid = false;
            }
            labels.add(text);
        }
        if(!valid) return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                getQuestion(labels);
            }
        }).start();
    }

    public void getQuestion(List<String> labelList) {
        Log.e("test_activity", "enter get question");
        List<Question> questionList = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(labelList.size());
        Log.e("test_activity", String.format("init latch with %d thread", questionList.size()));
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.eduKGBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        EduKGService service = retrofit.create(EduKGService.class);


        runOnUiThread(() -> loadingDialog.show());

        MyDatabase myDatabase = MyDatabase.getDatabase(this);

        for (String label : labelList) {
            service.questionListByUriName(Constant.eduKGId, label).enqueue(
                    new Callback<JSONArray<Question>>() {
                @Override
                public void onResponse(@NotNull Call<JSONArray<Question>> call,
                                       @NotNull Response<JSONArray<Question>> response) {
                    if (response.body() != null) {
                        for (Question question : response.body().data) {
                            if (question.qAnswer.length() == 1) {
//                                 只看选择题
                                Future<Question> questionFuture = MyDatabase.databaseWriteExecutor.submit(new Callable<Question>() {
                                    @Override
                                    public Question call() throws Exception {
                                        return myDatabase.questionDAO().loadQuestionById(question.id);
                                    }
                                });
                                try {
                                    Question localQuestion = questionFuture.get();
                                    if (localQuestion != null) {
                                        question.course = localQuestion.course;
                                        question.wrongCount = localQuestion.wrongCount;
                                        question.totalCount = localQuestion.totalCount;
                                        question.hasStar = localQuestion.hasStar;
                                        question.label = localQuestion.label;
                                    } else {
                                        question.hasStar = false;
                                    }
                                } catch (InterruptedException | ExecutionException e) {
                                    e.printStackTrace();
                                } finally {
                                    questionList.add(question);
                                }
                            }
                        }
                    }
                    latch.countDown();
                    Log.e("test_activity", "latch count down");
                }

                @Override
                public void onFailure(@NotNull Call<JSONArray<Question>> call,
                                      @NotNull Throwable t) {
                    latch.countDown();
                    Log.e("test_activity", "latch count down");
                }
            });
        }

        try {
            latch.await();
            Log.e("test_activity", "latch end wait");
            if (questionList.size() != 0) {
                Log.e("test_activity", "get some question");
                Collections.shuffle(questionList);
                Intent intent = new Intent(this, AnswerActivity.class);
                intent.putExtra("position", 0);
                intent.putExtra("questionList", (Serializable)questionList);
                intent.putExtra("label", "专项测试");
                intent.putExtra("immediateAnswer", false);
                startActivity(intent);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            runOnUiThread(() -> {loadingDialog.dismiss();});
        }

    }
}