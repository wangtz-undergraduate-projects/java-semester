package com.wudaokou.backend.question;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wudaokou.backend.history.Course;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Getter
@Setter
@Entity
public class Question {
    @NotNull
    @Id
    private Integer id;

    @JsonProperty("qAnswer")
    private String qAnswer;

    @JsonProperty("qBody")
    private String qBody;

    private String label;

    @Enumerated(EnumType.STRING)
    private Course course;

    public Question(Integer id, String qAnswer, String qBody, String label, Course course) {
        this.id = id;
        this.qAnswer = qAnswer;
        this.qBody = qBody;
        this.label = label;
        this.course = course;
    }

    public Question() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question question = (Question) o;
        return Objects.equals(id, question.id);
    }

//    @Override
//    public String toString() {
//        return "Question{" +
//                "id=" + id +
//                ", qAnswer='" + qAnswer + '\'' +
//                ", qBody='" + qBody + '\'' +
//                ", label='" + label + '\'' +
//                ", course=" + course +
//                '}';
//    }
}