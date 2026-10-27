package com.aadiandjava.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aadiandjava.service.ai.AiOrchestratorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "AI Chat Assistant & Direct Healthcare Messaging", description = "24/7 AI Mental Health Conversation & Secure Doctor/Counselor Communications")
public class ChatController {

    private final AiOrchestratorService aiOrchestrator;

    // Completely separated in-memory storage maps for Doctor and Counselor chats
    private static final Map<Long, List<ChatMessageDto>> doctorChatStorage = new ConcurrentHashMap<>();
    private static final Map<Long, List<ChatMessageDto>> counselorChatStorage = new ConcurrentHashMap<>();

    @Operation(
        summary = "Process student message",
        description = "Routes user chat messages through safety filters, triggers emergency dispatches with live location on crisis detection, and orchestrates LLM responses."
    )
    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(
            @RequestParam(defaultValue = "1") Long studentId,
            @RequestParam(required = false, defaultValue = "Hostel Block B / Campus Grounds") String location,
            @RequestBody(required = false) String message) {

        String cleanedMessage = (message != null && !message.trim().isEmpty())
                ? message.trim().replaceAll("^\"|\"$", "")
                : "Hello";

        log.info("Received AI chat request for Student ID: {} at Location: [{}] with Message: {}",
                 studentId, location, cleanedMessage);

        String response = aiOrchestrator.processStudentMessage(studentId, cleanedMessage, location);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get direct chat history with Doctor for a student")
    @GetMapping("/doctor-history")
    public ResponseEntity<List<ChatMessageDto>> getDoctorChatHistory(@RequestParam Long studentId) {
        List<ChatMessageDto> history = doctorChatStorage.getOrDefault(studentId, new ArrayList<>());
        return ResponseEntity.ok(history);
    }

    @Operation(summary = "Get direct chat history with Counselor for a student")
    @GetMapping("/counselor-history")
    public ResponseEntity<List<ChatMessageDto>> getCounselorChatHistory(@RequestParam Long studentId) {
        List<ChatMessageDto> history = counselorChatStorage.getOrDefault(studentId, new ArrayList<>());
        return ResponseEntity.ok(history);
    }

    @Operation(summary = "Send message from student directly to doctor")
    @PostMapping("/student-to-doctor")
    public ResponseEntity<String> studentToDoctorMessage(
            @RequestParam Long studentId,
            @RequestBody String message) {

        String cleanedMessage = cleanMessage(message);
        if (cleanedMessage.isEmpty()) {
            return ResponseEntity.badRequest().body("Message cannot be empty");
        }

        log.info("Student ID: {} sending direct message to doctor: {}", studentId, cleanedMessage);
        saveMessage(doctorChatStorage, studentId, "STUDENT", cleanedMessage);

        return ResponseEntity.ok("Message sent to doctor.");
    }

    @Operation(summary = "Send message from student directly to counselor")
    @PostMapping("/student-to-counselor")
    public ResponseEntity<String> studentToCounselorMessage(
            @RequestParam Long studentId,
            @RequestBody String message) {

        String cleanedMessage = cleanMessage(message);
        if (cleanedMessage.isEmpty()) {
            return ResponseEntity.badRequest().body("Message cannot be empty");
        }

        log.info("Student ID: {} sending direct message to counselor: {}", studentId, cleanedMessage);
        saveMessage(counselorChatStorage, studentId, "STUDENT", cleanedMessage);

        return ResponseEntity.ok("Message sent to counselor.");
    }

    @Operation(summary = "Send message from doctor to student")
    @PostMapping("/doctor-send")
    public ResponseEntity<String> doctorSendMessage(
            @RequestParam Long studentId,
            @RequestBody String message) {

        String cleanedMessage = cleanMessage(message);
        if (cleanedMessage.isEmpty()) {
            return ResponseEntity.badRequest().body("Message cannot be empty");
        }

        log.info("Doctor sending secure message to Student ID: {}: {}", studentId, cleanedMessage);
        saveMessage(doctorChatStorage, studentId, "DOCTOR", cleanedMessage);

        return ResponseEntity.ok("Doctor message dispatched securely.");
    }

    @Operation(summary = "Send message from counselor to student")
    @PostMapping("/counselor-send")
    public ResponseEntity<String> counselorSendMessage(
            @RequestParam Long studentId,
            @RequestBody String message) {

        String cleanedMessage = cleanMessage(message);
        if (cleanedMessage.isEmpty()) {
            return ResponseEntity.badRequest().body("Message cannot be empty");
        }

        log.info("Counselor sending secure message to Student ID: {}: {}", studentId, cleanedMessage);
        saveMessage(counselorChatStorage, studentId, "COUNSELOR", cleanedMessage);

        return ResponseEntity.ok("Counselor message dispatched securely.");
    }

    private String cleanMessage(String message) {
        return (message != null && !message.trim().isEmpty())
                ? message.trim().replaceAll("^\"|\"$", "")
                : "";
    }

    private synchronized void saveMessage(Map<Long, List<ChatMessageDto>> storage, Long studentId, String sender, String text) {
        storage.putIfAbsent(studentId, new ArrayList<>());
        ChatMessageDto dto = new ChatMessageDto();
        dto.setSender(sender);
        dto.setMessage(text);
        dto.setTimestamp(LocalDateTime.now().toString());
        storage.get(studentId).add(dto);
    }

    @Data
    public static class ChatMessageDto {
        private String sender; // "STUDENT", "DOCTOR", "COUNSELOR", or "AI"
        private String message;
        private String timestamp;
    }
}