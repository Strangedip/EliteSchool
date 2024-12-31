package com.school.elite.repository.dbservice;

import com.school.elite.entity.User;
import com.school.elite.repository.UserAccountRepo;
import com.school.elite.repository.TaskRepo;
import com.school.elite.repository.TaskReviewRequestRepo;
import com.school.elite.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EliteDBService {
    @Autowired
    UserRepo userRepo;
    @Autowired
    UserAccountRepo eliteAccountRepo;
    @Autowired
    TaskRepo taskRepo;
    @Autowired
    TaskReviewRequestRepo taskReviewRequestRepo;

    public User saveUser(User user){
        return userRepo.save(user);
    }


}
