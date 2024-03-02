package com.quiz.darkhold.user.service;

import com.quiz.darkhold.user.entity.Role;
import com.quiz.darkhold.user.entity.User;
import com.quiz.darkhold.user.exception.UserNotFoundException;
import com.quiz.darkhold.user.repository.RoleRepository;
import com.quiz.darkhold.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
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
    public User save(final User user) {
        if (isUpdatingUser(user)) {
            var existingUser = findExistingUser(user.getId());
            user.setPassword(getPassword(user, existingUser));
        } else {
            user.setPassword(encodePassword(user.getPassword()));
        }
        return userRepository.save(user);
    }

    private boolean isUpdatingUser(final User user) {
        return user.getId() != null;
    }

    private User findExistingUser(final Long id) {
        return userRepository.findById(id).get();
    }

    private String getPassword(final User newUser, final User existingUser) {
        return newUser.getPassword().isEmpty() ? existingUser.getPassword() : encodePassword(newUser.getPassword());
    }

    private String encodePassword(final String password) {
        return encoder.encode(password);
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
        return isCreatingNew(id) ? userByEmail == null : userByEmail.getId().equals(id);
    }

    private boolean isCreatingNew(final Long id) {
        return id == null;
    }

    @Override
    public User get(final Long id) throws UserNotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Could not find any user with the id " + id));
    }

    @Override
    public void delete(final Long id) throws UserNotFoundException {
        var countById = userRepository.countById(id);
        if (countById == null ||  countById == 0) {
            throw new UserNotFoundException("Could not find any user with the id " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public void updateUserEnabledStatus(final Long id, final boolean enabled) {
        userRepository.updateEnabledStatus(id, enabled);
    }
}
