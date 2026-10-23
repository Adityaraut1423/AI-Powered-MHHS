package com.aadiandjava.controller;

import com.aadiandjava.entity.Receipt;
import com.aadiandjava.entity.StudentUser;
import com.aadiandjava.repository.ReceiptRepository;
import com.aadiandjava.repository.StudentUserRepository;
import com.aadiandjava.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/receipts")
@RequiredArgsConstructor
@Tag(name = "Receipt Management", description = "Endpoints for generating and viewing student billing receipts")
public class ReceiptController {

    private final ReceiptRepository receiptRepository;
    private final StudentUserRepository studentUserRepository;
    private final EmailService emailService;

    @Operation(summary = "Generate and email a payment receipt to a student")
    @PostMapping("/issue")
    public ResponseEntity<?> issueReceipt(@RequestBody Receipt request) {
        StudentUser student = studentUserRepository.findById(request.getStudentId()).orElse(null);
        if (student == null) {
            return ResponseEntity.badRequest().body("Student ID not found.");
        }

        request.setStudentName(student.getName());
        request.setStudentEmail(student.getEmail());
        request.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        request.setPaymentDate(LocalDateTime.now());

        Receipt savedReceipt = receiptRepository.save(request);

        // Send email copy
        String formattedDate = savedReceipt.getPaymentDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        emailService.sendReceiptEmail(
            student.getEmail(),
            student.getName(),
            savedReceipt.getServiceDescription(),
            savedReceipt.getAmountPaid(),
            savedReceipt.getTransactionId(),
            formattedDate
        );

        return ResponseEntity.ok(savedReceipt);
    }

    @Operation(summary = "Get all receipts for a specific student")
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Receipt>> getStudentReceipts(@PathVariable Long studentId) {
        return ResponseEntity.ok(receiptRepository.findByStudentId(studentId));
    }
}