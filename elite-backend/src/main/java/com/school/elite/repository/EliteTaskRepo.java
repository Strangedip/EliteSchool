package com.school.elite.repository;

import com.school.elite.entity.EliteTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EliteTaskRepo extends JpaRepository<EliteTask,Long> {
}
