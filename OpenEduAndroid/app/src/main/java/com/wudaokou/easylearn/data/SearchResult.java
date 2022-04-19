package com.wudaokou.easylearn.data;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

@Entity(tableName = "search_result")
public class SearchResult implements Serializable {
    @NotNull
    @PrimaryKey
    public String uri;

    public String label;

    public String category;

    public boolean hasRead;

    public boolean hasStar;

    public String course;    // 搜索的学科

    public String searchKey; // 搜索的关键词

    public int id;  // 收藏后后端生成的id

    public SearchResult(final String label, final String category, @NotNull final String uri) {
        this.category = category;
        this.label = label;
        this.uri = uri;
        this.searchKey = null;
        this.course = null;
        this.hasRead = false;
        this.hasStar = false;
        this.id = 0;
    }

    @Ignore
    public SearchResult(final String label, final String category, @NotNull final String uri,
                        final String course, final String searchKey) {
        this.category = category;
        this.label = label;
        this.uri = uri;
        this.course = course;
        this.searchKey = searchKey;
        this.hasRead = false;
        this.hasStar = false;
        this.id = 0;
    }

    public SearchResult(final SearchResult result) {
        this.uri = result.uri;
        this.label = result.label;
        this.category = result.category;
        this.hasRead = result.hasRead;
        this.hasStar = result.hasStar;
        this.course = result.course;
        this.searchKey = result.searchKey;
        this.id = result.id;
    }

    @NotNull
    public String getUri() {
        return uri;
    }

    public void setUri(@NotNull String uri) {
        this.uri = uri;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isHasRead() {
        return hasRead;
    }

    public void setHasRead(boolean hasRead) {
        this.hasRead = hasRead;
    }

    public boolean isHasStar() {
        return hasStar;
    }

    public void setHasStar(boolean hasStar) {
        this.hasStar = hasStar;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
