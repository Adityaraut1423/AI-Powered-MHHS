package com.aadiandjava.service;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Call;
import com.twilio.type.PhoneNumber;

import jakarta.annotation.PostConstruct;

@Service
public class TwilioVoiceService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String twilioPhoneNumber;

    @PostConstruct
    public void initTwilio() {
        if (accountSid != null && !accountSid.startsWith("your_")) {
            Twilio.init(accountSid, authToken);
        }
    }

    public void triggerEmergencyCall(String guardianPhoneNumber, String studentName, String location) {
        try {
            // Ensure phone number format is valid (E.164 format with country code)
            String toPhone = guardianPhoneNumber.startsWith("+") ? guardianPhoneNumber : "+91" + guardianPhoneNumber;

            // TwiML Bin URL from your Twilio Console
            String twimlBinUrl = "https://handler.twilio.com/twiml/EHe24cbff14e6bc2e4b7dd494d76778d27";

            // Using URI instead of inline Twiml to completely bypass trial parameter restrictions
            Call.creator(
                new PhoneNumber(toPhone),
                new PhoneNumber(twilioPhoneNumber),
                URI.create(twimlBinUrl)
            ).create();

            System.out.println("🚨 Emergency voice call successfully initiated to guardian: " + toPhone);
        } catch (Exception e) {
            System.err.println("Failed to initiate Twilio emergency call: " + e.getMessage());
        }
    }
}