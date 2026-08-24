package com.aadiandjava.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aadiandjava.entity.ScreeningTest;

public interface ScreeningTestRepository extends JpaRepository<ScreeningTest, Long> {
    List<ScreeningTest> findByStudentIdOrderByCompletedAtDesc(Long studentId);
}