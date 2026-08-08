package com.eliteschool.task_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompletionCheckResponse {
    private boolean allCompleted;

    @Builder.Default
    private List<TaskProgress> tasks = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TaskProgress {
        private UUID taskId;
        private String title;
        private boolean completed;
    }
}
