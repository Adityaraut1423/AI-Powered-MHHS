package com.aadiandjava.dto;

import lombok.Data;

@Data
public class StudentRegisterRequest {
    private String name;
    private String email;
    private String phoneNumber;
    private String password;

    // Mandatory Guardian / Emergency Details
    private String guardianName;
    private String guardianPhone;
    private String guardianEmail;
}