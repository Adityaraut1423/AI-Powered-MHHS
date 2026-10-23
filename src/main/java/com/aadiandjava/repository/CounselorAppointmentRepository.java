package com.aadiandjava.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.aadiandjava.dto.AppointmentDetailsProjection;
import com.aadiandjava.entity.CounselorAppointment;

@Repository
public interface CounselorAppointmentRepository extends JpaRepository<CounselorAppointment, Long> {

    List<CounselorAppointment> findByStudentId(Long studentId);
    List<CounselorAppointment> findByStudentIdOrderByAppointmentTimeDesc(Long studentId);
    List<CounselorAppointment> findByCounselorIdOrderByAppointmentTimeDesc(Long counselorId);

    @Query(value = """
        SELECT
            ca.id AS appointmentId,
            s.id AS studentId,
            s.name AS studentName,
            s.email AS studentEmail,
            s.phone_number AS studentPhone,
            c.name AS counselorName,
            c.specialization AS counselorSpecialization,
            ca.appointment_time AS appointmentTime,
            ca.notes AS notes,
            ca.status AS status
        FROM counselor_appointments ca
        LEFT JOIN students s ON ca.student_id = s.id
        LEFT JOIN counselors c ON ca.counselor_id = c.id
        ORDER BY ca.appointment_time DESC
        """, nativeQuery = true)
    List<AppointmentDetailsProjection> fetchAllAppointmentsWithDetails();
}