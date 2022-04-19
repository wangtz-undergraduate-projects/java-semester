package com.wudaokou.easylearn.data;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.wudaokou.easylearn.retrofit.HistoryParam;

import java.util.List;

public class HomeCourseItem {
    public SearchResult result;

//    @Relation(parentColumn = "uri",
//            entityColumn = "parentUri"
//    )
    public List<Property> propertyList;

    public HomeCourseItem(SearchResult searchResult, List<Property> propertyList) {
        this.propertyList = propertyList;
        this.result = searchResult;
    }

    public String createdAt; // 用于浏览时间
}
