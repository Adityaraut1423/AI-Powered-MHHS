package com.aadiandjava.controller;

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

import com.aadiandjava.entity.ForumPost;
import com.aadiandjava.entity.GratitudeEntry;
import com.aadiandjava.entity.ScreeningTest;
import com.aadiandjava.repository.ForumPostRepository;
import com.aadiandjava.repository.GratitudeEntryRepository;
import com.aadiandjava.repository.ScreeningTestRepository;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1")
public class ExtendedFeaturesController {

    @Autowired private ForumPostRepository forumPostRepository;
    @Autowired private ScreeningTestRepository screeningTestRepository;
    @Autowired private GratitudeEntryRepository gratitudeEntryRepository;

    // --- ANONYMOUS FORUM ---
    @PostMapping("/forum/create")
    public ResponseEntity<ForumPost> createPost(@RequestParam Long studentId, @RequestBody ForumPost post) {
        post.setStudentId(studentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(forumPostRepository.save(post));
    }

    @GetMapping("/forum/all")
    public ResponseEntity<List<ForumPost>> getAllPosts() {
        return ResponseEntity.ok(forumPostRepository.findAllByOrderByCreatedAtDesc());
    }

    // --- CLINICAL SCREENING TESTS (PHQ-9 / GAD-7) ---
    @PostMapping("/screening/submit")
    public ResponseEntity<ScreeningTest> submitTest(@RequestParam Long studentId, @RequestBody ScreeningTest test) {
        test.setStudentId(studentId);

        // Auto-compute severity level
        int score = test.getTotalScore();
        if (score <= 4) {
			test.setSeverity("MINIMAL");
		} else if (score <= 9) {
			test.setSeverity("MILD");
		} else if (score <= 14) {
			test.setSeverity("MODERATE");
		} else {
			test.setSeverity("SEVERE");
		}

        return ResponseEntity.status(HttpStatus.CREATED).body(screeningTestRepository.save(test));
    }

    @GetMapping("/screening/student/{studentId}")
    public ResponseEntity<List<ScreeningTest>> getStudentScreenings(@PathVariable Long studentId) {
        return ResponseEntity.ok(screeningTestRepository.findByStudentIdOrderByCompletedAtDesc(studentId));
    }

    // --- GRATITUDE JOURNALING ---
    @PostMapping("/gratitude/save")
    public ResponseEntity<GratitudeEntry> saveGratitude(@RequestParam Long studentId, @RequestBody GratitudeEntry entry) {
        entry.setStudentId(studentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(gratitudeEntryRepository.save(entry));
    }

    @GetMapping("/gratitude/student/{studentId}")
    public ResponseEntity<List<GratitudeEntry>> getStudentGratitude(@PathVariable Long studentId) {
        return ResponseEntity.ok(gratitudeEntryRepository.findByStudentIdOrderByCreatedAtDesc(studentId));
    }
}