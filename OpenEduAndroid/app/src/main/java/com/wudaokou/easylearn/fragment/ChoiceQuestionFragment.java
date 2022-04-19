package com.wudaokou.easylearn.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.wudaokou.easylearn.R;
import com.wudaokou.easylearn.constant.Constant;
import com.wudaokou.easylearn.data.MyDatabase;
import com.wudaokou.easylearn.data.Question;
import com.wudaokou.easylearn.databinding.FragmentChoiceQuestionBinding;
import com.wudaokou.easylearn.retrofit.BackendService;
import com.wudaokou.easylearn.utils.LoadingDialog;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ChoiceQuestionFragment extends Fragment {

    String qAnswer;
    String qBody;
    String[] choices;
    FragmentChoiceQuestionBinding binding;
    List<RadioButton> radioButtonList;
    int selectedOption;
    Question question;
    boolean isValidChoiceQuestion;
    int optionNum;
    boolean immediateAnswer;
    boolean hasSubmit = false;
    int position; //记录给页面在试题页面中的顺序,从0开始

    //定义回调接口
    public interface MyListener{
        public void sendValue(int position, int option);
    }

    private MyListener myListener;


    public ChoiceQuestionFragment(Question question, int selectedOption,
                                  boolean immediateAnswer, int position) {
        // Required empty public constructor
        this.question = question;
        this.selectedOption = selectedOption;
        this.immediateAnswer = immediateAnswer;
        this.position = position;
        qAnswer = question.qAnswer;
        qBody = question.qBody;
        int aPos = 0;
        String ch = null;
        for (String splitChar : Constant.choiceSplitChars) {
            aPos = qBody.lastIndexOf("A" + splitChar);
            if (aPos != -1) {
                ch = splitChar;
                break;
            }
        }
        isValidChoiceQuestion = (ch != null);
        if (isValidChoiceQuestion) {
            optionNum = 0;
            int bPos = qBody.lastIndexOf("B" + ch);
            int cPos = qBody.lastIndexOf("C" + ch);
            int dPos = qBody.lastIndexOf("D" + ch);
            choices = new String[4];
            try {
                choices[0] = qBody.substring(aPos, bPos);
                optionNum++;
            } catch (IndexOutOfBoundsException e) {
                Log.d("choice question", "split option A fail");
            }

            try {
                choices[1] = qBody.substring(bPos, cPos);
                optionNum++;
            } catch (IndexOutOfBoundsException e) {
                Log.d("choice question", "split option B fail");
            }

            try {
                if (dPos != -1)
                    choices[2] = qBody.substring(cPos, dPos);
                else
                    choices[2] = qBody.substring(cPos);
                optionNum++;
            } catch (IndexOutOfBoundsException e) {
                Log.d("choice question", "split option C fail");
            }

            try {
                choices[3] = qBody.substring(dPos);
                optionNum++;
            } catch (IndexOutOfBoundsException e) {
                Log.d("choice question", "split option C fail");
            }

            qBody = qBody.substring(0, aPos);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChoiceQuestionBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.choiceQuestionBody.setText(qBody);
        binding.choiceQuestionAnswer.setText(qAnswer);

        if (!isValidChoiceQuestion)
            return root;

        radioButtonList = new ArrayList<>();

        for (int i = 0; i != optionNum; i++) {
            RadioButton radioButton = new RadioButton(getActivity());
            radioButton.setText(choices[i]);
            radioButton.setTextSize(20);
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RadioButton radioButton1 = (RadioButton) v;
                    String text = (String) radioButton1.getText();

                    int option = (int)(text.charAt(0) - 'A');
                    if (option < 0 || option > 3) {
                        option = -1;
                        Log.e("choice question", String.format("illegal option: %d", option));
                    }
                    selectedOption = option;

                    if (!hasSubmit) {
                        myListener.sendValue(position, option);
                    }

                    if (immediateAnswer) { // 点击后立即显示答案，判断对错
                        boolean isCorrect = text.substring(0, 1).equals(qAnswer);
                        if (isCorrect) {
                            radioButton1.setTextColor(getResources().getColor(R.color.green_A700));
                        } else {
                            radioButton1.setTextColor(getResources().getColor(R.color.red_900));
                        }
                        binding.answerLayout.setVisibility(View.VISIBLE);
                        for (RadioButton button : radioButtonList) {
                            button.setEnabled(false);
                        }

                        if (question.course != null) {
                            Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl(Constant.backendBaseUrl)
                                    .addConverterFactory(ScalarsConverterFactory.create())
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build();
                            BackendService backendService = retrofit.create(BackendService.class);
                            backendService.putQuestionCount(Constant.backendToken, question.id, !isCorrect,
                                qAnswer, qBody, question.label, question.course.toUpperCase())
                                .enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(@NotNull Call<String> call,
                                                           @NotNull Response<String> response) {
                                        if (response.code() == 200) {
                                            Log.e("question", "send answer ok");
                                        } else {
                                            Log.e("question", "send answer fail");
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NotNull Call<String> call,
                                                          @NotNull Throwable t) {
                                        Log.e("question", "send answer error");
                                    }
                                });
                        }
                    } else { // 做完全部题目后显示
                        radioButton1.setTextColor(getResources().getColor(R.color.blue_500));
                    }
                }
            });
            binding.radioGroup.addView(radioButton);
            radioButtonList.add(radioButton);
        }

        if (selectedOption != -1) {
            radioButtonList.get(selectedOption).callOnClick();
        }
        return root;
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        //获取实现接口的activity
        myListener = (MyListener) getActivity();
    }

    public void setImmediate(boolean newImmediate) {
        this.immediateAnswer = newImmediate;
    }

    public void onSubmit() {
        this.hasSubmit = true;
    }
}