package com.utg.studentmanagement.service;

import com.utg.studentmanagement.dto.EnrollmentRequestDTO;
import com.utg.studentmanagement.dto.EnrollmentResponseDTO;
import com.utg.studentmanagement.exception.ResourceNotFoundException;
import com.utg.studentmanagement.model.Course;
import com.utg.studentmanagement.model.Enrollment;
import com.utg.studentmanagement.model.EnrollmentStatus;
import com.utg.studentmanagement.model.Student;
import com.utg.studentmanagement.repository.CourseRepository;
import com.utg.studentmanagement.repository.EnrollmentRepository;
import com.utg.studentmanagement.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;


    public EnrollmentResponseDTO createEnrollment(EnrollmentRequestDTO dto) {

        // Step 1: find the student — throws 404 if not found
        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student not found with ID: " + dto.getStudentId()
                ));

        // Step 2: find the course — throws 404 if not found
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Course not found with ID: " + dto.getCourseId()
                ));

        // Step 3: business rule — a student cannot enroll in the same course twice
        if (enrollmentRepository.existsByStudentIdAndCourseId(
                dto.getStudentId(), dto.getCourseId())) {
            throw new IllegalArgumentException(
                    "Student is already enrolled in this course"
            );
        }

        // Step 4: build the Enrollment object
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);

        // Default enrollment date to today if not provided
        enrollment.setEnrollmentDate(
                dto.getEnrollmentDate() != null ? dto.getEnrollmentDate() : LocalDate.now()
        );

        // Default status to ACTIVE if not provided
        enrollment.setStatus(
                dto.getStatus() != null ? dto.getStatus() : EnrollmentStatus.ACTIVE
        );

        enrollment.setGrade(dto.getGrade());

        // Step 5: save and return as DTO
        Enrollment saved = enrollmentRepository.save(enrollment);
        return convertToDTO(saved);
    }

    public List<EnrollmentResponseDTO> getAllEnrollments() {
        return enrollmentRepository.findAll()
                .stream()                        // convert list to a stream
                .map(this::convertToDTO)         // convert each Enrollment to DTO
                .collect(Collectors.toList());   // collect back to a list
    }


    public EnrollmentResponseDTO getEnrollmentById(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Enrollment not found with ID: " + id
                ));
        return convertToDTO(enrollment);
    }


    public List<EnrollmentResponseDTO> getEnrollmentsByStudent(Long studentId) {
        // First confirm the student exists
        studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student not found with ID: " + studentId
                ));

        return enrollmentRepository.findByStudentId(studentId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    public List<EnrollmentResponseDTO> getEnrollmentsByCourse(Long courseId) {
        courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Course not found with ID: " + courseId
                ));

        return enrollmentRepository.findByCourseId(courseId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    public EnrollmentResponseDTO updateEnrollment(Long id, EnrollmentRequestDTO dto) {
        Enrollment existing = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Enrollment not found with ID: " + id
                ));

        // Only update status and grade — student and course cannot change
        if (dto.getStatus() != null) {
            existing.setStatus(dto.getStatus());
        }
        if (dto.getGrade() != null) {
            existing.setGrade(dto.getGrade());
        }

        Enrollment updated = enrollmentRepository.save(existing);
        return convertToDTO(updated);
    }

    public void deleteEnrollment(Long id) {
        enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Enrollment not found with ID: " + id
                ));
        enrollmentRepository.deleteById(id);
    }


    private EnrollmentResponseDTO convertToDTO(Enrollment enrollment) {
        EnrollmentResponseDTO dto = new EnrollmentResponseDTO();

        dto.setId(enrollment.getId());

        // Flatten student fields
        dto.setStudentId(enrollment.getStudent().getId());
        dto.setStudentFirstName(enrollment.getStudent().getFirstName());
        dto.setStudentLastName(enrollment.getStudent().getLastName());
        dto.setStudentEmail(enrollment.getStudent().getEmail());

        // Flatten course fields
        dto.setCourseId(enrollment.getCourse().getId());
        dto.setCourseName(enrollment.getCourse().getCourseName());
        dto.setCourseCode(enrollment.getCourse().getCourseCode());

        // Enrollment fields
        dto.setEnrollmentDate(enrollment.getEnrollmentDate());
        dto.setStatus(enrollment.getStatus());
        dto.setGrade(enrollment.getGrade());

        return dto;
    }
}