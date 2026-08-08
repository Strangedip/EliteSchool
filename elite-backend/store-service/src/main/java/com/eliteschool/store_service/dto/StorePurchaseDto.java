package com.eliteschool.store_service.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StorePurchaseDto {
    private UUID id;
    private UUID studentId;
    private String studentName;
    private UUID itemId;
    private String itemName;
    private String acquisitionType;
    private String itemCategory;
    private LocalDateTime claimedAt;
}
