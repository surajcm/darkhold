package com.quiz.darkhold.analytics.repository;

import com.quiz.darkhold.analytics.entity.QuestionResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for accessing QuestionResult entities.
 */
@Repository
public interface QuestionResultRepository extends JpaRepository<QuestionResult, Long> {
    // Additional query methods can be added here as needed
}
