package com.school.elite.repository.dbservice;

import com.school.elite.entity.User;
import com.school.elite.exception.UserExceptions;
import com.school.elite.repository.UserAccountRepository;
import com.school.elite.repository.TaskRepository;
import com.school.elite.repository.TaskReviewRequestRepository;
import com.school.elite.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EliteDBService {

    // Repo section
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserAccountRepository eliteAccountRepo;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    TaskReviewRequestRepository taskReviewRequestRepository;

    //todo add db methods if needed
}
