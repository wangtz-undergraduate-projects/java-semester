package com.wudaokou.backend.question;

public class StarredQuestionReturn extends Question{
    private Integer totalCount;
    private Integer wrongCount;

    public StarredQuestionReturn(Question q, Integer totalCount, Integer wrongCount) {
        super(q.getId(), q.getQAnswer(), q.getQBody(), q.getLabel(), q.getCourse());
        this.totalCount = totalCount;
        this.wrongCount = wrongCount;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getWrongCount() {
        return wrongCount;
    }

    public void setWrongCount(Integer wrongCount) {
        this.wrongCount = wrongCount;
    }
}
