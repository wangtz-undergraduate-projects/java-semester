package com.wudaokou.easylearn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.wudaokou.easylearn.databinding.ActivityBrowsingHistoryBinding;
import com.wudaokou.easylearn.databinding.ActivityStarHistoryBinding;
import com.wudaokou.easylearn.fragment.EntityQuestionFragment;
import com.wudaokou.easylearn.fragment.HomePagerFragment;

import org.jetbrains.annotations.NotNull;

public class StarHistoryActivity extends AppCompatActivity {

    ActivityStarHistoryBinding binding;
    HomePagerFragment homePagerFragment;
    EntityQuestionFragment entityQuestionFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStarHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        bindPagerWithTab();
    }

    public void getBack(View view) {
        this.finish();
    }

    public void bindPagerWithTab() {
        binding.starPager.setAdapter(new FragmentStateAdapter(
                getSupportFragmentManager(), getLifecycle()) {
            @NonNull
            @NotNull
            @Override
            public Fragment createFragment(int position) {
                if (position == 0) {
                    if (homePagerFragment == null) {
                        homePagerFragment = new HomePagerFragment(true);
                    }
                    return homePagerFragment;
                } else {
                    if (entityQuestionFragment == null) {
                        entityQuestionFragment = new EntityQuestionFragment(true);
                    }
                    return entityQuestionFragment;
                }
            }

            @Override
            public int getItemCount() {
                return 2;
            }
        });

        new TabLayoutMediator(binding.starTabs, binding.starPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull @NotNull TabLayout.Tab tab, int position) {
                TextView tabView = new TextView(StarHistoryActivity.this);
                tabView.setGravity(Gravity.CENTER);
                String[] titles = {"实体", "习题"};
                tabView.setText(titles[position]);
                tab.setCustomView(tabView);
            }
        }).attach();
    }
}