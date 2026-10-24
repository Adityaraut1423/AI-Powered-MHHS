package com.aadiandjava.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aadiandjava.entity.Counselor;
import com.aadiandjava.entity.CounselorAppointment;
import com.aadiandjava.entity.StudentUser;
import com.aadiandjava.repository.CounselorAppointmentRepository;
import com.aadiandjava.repository.CounselorRepository;
import com.aadiandjava.repository.StudentUserRepository;
import com.aadiandjava.service.EmailService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/counselors")
@RequiredArgsConstructor
@Tag(name = "Campus Counselor Management", description = "Endpoints for counselor onboarding, appointment scheduling, and lifecycle status management")
public class CounselorBookingController {

    private final CounselorRepository counselorRepository;
    private final CounselorAppointmentRepository appointmentRepository;
    private final StudentUserRepository studentUserRepository;
    private final EmailService emailService;

    @Operation(summary = "Register a new campus counselor profile")
    @PostMapping("/add")
    public ResponseEntity<Counselor> addCounselor(@RequestBody Counselor counselor) {
        return ResponseEntity.status(HttpStatus.CREATED).body(counselorRepository.save(counselor));
    }

    @Operation(summary = "Retrieve all currently available counselors")
    @GetMapping("/available")
    public ResponseEntity<List<Counselor>> getAvailableCounselors() {
        return ResponseEntity.ok(counselorRepository.findByIsAvailableTrue());
    }

    @Operation(summary = "Book a confidential campus counselor appointment and trigger student confirmation email")
    @PostMapping("/book")
    public ResponseEntity<?> bookAppointment(
            @RequestParam Long studentId,
            @RequestParam Long counselorId,
            @RequestBody CounselorAppointment appointmentRequest) {

        try {
            // 1. Verify if the counselor exists
            Counselor counselor = counselorRepository.findById(counselorId).orElse(null);
            if (counselor == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Counselor ID not found.");
            }

            // 2. Save the appointment in MySQL
            appointmentRequest.setStudentId(studentId);
            appointmentRequest.setCounselorId(counselorId);
            appointmentRequest.setStatus("CONFIRMED");
            CounselorAppointment savedAppointment = appointmentRepository.save(appointmentRequest);

            log.info("Counselor appointment scheduled successfully for Student ID: {} with Counselor ID: {}", studentId, counselorId);

            // 3. Fetch student details to get their email and name
            StudentUser student = studentUserRepository.findById(studentId).orElse(null);

            // 4. Dispatch professional HTML confirmation email to the student[cite: 3]
            if (student != null && student.getEmail() != null && !student.getEmail().isBlank()) {
                emailService.sendAppointmentConfirmationEmail(
                    student.getEmail(),
                    student.getName(),
                    savedAppointment.getAppointmentTime() != null ? savedAppointment.getAppointmentTime().toString() : "As Scheduled",
                    savedAppointment.getNotes()
                );
            } else {
                log.warn("Student email not found for ID: {}. Skipping confirmation email.", studentId);
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(savedAppointment);

        } catch (Exception e) {
            log.error("Failed to book counselor appointment: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Failed to schedule appointment: " + e.getMessage());
        }
    }

    @Operation(summary = "Fetch appointment history for a student ordered by date descending")
    @GetMapping("/appointments/student/{studentId}")
    public ResponseEntity<List<CounselorAppointment>> getStudentAppointments(@PathVariable Long studentId) {
        return ResponseEntity.ok(appointmentRepository.findByStudentIdOrderByAppointmentTimeDesc(studentId));
    }

    @Operation(summary = "Fetch assigned appointments for a specific counselor practitioner")
    @GetMapping("/appointments/counselor/{counselorId}")
    public ResponseEntity<List<CounselorAppointment>> getCounselorAppointments(@PathVariable Long counselorId) {
        return ResponseEntity.ok(appointmentRepository.findByCounselorIdOrderByAppointmentTimeDesc(counselorId));
    }

    @Operation(summary = "Update lifecycle state of an appointment with protection against altering confirmed sessions")
    @PutMapping("/appointments/{appointmentId}/status")
    public ResponseEntity<?> updateAppointmentStatus(
            @PathVariable Long appointmentId,
            @RequestParam String status) {

        return appointmentRepository.findById(appointmentId).map(appointment -> {
            String currentStatus = appointment.getStatus() != null ? appointment.getStatus().toUpperCase() : "";
            String newStatus = status.toUpperCase();

            // Prevent changing status if it's already confirmed or completed
            if ("CONFIRMED".equals(currentStatus) && "CANCELLED".equals(newStatus)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Cannot reject or cancel an appointment that has already been confirmed.");
            }
            if ("COMPLETED".equals(currentStatus)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Cannot modify an appointment that has already been marked as completed.");
            }

            appointment.setStatus(newStatus);
            return ResponseEntity.ok(appointmentRepository.save(appointment));
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Appointment not found."));
    }
}