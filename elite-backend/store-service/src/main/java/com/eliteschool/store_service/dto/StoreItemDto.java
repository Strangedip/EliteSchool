package com.eliteschool.store_service.dto;

import com.eliteschool.store_service.model.enums.AcquisitionType;
import com.eliteschool.store_service.model.enums.ClaimWindowStatus;
import com.eliteschool.store_service.model.enums.ItemCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreItemDto {
    private UUID id;

    @NotBlank(message = "Item name cannot be empty")
    private String name;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price cannot be negative")
    private Integer price;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;

    private String description;
    private String imageUrl;

    private String opportunityBrief;
    private String intendedAudience;

    @Builder.Default
    private List<String> eligibilityChecklist = new ArrayList<>();

    @Builder.Default
    private AcquisitionType acquisitionType = AcquisitionType.POINTS;

    @Builder.Default
    private ItemCategory itemCategory = ItemCategory.MATERIAL;

    private LocalDateTime claimOpensAt;
    private LocalDateTime claimClosesAt;

    @Builder.Default
    private List<UUID> requiredTaskIds = new ArrayList<>();

    private List<RequiredTaskProgressDto> requiredTasks;
    private Boolean eligible;
    private Boolean withinClaimWindow;
    private ClaimWindowStatus windowStatus;
    private List<ClaimInfoDto> claims;
}
