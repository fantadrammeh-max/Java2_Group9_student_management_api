package com.utg.studentmanagement.dto;

import com.utg.studentmanagement.model.EnrollmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data  // Lombok: auto-generates getters, setters, toString
public class EnrollmentRequestDTO {

    @NotNull(message = "Student ID is required")
    private Long studentId;   // We only need the ID, not the whole Student object

    @NotNull(message = "Course ID is required")
    private Long courseId;    // Same for course

    private LocalDate enrollmentDate;  // Optional — defaults to today if not provided

    private EnrollmentStatus status;   // Optional — defaults to ACTIVE

    private String grade;              // Optional — can be added later
}