package com.eliteschool.task_service.model.enums;

public enum TaskStatus {
    OPEN,       // Task is available for students to complete
    SUBMITTED,  // Task is submitted by student and waiting for verification
    COMPLETED,  // Task is completed and verified
    REJECTED,   // Task submission was rejected
    CLOSED      // Task is no longer available
}