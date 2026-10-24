package com.aadiandjava.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aadiandjava.dto.Phq9SubmissionDto;
import com.aadiandjava.entity.Phq9Result;
import com.aadiandjava.service.Phq9Service;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/phq9") // Added /v1 here to match BASE_URL
@RequiredArgsConstructor
public class Phq9Controller {

    private final Phq9Service phq9Service;

    @PostMapping("/submit")
    public ResponseEntity<Phq9Result> submitTest(@RequestBody Phq9SubmissionDto submission) {
        Phq9Result result = phq9Service.evaluateAndSaveTest(submission);
        return ResponseEntity.ok(result);
    }
}