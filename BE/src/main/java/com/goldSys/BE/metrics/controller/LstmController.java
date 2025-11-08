package com.goldSys.BE.metrics.controller;

import com.goldSys.BE.metrics.service.LstmService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

/**
 * 개발자 : 최승희
 * LSTM 예측 데이터 컨트롤러
 * 엔드포인트: /api/lstm
 * 메서드 기능
 * getSeries     : 지정한 기간(from~to)의 LSTM 예측 시계열 데이터 조회
 * getAll        : 특정 날짜(to) 기준 전체 메트릭(gold, usd, oil 등) 예측 데이터 일괄 조회
 * proxyHealth   : FastAPI 서버의 상태를 확인하는 헬스체크 기능
 */
@RestController
@RequestMapping("/api/lstm")
@RequiredArgsConstructor
public class LstmController {
    private final LstmService service;      // LSTM 예측 서비스
    private final WebClient fastapiClient;  // FastAPI 서버와 통신하기 위한 WebClient

    /**
     * 개발자 : 최승희
     * LSTM 예측 시계열 조회 기능
     * @param from   조회 시작일 (예: 2023-01-01)
     * @param to     조회 종료일 (예: 2024-12-31)
     * @param metric 예측 대상 메트릭명 (gold, usd, oil 등)
     * @return       [{ "date": "2024-01-01", "value": 1234.56 }, ...] 형태의 예측 시계열 리스트
     */
    @GetMapping("/series")
    public List<Map<String, Object>> getSeries(@RequestParam String from, @RequestParam String to,
                                               @RequestParam(required = false) String metric) {
        return service.getPredSeries(from, to);
    }

    /**
     * 개발자 : 최승희
     * 전체 메트릭 예측 데이터 일괄 조회 기능
     * @param to  조회 기준일 (예: 2024-12-31)
     * @return    [{ "metric": "gold", "date": "...", "value": ... }, ...] 형태의 전체 예측 데이터
     */
    @GetMapping("/series-all")
    public List<Map<String, Object>> getAll(@RequestParam String to) {
        return service.getPredAll(to);
    }

    /**
     * 개발자 : 최승희
     * FastAPI 서버 헬스체크 기능
     * @return { "spring_ok": true, "fastapi_ok": true/false, "fastapi": {...} }
     */
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