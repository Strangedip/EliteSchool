package com.eliteschool.store_service.dto;

import com.eliteschool.store_service.model.StoreItem;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class to convert between StoreItem entity and StoreItemDto.
 */
public class StoreItemMapper {

    /**
     * Convert from StoreItem entity to StoreItemDto.
     * @param storeItem The StoreItem entity.
     * @return StoreItemDto.
     */
    public static StoreItemDto toDto(StoreItem storeItem) {
        if (storeItem == null) {
            return null;
        }
        
        return StoreItemDto.builder()
                .id(storeItem.getId())
                .name(storeItem.getName())
                .price(storeItem.getPrice())
                .stock(storeItem.getStock())
                .description(storeItem.getDescription())
                .imageUrl(storeItem.getImageUrl())
                .build();
    }

    /**
     * Convert from StoreItemDto to StoreItem entity.
     * @param dto The StoreItemDto.
     * @return StoreItem entity.
     */
    public static StoreItem toEntity(StoreItemDto dto) {
        if (dto == null) {
            return null;
        }
        
        return StoreItem.builder()
                .id(dto.getId())
                .name(dto.getName())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .description(dto.getDescription())
                .imageUrl(dto.getImageUrl())
                .build();
    }

    /**
     * Convert a list of StoreItem entities to a list of StoreItemDtos.
     * @param items List of StoreItem entities.
     * @return List of StoreItemDtos.
     */
    public static List<StoreItemDto> toDtoList(List<StoreItem> items) {
        if (items == null) {
            return List.of();
        }
        
        return items.stream()
                .map(StoreItemMapper::toDto)
                .collect(Collectors.toList());
    }
} 