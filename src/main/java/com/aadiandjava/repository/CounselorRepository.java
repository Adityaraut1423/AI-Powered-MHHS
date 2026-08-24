package com.aadiandjava.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aadiandjava.entity.Counselor;

public interface CounselorRepository extends JpaRepository<Counselor, Long> {
    List<Counselor> findByIsAvailableTrue();
}