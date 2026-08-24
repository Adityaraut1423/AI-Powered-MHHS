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

    @Operation(summary = "Book a confidential counseling session and trigger student confirmation email")
    @PostMapping("/book")
    public ResponseEntity<CounselorAppointment> bookAppointment(
            @RequestParam Long studentId,
            @RequestParam Long counselorId,
            @RequestBody CounselorAppointment appointment) {

        return counselorRepository.findById(counselorId).map(counselor -> {
            appointment.setStudentId(studentId);
            appointment.setCounselorId(counselorId);
            appointment.setStatus("REQUESTED");

            CounselorAppointment savedAppointment = appointmentRepository.save(appointment);

            // Safely fetch student record and trigger confirmation email without interrupting the booking process[cite: 3]
            try {
                StudentUser student = studentUserRepository.findById(studentId).orElse(null);
                if (student != null && student.getEmail() != null) {
                    emailService.sendAppointmentConfirmationEmail(
                        student.getEmail(),
                        student.getName(),
                        savedAppointment.getAppointmentTime() != null ? savedAppointment.getAppointmentTime().toString() : "Scheduled",
                        savedAppointment.getNotes()
                    );
                }
            } catch (Exception e) {
                log.error("Non-blocking email dispatch error during appointment booking: {}", e.getMessage(), e);
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(savedAppointment);
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
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

    @Operation(summary = "Update lifecycle state of an appointment (CONFIRMED, COMPLETED, CANCELLED)")
    @PutMapping("/appointments/{appointmentId}/status")
    public ResponseEntity<CounselorAppointment> updateAppointmentStatus(
            @PathVariable Long appointmentId,
            @RequestParam String status) {

        return appointmentRepository.findById(appointmentId).map(appointment -> {
            appointment.setStatus(status.toUpperCase());
            return ResponseEntity.ok(appointmentRepository.save(appointment));
        }).orElse(ResponseEntity.notFound().build());
    }
}
