package com.aadiandjava.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aadiandjava.entity.Counselor;
import com.aadiandjava.entity.Doctor;
import com.aadiandjava.entity.EmergencyContact;
import com.aadiandjava.entity.StudentUser;
import com.aadiandjava.entity.UserRole;
import com.aadiandjava.repository.CounselorRepository;
import com.aadiandjava.repository.DoctorRepository;
import com.aadiandjava.repository.EmergencyContactRepository;
import com.aadiandjava.repository.StudentUserRepository;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final StudentUserRepository studentUserRepository;
    private final CounselorRepository counselorRepository;
    private final DoctorRepository doctorRepository;
    private final EmergencyContactRepository emergencyContactRepository;
    private final PasswordEncoder passwordEncoder;

    // In-memory store for OTPs (Email -> OTP code)
    private static final Map<String, String> otpStorage = new ConcurrentHashMap<>();

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<?> registerStudent(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email != null && studentUserRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email is already registered!"));
        }

        StudentUser student = new StudentUser();
        student.setName(request.get("name"));
        student.setEmail(email);
        student.setPhoneNumber(request.get("phoneNumber"));
        // Encodes student password securely using BCrypt
        student.setPassword(passwordEncoder.encode(request.get("password")));

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
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String rawPassword = request.get("password");
        String expectedRole = request.get("role"); // e.g., "ROLE_DOCTOR", "ROLE_COUNSELOR", "ROLE_STUDENT"

        if (email == null || rawPassword == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email and password are required!"));
        }

        // 1. Handle COUNSELOR Login (Plain-text password comparison)
        if ("ROLE_COUNSELOR".equalsIgnoreCase(expectedRole)) {
            Optional<Counselor> counselorOpt = counselorRepository.findByEmail(email);
            if (counselorOpt.isPresent()) {
                Counselor counselor = counselorOpt.get();
                if (rawPassword.equals(counselor.getPassword())) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", "SUCCESS");
                    response.put("token", "dummy-jwt-token-counselor-" + counselor.getId());
                    response.put("id", counselor.getId());
                    response.put("name", counselor.getName());
                    response.put("email", counselor.getEmail());
                    response.put("role", "ROLE_COUNSELOR");
                    return ResponseEntity.ok(response);
                }
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid counselor email or password"));
        }

        // 2. Handle DOCTOR Login (Plain-text password comparison)
        if ("ROLE_DOCTOR".equalsIgnoreCase(expectedRole)) {
            Optional<Doctor> doctorOpt = doctorRepository.findByEmail(email);
            if (doctorOpt.isPresent()) {
                Doctor doctor = doctorOpt.get();
                if (rawPassword.equals(doctor.getPassword())) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", "SUCCESS");
                    response.put("token", "dummy-jwt-token-doctor-" + doctor.getId());
                    response.put("id", doctor.getId());
                    response.put("name", doctor.getName());
                    response.put("email", doctor.getEmail());
                    response.put("role", "ROLE_DOCTOR");
                    return ResponseEntity.ok(response);
                }
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid doctor email or password"));
        }

        // 3. Handle STUDENT Login (BCrypt hashed password comparison)
        Optional<StudentUser> userOpt = studentUserRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            StudentUser user = userOpt.get();
            if (passwordEncoder.matches(rawPassword, user.getPassword())) {
                
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

    @Operation(summary = "Request Password Reset OTP")
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String role = request.get("role");

        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email is required"));
        }

        boolean userExists = false;
        if ("ROLE_STUDENT".equalsIgnoreCase(role)) {
            userExists = studentUserRepository.findByEmail(email).isPresent();
        } else if ("ROLE_COUNSELOR".equalsIgnoreCase(role)) {
            userExists = counselorRepository.findByEmail(email).isPresent();
        } else if ("ROLE_DOCTOR".equalsIgnoreCase(role)) {
            userExists = doctorRepository.findByEmail(email).isPresent();
        } else {
            userExists = studentUserRepository.findByEmail(email).isPresent();
        }

        if (!userExists) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Email address not found!"));
        }

        // Generate 6-digit random OTP
        String otp = String.valueOf((int) (Math.random() * 900000) + 100000);
        otpStorage.put(email, otp);

        log.info("🔐 PASSWORD RESET OTP for [{}]: [{}]", email, otp);

        return ResponseEntity.ok(Map.of("message", "OTP sent successfully. Check server logs for testing code."));
    }

    @Operation(summary = "Verify OTP and Reset Password")
    @PostMapping("/reset-password")
    @Transactional
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        String newPassword = request.get("newPassword");
        String role = request.get("role");

        if (email == null || otp == null || newPassword == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "All fields are required"));
        }

        String storedOtp = otpStorage.get(email);
        if (storedOtp == null || !storedOtp.equals(otp)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid or expired OTP"));
        }

        if ("ROLE_STUDENT".equalsIgnoreCase(role) || role == null) {
            Optional<StudentUser> studentOpt = studentUserRepository.findByEmail(email);
            if (studentOpt.isPresent()) {
                StudentUser student = studentOpt.get();
                student.setPassword(passwordEncoder.encode(newPassword));
                studentUserRepository.save(student);
            }
        } else if ("ROLE_COUNSELOR".equalsIgnoreCase(role)) {
            Optional<Counselor> counselorOpt = counselorRepository.findByEmail(email);
            if (counselorOpt.isPresent()) {
                Counselor counselor = counselorOpt.get();
                counselor.setPassword(newPassword);
                counselorRepository.save(counselor);
            }
        } else if ("ROLE_DOCTOR".equalsIgnoreCase(role)) {
            Optional<Doctor> doctorOpt = doctorRepository.findByEmail(email);
            if (doctorOpt.isPresent()) {
                Doctor doctor = doctorOpt.get();
                doctor.setPassword(newPassword);
                doctorRepository.save(doctor);
            }
        }

        otpStorage.remove(email);

        return ResponseEntity.ok(Map.of("message", "Password reset successfully! You can now sign in."));
    }
}