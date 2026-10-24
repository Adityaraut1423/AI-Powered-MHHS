package com.aadiandjava.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "phq9_results")
public class Phq9Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long studentId;
    private int totalScore;
    private String severityLevel; // e.g., Minimal, Mild, Moderate, Moderately Severe, Severe
    private LocalDateTime timestamp;
}