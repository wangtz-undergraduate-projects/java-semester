package com.wudaokou.easylearn.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface EntityInfoDAO {
    @Insert
    void insertEntityInfo(EntityInfo entityInfo);

    @Query("DELETE FROM entity_info")
    public void deleteAllEntityInfo();

    @Delete
    public void deleteEntityInfo(EntityInfo entityInfo);

    @Query("SELECT * FROM entity_info")
    public List<EntityInfo> loadAllEntityInfo();
}
