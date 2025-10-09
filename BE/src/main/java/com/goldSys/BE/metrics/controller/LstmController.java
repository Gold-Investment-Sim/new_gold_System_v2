package com.goldSys.BE.metrics.controller;

import com.goldSys.BE.metrics.service.LstmService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lstm")
@RequiredArgsConstructor
public class LstmController {
    private final LstmService service;
    private final WebClient fastapiClient; // ← 필드 주입(생성자)

    @GetMapping("/series")
    public List<Map<String, Object>> getSeries(@RequestParam String from, @RequestParam String to,
                                               @RequestParam(required = false) String metric) {
        return service.getPredSeries(from, to);
    }

    @GetMapping("/series-all")
    public List<Map<String, Object>> getAll(@RequestParam String to) {
        return service.getPredAll(to);
    }

    // ---- 진단용 헬스(메서드 파라미터 제거) ----
    @GetMapping("/_health")
    public Map<String, Object> proxyHealth() {
        try {
            Map body = fastapiClient.get().uri("/health")
                    .retrieve().bodyToMono(Map.class).block();
            return Map.of("spring_ok", true, "fastapi_ok", true, "fastapi", body);
        } catch (Exception e) {
            return Map.of("spring_ok", true, "fastapi_ok", false, "error", e.getMessage());
        }
    }
}