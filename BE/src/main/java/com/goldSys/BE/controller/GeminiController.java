// BE/src/main/java/com/goldSys/BE/controller/GeminiController.java

package com.goldSys.BE.controller;

import com.goldSys.BE.service.GeminiService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/gemini")
@CrossOrigin(origins = "http://localhost:5173") // React 개발 서버 주소 허용
public class GeminiController {

    private final GeminiService geminiService;

    public GeminiController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping("/analyze")
    public Map<String, String> analyzeResult(@RequestBody Map<String, String> requestBody) {
        String simulationResult = requestBody.get("resultText");
        String analysis = geminiService.getAnalysisFeedback(simulationResult);
        return Map.of("analysis", analysis);
    }
}