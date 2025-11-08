package com.goldSys.BE.metrics.service.impl;

import com.goldSys.BE.history.entity.QuotesDaily;
import com.goldSys.BE.metrics.dto.SeriesPointDto;
import com.goldSys.BE.metrics.repository.QuotesDailyRepository;
import com.goldSys.BE.metrics.service.MetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 개발자 : 최승희
 * 메트릭(지표) 서비스 구현체
 *
 * 역할
 * 1. QUOTES_DAILY 테이블에서 기간별 시세 데이터 조회
 * 2. metric 값에 따라 해당 컬럼을 동적으로 선택 (리플렉션 사용)
 * 3. 날짜 + 값 형식의 SeriesPointDto 리스트로 변환하여 컨트롤러에 전달
 */
@Service
@RequiredArgsConstructor
public class MetricsServiceImpl implements MetricsService {

    private final QuotesDailyRepository repo;

    /**
     * 메트릭 시계열 데이터 조회
     * @param metric 조회 대상 지표명 (예: krw_g_open, krw_g_close, fx_rate 등)
     * @param from   조회 시작일
     * @param to     조회 종료일
     * @return       날짜 + 해당 메트릭 값으로 구성된 SeriesPointDto 리스트
     */
    @Override
    public List<SeriesPointDto> getSeries(String metric, LocalDate from, LocalDate to) {

        // 기간 내 모든 시세 데이터 조회 (오름차순 정렬)
        List<QuotesDaily> rows = repo.findByDateBetweenOrderByDate(from, to);

        // metric 문자열 → 대응되는 엔티티 getter 메서드명으로 매핑
        String getterName = switch (metric.toLowerCase()) {
            case "krw_g_open"   -> "getKrwGOpen";
            case "krw_g_close"  -> "getKrwGClose";
            case "usd_oz_open"  -> "getUsdOzOpen";
            case "usd_oz_close" -> "getUsdOzClose";
            case "vix"          -> "getVix";
            case "etf_volume"   -> "getEtfVolume";
            case "fx_rate"      -> "getFxRate";
            default -> throw new IllegalArgumentException("Unknown metric: " + metric);
        };

        // 각 Row에 대해 선택된 getter를 호출해 값 추출 → SeriesPointDto로 변환
        return rows.stream()
                .map(r -> {
                    try {
                        Method getter = QuotesDaily.class.getMethod(getterName);
                        Double value = (Double) getter.invoke(r);
                        return new SeriesPointDto(r.getDate(), value);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }
}
