package com.eliteschool.task_service.controller;

import com.eliteschool.common_utils.dto.CommonResponseDto;
import com.eliteschool.common_utils.security.GatewayAuth;
import com.eliteschool.common_utils.util.ResponseUtil;
import com.eliteschool.task_service.dto.TaskTemplateDto;
import com.eliteschool.task_service.service.TaskTemplateService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/task-templates")
@RequiredArgsConstructor
public class TaskTemplateController {

    private final TaskTemplateService taskTemplateService;

    @GetMapping
    public ResponseEntity<CommonResponseDto<List<TaskTemplateDto>>> getAllTemplates(HttpServletRequest request) {
        GatewayAuth.requireRoles(request, "FACULTY", "ADMIN", "MANAGEMENT");
        return ResponseUtil.success("Task templates retrieved successfully", taskTemplateService.getAllTemplates());
    }

    @GetMapping("/{templateId}")
    public ResponseEntity<CommonResponseDto<TaskTemplateDto>> getTemplateById(@PathVariable UUID templateId,
                                                                              HttpServletRequest request) {
        GatewayAuth.requireRoles(request, "FACULTY", "ADMIN", "MANAGEMENT");
        return taskTemplateService.getTemplateById(templateId)
                .map(template -> ResponseUtil.success("Task template retrieved successfully", template))
                .orElseGet(() -> ResponseUtil.error(HttpStatus.NOT_FOUND, "TEMPLATE_NOT_FOUND",
                        "Task template with ID " + templateId + " not found", null));
    }

    @PostMapping
    public ResponseEntity<CommonResponseDto<TaskTemplateDto>> createTemplate(
            @Valid @RequestBody TaskTemplateDto templateDto,
            HttpServletRequest request) {
        GatewayAuth.requireRoles(request, "FACULTY", "ADMIN", "MANAGEMENT");
        UUID creatorId = GatewayAuth.requireUserId(request);
        templateDto.setCreatedBy(creatorId);
        return ResponseUtil.success("Task template created successfully",
                taskTemplateService.createTemplate(templateDto));
    }

    @PutMapping("/{templateId}")
    public ResponseEntity<CommonResponseDto<TaskTemplateDto>> updateTemplate(
            @PathVariable UUID templateId,
            @Valid @RequestBody TaskTemplateDto templateDto,
            HttpServletRequest request) {
        GatewayAuth.requireRoles(request, "FACULTY", "ADMIN", "MANAGEMENT");
        return taskTemplateService.updateTemplate(templateId, templateDto)
                .map(template -> ResponseUtil.success("Task template updated successfully", template))
                .orElseGet(() -> ResponseUtil.error(HttpStatus.NOT_FOUND, "TEMPLATE_NOT_FOUND",
                        "Task template with ID " + templateId + " not found", null));
    }

    @DeleteMapping("/{templateId}")
    public ResponseEntity<CommonResponseDto<Void>> deleteTemplate(@PathVariable UUID templateId,
                                                                  HttpServletRequest request) {
        GatewayAuth.requireRoles(request, "FACULTY", "ADMIN", "MANAGEMENT");
        if (taskTemplateService.deleteTemplate(templateId)) {
            return ResponseUtil.success("Task template deleted successfully", null);
        }
        return ResponseUtil.error(HttpStatus.NOT_FOUND, "TEMPLATE_NOT_FOUND",
                "Task template with ID " + templateId + " not found", null);
    }
}
