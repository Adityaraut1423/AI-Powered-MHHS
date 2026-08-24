package com.aadiandjava.dto;

public interface StudentFullProfileProjection {
    Long getStudentId();
    String getStudentName();
    String getStudentEmail();
    String getStudentPhone();
    String getStudentPassword();
    String getGuardianName();
    String getRelationship();
    String getGuardianPhone();
    String getGuardianEmail();
}