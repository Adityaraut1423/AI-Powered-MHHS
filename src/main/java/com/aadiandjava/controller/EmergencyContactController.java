package com.aadiandjava.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aadiandjava.entity.EmergencyContact;
import com.aadiandjava.repository.EmergencyContactRepository;
import com.aadiandjava.repository.StudentUserRepository;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/contacts")
public class EmergencyContactController {

    @Autowired
    private EmergencyContactRepository contactRepository;

    @Autowired
    private StudentUserRepository studentRepository;

    @PostMapping("/add")
    public ResponseEntity<?> addContact(@RequestParam Long studentId, @RequestBody EmergencyContact contact) {
        return studentRepository.findById(studentId).map(student -> {
            contact.setStudentId(student.getId());
            EmergencyContact saved = contactRepository.save(contact);
            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<EmergencyContact>> getContactsByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(contactRepository.findByStudentId(studentId));
    }
}