package com.utg.studentmanagement.service;

import com.utg.studentmanagement.exception.ResourceNotFoundException;
import com.utg.studentmanagement.model.Student;
import com.utg.studentmanagement.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {


    private final StudentRepository studentRepository;

    public Student createStudent(Student student) {
        // Business rule: no two students can have the same email
        if (studentRepository.existsByEmail(student.getEmail())) {
            throw new IllegalArgumentException(
                    "A student with email '" + student.getEmail() + "' already exists"
            );
        }
        // Save to database — JPA generates: INSERT INTO students (...) VALUES (...)
        return studentRepository.save(student);
    }

    public List<Student> getAllStudents() {
        // JPA generates: SELECT * FROM students
        return studentRepository.findAll();
    }


    public Student getStudentById(Long id) {
        // findById returns Optional<Student> — orElseThrow handles the empty case
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student not found with ID: " + id
                ));
    }


    public Student updateStudent(Long id, Student updated) {
        // Step 1: find the existing student (throws 404 if not found)
        Student existing = getStudentById(id);

        // Step 2: check if the new email is already used by a DIFFERENT student
        if (!existing.getEmail().equals(updated.getEmail()) &&
                studentRepository.existsByEmail(updated.getEmail())) {
            throw new IllegalArgumentException(
                    "Email '" + updated.getEmail() + "' is already in use"
            );
        }

        // Step 3: update the fields (we never update id or createdAt)
        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setEmail(updated.getEmail());
        existing.setPhone(updated.getPhone());
        existing.setAddress(updated.getAddress());

        // Step 4: save — JPA generates: UPDATE students SET ... WHERE id = ?
        return studentRepository.save(existing);
    }


    public void deleteStudent(Long id) {
        // Confirm the student exists first (throws 404 if not found)
        getStudentById(id);
        // JPA generates: DELETE FROM students WHERE id = ?
        studentRepository.deleteById(id);
    }


    public List<Student> searchStudents(String name) {
        return studentRepository.findByFirstNameContainingIgnoreCase(name);
    }
}