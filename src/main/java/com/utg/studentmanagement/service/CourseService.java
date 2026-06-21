package com.utg.studentmanagement.service;

import com.utg.studentmanagement.exception.ResourceNotFoundException;
import com.utg.studentmanagement.model.Course;
import com.utg.studentmanagement.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;


    public Course createCourse(Course course) {
        // Business rule: course codes must be unique (e.g. no two "CS201" courses)
        if (courseRepository.existsByCourseCode(course.getCourseCode())) {
            throw new IllegalArgumentException(
                    "A course with code '" + course.getCourseCode() + "' already exists"
            );
        }
        return courseRepository.save(course);
    }


    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Course not found with ID: " + id
                ));
    }


    public Course updateCourse(Long id, Course updated) {
        Course existing = getCourseById(id);

        // Check if the new course code is already used by a DIFFERENT course
        if (!existing.getCourseCode().equals(updated.getCourseCode()) &&
                courseRepository.existsByCourseCode(updated.getCourseCode())) {
            throw new IllegalArgumentException(
                    "Course code '" + updated.getCourseCode() + "' is already in use"
            );
        }

        // pdate only the allowed fields
        existing.setCourseName(updated.getCourseName());
        existing.setCourseCode(updated.getCourseCode());
        existing.setDescription(updated.getDescription());
        existing.setCredits(updated.getCredits());
        existing.setInstructor(updated.getInstructor());

        return courseRepository.save(existing);
    }


    public void deleteCourse(Long id) {
        getCourseById(id);
        courseRepository.deleteById(id);
    }


    public List<Course> searchCourses(String name) {
        return courseRepository.findByCourseNameContainingIgnoreCase(name);
    }
}