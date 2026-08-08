package com.eliteschool.wallet_service.client;

import com.eliteschool.common_utils.dto.CommonResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "task-service", configuration = InternalFeignConfig.class)
public interface TaskServiceClient {

    @PostMapping("/api/task-submissions/completion-check")
    ResponseEntity<CommonResponseDto<CompletionCheckResponse>> checkCompletion(
            @RequestBody CompletionCheckRequest request);

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    class CompletionCheckRequest {
        private UUID studentId;
        private List<UUID> taskIds;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    class CompletionCheckResponse {
        private boolean allCompleted;
    }
}
