package com.quiz.darkhold.login.service;

import com.quiz.darkhold.login.entity.DarkholdUserDetails;
import com.quiz.darkhold.login.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final Logger logger = LogManager.getLogger(UserDetailsServiceImpl.class);

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(final String username) {
        logger.info("requested user name is {}", username);
        var user = userRepository.findByEmail(username);
        if (user == null) {
            logger.error("Username not found !!");
            throw new UsernameNotFoundException(username);
        }
        //logger.info("current user -> {}", user);
        return new DarkholdUserDetails(user);
    }
}
