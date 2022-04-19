package com.wudaokou.easylearn.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SearchRecordDAO {
    // 用户名（主键）不能重复
    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insertRecord(SearchRecord searchRecord);

    @Query("DELETE FROM search_record")
    public void deleteAllRecord();

    @Delete
    public void deleteRecord(SearchRecord searchRecord);

    @Query("SELECT * FROM search_record")
    public List<SearchRecord> loadAllRecords();

    @Query("SELECT * FROM search_record ORDER BY timestamp DESC LIMIT :limit OFFSET 0")
    public List<SearchRecord> loadLimitedRecords(final int limit);
}
