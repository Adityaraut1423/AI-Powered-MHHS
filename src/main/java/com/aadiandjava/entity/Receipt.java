package com.aadiandjava.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "receipts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long studentId;
    private String studentName;
    private String studentEmail;

    private String serviceDescription; // e.g., "Clinical Psychiatric Consultation & Rx"
    private Double amountPaid;         // e.g., 500.00
    private String paymentMethod;      // e.g., "Campus Wallet / UPI / Credit Card"
    private String transactionId;      // e.g., "TXN-99827160"

    private LocalDateTime paymentDate;
}