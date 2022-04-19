package com.wudaokou.easylearn.data;

import android.widget.ListView;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

import java.util.List;

@Entity(tableName = "content")
public class Content {
    @PrimaryKey(autoGenerate = true)
    public Integer uid;

    public String predicate;

    public String predicate_label;

    public String object_label;

    public String object;

    public String subject_label;

    public String subject;

    public boolean hasStar;

    public boolean hasRead;

    public String label;  // 记录所属知识点

    public String course;  // 记录所属学科

//    @Relation(
//            parentColumn = "predicate_label",
//            entityColumn = "parentId"
//    )
//    public List<EntityFeature> entityFeatureList;
}
