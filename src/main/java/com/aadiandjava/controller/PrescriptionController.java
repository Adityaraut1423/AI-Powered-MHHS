package com.aadiandjava.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aadiandjava.entity.StudentUser;
import com.aadiandjava.repository.StudentUserRepository;
import com.aadiandjava.service.EmailService;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/prescriptions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PrescriptionController {

    private final EmailService emailService;
    private final StudentUserRepository studentUserRepository;

    /**
     * Look up student details by ID to auto-populate the doctor's prescription console
     */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getStudentDetailsById(@PathVariable Long studentId) {
        StudentUser student = studentUserRepository.findById(studentId).orElse(null);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(student);
    }

    /**
     * Issues digital prescription and sends a copy directly to the student's email
     */
    @PostMapping("/issue-and-email")
    public ResponseEntity<?> issueAndEmailPrescription(@RequestBody PrescriptionRequest request) {
        if (request.getStudentId() == null) {
            return ResponseEntity.badRequest().body("Student ID is required.");
        }

        // Fetch student details from MySQL to get their registered email and phone number[cite: 10]
        StudentUser student = studentUserRepository.findById(request.getStudentId()).orElse(null);
        if (student == null || student.getEmail() == null || student.getEmail().isBlank()) {
            return ResponseEntity.badRequest().body("Student not found or missing registered email address.");
        }

        // Use name from request payload if provided, otherwise fallback to database record[cite: 10]
        String nameToUse = (request.getStudentName() != null && !request.getStudentName().isBlank())
                ? request.getStudentName()
                : student.getName();

        log.info("Issuing digital prescription for Student ID: {} ({})", student.getId(), nameToUse);

        // Dispatch prescription via EmailService including student ID and name[cite: 10]
        emailService.sendPrescriptionEmail(
            student.getEmail(),
            student.getId(),
            nameToUse,
            student.getPhoneNumber(),
            request.getDiagnosis(),
            request.getMedications(),
            request.getDirectives()
        );

        return ResponseEntity.ok("Prescription successfully issued and emailed to the student.");
    }

    // DTO payload definition including studentName[cite: 10]
    @Data
    public static class PrescriptionRequest {
        private Long studentId;
        private String studentName; // To capture student name from frontend[cite: 10]
        private String diagnosis;
        private String medications;
        private String directives;
    }
}