package com.school.elite.repository;

import com.school.elite.entity.EliteUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EliteUserRepo extends JpaRepository<EliteUser,String> {
}
