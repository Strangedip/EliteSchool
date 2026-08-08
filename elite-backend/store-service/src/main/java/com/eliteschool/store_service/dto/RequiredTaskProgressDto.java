package com.eliteschool.store_service.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequiredTaskProgressDto {
    private UUID taskId;
    private String title;
    private boolean completed;
}
