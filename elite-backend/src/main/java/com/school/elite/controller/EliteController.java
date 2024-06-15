package com.school.elite.controller;

import com.school.elite.DTO.EliteTask;
import com.school.elite.DTO.EliteUser;
import com.school.elite.repository.EliteTaskRepo;
import com.school.elite.repository.EliteUserRepo;
import com.school.elite.utils.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

@RestController
public class EliteController {
    @Autowired
    EliteUserRepo userRepo;
    @Autowired
    EliteTaskRepo eliteTaskRepo;

    @GetMapping(path = "/status")
    public String status() {
        return "Running";
    }

    @GetMapping(path = "/create")
    public EliteTask create() {
        EliteUser user = new EliteUser(UUID.randomUUID().toString(),"name",21,"male","email","mobile","admin","username1","pass");
        userRepo.save(user);
        EliteTask task = new EliteTask("id", "name", "detail", 15, Timestamp.valueOf(TimeUtil.getCurrentTimeOfIndia()));
        return eliteTaskRepo.save(task);
    }
}
