package com.school.elite.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/app")
public class AdminController {

    @GetMapping(path = "/status")
    public String status() {
        return "Running";
    }
}
