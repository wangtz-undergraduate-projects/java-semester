package com.wudaokou.easylearn.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ContentDAO {
    @Insert
    void insertContent(Content content);

    @Query("DELETE FROM content")
    public void deleteAllContent();

    @Delete
    public void deleteContent(Content content);

//    @Query("")
//    public void updateContent();
    @Update
    public void updateContent(Content content);

    @Query("SELECT * FROM content")
    public List<Content> loadAllContent();

    @Query("SELECT * FROM content WHERE course = :course AND label = :label")
    public List<Content> loadContentByCourseAndLabel(String course, String label);
}
