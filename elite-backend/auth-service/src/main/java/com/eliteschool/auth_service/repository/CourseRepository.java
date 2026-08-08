package com.eliteschool.auth_service.repository;

import com.eliteschool.auth_service.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, UUID> {
}
