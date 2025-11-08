package com.goldSys.BE.metrics.controller;

import com.goldSys.BE.metrics.dto.SeriesPointDto;
import com.goldSys.BE.metrics.service.MetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 개발자 : 최승희
 * 메트릭(지표) 데이터 컨트롤러
 * 엔드포인트: /api/metrics
 * 메서드 기능
 * getSeries : 지정된 기간(from~to) 동안 특정 지표(metric)의 시계열 데이터 조회
 */
@RestController
@RequestMapping("/api/metrics")
@RequiredArgsConstructor
public class MetricsController {

    private final MetricsService service;

    /**
     * 개발자 : 최승희
     * 메트릭 시계열 데이터 조회 기능
     * @param metric 조회할 지표명 (예: gold, usd, oil 등)
     * @param from   조회 시작일 (ISO 포맷: 2023-01-01)
     * @param to     조회 종료일 (ISO 포맷: 2024-12-31)
     * @return       [{ "date": "2024-01-01", "value": 1234.56 }, ...] 형태의 시계열 데이터 리스트
     */
    @GetMapping("/series")
    public List<SeriesPointDto> getSeries(
            @RequestParam String metric,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        List<SeriesPointDto> result = service.getSeries(metric, from, to);
        return result;
    }
}
