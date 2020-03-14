package com.quiz.darkhold.login.service;

import com.quiz.darkhold.login.entity.User;
import com.quiz.darkhold.login.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(final String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        logger.info("current user ->" + user);

        Set<GrantedAuthority> grantedAuthorities =
                user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName()))
                        .collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPassword(), grantedAuthorities);
    }
}
