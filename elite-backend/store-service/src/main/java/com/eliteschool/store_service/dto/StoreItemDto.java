package com.eliteschool.store_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
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
    @Min(value = 1, message = "Price must be at least 1 point")
    private Integer price;
    
    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;
    
    private String description;
    private String imageUrl;
} 