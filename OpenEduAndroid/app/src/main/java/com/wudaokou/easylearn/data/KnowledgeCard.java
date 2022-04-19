package com.wudaokou.easylearn.data;

import androidx.room.Entity;

import java.util.List;

@Entity(tableName = "knowledge_card")
public class KnowledgeCard {
    public String entity_type;
    public String entity_name;
    public List<EntityFeature> entity_features;
}
