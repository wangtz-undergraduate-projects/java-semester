package com.wudaokou.easylearn.ui.train;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wudaokou.easylearn.R;
import com.wudaokou.easylearn.databinding.FragmentTrainBinding;

import org.jetbrains.annotations.NotNull;

public class TrainFragment extends Fragment {

    private TrainViewModel trainViewModel;
    private FragmentTrainBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        trainViewModel =
                new ViewModelProvider(this).get(TrainViewModel.class);

        binding = FragmentTrainBinding.inflate(inflater, container, false);
        View root = binding.getRoot();



        // 显示定制的toolbar
//        NavController navController = Navigation.findNavController(requireActivity(),
//                R.id.nav_host_fragment_activity_main);
//        AppBarConfiguration appBarConfiguration =
//                new AppBarConfiguration.Builder(navController.getGraph()).build();
//        Toolbar toolbar = binding.toolbar3;
//        NavigationUI.setupWithNavController(
//                toolbar, navController, appBarConfiguration);

        setHasOptionsMenu(true);

        return root;
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