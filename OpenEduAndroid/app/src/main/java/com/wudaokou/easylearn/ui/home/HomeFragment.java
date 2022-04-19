package com.wudaokou.easylearn.ui.home;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.wudaokou.easylearn.R;
import com.wudaokou.easylearn.SearchableActivity;
import com.wudaokou.easylearn.SubjectManageActivity;
import com.wudaokou.easylearn.bean.SubjectChannelBean;
import com.wudaokou.easylearn.constant.Constant;
import com.wudaokou.easylearn.constant.SubjectMap;
import com.wudaokou.easylearn.constant.SubjectMapChineseToEnglish;
import com.wudaokou.easylearn.databinding.FragmentHomeBinding;
import com.wudaokou.easylearn.fragment.HomePagerFragment;
import com.wudaokou.easylearn.utils.ListDataSave;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    private final int subjectManageRequestCode = 789;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // 启动后检查是否勾选学科为空，是则默认添加语数英

        final String[] keys = Constant.subjectList;
//        SharedPreferences sharedPreferences =
//                PreferenceManager.getDefaultSharedPreferences(requireContext());
//        boolean oneSelected = false;
//        for (String key : keys) {
//            if (sharedPreferences.getBoolean(key, false)) {
//                oneSelected = true;
//                break;
//            }
//        }
//        if (!oneSelected) {
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putBoolean("chinese", true);
//            editor.putBoolean("math", true);
//            editor.putBoolean("english", true);
//            editor.apply();
//        }

//        intiSearchView();

        // 显示定制的toolbar
//        NavController navController = Navigation.findNavController(requireActivity(),
//                R.id.nav_host_fragment_activity_main);
//        AppBarConfiguration appBarConfiguration =
//                new AppBarConfiguration.Builder(navController.getGraph()).build();
//        Toolbar toolbar = binding.toolbar;
//        NavigationUI.setupWithNavController(
//                toolbar, navController, appBarConfiguration);

        // 使activity回调fragment的onCreateOptionsMenu函数
//        setHasOptionsMenu(true);
//        ((AppCompatActivity) requireActivity()).setSupportActionBar(binding.toolbar);

        // 监听按钮事件，启动添加学科tab的activity
        binding.imageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SubjectManageActivity.class);
                intent.putExtra("tabPosition", binding.pager.getCurrentItem());
                startActivityForResult(intent, subjectManageRequestCode);
            }
        });

        binding.searchLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("HomeFragment", "click search box");
                Intent intent = new Intent(getActivity(), SearchableActivity.class);
                startActivity(intent);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        Vector<String> courseList = new Vector<>();
        ListDataSave listDataSave = new ListDataSave(requireContext(), "channel");
        List<SubjectChannelBean> subjectChannelBeanList = listDataSave
                .getDataList("myChannel", SubjectChannelBean.class);
        for (SubjectChannelBean subjectChannelBean : subjectChannelBeanList) {
            courseList.add(subjectChannelBean.tid);
        }

        binding.pager.setAdapter(new FragmentStateAdapter(getChildFragmentManager(),
                getLifecycle()) {
            @NonNull
            @NotNull
            @Override
            public Fragment createFragment(int position) {
                String key = courseList.get(position);
                return new HomePagerFragment(key);
            }

            @Override
            public int getItemCount() {
                return courseList.size();
            }
        });

        HashMap<String, String> map = SubjectMap.getMap();
        new TabLayoutMediator(binding.tabs, binding.pager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull @NotNull TabLayout.Tab tab, int position) {
                TextView tabView = new TextView(requireContext());
                tabView.setGravity(Gravity.CENTER);
                tabView.setText(map.get(courseList.get(position)));
                tab.setCustomView(tabView);
            }
        }).attach();
        Log.w("HomeFragment","tabLayout has been initiated");
    }

    /**
     * 接收从该fragment启动activity的返回结果
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case subjectManageRequestCode:
                // 获取当前展示的学科目录
//                Intent intent =
                break;
            default:
                break;
        }
    }

    public void intiSearchView() {
//        SearchView mSearchView = binding.searchView;
//        mSearchView.setIconifiedByDefault(false);
////        mSearchView.setSubmitButtonEnabled(true);
//        mSearchView.setQueryRefinementEnabled(true);
//        mSearchView.setQueryHint("搜索知识点");

        // 去掉搜索框默认的下划线
//        mSearchView.findViewById(R.id.search_plate).setBackground(null);
//        mSearchView.findViewById(R.id.submit_area).setBackground(null);

        // 设置搜索框背景样式
//        mSearchView.setBackground(ContextCompat.getDrawable(
//                requireActivity(), R.drawable.search_view_background));


        // 搜索内容自动提示
        // 待完成Adapter
//        mSearchView.setSuggestionsAdapter();


        // Get the SearchView and set the searchable configuration
//        SearchManager searchManager = (SearchManager) requireActivity().getSystemService(Context.SEARCH_SERVICE);
//        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().getComponentName()));

//         搜索框打开监听
//        mSearchView.setOnSearchClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Intent intent = new Intent(getActivity(), SearchableActivity.class);
////                intent.setAction(Intent.ACTION_SEARCH);
////                startActivity(intent);
////                Toast.makeText(requireActivity(),"open",Toast.LENGTH_SHORT).show();
//            }
//        });

        // 搜索框关闭监听
//        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
//            @Override
//            public boolean onClose() {
//                Toast.makeText(requireActivity(),"close",Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        });

        // 输入文本变化监听
//        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                // 提交文本时调用
////                Snackbar.make(mSearchView.findViewById(R.id.search_go_btn),query,Snackbar.LENGTH_SHORT).show();
//
//                Intent intent = new Intent(getActivity(), SearchableActivity.class);
//                intent.setAction(Intent.ACTION_SEARCH);
//                intent.putExtra("QUERY", query);
//                startActivity(intent);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                // 文本搜索框发生变化时调用
//                return false;
//            }
//        });
    }
}