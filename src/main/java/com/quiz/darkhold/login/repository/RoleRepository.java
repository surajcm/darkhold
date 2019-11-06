package com.quiz.darkhold.login.repository;

import com.quiz.darkhold.login.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
