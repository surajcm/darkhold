package com.quiz.darkhold.user.repository;

import com.quiz.darkhold.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    Long countById(Long id);
}


