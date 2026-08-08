package com.eliteschool.store_service.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimInfoDto {
    private UUID studentId;
    private String studentName;
    private LocalDateTime claimedAt;
}
