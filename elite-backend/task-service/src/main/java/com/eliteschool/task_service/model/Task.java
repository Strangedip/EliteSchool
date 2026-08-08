package com.eliteschool.task_service.model;

import com.eliteschool.task_service.model.enums.TaskType;
import com.eliteschool.task_service.model.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
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
    private TaskType taskType;

    @Column(nullable = false)
    private int minLevel;

    @Column(nullable = false)
    private int rewardPoints;

    @Column(nullable = false)
    private UUID createdBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    private UUID completedBy;

    private LocalDateTime completedAt;

    @Column(nullable = false)
    @Builder.Default
    private boolean evidenceRequired = true;

    @Column(nullable = false)
    @Builder.Default
    private int minNotesLength = 40;

    @ElementCollection
    @CollectionTable(name = "task_rubric_checklist", joinColumns = @JoinColumn(name = "task_id"))
    @Column(name = "rubric_item", length = 500)
    @OrderColumn(name = "item_order")
    @Builder.Default
    private List<String> rubricChecklist = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
