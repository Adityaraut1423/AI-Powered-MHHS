package com.aadiandjava.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

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