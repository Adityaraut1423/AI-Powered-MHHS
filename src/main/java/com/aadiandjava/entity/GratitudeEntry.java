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
@Table(name = "gratitude_entries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GratitudeEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long studentId;

    @Column(columnDefinition = "TEXT")
    private String noteOne;

    @Column(columnDefinition = "TEXT")
    private String noteTwo;

    @Column(columnDefinition = "TEXT")
    private String noteThree;

    private LocalDateTime createdAt = LocalDateTime.now();
}