package com.quiz.darkhold.challenge.repository;

import com.quiz.darkhold.challenge.entity.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    List<Challenge> findByChallengeOwner(Long challengeOwner);
}
