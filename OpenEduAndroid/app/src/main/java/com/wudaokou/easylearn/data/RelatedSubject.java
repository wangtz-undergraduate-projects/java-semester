package com.wudaokou.easylearn.data;

import androidx.room.Entity;

@Entity(tableName = "related_subject")
public class RelatedSubject {
    public String all;
    public String fsanswer;
    public String subject;
    public String message;
    public String tamplateContent;
    public String fs;
    public String filterStr;
    public String subjectUri;
    public String predicate;
    public String score;
    public String answerflag;
    public String attention;
    public String fsscore;
    public String value;

    public String label;  // 记录所属知识点

    public String course;  // 记录所属学科
}
