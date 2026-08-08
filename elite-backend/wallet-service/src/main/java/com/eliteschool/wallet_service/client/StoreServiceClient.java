package com.eliteschool.wallet_service.client;

import com.eliteschool.common_utils.dto.CommonResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@FeignClient(name = "store-service", configuration = InternalFeignConfig.class)
public interface StoreServiceClient {

    @PostMapping("/api/store/purchase/{itemId}")
    ResponseEntity<CommonResponseDto<Object>> purchaseItem(
            @PathVariable("itemId") UUID itemId,
            @RequestParam("studentId") UUID studentId,
            @RequestParam(value = "studentName", required = false) String studentName);

    @GetMapping("/api/store/items/{itemId}")
    ResponseEntity<CommonResponseDto<StoreItemView>> getItem(@PathVariable("itemId") UUID itemId);

    @GetMapping("/api/store/items/{itemId}/price")
    ResponseEntity<CommonResponseDto<Object>> getItemPrice(@PathVariable("itemId") UUID itemId);

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    class StoreItemView {
        private UUID id;
        private String name;
        private Integer price;
        private Integer stock;
        private String acquisitionType;
        private String itemCategory;
        private LocalDateTime claimOpensAt;
        private LocalDateTime claimClosesAt;
        private String windowStatus;
        private Boolean withinClaimWindow;
        @Builder.Default
        private List<UUID> requiredTaskIds = new ArrayList<>();
    }
}
