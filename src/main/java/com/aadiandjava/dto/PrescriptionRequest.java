package com.aadiandjava.dto;

import lombok.Data;

@Data
public class PrescriptionRequest {
    private Long studentId;
    private String studentName; // Added field to receive patient name
    private String diagnosis;
    private String medications;
    private String directives;
}