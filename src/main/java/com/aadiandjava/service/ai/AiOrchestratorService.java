package com.aadiandjava.service.ai;

import java.util.List;

import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aadiandjava.service.SosOrchestratorService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AiOrchestratorService {

    @Autowired
    private ChatModel chatModel;

    @Autowired
    private CrisisAnalysisService crisisAnalysisService;

    @Autowired
    private SosOrchestratorService sosOrchestratorService;

    private static final String JARVIS_SYSTEM_PROMPT = """
        You are JARVIS, an articulate, polite, empathetic, and highly supportive AI assistant.
        Your primary focus is the mental health and well-being of the user.
        - Tone: Calm, structured, warm, and sophisticated.
        - Greeting Style: Direct, polite, and reassuring.
        - Guidance: Offer practical, thoughtful coping mechanisms, deep breathing exercises, or simple reflective questions.
        - Keep responses focused, gentle, and respectful at all times.
        """;

    public String processStudentMessage(Long studentId, String userMessage, String location) {

        // Validate and assign fallback for location parameter
        String validLocation = (location != null && !location.isBlank())
                ? location
                : "Hostel Block B / Campus Grounds";

        // 1. Safety Gate: Intercept high-risk / critical statements
        if (crisisAnalysisService.isCriticalRisk(userMessage)) {

            log.warn("CRITICAL RISK DETECTED for Student ID: {} at Location: [{}]. Message: '{}'",
                     studentId, validLocation, userMessage);

            // Asynchronously dispatch SOS alert, SMS, and Email with live location to emergency contacts/guardians
            sosOrchestratorService.triggerEmergency(
                studentId,
                "CRISIS_KEYWORD_DETECTED: " + userMessage,
                validLocation
            );

            // Return localized emergency notice to student
            return """
                CRITICAL NOTICE: We noticed you are in severe distress. An emergency alert containing your location has been sent to your registered guardian and campus response teams.
                Please reach out immediately for support:
                • Tele-MANAS (India): Call 14416 or 1800-891-4416
                • KIRAN Helpline: 1800-599-0019
                • Vandrevala Foundation: +91 9999 666 555
                • National Crisis Lifeline: Call/Text 988
                """;
        }

        // 2. Standard Interaction: Route to JARVIS LLM
        try {
            SystemMessage systemMessage = new SystemMessage(JARVIS_SYSTEM_PROMPT);
            UserMessage message = new UserMessage(userMessage);

            Prompt prompt = new Prompt(List.of(systemMessage, message));
            return chatModel.call(prompt).getResult().getOutput().getContent();

        } catch (Exception e) {
            log.error("Error communicating with AI ChatModel: {}", e.getMessage());

            // 3. Fallback: Offline Self-Care Mode
            return """
                I am currently operating in Offline Self-Care Mode.
                • Try a Box Breathing exercise: Inhale (4s), Hold (4s), Exhale (4s), Hold (4s).
                • Log your current mood in the Mood Log tab.
                • If you are experiencing distress, press the Emergency SOS button or call Tele-MANAS at 14416.
                """;
        }
    }

    // Overloaded method for backward compatibility
    public String processStudentMessage(Long studentId, String userMessage) {
        return processStudentMessage(studentId, userMessage, "Hostel Block B / Campus Grounds");
    }
}