package com.aadiandjava.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aadiandjava.dto.AppointmentDetailsProjection;
import com.aadiandjava.dto.StudentComprehensiveProjection;
import com.aadiandjava.entity.Admin;
import com.aadiandjava.entity.CounselorAppointment;
import com.aadiandjava.repository.AdminRepository;
import com.aadiandjava.repository.CounselorAppointmentRepository;
import com.aadiandjava.repository.StudentUserRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "Admin Management", description = "Endpoints for administrator authentication and system operations")
public class AdminController {

    @Autowired
    private StudentUserRepository studentUserRepository;

    @Autowired
    private CounselorAppointmentRepository appointmentRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 0. Secure Administrator Login Endpoint (Updated to use BCrypt)
    @Operation(summary = "Authenticate system administrator login")
    @PostMapping("/login")
    public ResponseEntity<?> authenticateAdmin(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        if (email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email and password are required"));
        }

        Admin admin = adminRepository.findByEmail(email).orElse(null);

        // Uses passwordEncoder.matches() for encrypted hashes, with a fallback for plaintext if needed
        if (admin != null && (passwordEncoder.matches(password, admin.getPassword()) || admin.getPassword().equals(password))) {
            return ResponseEntity.ok(admin);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid administrator credentials."));
        }
    }

    // 1. Fetch Comprehensive Student Master Directory
    @GetMapping("/students/comprehensive")
    public ResponseEntity<List<StudentComprehensiveProjection>> getFullStudentDirectory() {
        return ResponseEntity.ok(studentUserRepository.fetchComprehensiveStudentDirectory());
    }

    // 2. Fetch All Scheduled Counselor Appointments
    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentDetailsProjection>> getAllAppointments() {
        List<AppointmentDetailsProjection> appointments = appointmentRepository.fetchAllAppointmentsWithDetails();
        return ResponseEntity.ok(appointments);
    }

    // 3. Update Appointment Status (CONFIRMED, CANCELLED, COMPLETED)
    @PatchMapping("/appointments/{id}/status")
    public ResponseEntity<?> updateAppointmentStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String newStatus = body.get("status");

        if (newStatus == null || newStatus.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Status field is required"));
        }

        Optional<CounselorAppointment> apptOpt = appointmentRepository.findById(id);
        if (apptOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        CounselorAppointment appt = apptOpt.get();
        appt.setStatus(newStatus.toUpperCase());
        appointmentRepository.save(appt);

        return ResponseEntity.ok(Map.of("message", "Appointment status successfully updated to " + newStatus.toUpperCase()));
    }

    // 4. Export Student Master Directory to CSV File
    @GetMapping("/students/export-csv")
    public void exportStudentsToCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"student_records.csv\"");

        List<StudentComprehensiveProjection> students = studentUserRepository.fetchComprehensiveStudentDirectory();

        try (PrintWriter writer = response.getWriter()) {
            // CSV Header Row
            writer.println("Student ID,Student Name,Student Email,Student Phone,Guardian Name,Relationship,Guardian Phone,Guardian Email,Total Appts,Total SOS,Last SOS Timestamp");

            // CSV Data Rows
            for (StudentComprehensiveProjection s : students) {
                writer.println(String.format("%d,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",%d,%d,\"%s\"",
                    s.getStudentId(),
                    escapeCsv(s.getStudentName()),
                    escapeCsv(s.getStudentEmail()),
                    escapeCsv(s.getStudentPhone()),
                    escapeCsv(s.getGuardianName()),
                    escapeCsv(s.getRelationship()),
                    escapeCsv(s.getGuardianPhone()),
                    escapeCsv(s.getGuardianEmail()),
                    s.getTotalAppointments() != null ? s.getTotalAppointments() : 0,
                    s.getTotalSosAlerts() != null ? s.getTotalSosAlerts() : 0,
                    s.getLastSosTimestamp() != null ? s.getLastSosTimestamp().toString() : "None"
                ));
            }
        }
    }

    // Helper method to sanitize string fields for CSV safety
    private String escapeCsv(String input) {
        if (input == null) {
            return "N/A";
        }
        return input.replace("\"", "\"\"");
    }
}