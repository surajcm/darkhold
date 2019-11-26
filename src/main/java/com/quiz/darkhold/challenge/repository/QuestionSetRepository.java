package com.quiz.darkhold.challenge.repository;

import com.quiz.darkhold.challenge.entity.QuestionSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionSetRepository extends JpaRepository<QuestionSet, Long> {

}
