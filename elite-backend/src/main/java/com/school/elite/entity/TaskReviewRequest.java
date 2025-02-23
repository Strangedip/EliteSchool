package com.school.elite.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "task_review_requests")
public class TaskReviewRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;
    private Long taskId;
    private String eliteId;
    private String requestStatus;
    private LocalDateTime createdOn;
    private LocalDateTime actionTakenOn;

    public TaskReviewRequest(Long taskId, String eliteId, String requestStatus, LocalDateTime createdOn, LocalDateTime actionTakenOn) {
        this.taskId = taskId;
        this.eliteId = eliteId;
        this.requestStatus = requestStatus;
        this.createdOn = createdOn;
        this.actionTakenOn = actionTakenOn;
    }
}
