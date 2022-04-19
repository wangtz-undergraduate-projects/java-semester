package com.wudaokou.easylearn.retrofit;

public class HistoryParam {
    public String course;
    public String name;
    public String uri;
    public String category;
    public String searchKey;

    public HistoryParam(final String course, final String name) {
        this.course = course;
        this.name = name;
    }

    public HistoryParam(final String course, final String name, final String uri) {
        this.course = course;
        this.name = name;
        this.uri = uri;
    }

    public HistoryParam(final String course, final String name,
                        final String uri, final String category,
                        final String searchKey) {
        this.searchKey = searchKey;
        this.name = name;
        this.course = course;
        this.uri = uri;
        this.category = category;
    }
}
