package com.aadiandjava.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "prescriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long studentId;
    private Long doctorId;

    @Column(columnDefinition = "TEXT")
    private String diagnosis; // ICD-10 Code & Narrative

    @Column(columnDefinition = "TEXT")
    private String medicationsJson; // JSON string of [{name, dosage, frequency, duration}]

    @Column(columnDefinition = "TEXT")
    private String clinicalInstructions;

    private String pdfStoragePath;
    private LocalDateTime issuedAt = LocalDateTime.now();
}