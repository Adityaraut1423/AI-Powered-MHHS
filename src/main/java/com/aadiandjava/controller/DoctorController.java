package com.aadiandjava.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aadiandjava.entity.Doctor;
import com.aadiandjava.repository.DoctorRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/doctors")
@RequiredArgsConstructor
@Tag(name = "Doctor Management", description = "Endpoints for doctor authentication and profile management")
public class DoctorController {

    private final DoctorRepository doctorRepository;

    @Operation(summary = "Authenticate doctor login for dashboard access")
    @PostMapping("/login")
    public ResponseEntity<?> authenticateDoctor(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        Doctor doctor = doctorRepository.findByEmail(email).orElse(null);

        if (doctor != null && doctor.getPassword().equals(password)) {
            return ResponseEntity.ok(doctor);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid doctor email or password.");
        }
    }
}