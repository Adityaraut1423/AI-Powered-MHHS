package com.aadiandjava.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aadiandjava.entity.GratitudeEntry;

public interface GratitudeEntryRepository extends JpaRepository<GratitudeEntry, Long> {
    List<GratitudeEntry> findByStudentIdOrderByCreatedAtDesc(Long studentId);
}