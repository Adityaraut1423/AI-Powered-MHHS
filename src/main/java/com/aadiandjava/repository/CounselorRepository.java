package com.aadiandjava.repository;

import java.util.List;
import java.util.Optional; // <-- Make sure to import Optional
import org.springframework.data.jpa.repository.JpaRepository;
import com.aadiandjava.entity.Counselor;

public interface CounselorRepository extends JpaRepository<Counselor, Long> {
    List<Counselor> findByIsAvailableTrue();
    Optional<Counselor> findByEmail(String email); // <-- Add this line
}