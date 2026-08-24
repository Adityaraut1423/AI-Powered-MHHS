package com.aadiandjava.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aadiandjava.entity.MoodLog;
import com.aadiandjava.repository.MoodLogRepository;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/moods")
public class MoodLogController {

    @Autowired
    private MoodLogRepository moodLogRepository;

    @PostMapping("/log")
    public ResponseEntity<MoodLog> logMood(@RequestParam Long studentId, @RequestBody MoodLog moodLog) {
        moodLog.setStudentId(studentId);
        moodLog.setTimestamp(LocalDateTime.now());
        MoodLog savedLog = moodLogRepository.save(moodLog);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedLog);
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<MoodLog>> getStudentMoodHistory(@PathVariable Long studentId) {
        List<MoodLog> logs = moodLogRepository.findByStudentIdOrderByTimestampDesc(studentId);
        return ResponseEntity.ok(logs);
    }
}