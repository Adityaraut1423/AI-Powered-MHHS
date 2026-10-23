package com.aadiandjava.dto;

import lombok.Data;
import java.util.List;

@Data
public class Phq9SubmissionDto {
    private Long studentId;
    private List<Integer> answers; // List of 9 integers (0 to 3)
}