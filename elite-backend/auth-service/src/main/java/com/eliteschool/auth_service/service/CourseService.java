package com.eliteschool.auth_service.service;

import com.eliteschool.auth_service.dto.CourseDto;
import com.eliteschool.auth_service.mapper.CourseMapper;
import com.eliteschool.auth_service.model.Course;
import com.eliteschool.auth_service.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {

    private final CourseRepository courseRepository;

    public List<CourseDto> getCourses(String subject, String grade, Boolean active) {
        return courseRepository.findAll().stream()
                .filter(c -> subject == null || c.getSubject().equalsIgnoreCase(subject))
                .filter(c -> grade == null || c.getGrade().equalsIgnoreCase(grade))
                .filter(c -> active == null || c.isActive() == active)
                .map(CourseMapper::toDto)
                .toList();
    }

    public Optional<CourseDto> getCourseById(UUID id) {
        return courseRepository.findById(id).map(CourseMapper::toDto);
    }

    public CourseDto createCourse(CourseDto dto) {
        Course course = CourseMapper.toEntity(dto);
        if (course.getCourseCode() == null || course.getCourseCode().isBlank()) {
            course.setCourseCode(generateCourseCode(course.getSubject(), course.getGrade()));
        }
        return CourseMapper.toDto(courseRepository.save(course));
    }

    @Transactional
    public Optional<CourseDto> updateCourse(UUID id, CourseDto dto) {
        return courseRepository.findById(id).map(existing -> {
            existing.setName(dto.getName());
            existing.setDescription(dto.getDescription());
            existing.setSubject(dto.getSubject());
            existing.setGrade(dto.getGrade());
            return CourseMapper.toDto(courseRepository.save(existing));
        });
    }

    @Transactional
    public Optional<CourseDto> setActive(UUID id, boolean active) {
        return courseRepository.findById(id).map(existing -> {
            existing.setActive(active);
            return CourseMapper.toDto(courseRepository.save(existing));
        });
    }

    public void deleteCourse(UUID id) {
        courseRepository.deleteById(id);
        log.info("Course with ID {} deleted successfully", id);
    }

    private String generateCourseCode(String subject, String grade) {
        String prefix = (subject.length() >= 3 ? subject.substring(0, 3) : subject).toUpperCase();
        String gradeDigits = grade.replaceAll("\\D", "");
        String suffix = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return prefix + (gradeDigits.isBlank() ? "" : "-" + gradeDigits) + "-" + suffix;
    }
}
