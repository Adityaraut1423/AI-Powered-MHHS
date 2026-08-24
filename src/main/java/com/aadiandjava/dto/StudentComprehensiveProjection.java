package com.aadiandjava.dto;

import java.time.LocalDateTime;

public interface StudentComprehensiveProjection {
    Long getStudentId();
    String getStudentName();
    String getStudentEmail();
    String getStudentPhone();

    // Guardian / Emergency Contact Info
    String getGuardianName();
    String getRelationship();
    String getGuardianPhone();
    String getGuardianEmail();

    // Activity & Crisis Metrics
    Long getTotalAppointments();
    Long getTotalSosAlerts();
    LocalDateTime getLastSosTimestamp();
}