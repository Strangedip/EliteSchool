package com.eliteschool.task_service.model;

import com.eliteschool.task_service.model.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.ArrayList;
import java.util.List;
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
    private String evidence;

    @ElementCollection
    @CollectionTable(name = "task_submission_rubric_checks", joinColumns = @JoinColumn(name = "submission_id"))
    @Column(name = "rubric_item", length = 500)
    @OrderColumn(name = "item_order")
    @Builder.Default
    private List<String> rubricChecked = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    @Column(columnDefinition = "TEXT")
    private String feedbackNotes;

    private UUID verifiedBy;

    @CreationTimestamp
    private LocalDateTime submittedAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private LocalDateTime verifiedAt;

    @Column(nullable = false)
    @Builder.Default
    private boolean pointsAwarded = false;
}
