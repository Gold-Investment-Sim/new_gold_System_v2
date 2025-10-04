package com.goldSys.BE.metrics.controller;

import com.goldSys.BE.metrics.dto.SeriesPointDto;
import com.goldSys.BE.metrics.service.MetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/metrics")
@RequiredArgsConstructor
public class MetricsController {

    private final MetricsService service;

    @GetMapping("/series")
    public List<SeriesPointDto> getSeries(
            @RequestParam String metric,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        System.out.println("📩 [요청 도착] metric=" + metric + ", from=" + from + ", to=" + to);
        List<SeriesPointDto> result = service.getSeries(metric, from, to);
        System.out.println("📤 [응답 데이터 수] = " + result.size());
        return result;
    }
}
