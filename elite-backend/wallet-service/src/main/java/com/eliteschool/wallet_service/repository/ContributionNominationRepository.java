package com.eliteschool.wallet_service.repository;

import com.eliteschool.wallet_service.model.ContributionNomination;
import com.eliteschool.wallet_service.model.enums.NominationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ContributionNominationRepository extends JpaRepository<ContributionNomination, UUID> {
    List<ContributionNomination> findByStatusOrderByCreatedAtDesc(NominationStatus status);

    List<ContributionNomination> findAllByOrderByCreatedAtDesc();

    List<ContributionNomination> findByNominatedByOrderByCreatedAtDesc(UUID nominatedBy);
}
