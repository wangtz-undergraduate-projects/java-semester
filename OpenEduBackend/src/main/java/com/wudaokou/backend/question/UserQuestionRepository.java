package com.wudaokou.backend.question;

import com.wudaokou.backend.history.Course;
import com.wudaokou.backend.login.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserQuestionRepository extends JpaRepository<UserQuestion, Integer> {
    Optional<UserQuestion> findByUserQuestionId(UserQuestionId userQuestionId);
    List<UserQuestion> findByUserQuestionId_CustomerAndUserQuestionId_Question_Course(Customer customer, Course course);
    List<UserQuestion> findByUserQuestionId_CustomerAndHasStar(Customer customer, boolean hasStar);
}