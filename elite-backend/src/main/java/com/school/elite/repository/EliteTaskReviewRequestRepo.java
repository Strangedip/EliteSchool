package com.school.elite.repository;

import com.school.elite.DTO.EliteTaskReviewRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EliteTaskReviewRequestRepo extends JpaRepository<EliteTaskReviewRequest,Long> {
}
