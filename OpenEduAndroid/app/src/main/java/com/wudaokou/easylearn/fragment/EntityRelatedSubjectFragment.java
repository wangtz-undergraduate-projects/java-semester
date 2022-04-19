package com.wudaokou.easylearn.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wudaokou.easylearn.R;
import com.wudaokou.easylearn.constant.Constant;
import com.wudaokou.easylearn.databinding.FragmentEntityRelatedSubjectBinding;
import com.wudaokou.easylearn.retrofit.EduKGService;
import com.wudaokou.easylearn.utils.LoadingDialog;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EntityRelatedSubjectFragment extends Fragment {

    private FragmentEntityRelatedSubjectBinding binding;
    private LoadingDialog loadingDialog;
    private String course;
    private String label;
    EduKGService service;

    public EntityRelatedSubjectFragment(final String course, final String label) {
        this.course = course;
        this.label = label;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEntityRelatedSubjectBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.eduKGBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(EduKGService.class);

        return root;
    }
}