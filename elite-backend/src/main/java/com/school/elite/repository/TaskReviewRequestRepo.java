package com.school.elite.repository;

import com.school.elite.entity.TaskReviewRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskReviewRequestRepo extends JpaRepository<TaskReviewRequest,Long> {
}
