package com.wudaokou.easylearn;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wudaokou.easylearn.adapter.ChannelAdapter;
import com.wudaokou.easylearn.bean.SubjectChannelBean;
import com.wudaokou.easylearn.constant.Constant;
import com.wudaokou.easylearn.data.DataStore;
import com.wudaokou.easylearn.databinding.ActivitySubjectManageBinding;
import com.wudaokou.easylearn.utils.GridItemDecoration;
import com.wudaokou.easylearn.utils.IChannelType;
import com.wudaokou.easylearn.utils.ListDataSave;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.security.auth.Subject;

public class SubjectManageActivity extends AppCompatActivity {

    ActivitySubjectManageBinding binding;
    private List<SubjectChannelBean> myChannelList;
    private List<SubjectChannelBean> recChannelList;
    private RecyclerView mRecyclerView;
    private ChannelAdapter mRecyclerAdapter;
    private int tabPosition;
    private ListDataSave listDataSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubjectManageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        // todo pass the current tab position through intent before starting this activity
        tabPosition = intent.getIntExtra("tabPosition", 0);

        listDataSave = new ListDataSave(this, "channel");

        mRecyclerView = binding.manageRecyclerView;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                boolean isHeader = mRecyclerAdapter.getItemViewType(position) == IChannelType.TYPE_MY_CHANNEL_HEADER ||
                        mRecyclerAdapter.getItemViewType(position) == IChannelType.TYPE_REC_CHANNEL_HEADER;
                return isHeader ? 3 : 1;
            }
        });

        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.addItemDecoration(new GridItemDecoration(Constant.ITEM_SPACE));

        initData();
        mRecyclerAdapter = new ChannelAdapter(this, mRecyclerView, myChannelList,
                recChannelList, 1, 1);
        mRecyclerAdapter.setChannelItemClickListener(new ChannelAdapter.ChannelItemClickListener() {
            @Override
            public void onChannelItemClick(List<SubjectChannelBean> list, int position) {

            }
        });
        mRecyclerView.setAdapter(mRecyclerAdapter);
    }

    public void initData() {
        myChannelList = new ArrayList<>();

        List<SubjectChannelBean> list = listDataSave
                .getDataList("myChannel", SubjectChannelBean.class);
        for (int i = 0; i < list.size(); i ++){
            SubjectChannelBean SubjectChannelBean = list.get(i);
//            if (i == tabPosition){
//                SubjectChannelBean.tabType = Constant.ITEM_DEFAULT;
//            } else {
//                // 判断i是否为0或者1,如果为0设置标题为红色（当前浏览的tab标签），
//                // 如果为1则设置type为1（不可编辑移动），不为1则type为2
//                // type为2表示该标签可供编辑移动
////                int type;
////                if (i == 0  || i == 1){
////                    type = 1;
////                } else {
////                    type = 2;
////                }
//
//            }
            SubjectChannelBean.tabType = 2;
            myChannelList.add(SubjectChannelBean);
        }

        recChannelList = new ArrayList<>();
        List<SubjectChannelBean> moreChannelList = listDataSave
                .getDataList("moreChannel", SubjectChannelBean.class);
        for (SubjectChannelBean subjectChannelBean:moreChannelList) {
            subjectChannelBean.setTabType(2);
            recChannelList.add(subjectChannelBean);
        }
    }

    @Override
    protected void onPause() {
        for (SubjectChannelBean subjectChannelBean : myChannelList) {
            // 将当前模式设置为不可编辑状态
            subjectChannelBean.setEditStatus(0);
        }
        listDataSave.setDataList("myChannel", myChannelList);
        listDataSave.setDataList("moreChannel", recChannelList);

        super.onPause();
    }

    @Override
    public void finish() {
        mRecyclerAdapter.doCancelEditMode(mRecyclerView);

        for (int i = 0; i < myChannelList.size(); i ++) {
            SubjectChannelBean projectChannelBean = myChannelList.get(i);
            if (projectChannelBean.getTabType() == 0){
                tabPosition = i;
            }
        }
        Intent intent = new Intent();
        intent.putExtra("NewTabPosition", tabPosition);
        setResult(789, intent);

        super.finish();
    }
}