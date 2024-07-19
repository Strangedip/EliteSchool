package com.school.elite.repository.dbservice;

import com.school.elite.entity.EliteUser;
import com.school.elite.repository.EliteAccountRepo;
import com.school.elite.repository.EliteTaskRepo;
import com.school.elite.repository.EliteTaskReviewRequestRepo;
import com.school.elite.repository.EliteUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EliteDBService {
    @Autowired
    EliteUserRepo eliteUserRepo;
    @Autowired
    EliteAccountRepo eliteAccountRepo;
    @Autowired
    EliteTaskRepo eliteTaskRepo;
    @Autowired
    EliteTaskReviewRequestRepo eliteTaskReviewRequestRepo;

    public EliteUser saveUser(EliteUser user){
        return eliteUserRepo.save(user);
    }


}
