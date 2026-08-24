package com.aadiandjava.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aadiandjava.dto.StudentRegisterRequest;
import com.aadiandjava.entity.EmergencyContact;
import com.aadiandjava.entity.StudentUser;
import com.aadiandjava.entity.UserRole;
import com.aadiandjava.repository.EmergencyContactRepository;
import com.aadiandjava.repository.StudentUserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final StudentUserRepository studentUserRepository;
    private final EmergencyContactRepository emergencyContactRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public StudentUser registerStudent(StudentRegisterRequest request) {
        StudentUser student = new StudentUser();
        student.setName(request.getName());
        student.setEmail(request.getEmail());
        student.setPhoneNumber(request.getPhoneNumber());
        student.setPassword(passwordEncoder.encode(request.getPassword()));
        student.setRole(UserRole.ROLE_STUDENT);

        StudentUser savedStudent = studentUserRepository.save(student);

        if (request.getGuardianEmail() != null && !request.getGuardianEmail().isBlank()) {
            EmergencyContact guardianContact = new EmergencyContact();
            guardianContact.setStudentId(savedStudent.getId());
            guardianContact.setName(request.getGuardianName());
            guardianContact.setRelationship("Guardian");
            guardianContact.setPhoneNumber(request.getGuardianPhone());
            guardianContact.setEmail(request.getGuardianEmail());

            emergencyContactRepository.save(guardianContact);
        }

        return savedStudent;
    }
}