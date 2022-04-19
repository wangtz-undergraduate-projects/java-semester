package com.wudaokou.easylearn.ui.user;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.wudaokou.easylearn.BrowsingHistoryActivity;
import com.wudaokou.easylearn.ChangePasswordActivity;
import com.wudaokou.easylearn.LoginActivity;
import com.wudaokou.easylearn.StarHistoryActivity;
import com.wudaokou.easylearn.constant.Constant;
import com.wudaokou.easylearn.databinding.FragmentUserBinding;

import org.jetbrains.annotations.NotNull;

public class UserFragment extends Fragment{

    private UserViewModel userViewModel;
    private FragmentUserBinding binding;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        binding = FragmentUserBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        arrangeLayout();

        binding.loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            startActivity(intent);
        });

        binding.logoffButton.setOnClickListener(v -> new MaterialAlertDialogBuilder(requireContext())
                .setTitle("确认退出登录？")
                .setMessage("这将会删除本地所有的个人数据及历史记录")
                .setNegativeButton("取消", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setPositiveButton("确认", (dialog, which) -> {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("token", "-1");
                    editor.apply();
                    Constant.backendToken = "";
                    arrangeLayout();

                    // TODO 删库

                    dialog.dismiss();
                })
                .show());

        binding.starButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), StarHistoryActivity.class);
            startActivity(intent);
        });

        binding.historyButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), BrowsingHistoryActivity.class);
            startActivity(intent);
        });

        binding.changePasswordButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ChangePasswordActivity.class);
            startActivity(intent);
        });

        return root;
    }

    private void arrangeLayout() {
        String token = sharedPreferences.getString("token", "-1");
        if (token.equals("-1")) {
            String text = "请登录";
            binding.nameText.setText(text);
            binding.loginButton.setVisibility(View.VISIBLE);
            binding.logoffButton.setVisibility(View.GONE);
            binding.changePasswordButton.setVisibility(View.GONE);
        } else {
            String text = "欢迎  " + sharedPreferences.getString("username", "");
            binding.nameText.setText(text);
            binding.loginButton.setVisibility(View.INVISIBLE);
            binding.logoffButton.setVisibility(View.VISIBLE);
            binding.changePasswordButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        arrangeLayout();
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, MenuInflater inflater) {
        menu.clear();
    }
}