package com.aadiandjava.dto;

import java.util.List;

import lombok.Data;

@Data
public class Phq9SubmissionDto {
    private Long studentId;
    private List<Integer> answers; // List of 9 integers (0 to 3)
}