package com.aadiandjava.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TwilioSmsService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String fromNumber;

    @PostConstruct
    public void initTwilio() {
        try {
            Twilio.init(accountSid, authToken);
            log.info("Twilio SDK initialized successfully.");
        } catch (Exception e) {
            log.error("Failed to initialize Twilio SDK: {}", e.getMessage());
        }
    }

    @Async
    public void sendSms(String toPhoneNumber, String body) {
        if (toPhoneNumber == null || toPhoneNumber.isBlank()) {
            log.warn("Target phone number is empty. Skipping SMS dispatch.");
            return;
        }

        // Clean white spaces and sanitize phone number
        String formattedPhone = toPhoneNumber.trim().replaceAll("\\s+", "");

        // Auto-format local 10-digit Indian numbers to international E.164 standard
        if (!formattedPhone.startsWith("+")) {
            if (formattedPhone.length() == 10) {
                formattedPhone = "+91" + formattedPhone;
            } else {
                formattedPhone = "+" + formattedPhone;
            }
        }

        try {
            Message message = Message.creator(
                    new PhoneNumber(formattedPhone),
                    new PhoneNumber(fromNumber),
                    body
            ).create();

            log.info("Emergency SMS alert successfully queued/sent via Twilio to {} (SID: {})",
                     formattedPhone, message.getSid());

        } catch (Exception e) {
            log.warn("Twilio SMS dispatch to {} failed (Trial account restriction or unverified number): {}",
                     formattedPhone, e.getMessage());
        }
    }
}