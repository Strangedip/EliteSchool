package com.eliteschool.store_service.dto;

import com.eliteschool.store_service.model.StoreItem;
import com.eliteschool.store_service.model.enums.AcquisitionType;
import com.eliteschool.store_service.model.enums.ItemCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StoreItemMapper {

    public static StoreItemDto toDto(StoreItem storeItem) {
        if (storeItem == null) {
            return null;
        }

        AcquisitionType type = storeItem.getAcquisitionType() != null
                ? storeItem.getAcquisitionType()
                : AcquisitionType.POINTS;
        ItemCategory category = storeItem.getItemCategory() != null
                ? storeItem.getItemCategory()
                : ItemCategory.MATERIAL;

        List<java.util.UUID> taskIds = storeItem.getRequiredTaskIds() != null
                ? new ArrayList<>(storeItem.getRequiredTaskIds())
                : new ArrayList<>();

        return StoreItemDto.builder()
                .id(storeItem.getId())
                .name(storeItem.getName())
                .price(storeItem.getPrice())
                .stock(storeItem.getStock())
                .description(storeItem.getDescription())
                .imageUrl(storeItem.getImageUrl())
                .opportunityBrief(storeItem.getOpportunityBrief())
                .intendedAudience(storeItem.getIntendedAudience())
                .eligibilityChecklist(storeItem.getEligibilityChecklist() != null
                        ? new ArrayList<>(storeItem.getEligibilityChecklist())
                        : new ArrayList<>())
                .acquisitionType(type)
                .itemCategory(category)
                .claimOpensAt(storeItem.getClaimOpensAt())
                .claimClosesAt(storeItem.getClaimClosesAt())
                .requiredTaskIds(taskIds)
                .build();
    }

    public static StoreItem toEntity(StoreItemDto dto) {
        if (dto == null) {
            return null;
        }

        AcquisitionType type = dto.getAcquisitionType() != null
                ? dto.getAcquisitionType()
                : AcquisitionType.POINTS;
        ItemCategory category = dto.getItemCategory() != null
                ? dto.getItemCategory()
                : ItemCategory.MATERIAL;

        List<java.util.UUID> taskIds = dto.getRequiredTaskIds() != null
                ? new ArrayList<>(dto.getRequiredTaskIds())
                : new ArrayList<>();

        return StoreItem.builder()
                .id(dto.getId())
                .name(dto.getName())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .description(dto.getDescription())
                .imageUrl(dto.getImageUrl())
                .opportunityBrief(dto.getOpportunityBrief())
                .intendedAudience(dto.getIntendedAudience())
                .eligibilityChecklist(dto.getEligibilityChecklist() != null
                        ? new ArrayList<>(dto.getEligibilityChecklist())
                        : new ArrayList<>())
                .acquisitionType(type)
                .itemCategory(category)
                .claimOpensAt(dto.getClaimOpensAt())
                .claimClosesAt(dto.getClaimClosesAt())
                .requiredTaskIds(taskIds)
                .build();
    }

    public static List<StoreItemDto> toDtoList(List<StoreItem> items) {
        if (items == null) {
            return List.of();
        }

        return items.stream()
                .map(StoreItemMapper::toDto)
                .collect(Collectors.toList());
    }
}
