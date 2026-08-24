package com.aadiandjava.dto;

import java.time.LocalDateTime;

public interface AppointmentDetailsProjection {
    Long getAppointmentId();
    Long getStudentId();
    String getStudentName();
    String getStudentEmail();
    String getStudentPhone();
    String getCounselorName();
    String getCounselorSpecialization();
    LocalDateTime getAppointmentTime();
    String getNotes();
    String getStatus();
}