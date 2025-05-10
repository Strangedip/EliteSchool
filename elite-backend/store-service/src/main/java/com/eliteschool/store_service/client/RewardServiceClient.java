package com.eliteschool.store_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

/**
 * Client interface for communicating with the Reward Service.
 * Uses Feign to create a declarative HTTP client.
 */
@FeignClient(name = "reward-service")
public interface RewardServiceClient {

    /**
     * Earn reward points for a student.
     * 
     * @param studentId The student's ID.
     * @param points The points to add.
     * @param description The reason for earning points.
     * @return ResponseEntity with success message.
     */
    @PostMapping("/api/rewards/earn/{studentId}")
    ResponseEntity<String> earnPoints(
            @PathVariable("studentId") UUID studentId,
            @RequestParam("points") int points,
            @RequestParam("description") String description);
} 