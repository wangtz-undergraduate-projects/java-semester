package com.wudaokou.easylearn.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "content_without_feature")
public class ContentWithoutFeature {
    @PrimaryKey(autoGenerate = true)
    public Integer id;

    public String predicate;

    public String predicate_label;

    public String object_label;

    public String object;

    public String subject_label;

    public String subject;

    public boolean hasRead;

    public String label;  // 记录所属知识点

    public String course;  // 记录所属学科
}
