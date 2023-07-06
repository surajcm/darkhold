package com.quiz.darkhold.login.service;

import com.quiz.darkhold.login.entity.User;
import com.quiz.darkhold.login.repository.RoleRepository;
import com.quiz.darkhold.login.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder encoder;

    public UserServiceImpl(final UserRepository userRepository,
                           final RoleRepository roleRepository,
                           final BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
    }

    @Override
    public void save(final User user) {
        var pass = encoder.encode(user.getPassword());
        logger.info("User first name is " + user.getFirstName());
        logger.info("User last name is " + user.getLastName());
        logger.info("password is " + pass);
        user.setPassword(pass);
        user.setEnabled(false);
        //todo : this need to be corrected
        //user.setRoles(new HashSet<>(roleRepository.findAll()));
        userRepository.save(user);
    }

    @Override
    public User findByUsername(final String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> listAll() {
        return userRepository.findAll();
    }
}
