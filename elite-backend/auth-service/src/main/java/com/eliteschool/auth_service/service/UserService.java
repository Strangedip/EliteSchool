package com.eliteschool.auth_service.service;

import com.eliteschool.auth_service.model.User;
import com.eliteschool.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Finds a user by email.
     * @param email The email of the user.
     * @return Optional<User>
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Finds a user by username.
     * @param username The username of the user.
     * @return Optional<User>
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Checks if an email is already registered.
     * @param email The email to check.
     * @return boolean
     */
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Checks if a username is already taken.
     * @param username The username to check.
     * @return boolean
     */
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Saves a new user.
     * @param user The user entity to save.
     * @return User (saved entity)
     */
    public User saveUser(User user) {
        return userRepository.save(user);
    }
}
