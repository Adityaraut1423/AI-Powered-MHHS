package com.aadiandjava.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aadiandjava.entity.Phq9Result;

public interface Phq9ResultRepository extends JpaRepository<Phq9Result, Long> {
    List<Phq9Result> findByStudentId(Long studentId);
}