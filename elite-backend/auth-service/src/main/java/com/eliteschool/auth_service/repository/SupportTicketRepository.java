package com.eliteschool.auth_service.repository;

import com.eliteschool.auth_service.model.SupportTicket;
import com.eliteschool.auth_service.model.enums.SupportTicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SupportTicketRepository extends JpaRepository<SupportTicket, UUID> {

    List<SupportTicket> findByStudentIdOrderByCreatedAtDesc(UUID studentId);

    List<SupportTicket> findAllByOrderByCreatedAtDesc();

    List<SupportTicket> findByStatusOrderByCreatedAtDesc(SupportTicketStatus status);
}
