package com.quiz.darkhold.team.repository;

import com.quiz.darkhold.team.entity.TeamResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamResultRepository extends JpaRepository<TeamResult, Long> {

    List<TeamResult> findByGameResultId(Long gameResultId);

    List<TeamResult> findByGameResultIdOrderByFinalRankAsc(Long gameResultId);
}
