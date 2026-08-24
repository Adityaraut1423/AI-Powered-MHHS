package com.aadiandjava.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aadiandjava.entity.MoodLog;

public interface MoodLogRepository extends JpaRepository<MoodLog, Long> {
    List<MoodLog> findByStudentIdOrderByTimestampDesc(Long studentId);
}