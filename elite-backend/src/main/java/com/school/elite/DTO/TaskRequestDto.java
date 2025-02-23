package com.school.elite.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskRequestDto {
    private String status;
    private String name;
    private String detail;
    private Integer reward;
    private Timestamp createdOn;
    private String createdBy;

}
