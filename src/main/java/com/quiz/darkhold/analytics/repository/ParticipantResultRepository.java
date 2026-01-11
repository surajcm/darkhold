package com.quiz.darkhold.analytics.repository;

import com.quiz.darkhold.analytics.entity.ParticipantResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for accessing ParticipantResult entities.
 */
@Repository
public interface ParticipantResultRepository extends JpaRepository<ParticipantResult, Long> {

    /**
     * Find all results for a specific participant across all games.
     *
     * @param username the participant username
     * @return list of participant results
     */
    List<ParticipantResult> findByUsername(String username);

    /**
     * Find top performers across all games (by average score).
     *
     * @param limit max number of results
     * @return list of top participants
     */
    @Query("SELECT pr FROM ParticipantResult pr ORDER BY pr.finalScore DESC")
    List<ParticipantResult> findTopPerformers(@Param("limit") int limit);
}
