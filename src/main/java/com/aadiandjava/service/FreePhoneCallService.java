package com.aadiandjava.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FreePhoneCallService {

    private final RestTemplate restTemplate = new RestTemplate();

    public void triggerEmergencyCall(String guardianPhoneNumber, String studentName, String location) {
        try {
            // Ensure phone format is clean (E.164 format with country code)
            String toPhone = guardianPhoneNumber.startsWith("+") ? guardianPhoneNumber : "+91" + guardianPhoneNumber;
            
            // Construct the text-to-speech message that the automated voice will read aloud
            String speechMessage = String.format(
                "Emergency Alert! Student %s is in severe distress at location %s. Please check your email and database logs immediately.",
                studentName, location
            );

            // Free call gateway API endpoint
            String url = UriComponentsBuilder.fromHttpUrl("https://api.inout.bot/call")
                    .queryParam("phone", toPhone)
                    .queryParam("apikey", "YOUR_FREE_API_KEY") // Replace with your free key or test directly
                    .queryParam("message", speechMessage)
                    .queryParam("language", "en-US")
                    .toUriString();

            restTemplate.getForObject(url, String.class);
            log.info("🚨 Free emergency phone call successfully initiated to guardian: {}", toPhone);

        } catch (Exception e) {
            log.error("Failed to trigger free emergency phone call: {}", e.getMessage());
        }
    }
}