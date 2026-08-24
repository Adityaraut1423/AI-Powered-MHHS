package com.aadiandjava.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.aadiandjava.entity.StudentUser;
import com.aadiandjava.repository.StudentUserRepository;
import com.aadiandjava.service.StudentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentUserRepository studentRepository;

    @Override
    public StudentUser saveStudent(StudentUser student) {
        return studentRepository.save(student);
    }

    @Override
    public Optional<StudentUser> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    @Override
    public List<StudentUser> getAllStudents() {
        return studentRepository.findAll();
    }
}