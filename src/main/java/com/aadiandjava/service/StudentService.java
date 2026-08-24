package com.aadiandjava.service;

import java.util.List;
import java.util.Optional;

import com.aadiandjava.entity.StudentUser;

public interface StudentService {
    StudentUser saveStudent(StudentUser student);
    Optional<StudentUser> getStudentById(Long id);
    List<StudentUser> getAllStudents();
}