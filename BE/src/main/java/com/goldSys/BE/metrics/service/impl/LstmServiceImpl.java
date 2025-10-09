// src/main/java/com/goldSys/BE/metrics/service/impl/LstmServiceImpl.java
package com.goldSys.BE.metrics.service.impl;

import com.goldSys.BE.history.entity.QuotesDaily;
import com.goldSys.BE.metrics.dto.PredictReq;
import com.goldSys.BE.metrics.dto.PredictRes;
import com.goldSys.BE.metrics.dto.RowDto;
import com.goldSys.BE.metrics.repository.MetricDailyRepository;
import com.goldSys.BE.metrics.service.LstmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class LstmServiceImpl implements LstmService {
    private static final int WINDOW = 30;

    private final MetricDailyRepository repo;   // ← 변경
    private final WebClient fastapiClient;

    @Override
    public List<Map<String, Object>> getPredSeries(String from, String to) {
        LocalDate f = LocalDate.parse(from);
        LocalDate t = LocalDate.parse(to);

        LocalDate startWide = f.minusDays(120);
        List<QuotesDaily> all = repo.findByDateBetweenOrderByDateAsc(startWide, t);
        if (all.size() <= WINDOW) return List.of();

        List<RowDto> payload = toRows(all);
        PredictReq req = PredictReq.builder()
                .rows(payload)
                .returnLastN(Math.max(payload.size() - WINDOW, 1))  // ← camelCase 필드명
                .nextDay(false)
                .build();

        PredictRes res = callFastApi(req);
        if (res == null) return List.of();

        return zip(res).stream()
                .filter(m -> {
                    LocalDate d = LocalDate.parse((String) m.get("date"));
                    return !d.isBefore(f) && !d.isAfter(t);
                })
                .toList();
    }

    @Override
    public List<Map<String, Object>> getPredAll(String to) {
        LocalDate t = LocalDate.parse(to);
        LocalDate startWide = t.minusYears(12); // 10년 + 여유
        List<QuotesDaily> all = repo.findByDateBetweenOrderByDateAsc(startWide, t);
        if (all.size() <= WINDOW) return List.of();

        List<RowDto> payload = toRows(all);
        PredictReq req = PredictReq.builder()
                .rows(payload)
                .returnLastN(payload.size() - WINDOW)
                .nextDay(false)
                .build();

        PredictRes res = callFastApi(req);
        if (res == null) return List.of();

        return zip(res);
    }

    /* ---------- helpers ---------- */

    private List<RowDto> toRows(List<QuotesDaily> src) {
        return src.stream()
                .map(r -> RowDto.builder()
                        .date(r.getDate().toString())
                        .krw_g_open(nz(r.getKrwGOpen()))   // ← 소문자 빌더명
                        .fx_rate(nz(r.getFxRate()))
                        .vix(nz(r.getVix()))
                        .etf_volume(nz(r.getEtfVolume()))
                        .krw_g_close(nz(r.getKrwGClose()))
                        .build())
                .toList();
    }

    private PredictRes callFastApi(PredictReq req) {
        int n = req.getRows() == null ? 0 : req.getRows().size();
        log.info("POST /predict rows={}, returnLastN={}, nextDay={}", n, req.getReturnLastN(), req.getNextDay());

        return fastapiClient.post()
                .uri("/predict")
                .bodyValue(req)
                .retrieve()
                .onStatus(s -> s.value() == 422,
                        r -> r.bodyToMono(String.class)
                                .map(body -> new RuntimeException("FastAPI 422: " + body)))
                .bodyToMono(PredictRes.class)
                .onErrorResume(e -> Mono.error(new RuntimeException("FastAPI 호출 실패: " + e.getMessage())))
                .block();
    }

    private List<Map<String, Object>> zip(PredictRes res) {
        if (res.getTimestamps() == null || res.getYPred() == null) return List.of();
        return IntStream.range(0, res.getTimestamps().size())
                .mapToObj(i -> Map.<String, Object>of(
                        "date", res.getTimestamps().get(i),
                        "value", res.getYPred().get(i)))
                .toList();
    }


    private double nz(Double v) { return v == null ? 0.0 : v; }
}
