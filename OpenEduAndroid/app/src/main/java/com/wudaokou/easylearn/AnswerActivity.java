package com.wudaokou.easylearn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.wudaokou.easylearn.constant.Constant;
import com.wudaokou.easylearn.data.MyDatabase;
import com.wudaokou.easylearn.data.Question;
import com.wudaokou.easylearn.databinding.ActivityAnswerBinding;
import com.wudaokou.easylearn.fragment.ChoiceQuestionFragment;
import com.wudaokou.easylearn.fragment.ChoiceQuestionSubmitFragment;
import com.wudaokou.easylearn.retrofit.BackendService;
import com.wudaokou.easylearn.retrofit.EduKGService;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class AnswerActivity extends AppCompatActivity
        implements ChoiceQuestionFragment.MyListener, ChoiceQuestionSubmitFragment.MyListener {

    private ActivityAnswerBinding binding;
    private List<Question> questionList;
    private List<Integer> questionAnswerList; // -1 for unselected, 0~3 for A ~ D
    private List<Integer> questionStatusList; // -1 for unselected, 0 for false, 1 for correct
    private boolean immediateAnswer; // true for show answer immediately
    private boolean hasSubmit = false;

    List<ChoiceQuestionFragment> choiceQuestionFragmentList;
    ChoiceQuestionSubmitFragment choiceQuestionSubmitFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAnswerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 获取试题信息
        Intent intent = getIntent();
        questionList = (List<Question>) intent.getSerializableExtra("questionList");
        immediateAnswer = intent.getBooleanExtra("immediateAnswer", true);
        int position = intent.getIntExtra("position", 0);
        String label = intent.getStringExtra("label");

        // 初始化答案列表
        questionAnswerList = new ArrayList<>();
        questionStatusList = new ArrayList<>();
        if (questionList != null) {
            for (Question question : questionList){
                questionAnswerList.add(-1);
                questionStatusList.add(-1);
            }
        }

        for (int i = 0; i < questionAnswerList.size(); i++) {
            Log.e("answer", String.format("questionAnswerList[%d] = %d", i, questionAnswerList.get(i)));
        }

        choiceQuestionFragmentList = new ArrayList<>();
        for (int i = 0; i != questionList.size(); i++) {
            choiceQuestionFragmentList.add(new ChoiceQuestionFragment(questionList.get(i),
                    questionAnswerList.get(i), immediateAnswer, i));
        }

        // 为viewpager2设置adapter
        binding.questionViewPager2.setAdapter(new FragmentStateAdapter(getSupportFragmentManager(),
                getLifecycle()) {
            @NonNull
            @NotNull
            @Override
            public Fragment createFragment(int position) {
                // assert: questionList != null
                if (position != questionList.size()) {
                    return choiceQuestionFragmentList.get(position);
                }
                else {
                    if (choiceQuestionSubmitFragment == null) {
                        choiceQuestionSubmitFragment = new ChoiceQuestionSubmitFragment(questionStatusList);
                    }
                    return choiceQuestionSubmitFragment;
                }
            }

            @Override
            public int getItemCount() {
                if (questionList != null && immediateAnswer) {
                    return questionList.size();
                } else if (questionList != null && !immediateAnswer) {
                    return questionList.size() + 1;
                }
                return 0;
            }
        });
        binding.questionViewPager2.setCurrentItem(position, true);
        // 设置初始位置
        binding.workProgress.setText(String.format(Locale.CHINA, "%d/%d", position + 1, questionList.size()));
        binding.questionViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position != questionList.size()) {
                    binding.starQuestionButton.setVisibility(View.VISIBLE);
                    binding.workProgress.setVisibility(View.VISIBLE);
                    Question question = questionList.get(position);
                    binding.workProgress.setText(String.format(Locale.CHINA, "%d/%d",
                            binding.questionViewPager2.getCurrentItem() + 1, questionList.size()));
                    if (question.hasStar) {
                        binding.starQuestionButton.setImageResource(R.drawable.star_fill);
                    } else {
                        binding.starQuestionButton.setImageResource(R.drawable.star_blank);
                    }
                } else {
                    choiceQuestionSubmitFragment.updateData(questionStatusList);
                    binding.starQuestionButton.setVisibility(View.GONE);
                    binding.workProgress.setVisibility(View.GONE);
                }
            }
        });
        String label2 = label.length() < 10 ? label : label.substring(0, 10);
        binding.entityTitle.setText(String.format("%s相关习题", label2));
    }

    public void goBack(View view) {
        this.finish();
    }

    public void onStarQuestionClick(View view) {
        int position = binding.questionViewPager2.getCurrentItem();
        Question question = questionList.get(position);
        question.hasStar = !question.hasStar;

        if (question.hasStar) {
            binding.starQuestionButton.setImageResource(R.drawable.star_fill);
        } else {
            binding.starQuestionButton.setImageResource(R.drawable.star_blank);
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.backendBaseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        BackendService backendService = retrofit.create(BackendService.class);
        String course = question.course == null ? "" : question.course.toUpperCase();
        String label = question.label == null ? "" : question.label;
        backendService.onStarQuestion(Constant.backendToken, question.hasStar, question.id,
                question.qAnswer, question.qBody,
                label, course).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NotNull Call<String> call,
                                   @NotNull Response<String> response) {
                if (response.code() == 200) {
                    String msg = question.hasStar ? "收藏题目成功!" : "取消收藏成功!";
                    Toast.makeText(AnswerActivity.this, msg, Toast.LENGTH_LONG).show();
                } else {
                    String msg = question.hasStar ? "收藏题目失败!" : "取消收藏失败!";
                    Toast.makeText(AnswerActivity.this, "收藏题目失败!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<String> call,
                                  @NotNull Throwable t) {
                String msg = question.hasStar ? "收藏题目失败!" : "取消收藏失败!";
                Toast.makeText(AnswerActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });

        MyDatabase.databaseWriteExecutor.submit(new Runnable() {
            @Override
            public void run() {
                MyDatabase.getDatabase(AnswerActivity.this).questionDAO()
                        .updateQuestion(question);
            }
        });
    }

    @Override
    public void sendValue(int position, int option) {
        // 修改选中的选项值
        questionAnswerList.set(position, option);
        Question question = questionList.get(position);
        boolean isCorrect = (question.qAnswer.charAt(0) - 'A') == option;
        if (isCorrect) {
            questionStatusList.set(position, 1);
        } else {
            questionStatusList.set(position, 0);
        }
        Log.e("sendValue", String.format("question: %d, option: %s", position + 1, String.valueOf(isCorrect)));
    }

    @Override
    public void submitAnswer() {
        this.hasSubmit = true;
        for (ChoiceQuestionFragment fragment : choiceQuestionFragmentList){
            fragment.setImmediate(true);
        }
    }

    @Override
    public void goToTargetQuestion(int position) {
        binding.questionViewPager2.setCurrentItem(position);
    }

    @Override
    public void goBack() {
        this.finish();
    }

}