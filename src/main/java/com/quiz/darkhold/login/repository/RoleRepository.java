package com.quiz.darkhold.login.repository;

import com.quiz.darkhold.login.entity.Role;  
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {
}
