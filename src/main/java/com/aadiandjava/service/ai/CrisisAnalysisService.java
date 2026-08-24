package com.aadiandjava.service.ai;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class CrisisAnalysisService {

    // Primary explicit distress keywords and common variations
    private static final List<String> HIGH_RISK_KEYWORDS = List.of(
        // Primary explicit phrases
        "finish my life", "finish life", "finish it all", "finish myself", "finish my self",
        "want finish life", "wanna finish life", "want finish my life",
        "end my life", "end it all", "end life", "end myself", "end my self",
        "kill myself", "kill my self", "kill me",
        "want to die", "wanna die", "desire to die", "want die",
        "suicide", "suiside", "sucide", "suicidal", "soicide", "seicide",

        // Self-harm & severe distress phrases
        "self harm", "self-harm", "hurt myself", "cut myself",
        "don't want to live", "dont want to live", "dont wanna live", "dont want live",
        "no reason to live", "hopeless", "give up on life", "tired of living",
        "bye world", "goodbye world", "goodbye everyone"
    );

    public boolean isCriticalRisk(String userMessage) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return false;
        }

        String lowerMessage = userMessage.toLowerCase().trim();

        // 1. Direct keyword/phrase match
        boolean directMatch = HIGH_RISK_KEYWORDS.stream().anyMatch(lowerMessage::contains);
        if (directMatch) {
            return true;
        }

        // 2. Word-boundary aware regex rules for obfuscated or spaced variations
        // Prevents matching non-crisis phrases like "finish my homework for life class"

        // Matches variations of "finish ... life" with intentional spacing/symbols between letters
        boolean regexFinishLife = lowerMessage.matches(".*\\bf[i*#@!\\s]*n[i*#@!\\s]*s[h*#@!\\s]*\\b.*\\bl[i*#@!\\s]*f[e*#@!\\s]*\\b.*");

        // Matches variations of "suicide" (e.g., s-u-i-c-i-d-e, s u i c i d e)
        boolean regexSuicide = lowerMessage.matches(".*\\bs[u*#@!\\s]*i[c*#@!\\s]*i[d*#@!\\s]*e\\b.*");

        // Matches variations of "kill ... self/myself"
        boolean regexKillSelf = lowerMessage.matches(".*\\bk[i*#@!\\s]*l[l*#@!\\s]*\\b.*\\bm[y*#@!\\s]*s[e*#@!\\s]*l[f*#@!\\s]*\\b.*");

        // Matches variations of "end ... life"
        boolean regexEndLife = lowerMessage.matches(".*\\be[n*#@!\\s]*d\\b.*\\bl[i*#@!\\s]*f[e*#@!\\s]*\\b.*");

        return regexFinishLife || regexSuicide || regexKillSelf || regexEndLife;
    }
}