package com.eliteschool.task_service.model;

import com.eliteschool.task_service.model.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.UUID;
import java.time.LocalDateTime;

@Entity
@Table(name = "task_submissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID taskId;

    @Column(nullable = false)
    private UUID studentId;

    @Column(columnDefinition = "TEXT")
    private String submissionDetails;

    @Column(columnDefinition = "TEXT")
    private String evidence; // URL or description of submission evidence

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status; // SUBMITTED, COMPLETED, or REJECTED

    @Column(columnDefinition = "TEXT")
    private String feedbackNotes; // Notes from verifier

    private UUID verifiedBy; // Faculty/Management who verified the submission

    @CreationTimestamp
    private LocalDateTime submittedAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private LocalDateTime verifiedAt;
} 