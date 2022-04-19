package com.wudaokou.backend.question.recommend;

import com.wudaokou.backend.question.Question;
import lombok.Data;

import java.util.List;

@Data
public class QuestionResponse {
    private List<Question> data;
    private String code;
    private String msg;
    private Integer index;
}
