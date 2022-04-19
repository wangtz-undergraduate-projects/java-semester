package com.wudaokou.easylearn.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SearchResultDAO {
    @Insert
    void insertSearchResult(SearchResult searchResult);

    @Query("DELETE FROM search_result")
    public void deleteAllSearchResult();

    @Delete
    public void deleteSearchResult(SearchResult searchResult);

    @Update
    public void updateSearchResult(SearchResult searchResult);

    @Query("SELECT * FROM search_result")
    public List<SearchResult> loadAllSearchResult();

    @Query("SELECT * FROM search_result WHERE course = :course AND searchKey = :searchKey")
    public List<SearchResult> loadSearchResultByCourseAndLabel(String course, String searchKey);

    @Query("SELECT * FROM search_result WHERE uri = :uri")
    public SearchResult loadSearchResultByUri(String uri);

    // home page 使用
//    @Transaction
//    @Query("SELECT * FROM search_result")
//    public List<HomeCourseItem> loadSearchResultWithProperty();
}
