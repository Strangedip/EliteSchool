package com.eliteschool.reward_service.dto;

import com.eliteschool.reward_service.model.RedeemableItem;
import java.util.List;
import java.util.stream.Collectors;

public class RedeemableItemMapper {

    public static RedeemableItemDto toDto(RedeemableItem item) {
        if (item == null) {
            return null;
        }
        
        return RedeemableItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .price(item.getPrice())
                .stock(item.getStock())
                .build();
    }
    
    public static RedeemableItem toEntity(RedeemableItemDto dto) {
        if (dto == null) {
            return null;
        }
        
        return RedeemableItem.builder()
                .id(dto.getId())
                .name(dto.getName())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .build();
    }
    
    public static List<RedeemableItemDto> toDtoList(List<RedeemableItem> items) {
        if (items == null) {
            return List.of();
        }
        
        return items.stream()
                .map(RedeemableItemMapper::toDto)
                .collect(Collectors.toList());
    }
} 