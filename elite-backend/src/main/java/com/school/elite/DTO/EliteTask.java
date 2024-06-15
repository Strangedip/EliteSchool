package com.school.elite.DTO;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tasks")
public class EliteTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskId;
    private String taskStatus;
    private String taskName;
    private String taskDetail;
    private Integer taskReward;
    private Timestamp createdOn;

    public EliteTask(String taskStatus, String taskName, String taskDetail, Integer taskReward, Timestamp createdOn) {
        this.taskStatus = taskStatus;
        this.taskName = taskName;
        this.taskDetail = taskDetail;
        this.taskReward = taskReward;
        this.createdOn = createdOn;
    }
}
