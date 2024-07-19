package com.school.elite.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Data
@NoArgsConstructor
@Table(name = "task_review_requests")
public class EliteTaskReviewRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;
    private Long taskId;
    private String eliteId;
    private String requestStatus;
    private Timestamp createdOn;
    private Timestamp actionTakenOn;

    public EliteTaskReviewRequest(Long taskId, String eliteId, String requestStatus, Timestamp createdOn, Timestamp actionTakenOn) {
        this.taskId = taskId;
        this.eliteId = eliteId;
        this.requestStatus = requestStatus;
        this.createdOn = createdOn;
        this.actionTakenOn = actionTakenOn;
    }
}
