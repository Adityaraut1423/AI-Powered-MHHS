package com.aadiandjava.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aadiandjava.entity.EmergencyContact;
import com.aadiandjava.entity.SosAlert;
import com.aadiandjava.entity.StudentUser;
import com.aadiandjava.repository.EmergencyContactRepository;
import com.aadiandjava.repository.SosAlertRepository;
import com.aadiandjava.repository.StudentUserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SosOrchestratorService {

    private final EmergencyContactRepository contactRepository;
    private final SosAlertRepository sosAlertRepository;
    private final StudentUserRepository studentUserRepository;
    private final FreePhoneCallService freePhoneCallService; // Free Call Service injection

    @Transactional
    public SosAlert triggerEmergency(Long studentId, String triggerType, String liveLocation) {
        // Sanitize location input
        String validLocation = (liveLocation != null && !liveLocation.isBlank())
                ? liveLocation
                : "Hostel Block B / Campus Grounds";

        // 1. Audit trail record in MySQL
        SosAlert alert = new SosAlert();
        alert.setStudentId(studentId);
        alert.setTriggerType(triggerType);
        alert.setStatus("TRIGGERED");
        alert.setTimestamp(LocalDateTime.now());
        alert.setLocationData(validLocation);
        sosAlertRepository.save(alert);

        log.info("SOS Emergency Alert initialized for Student ID: {} at Location: [{}]", studentId, validLocation);

        // 2. Fetch Student details
        StudentUser student = studentUserRepository.findById(studentId).orElse(null);
        String studentName = (student != null) ? student.getName() : "Student ID " + studentId;

        // 3. Fetch linked Guardian Emergency Contacts
        List<EmergencyContact> contacts = contactRepository.findByStudentId(studentId);

        if (contacts.isEmpty()) {
            log.warn("No emergency contacts found for Student ID: {}", studentId);
        }

        // 4. Trigger Free Automated Emergency Voice Call to all registered guardian numbers
        for (EmergencyContact contact : contacts) {
            if (contact.getPhoneNumber() != null && !contact.getPhoneNumber().isBlank()) {
                freePhoneCallService.triggerEmergencyCall(
                    contact.getPhoneNumber(),
                    studentName,
                    validLocation
                );
            }
        }

        alert.setStatus("NOTIFIED");
        return sosAlertRepository.save(alert);
    }
}