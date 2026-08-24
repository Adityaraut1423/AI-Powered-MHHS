package com.aadiandjava.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aadiandjava.entity.EmergencyContact;
import com.aadiandjava.entity.StudentUser;
import com.aadiandjava.entity.UserRole;
import com.aadiandjava.repository.EmergencyContactRepository;
import com.aadiandjava.repository.StudentUserRepository;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final StudentUserRepository studentUserRepository;
    private final EmergencyContactRepository emergencyContactRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<?> registerStudent(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email != null && studentUserRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email is already registered!"));
        }

        // 1. Save Student Record with proper role assignment
        StudentUser student = new StudentUser();
        student.setName(request.get("name"));
        student.setEmail(email);
        student.setPhoneNumber(request.get("phoneNumber"));
        student.setPassword(passwordEncoder.encode(request.get("password")));

        // Handle role parsing from request payload (defaults to ROLE_STUDENT if missing/invalid)
        String roleStr = request.get("role");
        if (roleStr != null && !roleStr.isBlank()) {
            try {
                student.setRole(UserRole.valueOf(roleStr.toUpperCase()));
            } catch (IllegalArgumentException e) {
                student.setRole(UserRole.ROLE_STUDENT);
            }
        } else {
            student.setRole(UserRole.ROLE_STUDENT);
        }

        StudentUser savedStudent = studentUserRepository.save(student);

        // 2. Save Linked Guardian Emergency Contact
        String guardianEmail = request.get("guardianEmail");
        if (guardianEmail != null && !guardianEmail.isBlank()) {
            EmergencyContact contact = new EmergencyContact();
            contact.setStudentId(savedStudent.getId());
            contact.setName(request.get("guardianName"));
            contact.setPhoneNumber(request.get("guardianPhone"));
            contact.setEmail(guardianEmail);
            contact.setRelationship("Guardian");

            emergencyContactRepository.save(contact);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(savedStudent);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginStudent(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String rawPassword = request.get("password");
        String expectedRole = request.get("role"); // Receives "ROLE_ADMIN", "ROLE_DOCTOR", etc. from frontend portal

        if (email == null || rawPassword == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email and password are required!"));
        }

        Optional<StudentUser> userOpt = studentUserRepository.findByEmail(email);

        if (userOpt.isPresent()) {
            StudentUser user = userOpt.get();
            if (passwordEncoder.matches(rawPassword, user.getPassword())) {

                // STRICT ROLE ENFORCEMENT: Blocks students from logging into Admin/Doctor/Counselor portals
                if (expectedRole != null && !expectedRole.isBlank()) {
                    String userRoleName = user.getRole() != null ? user.getRole().name() : "ROLE_STUDENT";
                    if (!userRoleName.equalsIgnoreCase(expectedRole)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(Map.of("message", "Access Denied: Your account does not have authorization for this portal."));
                    }
                }

                Map<String, Object> response = new HashMap<>();
                response.put("status", "SUCCESS");
                response.put("token", "dummy-jwt-token-" + user.getId());
                response.put("studentId", user.getId());
                response.put("id", user.getId());
                response.put("name", user.getName());
                response.put("email", user.getEmail());
                response.put("role", user.getRole() != null ? user.getRole().name() : "ROLE_STUDENT");
                return ResponseEntity.ok(response);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid email or password"));
    }
}