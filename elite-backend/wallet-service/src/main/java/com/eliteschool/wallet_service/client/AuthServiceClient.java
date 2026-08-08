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

import java.util.UUID;

@FeignClient(name = "auth-service", configuration = InternalFeignConfig.class)
public interface AuthServiceClient {

    @GetMapping("/api/user/{id}")
    ResponseEntity<CommonResponseDto<UserNameView>> getUser(@PathVariable("id") UUID id);

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    class UserNameView {
        private UUID eliteId;
        private String name;
        private String username;
    }
}
