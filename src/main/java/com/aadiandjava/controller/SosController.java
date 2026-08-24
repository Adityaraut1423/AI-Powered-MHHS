package com.aadiandjava.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aadiandjava.entity.SosAlert;
import com.aadiandjava.repository.SosAlertRepository;
import com.aadiandjava.service.SosOrchestratorService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/sos")
public class SosController {

    @Autowired
    private SosOrchestratorService sosOrchestratorService;

    @Autowired
    private SosAlertRepository sosAlertRepository;

    // 1. Trigger Manual SOS Emergency
    @PostMapping("/trigger")
    public ResponseEntity<SosAlert> triggerManualSos(
            @RequestParam Long studentId,
            @RequestParam(defaultValue = "GPS Unavailable") String location) {

        SosAlert alert = sosOrchestratorService.triggerEmergency(studentId, "MANUAL_SOS", location);
        return ResponseEntity.ok(alert);
    }

    // 2. Fetch All Recorded SOS Alerts (For Admin Dashboard Live Feed)
    @GetMapping("/all")
    public ResponseEntity<List<SosAlert>> getAllSosAlerts() {
        List<SosAlert> alerts = sosAlertRepository.findAll();
        return ResponseEntity.ok(alerts);
    }
}