package com.school.elite.repository;

import com.school.elite.entity.EliteAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EliteAccountRepo extends JpaRepository<EliteAccount, String> {
}
