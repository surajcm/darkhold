package com.quiz.darkhold.user.service;

import com.quiz.darkhold.user.entity.Role;
import com.quiz.darkhold.user.entity.User;
import com.quiz.darkhold.user.exception.UserNotFoundException;
import com.quiz.darkhold.user.repository.RoleRepository;
import com.quiz.darkhold.user.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
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
        var isUpdating = (user.getId() != null);
        if (isUpdating) {
            var existingUser = userRepository.findById(user.getId()).get();
            if (user.getPassword().isEmpty()) {
                user.setPassword(existingUser.getPassword());
            } else {
                var pass = encoder.encode(user.getPassword());
                user.setPassword(pass);
            }
        } else {
            var pass = encoder.encode(user.getPassword());
            user.setPassword(pass);
        }
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

    @Override
    public List<Role> listRoles() {
        return (List<Role>) roleRepository.findAll();
    }

    @Override
    public Boolean isEmailUnique(final Long id, final String email) {
        var userByEmail = userRepository.findByEmail(email);
        if (userByEmail == null) {
            return true;
        }
        var isCreatingNew = (id == null);
        if (isCreatingNew) {
            if (userByEmail != null) {
                return false;
            }
        } else {
            if (userByEmail.getId().equals(id)) {
                return false;
            }
        }
        return userByEmail == null;
    }

    @Override
    public User get(final Long id) throws UserNotFoundException {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Could not find any user with the id " + id));
    }
}
