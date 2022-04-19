package com.wudaokou.easylearn.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface QuestionDAO {
    @Insert
    void insertQuestion(Question question);

    @Query("DELETE FROM question")
    public void deleteAllQuestion();

    @Delete
    public void deleteQuestion(Question question);

    @Update
    public void updateQuestion(Question question);

    @Query("SELECT * FROM question")
    public List<Question> loadAllQuestion();

    @Query("SELECT * FROM question WHERE course = :course AND label = :label")
    public List<Question> loadQuestionByCourseAndLabel(String course, String label);

    @Query("SELECT * FROM question WHERE id = :id")
    public Question loadQuestionById(int id);
}
