package com.utg.studentmanagement.controller;

import com.utg.studentmanagement.model.Course;
import com.utg.studentmanagement.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController              // Tells Spring: this class handles HTTP requests
@RequestMapping("/api/courses")  // All endpoints here start with /api/courses
@RequiredArgsConstructor     // Lombok: auto-creates constructor with all final fields
public class CourseController {

    private final CourseService courseService;

    // POST /api/courses — Create a new course
    @PostMapping
    public ResponseEntity<Course> createCourse(@Valid @RequestBody Course course) {
        Course created = courseService.createCourse(course);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // GET /api/courses — Get all courses (with optional search)
    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses(
            @RequestParam(required = false) String search) {
        if (search != null && !search.isEmpty()) {
            return ResponseEntity.ok(courseService.searchCourses(search));
        }
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    // GET /api/courses/1 — Get one course by ID
    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    // PUT /api/courses/1 — Update a course
    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody Course course) {
        return ResponseEntity.ok(courseService.updateCourse(id, course));
    }

    // DELETE /api/courses/1 — Delete a course
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}