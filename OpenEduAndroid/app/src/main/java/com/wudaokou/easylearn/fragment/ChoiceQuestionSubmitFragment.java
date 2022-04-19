package com.wudaokou.easylearn.fragment;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wudaokou.easylearn.R;
import com.wudaokou.easylearn.adapter.QuestionAnswerAdapter;
import com.wudaokou.easylearn.databinding.FragmentChoiceQuestionSubmitBinding;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

import javax.net.ssl.SSLEngineResult;


public class ChoiceQuestionSubmitFragment extends Fragment {

    FragmentChoiceQuestionSubmitBinding binding;
    RecyclerView recyclerView;
    Button submitButton, watchButton, redoButton;
    List<Integer> questionStatusList;
    QuestionAnswerAdapter adapter;
    ConstraintLayout beforeLayout;
    LinearLayout afterLayout;
    boolean hasSubmit = false;
    float accuracy;

    //定义回调接口
    public interface MyListener{
        public void submitAnswer();
        public void goToTargetQuestion(int position);
        public void goBack();
    }

    private MyListener myListener;

    public ChoiceQuestionSubmitFragment(List<Integer> questionStatusList) {
        this.questionStatusList = questionStatusList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChoiceQuestionSubmitBinding.inflate(inflater, container, false);
        recyclerView = binding.recyclerView2;
        submitButton = binding.submitAnsweButton;
        watchButton = binding.watchAnsweButton;
        redoButton = binding.redoButon;
        beforeLayout = binding.beforeSubmitLayout;
        afterLayout = binding.afterSubmitLayout;

        if (!hasSubmit) {
            afterLayout.setVisibility(View.GONE);
            beforeLayout.setVisibility(View.VISIBLE);
        } else {
            afterLayout.setVisibility(View.VISIBLE);
            beforeLayout.setVisibility(View.GONE);
            binding.accuracyLayout.setVisibility(View.VISIBLE);
            binding.circleBar.setPercentData(accuracy, new DecelerateInterpolator());
            if (adapter != null) {
                adapter.onSubmit();
                adapter.notifyDataSetChanged();
            }
        }

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 5));
        adapter = new QuestionAnswerAdapter(questionStatusList, hasSubmit);
        adapter.setOnItemClickListener(new QuestionAnswerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                myListener.goToTargetQuestion(position);
            }
        });
        recyclerView.setAdapter(adapter);

        submitButton.setOnClickListener(new View.OnClickListener() { //提交答案
            @Override
            public void onClick(View v) {
                if (adapter != null) {
                    adapter.onSubmit();
                    adapter.notifyDataSetChanged();
                }
                beforeLayout.setVisibility(View.GONE);
                afterLayout.setVisibility(View.VISIBLE);
                binding.accuracyLayout.setVisibility(View.VISIBLE);

                int totalCount = questionStatusList.size();
                int correctCount = 0;
                for (int idx = 0; idx != totalCount; idx++) {
                    if (questionStatusList.get(idx) == 1)
                        correctCount++;
                }

                accuracy = (float)correctCount / totalCount * 100;
                binding.circleBar.setPercentData(accuracy, new DecelerateInterpolator());

                hasSubmit = true;
                myListener.submitAnswer();
            }
        });

        watchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myListener.goToTargetQuestion(0);
            }
        });

        redoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myListener.goBack();
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        //获取实现接口的activity
        myListener = (MyListener) getActivity();
    }

    public void updateData(List<Integer> data) {
        this.questionStatusList = data;
        adapter.updateData(data);
        adapter.notifyDataSetChanged();
    }
}