package com.eliteschool.auth_service.mapper;

import com.eliteschool.auth_service.dto.CourseDto;
import com.eliteschool.auth_service.model.Course;

import java.util.List;
import java.util.stream.Collectors;

public final class CourseMapper {

    private CourseMapper() {}

    public static CourseDto toDto(Course course) {
        if (course == null) {
            return null;
        }

        return CourseDto.builder()
                .id(course.getId())
                .courseCode(course.getCourseCode())
                .name(course.getName())
                .description(course.getDescription())
                .subject(course.getSubject())
                .grade(course.getGrade())
                .active(course.isActive())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .build();
    }

    public static Course toEntity(CourseDto dto) {
        if (dto == null) {
            return null;
        }

        return Course.builder()
                .courseCode(dto.getCourseCode())
                .name(dto.getName())
                .description(dto.getDescription())
                .subject(dto.getSubject())
                .grade(dto.getGrade())
                .isActive(dto.isActive())
                .build();
    }

    public static List<CourseDto> toDtoList(List<Course> courses) {
        if (courses == null) {
            return List.of();
        }

        return courses.stream()
                .map(CourseMapper::toDto)
                .collect(Collectors.toList());
    }
}
