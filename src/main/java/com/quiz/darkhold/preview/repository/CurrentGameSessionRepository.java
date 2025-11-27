package com.quiz.darkhold.preview.repository;

import com.quiz.darkhold.preview.entity.CurrentGameSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CurrentGameSessionRepository extends JpaRepository<CurrentGameSession, Long> {
    Optional<CurrentGameSession> findByPin(String pin);
}
