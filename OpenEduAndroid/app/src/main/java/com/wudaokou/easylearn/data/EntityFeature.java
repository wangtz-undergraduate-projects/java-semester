package com.wudaokou.easylearn.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "entity_feature")
public class EntityFeature {
    @PrimaryKey(autoGenerate = true)
    public Integer id;

    public Integer parentId;

    public String feature_key;
    public String feature_value;
}
