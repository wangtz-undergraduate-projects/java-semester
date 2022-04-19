package com.wudaokou.easylearn.retrofit;

public class Answer {
    String all;
    String fsanswer;
    String subject;
    String  message;
    String tamplateContent;
    Integer fs;
    String filterStr;
    String subjectUri;
    String predicate;

    public String getAll() {
        return all;
    }

    public String getFsanswer() {
        return fsanswer;
    }

    public String getSubject() {
        return subject;
    }

    public String getMessage() {
        return message;
    }

    public String getTamplateContent() {
        return tamplateContent;
    }

    public Integer getFs() {
        return fs;
    }

    public String getFilterStr() {
        return filterStr;
    }

    public String getSubjectUri() {
        return subjectUri;
    }

    public String getPredicate() {
        return predicate;
    }

    public Boolean getAnswerflag() {
        return answerflag;
    }

    public String getAttention() {
        return attention;
    }

    public String getFsscore() {
        return fsscore;
    }

    Double score;
    Boolean answerflag;
    String attention;
    String fsscore;
    String value;

    public Answer(String all, String subject, String fsanswer, String message, String tamplateContent, Integer fs, String filterStr,
                  String subjectUri, String predicate, Double score, Boolean answerflag, String attention, String fsscore, String value){
        this.all = all;
        this.subject = subject;
        this.fsanswer = fsanswer;
        this.message = message;
        this.tamplateContent = tamplateContent;
        this.fs = fs;
        this.filterStr = filterStr;
        this.subjectUri = subjectUri;
        this.predicate = predicate;
        this.score = score;
        this.answerflag = answerflag;
        this.attention = attention;
        this.fsscore = fsscore;
        this.value = value;
    }

    public Double getScore(){
        return score;
    }

    public String getValue(){
        return value;
    }
}
