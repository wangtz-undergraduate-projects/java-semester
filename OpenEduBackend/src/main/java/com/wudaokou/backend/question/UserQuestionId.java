package com.wudaokou.backend.question;

import com.wudaokou.backend.login.Customer;
import lombok.Getter;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Embeddable
public class UserQuestionId implements Serializable {
    @ManyToOne
    private Customer customer;

    @ManyToOne
    private Question question;

    public UserQuestionId() {}

    public UserQuestionId(Customer customer, Question question) {
        this.customer = customer;
        this.question = question;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserQuestionId that = (UserQuestionId) o;
        return Objects.equals(customer, that.customer) && Objects.equals(question, that.question);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customer, question);
    }
}
