package com.eliteschool.auth_service.controller;

import com.eliteschool.auth_service.dto.CourseDto;
import com.eliteschool.auth_service.service.CourseService;
import com.eliteschool.common_utils.dto.CommonResponseDto;
import com.eliteschool.common_utils.security.GatewayAuth;
import com.eliteschool.common_utils.util.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<CommonResponseDto<List<CourseDto>>> getCourses(
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) Boolean active,
            HttpServletRequest request) {
        GatewayAuth.requireRoles(request, "ADMIN", "MANAGEMENT", "FACULTY", "STUDENT");
        return ResponseUtil.success("Courses retrieved", courseService.getCourses(subject, grade, active));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponseDto<CourseDto>> getCourseById(@PathVariable UUID id,
                                                                      HttpServletRequest request) {
        GatewayAuth.requireRoles(request, "ADMIN", "MANAGEMENT", "FACULTY", "STUDENT");
        return courseService.getCourseById(id)
                .map(course -> ResponseUtil.success("Course retrieved", course))
                .orElseGet(() -> ResponseUtil.error(HttpStatus.NOT_FOUND, "COURSE_NOT_FOUND",
                        "Course not found: " + id, null));
    }

    @PostMapping
    public ResponseEntity<CommonResponseDto<CourseDto>> createCourse(@Valid @RequestBody CourseDto courseDto,
                                                                     HttpServletRequest request) {
        GatewayAuth.requireRoles(request, "ADMIN", "MANAGEMENT");
        return ResponseUtil.success("Course created", courseService.createCourse(courseDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommonResponseDto<CourseDto>> updateCourse(
            @PathVariable UUID id,
            @Valid @RequestBody CourseDto courseDto,
            HttpServletRequest request) {
        GatewayAuth.requireRoles(request, "ADMIN", "MANAGEMENT");
        return courseService.updateCourse(id, courseDto)
                .map(course -> ResponseUtil.success("Course updated", course))
                .orElseGet(() -> ResponseUtil.error(HttpStatus.NOT_FOUND, "COURSE_NOT_FOUND",
                        "Course not found: " + id, null));
    }

    @PutMapping("/{id}/active")
    public ResponseEntity<CommonResponseDto<CourseDto>> setActive(
            @PathVariable UUID id,
            @RequestParam boolean active,
            HttpServletRequest request) {
        GatewayAuth.requireRoles(request, "ADMIN", "MANAGEMENT");
        return courseService.setActive(id, active)
                .map(course -> ResponseUtil.success("Course status updated", course))
                .orElseGet(() -> ResponseUtil.error(HttpStatus.NOT_FOUND, "COURSE_NOT_FOUND",
                        "Course not found: " + id, null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponseDto<Void>> deleteCourse(@PathVariable UUID id,
                                                                HttpServletRequest request) {
        GatewayAuth.requireRoles(request, "ADMIN", "MANAGEMENT");
        courseService.deleteCourse(id);
        return ResponseUtil.success("Course deleted", null);
    }
}
