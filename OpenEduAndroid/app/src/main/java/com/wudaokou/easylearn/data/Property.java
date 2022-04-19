package com.wudaokou.easylearn.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "property")
public class Property {
    @PrimaryKey(autoGenerate = true)
    public Integer uid;

    public String predicate;

    public String predicateLabel;

    public String object;  // uri

    public String objectLabel;

    @NotNull
    @Override
    public String toString() {
        return predicateLabel + "：" + object + "\n\n";
    }

    public String label;  // 记录所属知识点

    public String course;  // 记录所属学科

    public String parentUri; // 记录所属知识点的uri

    public boolean hasStar;

    public boolean hasRead;
}
