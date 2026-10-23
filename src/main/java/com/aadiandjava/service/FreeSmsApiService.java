package com.aadiandjava.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FreeSmsApiService {

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendFreeSms(String phoneNumber, String studentName, String location, String crisisType) {
        try {
            // Clean phone number format (remove +91 if the gateway expects 10 digits)
            String cleanPhone = phoneNumber.replaceAll("[^0-9]", "");
            if (cleanPhone.length() > 10) {
                cleanPhone = cleanPhone.substring(cleanPhone.length() - 10);
            }

            String messageBody = String.format(
                "EMERGENCY: %s is in distress at %s. Reason: %s. Please check portal.",
                studentName, location, crisisType
            );

            String url = UriComponentsBuilder.fromHttpUrl("https://api.textbelt.com/text")
                    .queryParam("phone", cleanPhone)
                    .queryParam("message", messageBody)
                    .queryParam("key", "textbelt")
                    .toUriString();

            String response = restTemplate.postForObject(url, null, String.class);
            log.info("Free SMS API dispatch response for {}: {}", cleanPhone, response);

        } catch (Exception e) {
            // Gracefully catch timeout/network errors so the rest of the SOS pipeline remains uninterrupted
            log.warn("Free SMS gateway unreachable (Network Timeout). Email and Phone Call alerts remain active.");
        }
    }
}