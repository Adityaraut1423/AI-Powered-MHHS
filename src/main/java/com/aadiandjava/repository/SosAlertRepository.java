package com.aadiandjava.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.aadiandjava.entity.SosAlert;

public interface SosAlertRepository extends JpaRepository<SosAlert, Long> {}