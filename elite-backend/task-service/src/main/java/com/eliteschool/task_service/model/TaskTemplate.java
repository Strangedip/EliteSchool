package com.eliteschool.task_service.model;

import com.eliteschool.task_service.model.enums.TaskType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "task_templates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskTemplate {

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
    @Builder.Default
    private boolean evidenceRequired = true;

    @Column(nullable = false)
    @Builder.Default
    private int minNotesLength = 40;

    @ElementCollection
    @CollectionTable(name = "task_template_rubric_checklist", joinColumns = @JoinColumn(name = "template_id"))
    @Column(name = "rubric_item", length = 500)
    @OrderColumn(name = "item_order")
    @Builder.Default
    private java.util.List<String> rubricChecklist = new java.util.ArrayList<>();

    @Column(nullable = false)
    private UUID createdBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
