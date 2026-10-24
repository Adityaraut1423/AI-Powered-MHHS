package com.aadiandjava.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.aadiandjava.dto.Phq9SubmissionDto;
import com.aadiandjava.entity.Phq9Result;
import com.aadiandjava.repository.Phq9ResultRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class Phq9Service {

    private final Phq9ResultRepository phq9ResultRepository;
    private final SosOrchestratorService sosOrchestratorService;

    public Phq9Result evaluateAndSaveTest(Phq9SubmissionDto submission) {
        if (submission.getAnswers() == null || submission.getAnswers().size() != 9) {
            throw new IllegalArgumentException("PHQ-9 test requires exactly 9 answers.");
        }

        // 1. Calculate total score (each question scored 0-3)
        int totalScore = submission.getAnswers().stream().mapToInt(Integer::intValue).sum();

        // 2. Determine Severity Category
        String severity;
        if (totalScore >= 1 && totalScore <= 4) {
            severity = "Minimal Depression";
        } else if (totalScore >= 5 && totalScore <= 9) {
            severity = "Mild Depression";
        } else if (totalScore >= 10 && totalScore <= 14) {
            severity = "Moderate Depression";
        } else if (totalScore >= 15 && totalScore <= 19) {
            severity = "Moderately Severe Depression";
        } else if (totalScore >= 20) {
            severity = "Severe Depression";
        } else {
            severity = "None / Minimal";
        }

        // 3. Check Question 9 (Self-harm index, index 8)
        int q9Score = submission.getAnswers().get(8);
        if (q9Score > 0) {
            log.warn("CRITICAL: PHQ-9 Question 9 flagged self-harm ideation for Student ID: {}", submission.getStudentId());
            sosOrchestratorService.triggerEmergency(submission.getStudentId(), "PHQ-9 Q9 Self-Harm Flag", "Self-Assessment Portal");
        }

        // 4. Save to MySQL
        Phq9Result result = new Phq9Result();
        result.setStudentId(submission.getStudentId());
        result.setTotalScore(totalScore);
        result.setSeverityLevel(severity); // Matches severityLevel field in entity
        result.setTimestamp(LocalDateTime.now());

        Phq9Result saved = phq9ResultRepository.save(result);
        log.info("PHQ-9 Test evaluated for Student ID: {}. Total Score: {}, Severity: {}", submission.getStudentId(), totalScore, severity);

        return saved;
    }
}