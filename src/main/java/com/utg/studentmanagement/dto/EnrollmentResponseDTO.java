package com.utg.studentmanagement.dto;

import java.time.LocalDate;

import com.utg.studentmanagement.model.EnrollmentStatus;

import lombok.Data;

@Data
public class EnrollmentResponseDTO {

    private Long id;

    // Student info — flattened (no nested objects, just plain fields)
    private Long studentId;
    private String studentFirstName;
    private String studentLastName;
    private String studentEmail;

    // Course info — flattened
    private Long courseId;
    private String courseName;
    private String courseCode;

    // Enrollment details
    private LocalDate enrollmentDate;
    private EnrollmentStatus status;
    private String grade;
}