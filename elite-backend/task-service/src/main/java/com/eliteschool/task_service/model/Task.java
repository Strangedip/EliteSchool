package com.eliteschool.task_service.model;

import com.eliteschool.task_service.model.enums.TaskType;
import com.eliteschool.task_service.model.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskType taskType; // SINGLE or MULTIPLE

    @Column(nullable = false)
    private int minLevel; // Minimum level required to take the task

    @Column(nullable = false)
    private int rewardPoints; // Points awarded on completion

    @Column(nullable = false)
    private UUID createdBy; // Faculty/Management who created the task

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status; // OPEN, COMPLETED, CLOSED

    private UUID completedBy; // Only used if taskType = SINGLE

    private LocalDateTime completedAt; // When task was completed (Nullable)

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
