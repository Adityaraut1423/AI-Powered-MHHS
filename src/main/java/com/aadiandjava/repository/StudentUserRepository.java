package com.aadiandjava.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.aadiandjava.dto.StudentComprehensiveProjection;
import com.aadiandjava.entity.StudentUser;

@Repository
public interface StudentUserRepository extends JpaRepository<StudentUser, Long> {

    Optional<StudentUser> findByEmail(String email);

    @Query(value = """
        SELECT
            s.id AS studentId,
            s.name AS studentName,
            s.email AS studentEmail,
            s.phone_number AS studentPhone,
            ec.name AS guardianName,
            ec.relationship AS relationship,
            ec.phone_number AS guardianPhone,
            ec.email AS guardianEmail,
            COUNT(DISTINCT ca.id) AS totalAppointments,
            COUNT(DISTINCT sa.id) AS totalSosAlerts,
            MAX(sa.timestamp) AS lastSosTimestamp
        FROM students s
        LEFT JOIN emergency_contacts ec ON s.id = ec.student_id
        LEFT JOIN counselor_appointments ca ON s.id = ca.student_id
        LEFT JOIN sos_alerts sa ON s.id = sa.student_id
        GROUP BY s.id, ec.id
        ORDER BY s.id DESC
        """, nativeQuery = true)
    List<StudentComprehensiveProjection> fetchComprehensiveStudentDirectory();
}