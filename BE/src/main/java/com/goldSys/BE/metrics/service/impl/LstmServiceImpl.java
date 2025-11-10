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

/**
 * 개발자 : 최승희
 * LSTM 예측 서비스 구현체
 *
 * 역할
 * 1. 메트릭 일별 데이터 조회 및 LSTM 입력 포맷(RowDto) 변환
 * 2. FastAPI /predict 호출을 통한 예측 수행
 * 3. 예측 결과를 {date, value} 형태의 리스트로 변환하여 컨트롤러에 제공
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LstmServiceImpl implements LstmService {
    private static final int WINDOW = 30; // LSTM 입력 윈도우 크기

    private final MetricDailyRepository repo;  // 일별 메트릭 데이터 조회용 리포지토리
    private final WebClient fastapiClient;     // FastAPI 서버와 통신하는 WebClient

    /**
     * 지정된 기간(from~to)의 예측 시계열 조회
     * @param from 조회 시작일 (yyyy-MM-dd)
     * @param to   조회 종료일 (yyyy-MM-dd)
     * @return     요청 기간 내 {date, value} 예측 결과 리스트
     */
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

    /**
     * 기준일(to) 기준 전체 구간 예측 조회
     * @param to 조회 기준일 (yyyy-MM-dd)
     * @return   {date, value} 전체 예측 결과 리스트
     */
    @Override
    public List<Map<String, Object>> getPredAll(String to) {
        LocalDate t = LocalDate.parse(to);

        // to 포함 과거 데이터 조회 (여유 10년; 필요하면 조정)
        LocalDate startWide = t.minusYears(10);
        List<QuotesDaily> all = repo.findByDateBetweenOrderByDateAsc(startWide, t);
        if (all.size() <= WINDOW) {
            log.warn("[LSTM] getPredAll - not enough rows: {}", all.size());
            return Collections.emptyList();
        }

        // LSTM 입력
        List<RowDto> payload = toRows(all);
        int maxSeq = payload.size() - WINDOW; // 이론상 예측 개수
        if (maxSeq <= 0) {
            log.warn("[LSTM] getPredAll - maxSeq<=0, payload={}", payload.size());
            return Collections.emptyList();
        }

        PredictReq req = PredictReq.builder()
                .rows(payload)
                .returnLastN(maxSeq)   // 전체 시퀀스 예측
                .nextDay(false)
                .build();

        PredictRes res = callFastApi(req);
        if (res == null || res.getYPred() == null || res.getYPred().isEmpty()) {
            log.warn("[LSTM] getPredAll - empty PredictRes");
            return Collections.emptyList();
        }

        List<Double> yPred = res.getYPred();
        int n = all.size();
        int m = yPred.size();

        // 예측 개수가 기대보다 많으면 뒤에서 maxSeq개만 사용
        if (m > maxSeq) {
            log.warn("[LSTM] getPredAll - yPred size({}) > maxSeq({}), trimming tail", m, maxSeq);
            yPred = yPred.subList(m - maxSeq, m);
            m = maxSeq;
        }

        // all[n-m..n-1] 에 yPred[0..m-1] 매핑
        int startIdx = n - m;
        if (startIdx < 0) {
            log.error("[LSTM] getPredAll - negative startIdx. n={}, m={}", n, m);
            startIdx = 0;
        }

        List<Map<String, Object>> result = new ArrayList<>(m);
        for (int i = 0; i < m; i++) {
            LocalDate date = all.get(startIdx + i).getDate();
            Double val = yPred.get(i);

            Map<String, Object> row = new HashMap<>(2);
            row.put("date", date.toString());
            row.put("value", val);
            result.add(row);
        }

        LocalDate first = all.get(startIdx).getDate();
        LocalDate last = all.get(startIdx + m - 1).getDate();
        log.info("[LSTM] getPredAll mapped: inputRows={}, preds={}, first={}, last={}",
                n, m, first, last);

        return result;
    }


    /* ================== 내부 헬퍼 메서드 ================== */

    // QuotesDaily 리스트를 LSTM 입력용 RowDto 리스트로 변환
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

    // FastAPI /predict 호출 및 예측 결과
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

    // PredictRes의 timestamps, yPred를 {date, value} 리스트로 병합
    private List<Map<String, Object>> zip(PredictRes res) {
        if (res.getTimestamps() == null || res.getYPred() == null) return List.of();
        return IntStream.range(0, res.getTimestamps().size())
                .mapToObj(i -> Map.<String, Object>of(
                        "date", res.getTimestamps().get(i),
                        "value", res.getYPred().get(i)))
                .toList();
    }

    // null 방지용 기본값 처리
    private double nz(Double v) { return v == null ? 0.0 : v; }
}
