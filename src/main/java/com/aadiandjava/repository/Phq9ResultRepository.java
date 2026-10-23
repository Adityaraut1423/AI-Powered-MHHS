package com.aadiandjava.repository;

import com.aadiandjava.entity.Phq9Result;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface Phq9ResultRepository extends JpaRepository<Phq9Result, Long> {
    List<Phq9Result> findByStudentId(Long studentId);
}