package com.school.elite.repository.dbservice;

import com.school.elite.entity.User;
import com.school.elite.exception.UserExceptions;
import com.school.elite.repository.UserAccountRepo;
import com.school.elite.repository.TaskRepo;
import com.school.elite.repository.TaskReviewRequestRepo;
import com.school.elite.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class EliteDBService {

    // Repo section
    @Autowired
    UserRepo userRepo;

    @Autowired
    UserAccountRepo eliteAccountRepo;

    @Autowired
    TaskRepo taskRepo;

    @Autowired
    TaskReviewRequestRepo taskReviewRequestRepo;

    // Utility section
    @Autowired
    PasswordEncoder bcrypt;


    // User related methods
    public User saveUser(User user) {
        return userRepo.save(user);
    }

    public boolean isUserCredentialValid(String username, String password) {
        User user = userRepo.findByUsername(username).orElseThrow(
                () -> new UserExceptions.UserNotFoundException("User with username '" + username + "' not found"));
        if (!bcrypt.matches(password, user.getPassword())) {
            throw new UserExceptions.InvalidUserCredentialException("Invalid credentials. Please check and retry.");
        }
        return true;
    }

    // Task related methods


}
